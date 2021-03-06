"""Helper functions

Consists of functions to typically be used within templates, but also
available to Controllers. This module is available to both as 'h'.
"""
# Import helpers as desired, or define your own, ie:
# from webhelpers.html.tags import checkbox, password

from pylons import tmpl_context as c
from webhelpers.html.tags import stylesheet_link, javascript_link
from routes import url_for, redirect_to
from simplejson import dumps, JSONEncoder
import operator
import re


attrgetter = operator.attrgetter

def esc(str):
	str.replace

def hide(val, hidenum):
	ret = ""
	if val is None:
		return ret
	for ii in range(0, min(len(val), hidenum)):
		ret += val[ii]

	for ii in range(hidenum, len(val)):
		if val[ii] == ' ':
			ret += '&nbsp;&nbsp;'
		else:
			ret += '*'

	return ret

compresswhitespace = re.compile(r"^\s+", re.MULTILINE);

def oneline(text):
	return compresswhitespace.sub(" ", text)

def t3(val, sub = None, sign = False):
	if val is None or val == 0:
		return ''
	if sub is not None:
		val -= sub
	if type(val) is int:
		return str(val)

	if sign:	
		return "%+0.3f" % (val)
	else:
		return "%0.3f" % (val)

def ixstr(car):
	ret = c.classdata.getIndexStr(car)
	if ret == "":
		return ret
	return "(%s)" % ret


def encodesqlobj(obj):
	d = dict()
	for k in obj.__dict__.copy():
		if k.startswith("_"): continue
		d[k] = getattr(obj, k)
	return dumps(d, default=lambda x: None) 

