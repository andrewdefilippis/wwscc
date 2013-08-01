#!/usr/bin/env python

import sys
import os
import csv
import datetime
import re
import uuid
from nwrsc.model import *
from sqlalchemy import create_engine

class Tables(object):
	def __init__(self):
		self.drivers = []
		self.passwords = []
		self.roles = []	
		self.memberships = dict()
		self.series = []
		self.data = []
		self.events = []
		self.cars = []
		self.classlist = []
		self.indexlist = []
		self.payments = []
		self.registered = []
		self.rungroups = []
		self.runorder = []
		self.runs = []
		self.segments = []
		self.challenges = []
		self.challengeroundentries = []
		self.challengeruns = []

		self.seriesid = 0
		self.eventid = 0
		self.driverid = 0
		self.carid = 0
		self.challengeid = 0
		self.roundid = 0

	def seriesId(self):
		self.seriesid +=1
		return self.seriesid
	def eventId(self):
		self.eventid +=1
		return self.eventid
	def driverId(self):
		self.driverid += 1
		return self.driverid
	def carId(self):
		self.carid += 1
		return self.carid
	def challengeId(self):
		self.challengeid += 1
		return self.challengeid
	def roundId(self):
		self.roundid += 1
		return self.roundid


gTables = Tables()
gMatchDrivers = dict()

class MyObj(object):
	def __init__(self, **kwargs):
		for k,v in kwargs.iteritems():
			setattr(self, k, v)

def objs2lists(objs, fields):
	ret = list()
	for o in objs:
		ret.append([getattr(o, f) for f in fields])
	return ret

def writecsv(dirp, name, objs, fields, newfields = None):
	csvfile = open(os.path.join(dirp, name), 'w')
	w = csv.writer(csvfile)
	w.writerow(fields)
	w.writerows(objs2lists(objs, fields))
	csvfile.close()


ONLYW = re.compile("^\w+$")
MINDIG = re.compile("\d{4}")
nummatch = re.compile('^(\d{6}_\d)|(\d{6})$')

