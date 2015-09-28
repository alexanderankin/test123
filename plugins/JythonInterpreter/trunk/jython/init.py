# $Id: init.py,v 1.14 2003/05/27 21:26:15 tibu Exp $

import sys
import os

from xml.sax import handler, make_parser
from org.gjt.sp.util import Log

def _getUsersJythonDir():
	from org.gjt.sp.jedit.jEdit import getSettingsDirectory
	import os

	dirpath = os.path.join(getSettingsDirectory(), 'jython')
	try:
		if not os.path.exists(dirpath):
			os.makedirs(dirpath)
	except IOError, e:
		Log.log(Log.DEBUG, _getUsersJythonDir, u"Creating directory %s failed: %s" %
			(dirpath, unicode(e)))
	except OSError, e:
		Log.log(Log.DEBUG, _getUsersJythonDir, u"Creating directory %s failed: %s" %
			(dirpath, unicode(e)))
	return dirpath

def _start():
	"""
	Sets up the sys.path from the file jython.xml in the user's
	jython directory that is in the settings directory.
	"""
	#import os
	#import sys
	#from org.gjt.sp.jedit import jEdit

	#content = None
	try:
		filename = os.path.join(_getUsersJythonDir(), "jython.xml")
		Log.log(Log.DEBUG, _start, u"jython.xml: %s" % filename)
		if os.path.isfile(filename):
			parser = make_parser()
			contentHandler = PathLoaderContentHandler()
			parser.setContentHandler(contentHandler)
			parser.parse(filename)
	except Exception, e:
		Log.log(Log.ERROR, _start, u"Reading jython.xml failed: %s" % unicode(e))


class PathLoaderContentHandler(handler.ContentHandler):

	def __init__(self):
		self.level = 0

	def startElement(self, name, attrs):
		
		if 'pathentry' == name:
			self.level += 1
			path = attrs['path']
			order = int(attrs['order'])
			normpath = os.path.normpath(path)
			normsyspath = [os.path.normpath(x) for x in sys.path]
			if not normpath in normsyspath:
				sys.path.insert(order-1, normpath)

	def endElement(self, name):
		if 'pathentry' == name:
			self.level -= 1



if __name__ in ("__main__","main"):
	try:
		_start()
	finally:
		del handler
		del make_parser

# :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:
