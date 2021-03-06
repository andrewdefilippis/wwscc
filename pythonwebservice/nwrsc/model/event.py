from sqlalchemy import Table, Column
from sqlalchemy.orm import mapper, object_session
from sqlalchemy.types import Integer, SmallInteger, String, Boolean, Date, DateTime, Float
from sqlalchemy.sql import func

from meta import metadata

import sys

## Events table
t_events = Table('events', metadata,
	Column('id', Integer, primary_key=True, autoincrement=True),
	Column('name', String(64)),	
	Column('date', Date),	
	Column('location', String(64)),	
	Column('sponsor', String(64)),	
	Column('host', String(64)),	
	Column('chair', String(64)),	
	Column('designer', String(64)),	
	Column('ispro', Boolean),
	Column('courses', SmallInteger),
	Column('runs', SmallInteger),
	Column('countedruns', SmallInteger),
	Column('conepen', Float, default=2.0),
	Column('gatepen', Float, default=10.0),
	Column('segments', String(64)),
	Column('regopened', DateTime),
	Column('regclosed', DateTime),	
	Column('perlimit', SmallInteger),
	Column('totlimit', SmallInteger),
	Column('paypal', String(64)),	
	Column('snail', String(256)),	
	Column('cost', SmallInteger),
	Column('notes', String(256)),
	Column('practice', Boolean),
	Column('doublespecial', Boolean)
	)


class Event(object):

	def __init__(self, **kwargs):
		self.merge(**kwargs)

	def merge(self, **kwargs):
		for k, v in kwargs.iteritems():
			if hasattr(self, k):
				setattr(self, k, v)

	def _get_count(self):
		""" property getter to get number of entrants for an event based on registration, NOT recorded runs """
		from registration import Registration
		return object_session(self).query(Registration.id).filter(Registration.eventid==self.id).count()
	count = property(_get_count)

	def _get_drivers_count(self):
		""" property getter to get number of entrants for an event based on number of distinct drivers registered """
		return object_session(self).execute("select count(distinct(c.driverid)) from cars as c, registered as r where r.carid=c.id and r.eventid=%d"%self.id).fetchone()[0]
	drivercount = property(_get_drivers_count)


	def getFeed(self):
		d = dict()
		for k,v in self.__dict__.iteritems():
			if v is None or k in ['_sa_instance_state', 'paypal', 'snail', 'cost', ]:
				continue
			d[k] = v
		return d


	def getSegments(self):
		if hasattr(self, '_seglist'): # shortcut cache
			return self._seglist

		self._seglist = list()
		for seg in str(self.segments).strip().split(','):
			try:
				self._seglist.append(int(seg))
			except:
				pass
		return self._seglist

	def getSegmentCount(self):
		return len(self.getSegments())

	def getCountedRuns(self):
		if self.countedruns <= 0:
			return sys.maxint
		else:
			return self.countedruns
		
			
mapper(Event, t_events)

