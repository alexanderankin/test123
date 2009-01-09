#{{{ Marathon Fixture
from default import *
from java.lang import System
import os
#}}} Marathon Fixture

def makePath(dir, file):
	sep = System.getProperty('file.separator')
	return dir + sep + file

def enterString(str):
	for char in list(str):
		keystroke('JEditTextArea', char)

def test():
	java_recorded_version = '1.6.0_11'

	projDir = System.getProperty('marathon.project.dir')
	inputDir = makePath(projDir, 'Input')
	outputDir = makePath(projDir, 'Output')
	inputFile = makePath(inputDir, 'TestPlan.txt')
	saveFile = makePath(outputDir, 'savedFile.txt')
	os.remove(saveFile)
	
	if window('/jEdit - .*'):
		select_menu('File>>Open...')

		if window('File Browser'):
			select('File name', inputFile)
			click('Open')
		close()
	close()

	if window(r'/jEdit - .*TestPlan.txt'):
		# Verify that the text area shows the opened file
		textArea = get_component('JEditTextArea')
		s = textArea.getText(0, textArea.getLineEndOffset(0))
		assert s.startswith('jEdit Test Plan'), inputFile + ' not opened correctly.'
		select_menu('File>>New')
	close()

	if window('jEdit - Untitled-1'):
		enterString("This is a dummy file to test Save As...")
	close()

	if window('jEdit - Untitled-1 (modified)'):
		select_menu('File>>Save')

		if window('File Browser'):
			select('File name', saveFile)
			click('Save')
		close()
	close()

	if window(r'/jEdit - .*savedFile.txt'):
		a = 1
	close()
