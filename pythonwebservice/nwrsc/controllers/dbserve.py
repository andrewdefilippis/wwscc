import traceback
import sys
import shutil
import logging

log = logging.getLogger(__name__)

from pylons import request, response, session
from pylons.controllers.util import abort, url_for
from sqlalchemy import create_engine

from nwrsc.controllers.lib.base import BaseController, BeforePage
from nwrsc.lib.digest import authCheck
from nwrsc.lib.codec import Codec, DataInput
from nwrsc.lib.resultscalc import UpdateClassResults
from nwrsc.lib.passwordstrip import stripPasswords, restorePasswords
from nwrsc.model import *


class DbserveController(BaseController):
	"""
		DBServe is used as the contact point for the java applications when speaking to
		the web service.
	"""

	def available(self): 
		""" special URL, doesn't have database assigned, not verfication required """
		response.headers['Content-type'] = 'text/plain'
		data = ""
		for db in self._databaseList(archived=False):
			data += "%s %s %s\n" % (db.name, db.locked and "1" or "0", db.archived and "1" or "0")
		return data
		

	def __before__(self):
		action = self.routingargs.get('action', '')

		if action in ('available'):
			return
		if not self.database or action == 'index':
			raise BeforePage("")

		try:
			digestinfo = session.setdefault('digest', {})
			passwords = Password.load(self.session)
			authCheck(digestinfo, self.database, {"admin" : passwords['series'] }, request)
			# at this point, they are verified as knowing the password for database:series
		finally:
			session.save()
		

	def download(self):
		if self.settings.locked:
			log.warning("Download request for %s, but it is locked" % (self.database))
			abort(404, "Database locked, unavailable for download")
		self.settings.locked = True
		self.settings.save(self.session)
		self.session.commit()
		return self.copy()


	def copy(self):
		response.headers['Content-type'] = 'application/octet-stream'
		#data = stripPasswords(self.databasePath(self.database))
		fp = open(self.databasePath(self.database), 'rb')
		data = fp.read()
		fp.close()
		log.info("Read in database file of %d bytes", len(data))
		return data


	def upload(self):
		#restorePasswords(request.environ['wsgi.input'], self.databasePath(self.database))
		dbptr = open(self.databasePath(self.database), 'wb')
		shutil.copyfileobj(request.environ['wsgi.input'], dbptr)
		dbptr.close()
		
		engine = create_engine('sqlite:///%s' % self.databasePath(self.database))
		self.session.bind = engine
		Registration.updateFromRuns(self.session)

		self.settings = Settings()
		self.settings.load(self.session)
		self.settings.locked = False
		self.settings.save(self.session)
		self.session.commit()
		return "Complete"


	def sqlmap(self):
		try:
			stream = DataInput(request.environ['wsgi.input'].read(int(request.environ['CONTENT_LENGTH'])))
			ret = ""
			while stream.dataAvailable() > 0:
				(type, key, values) = Codec.decodeRequest(stream)

				log.debug("sqlmap request %s" % (key))
				if type == Codec.SELECT:
					ret = Codec.encodeResults(self.session.sqlmap(key, values))
				elif type == Codec.UPDATE:
					ret = Codec.encodeLastId(self.session.sqlmap(key, values).lastrowid)
				elif type == Codec.FUNCTION:
					if hasattr(self, key):
						ret = getattr(self, key)(*values)
					else:
						raise Exception("Unknown FUNCTION call %s" % (key))
				else:
					raise Exception("Unknown call type %s" % (type))

			self.session.commit()
			return ret

		except Exception, e:
			self.session.rollback()
			msg = Exception.__str__(e)
			filename, lineno, name, line = traceback.extract_tb(sys.exc_info()[2])[-1]
			return Codec.encodeError(filename, lineno, msg)


	# FUNCTION calls
	def GetCarAttributes(self, attr):
		rs = self.session.connection().execute(
			"select distinct %s from cars where LOWER(%s)!=%s and UPPER(%s)!=%s order by %s collate nocase" \
				% tuple([attr]*6))
		return Codec.encodeResults(rs)

	def UpdateClass(self, eventid, course, classcode, carid):
		UpdateClassResults(self.session, eventid, course, classcode, carid)
		return Codec.encodeLastId(-1)

