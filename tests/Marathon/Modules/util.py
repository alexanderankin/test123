from java.lang import System

def makePath(dir, file):
	from marathon.playback import *
	sep = System.getProperty('file.separator')
	return dir + sep + file
