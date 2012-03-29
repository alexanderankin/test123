# $Id: init.py,v 1.14 2003/05/27 21:26:15 tibu Exp $

from xmllib import XMLParser

def _getUsersJythonDir():
	from org.gjt.sp.jedit.jEdit import getSettingsDirectory
	import os

	dirpath = os.path.join(getSettingsDirectory(), 'jython')
	try:
		if not os.path.exists(dirpath):
			os.makedirs(dirpath)
	except IOError:
		pass
	except OSError:
		pass
	return dirpath

def _start():
	"""
	Adds the following directories to jython's path

		[jEdit user's home dir]\jython

	Will try to create directories if they don't exist.
	Then, if a file 'jedit.py' exists in either directory, this
	exec'd.  This can be used to setup Jython interpreter: import
	packages, run scripts, etc...

	Author Oliver Rutherfurd

	"""
	import os
	import sys
	from org.gjt.sp.jedit import jEdit

	parser = PathLoader()
	try:
		filename = os.path.join(_getUsersJythonDir(), "jython.xml")
		f = open(filename, 'r')
		content = f.read()
		f.close()
		parser.feed(content)
		parser.close()
	except Exception, e:
		pass


class PathLoader(XMLParser):
	""" This class parses the jython.xml file and adds the path entries
	"""
	def __init__(self, **kw):
		apply(XMLParser.__init__, (self,), kw)

	def unknown_starttag(self, tag, attrs):
		import sys
		if tag == "pathentry":
			if not attrs["path"] in sys.path:
				sys.path.insert(int(attrs["order"]), attrs["path"])

	def close(self):
		try:
			XMLParser.close(self)
		except:
			pass


if __name__ in ("__main__","main"):
	try:
		_start()
	finally:
		del XMLParser

# :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:
