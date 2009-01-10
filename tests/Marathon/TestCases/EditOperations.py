#{{{ Marathon Fixture
from default import *
from util import *
from java.lang import *
from javax.swing import SwingUtilities
from java.awt.datatransfer import *
from org.gjt.sp.jedit.textarea import Selection
#}}} Marathon Fixture

def enterKeystroke(key, count):
	for i in range(1, count + 1):
		keystroke('JEditTextArea', key)

def test():
	java_recorded_version = '1.6.0_11'
	clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard()

	class ClipboardAccess(java.lang.Runnable):
		def get(self):
			self.setString = ''
			SwingUtilities.invokeAndWait(self)
			return self.text

		def setText(self, st):
			self.setString = st
			SwingUtilities.invokeAndWait(self)

		def run(self):
			if (self.setString != ''):
				clipboard.setContents(StringSelection(self.setString), None)
			else:
				self.text = clipboard.getContents(None).getTransferData(DataFlavor.stringFlavor)
	
	if window('jEdit - TestPlan.txt'):
		selection = Selection.Range(284, 327)
		get_component('JEditTextArea').setSelection(selection)
		select_menu('Edit>>Cut')
		clipObj = ClipboardAccess()
		s = clipObj.get()
		assert s.startswith('test plan of the open source software jEdit'), 'Cut failure'
	close()

	if window('jEdit - TestPlan.txt (modified)'):
		select_menu('Edit>>Undo')
	close()

	if window('jEdit - TestPlan.txt'):
		selection = Selection.Range(1565, 1590)
		get_component('JEditTextArea').setSelection(selection)
		select_menu('Edit>>Copy')
		clipObj = ClipboardAccess()
		s = clipObj.get()
		assert s.startswith('Basic features of the GUI'), 'Copy failure'

		selection = Selection.Range(1583, 1590)
		get_component('JEditTextArea').setSelection(selection)
		clipObj = ClipboardAccess()
		s = clipObj.setText('jEdit')
		select_menu('Edit>>Paste')
		selection = Selection.Range(1565, 1590)
		get_component('JEditTextArea').setSelection(selection)
		s = get_component('JEditTextArea').getSelectedText()
		assert s.startswith('Basic features of jEdit'), 'Paste failure'
	close()