def convert(sourcefile):
	global gTables, gMatchDrivers

	remapdriver = dict()
	remapcar = dict()
	remapevent = dict()
	remapchallenge = dict()

	print "Read in %s" % sourcefile
	metadata.bind = create_engine('sqlite:///%s' % sourcefile)
	session = Session()
	session.bind = metadata.bind

	# Extra cleaning
	session.execute("delete from cars where id not in (select distinct carid from runs)")
	session.execute("delete from drivers where id not in (select distinct driverid from cars)")
	session.execute("delete from cars where driverid not in (select distinct id from drivers)")
	session.execute("delete from registered where carid not in (select distinct id from cars)")
	session.execute("delete from runorder where carid not in (select distinct id from cars)")
	session.execute("delete from challengerounds where challengeid not in (select distinct id from challenges)")

	# querys to get all the data
	drivers = session.query(Driver).all()
	cars = session.query(Car).all()
	settings = session.query(Setting).all()
	data = session.query(Data).all()
	events = session.query(Event).all()
	classes = session.query(Class).all()
	indexes = session.query(Index).all()
	payments = session.query(Payment).all()
	registered = session.query(Registration).all()
	rungroups = session.query(RunGroup).all()
	runorder = session.query(RunOrder).all()
	runs = session.query(Run).all()
	challenges = session.query(Challenge).all()
	challengerounds = session.query(ChallengeRound).all()
	session.close()

	# Create Series (rename some settings, create active)
	seriesid = gTables.seriesId()
	series = MyObj(id=seriesid, prefix=os.path.basename(sourcefile)[:-7], year=sourcefile[-7:-3])
	for s in settings:
		setattr(series, s.name, s.val)
	series.name = series.seriesname
	series.usepositionpoints = series.usepospoints
	series.positionpoints = series.pospointlist
	series.champuseevents = series.useevents
	series.champminevents = series.minevents
	series.active = False
	series.parentid = -1
	series.indexgroup = "%s%s PAX" % (series.prefix, series.year)
	gTables.series.append(series)


	#DRIVERS, add to global list and remap ids as necessary
	for d in drivers:
		d.guid = uuid.uuid1()
		d.phone = d.phone and d.phone[:16]
		d.brag = d.brag and d.brag[:128]
		d.sponsor = d.sponsor and d.sponsor[:128]

		key = d.firstname.strip().lower() + d.lastname.strip().lower() + d.email.strip().lower()
		if key in gMatchDrivers:
			remapdriver[d.id] = gMatchDrivers[key].id
			d.id = gMatchDrivers[key].id
		else:
			remapdriver[d.id] = gTables.driverId()
			d.id = remapdriver[d.id]
			d.emergencyname = ""
			d.emergencycontact = ""
			gTables.drivers.append(d)
			gMatchDrivers[key] = d
		if d.membership:
			if nummatch.match(d.membership):
				gTables.memberships[d.id,"SCCA"] = MyObj(driverid=d.id, club='SCCA', membership=d.membership)
			elif ONLYW.search(d.membership):
				gTables.memberships[d.id,"Other"] = MyObj(driverid=d.id, club='Other', membership=d.membership)

	#CARS (all the same fields, need to map carid, driverid and seriesid)
	for c in cars:
		try:
			remapcar[c.id] = gTables.carId()
			c.id = remapcar[c.id]
			c.driverid = remapdriver[c.driverid]
			c.seriesid = seriesid
			gTables.cars.append(c)
		except:
			print "invalid driverid %s, skipping" % c.driverid

	#DATA (all the same fields need to map seriesid)
	for d in data:
		d.seriesid = seriesid
		d.data = str(d.data).encode("hex")
		gTables.data.append(d)

	#EVENTS (all the same fields, change segments to segmentsetup, perlimit to personallimit, totlimit to totalimit, new blank field infopage, need to map eventid and seriesid)
	for e in events:
		remapevent[e.id] = gTables.eventId()
		e.id = remapevent[e.id]
		e.seriesid = seriesid
		e.segmentsetup = e.segments
		e.personallimit = e.perlimit
		e.totallimit = e.totlimit
		e.infopage = ""
		gTables.events.append(e)

	#CLASSLIST (map seriesid)
	for c in classes:
		c.description = c.descrip
		c.seriesid = seriesid
		gTables.classlist.append(c)

	#INDEXLIST (put into its own index group)
	for i in indexes:
		i.indexgroup = "%s%s PAX" % (series.prefix, series.year)
		i.description = i.descrip
		gTables.indexlist.append(i)

	#PAYMENTS (just leave blank, don't really need old payment data)

	#REGISTERED (map eventid, carid)
	for r in registered:
		if r.eventid > 1000:  # weird registered for a challenge junk from the old pros
			continue
		try:
			r.eventid = remapevent[r.eventid]
			r.carid = remapcar[r.carid]
			r.mod = "2000-01-01 00:00:00"
			gTables.registered.append(r)
		except:
			print "missing carid %s for registered, skipping" % r.carid

	#RUNGROUPS (map eventid, change gorder to position)
	for r in rungroups:
		r.eventid = remapevent[r.eventid]
		r.position = r.gorder
		gTables.rungroups.append(r)

	#RUNORDER (map eventid, carid)
	for r in runorder:
		try:
			r.eventid = remapevent[r.eventid]
			r.carid = remapcar[r.carid]
			gTables.runorder.append(r)
		except:
			print "missing carid %s for runorder, skipping" % r.carid

	#RUNS (map eventid, carid)
	challengeruns = list()
	for r in runs:
		try:
			r.carid = remapcar[r.carid]
		except:
			print "missing carid %s for run, skipping" % r.carid

		for ii in range(1,6):
			seg = getattr(r, 'seg%d'%ii)
			if seg > 0:
				segment = MyObj(**r.__dict__)
				segment.segment = ii
				segment.raw = seg
				gTables.segments.append(segment)

		if r.eventid > 0x0FFFF:
			r.roundentryid = -1
			challengeruns.append(r)
		else:
			r.eventid = remapevent[r.eventid]
			gTables.runs.append(r)

	#CHALLENGES (remap challengeid, eventid)
	for c in challenges:
		remapchallenge[c.id] = gTables.challengeId()
		c.id = remapchallenge[c.id]
		c.eventid = remapevent[c.eventid]
		gTables.challenges.append(c)

	def rewriteruns(challengeid, round, carid, entryid):
		for run in challengeruns:
			run.side = run.course
			if run.challengeid == challengeid and run.carid == carid and run.round == round:
				run.roundentryid = entryid

	#CHALLENGEROUNDS (remap roundid, challengeid, carid)
	for r in challengerounds:
		if r.round == 0:  # convert round into a round location
			upper = 1
			lower = 101
		elif r.round == 99:
			upper = 103
			lower = 102
		else:
			lower = r.round * 2
			upper = lower + 1

		r.challengeid = remapchallenge[r.challengeid]
		if r.car1id > 0:
			r.car1id = remapcar[r.car1id]
		if r.car2id > 0:
			r.car2id = remapcar[r.car2id]

		entryid = gTables.roundId()
		gTables.challengeroundentries.append(MyObj(id=entryid, challengeid=r.challengeid, location=upper, carid=r.car1id, indial=r.car1dial, result=r.car1result, outdial=r.car1newdial)) 
		rewriteruns(r.challengeid, r.round, r.car1id, entryid)

		entryid = gTables.roundId()
		gTables.challengeroundentries.append(MyObj(id=entryid, challengeid=r.challengeid, location=lower, carid=r.car2id, indial=r.car2dial, result=r.car2result, outdial=r.car2newdial)) 
		rewriteruns(r.challengeid, r.round, r.car2id, entryid)


	#CHALLENGRUNS
	gTables.challengeruns.extend(challengeruns)
	

