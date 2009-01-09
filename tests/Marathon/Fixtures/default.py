from org.gjt.sp.jedit import jEdit
from marathon.playback import *

import os
from java.lang import System

settingsDir = System.getProperty('user.home') + System.getProperty('file.separator') + '.jEditTest'

class Fixture:
	def start_application(self):
		args = ["-reuseview", "-settings=" + settingsDir]
		jEdit.main(args)
		
	def teardown(self):
		pass

	def setup(self):
		self.start_application()

	def test_setup(self):
		pass

fixture = Fixture()
