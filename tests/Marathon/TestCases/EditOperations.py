#{{{ Marathon Fixture
from default import *
from util import *
from java.lang import *
from javax.swing import SwingUtilities
from java.awt.datatransfer import *
#}}} Marathon Fixture

def test():
	java_recorded_version = '1.6.0_11'
	clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard()

	class ClipboardAccess(java.lang.Runnable):
		def get(self):
			SwingUtilities.invokeAndWait(self)
			return self.text
		def run(self):
			self.text = clipboard.getContents(None).getTransferData(DataFlavor.stringFlavor)
	
	if window('jEdit - TestPlan.txt'):
		click('TextAreaPainter', 177, 195)
		select_menu('Edit>>Cut')
		clipObj = ClipboardAccess()
		s = clipObj.get()
		print "Cut: " + s
		assert s.startswith('test plan of the open source software jEdit'), 'Cut failure'
	close()

	if window('jEdit - TestPlan.txt (modified)'):
		select_menu('Edit>>Undo')
	close()

	if window('jEdit - TestPlan.txt'):
		window_closed('jEdit - TestPlan.txt')
	close()