if len(sys.argv) < 3:
	print "\nUsage: %s <sourcedirectory> <outputdirectory>\n" % sys.argv[0]
	print "\tExtracts the data from all databases in <sourcedirectory> into <outputdirectory>\n\n"
	sys.exit(0)

sourcedir = sys.argv[1]
destdir = sys.argv[2]

for dbfile in os.listdir(sourcedir):
	if not dbfile.endswith('.db'):
		continue
	convert(os.path.join(sourcedir,dbfile))

writecsv(destdir, 'series.csv', gTables.series, ['id', 'prefix', 'year', 'name', 'locked', 'active', 'superuniquenumbers', 'indexafterpenalties', 'usepositionpoints', 'indexgroup', 'positionpoints', 'champsorting', 'sponsorlink', 'champuseevents', 'champminevents', 'largestcarnumber', 'parentid'])
writecsv(destdir, 'drivers.csv', gTables.drivers, ['id', 'guid', 'firstname', 'lastname', 'alias', 'email', 'address', 'city', 'state', 'zip', 'phone', 'emergencyname', 'emergencycontact', 'sponsor', 'brag'])
writecsv(destdir, 'memberships.csv', gTables.memberships.values(), ['driverid', 'club', 'membership'])
writecsv(destdir, 'cars.csv', gTables.cars, ['seriesid', 'id', 'driverid', 'year', 'make', 'model', 'color', 'number', 'classcode', 'indexcode', 'tireindexed'])
writecsv(destdir, 'data.csv', gTables.data,  ['seriesid', 'name', 'mime', 'mod', 'data'])
writecsv(destdir, 'events.csv', gTables.events,  ['id', 'seriesid', 'name', 'date', 'location', 'sponsor', 'host', 'chair', 'designer', 'ispro', 'courses', 'runs', 'countedruns', 'conepen', 'gatepen', 'segmentsetup', 'regopened', 'regclosed', 'personallimit', 'totallimit', 'paypal', 'snail', 'cost', 'practice', 'doublespecial', 'infopage'])
writecsv(destdir, 'classlist.csv', gTables.classlist, ['code', 'seriesid', 'description', 'carindexed', 'classindex', 'classmultiplier', 'eventtrophy', 'champtrophy', 'numorder', 'countedruns', 'usecarflag', 'caridxrestrict'])
writecsv(destdir, 'indexlist.csv', gTables.indexlist, ['indexgroup', 'code', 'description', 'value'])
writecsv(destdir, 'registered.csv', gTables.registered, ['eventid', 'carid', 'paid', 'mod'])
writecsv(destdir, 'rungroups.csv', gTables.rungroups, ['eventid', 'classcode', 'rungroup', 'position'])
writecsv(destdir, 'runorder.csv', gTables.runorder, ['eventid', 'course', 'carid', 'rungroup', 'row'])
writecsv(destdir, 'runs.csv', gTables.runs, ['eventid', 'carid', 'course', 'run', 'reaction', 'sixty', 'raw', 'cones', 'gates', 'status'])
writecsv(destdir, 'segments.csv', gTables.segments, ['eventid', 'carid', 'course', 'run', 'segment', 'raw'])
writecsv(destdir, 'challenges.csv', gTables.challenges, ['id', 'eventid', 'name', 'depth'])
writecsv(destdir, 'challengeroundentries.csv', gTables.challengeroundentries, ['id', 'challengeid', 'location', 'carid', 'indial', 'result', 'outdial'])
writecsv(destdir, 'challengeruns.csv', gTables.challengeruns, ['roundentryid', 'side', 'reaction', 'sixty', 'raw', 'cones', 'gates', 'status'])


print "done."

