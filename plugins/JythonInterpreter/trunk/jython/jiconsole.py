"""
 jiconsole.py - The console window
 Copyright (C) 2001 Carlos Quiroz

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
"""

from javax.swing.text import Document, SimpleAttributeSet, \
	StyleConstants, JTextComponent, TextAction, DefaultEditorKit
from javax.swing import JPanel, JToolBar, JButton, JTextPane, JScrollPane, \
	KeyStroke, JComponent, Action
from java.awt import BorderLayout, Insets, Font
from java.awt.event import KeyEvent, InputEvent
from org.gjt.sp.jedit import jEdit
from org.gjt.sp.util import Log
from org.python.util import InteractiveConsole

from jython import JythonExecutor
from jython import Console

from utils import centerDialog
from toolbar import ToolbarHandler
from init import _getUsersJythonDir
from history import History
from org.python.core import Py

import sys

""" experimental code completion module, not used in this version """
#import jintrospect

class TabIndenter(object):
	def __init__(self):
		self.indentlevel = 0
	def isPushable(self, text):
		char = text[:-1]
		return char != '\\'
	def increaseIndent(self):
		self.indentlevel += 1
		pass
	def reduceIndent(self):
		self.indentlevel = max(0, self.indentlevel)
		pass
	def toString():
		return "\t".join()

class ActionDelegator(TextAction):
	"""
		Class action delegator encapsulates a TextAction delegating the action
		event to a simple function
	"""
	def __init__(self, name, delegate):
		TextAction.__init__(self, name)
		self.delegate = delegate

	def actionPerformed(self, event):
		if isinstance(self.delegate, Action):
			self.delegate.actionPerformed(event)
		elif self.delegate:
			self.delegate(event)

class jiconsole(Console):
	"""
		Class jiconsole, it is the main class which handles user interaction
	"""
	PROMPT = sys.ps1
	PROCESS = sys.ps2
	BANNER = (InteractiveConsole.getDefaultBanner(), jEdit.getProperty("console-second-line"))

	def __init__(self, view):
		""" Constructor, initialized all the main variables and layout """
		# Initializes variables
		self.view = view
		self.history = History(self)
		self.bs = 0
		self.indenter = TabIndenter()
		self.exporterConsole = None
		self.executor = JythonExecutor.getExecutor()

		# Creates toolbar
		actions = [ ("Run.png", "jython.tooltip-run", self.runBuffer), \
			("RunToBuffer.png", "jython.tooltip-run-another", self.runBufferToWindow), \
			("RunAgain.png", "jython.tooltip-import", self.importBuffer), \
			("MultipleResults.png", "jython.tooltip-path", self.path), \
			("Open.png", "jython.tooltip-browse-path", self.browse), \
			("CopyToBuffer.png", "jython.tooltip-save-session", self.savesession), \
			("separator", None, None), \
			("Clear.png", "jython.tooltip-restart", self.restart),
			("separator", None, None),
			("Parse.png", "jython.tooltip-tabnanny", self.tabnanny),
			("separator", None, None),
			("Help.png", "jython.tooltip-about", self.about)]
		self.panel = JPanel(BorderLayout())
		self.panel.add(BorderLayout.NORTH, ToolbarHandler(actions).createToolbar())

		# Creates text pane and make keybindings
		# self.output = JTextPane(keyTyped = self.keyTyped, keyPressed = self.keyPressed, keyReleased = self.keyReleased)
		self.output = JTextPane(keyTyped = self.keyTyped, keyPressed = self.keyPressed)
		if jEdit.getBooleanProperty("options.jython.upDownFlag"):
			keyBindings = [
				(KeyEvent.VK_ENTER, 0, "jython.enter", self.enter),
				(KeyEvent.VK_DELETE, 0, "jython.delete", self.delete),
				(KeyEvent.VK_HOME, 0, "jython.home", self.home),
				(KeyEvent.VK_UP, 0, "jython.up", self.history.historyUp),
				(KeyEvent.VK_DOWN, 0, "jython.down", self.history.historyDown),
				(KeyEvent.VK_UP, InputEvent.CTRL_MASK, DefaultEditorKit.upAction, self.output.keymap.getAction(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0))),
				(KeyEvent.VK_DOWN, InputEvent.CTRL_MASK, DefaultEditorKit.downAction, self.output.keymap.getAction(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0)))
			]
		else:
			keyBindings = [
				(KeyEvent.VK_ENTER, 0, "jython.enter", self.enter),
				(KeyEvent.VK_DELETE, 0, "jython.delete", self.delete),
				(KeyEvent.VK_HOME, 0, "jython.home", self.home),
				(KeyEvent.VK_UP, InputEvent.CTRL_MASK, "jython.historyup", \
					self.history.historyUp),
				(KeyEvent.VK_DOWN, InputEvent.CTRL_MASK, "jython.historydown", \
					self.history.historyDown)
			]
		newmap = JTextComponent.addKeymap("jython", self.output.keymap)
		for (key, modifier, name, function) in keyBindings:
			newmap.addActionForKeyStroke(KeyStroke.getKeyStroke(key, modifier), ActionDelegator(name, function))
		self.output.keymap = newmap
		self.doc = self.output.document
		self.panel.add(BorderLayout.CENTER, JScrollPane(self.output))
		self.__propertiesChanged()
		self.__inittext()
		self.initialLocation = self.doc.createPosition(self.doc.length-1)

	def browse(self, event):
		""" Opens the browse window """
		self.view.dockableWindowManager.toggleDockableWindow("jython-browser-dockable");

	def stop(self, event):
		""" Stops the current execution """
		self.executor.interruptCurrent(self)

	def about(self, event):
		""" Opens the about dialog """
		from jiabout import AboutDialog
		a = AboutDialog(self.view, jEdit.getProperty("jython.about.title"))
		centerDialog(a)
		a.setVisible(1)

	def savesession(self, event):
		""" Saves the current session """
		from exportsession import export
		export(self.view, self.doc)

	def path(self, event):
		""" Opens the Path Handler """
		from pathhandler import PyPathHandler
		v = PyPathHandler(self.view, sys)
		v.pack()
		centerDialog(v)
		v.visible = 1

	def __buffer(self):
		"""
			Returns the current buffer ready for run/import, checking if it is
			a python buffer and whether has been saved
		"""
		from javax.swing import JOptionPane
		buf = self.view.editPane.buffer
		if buf.mode.name != "python":
			if buf.getProperty("jython.originator"):
				buf = buf.getProperty("jython.originator")

		if buf.mode.name == "python":
			if buf.dirty:
				if not jEdit.getBooleanProperty("options.jython.autosave"):
					if jEdit.getBooleanProperty("options.jython.saveask"):
						result = JOptionPane.showConfirmDialog(self.panel, "Do you want to save before running", "Confirm", JOptionPane.YES_NO_CANCEL_OPTION)
						if result == JOptionPane.CANCEL_OPTION:
							return None
						if result == JOptionPane.YES_OPTION:
							buf.save(self.view, None)
				else:
					buf.save(self.view, None)
			return buf

		return None

	def runBuffer(self, event):
		""" Runs the current buffer """
		buffer = self.__buffer()
		if buffer:
			JythonExecutor.getExecutor().execBuffer(self.view, buffer, self)

	def runBufferToWindow(self, event):
		""" Runs the current buffer to and exports the result to another window """
		from exportconsole import ConsoleToBuffer
		buffer = self.__buffer()
		if buffer:
			if jEdit.getBooleanProperty("options.jython.reuseOutputBuffer"):
				if not self.exporterConsole:
					self.exporterConsole = ConsoleToBuffer(self.view, buffer)
				self.exporterConsole.clear()
			else:
				self.exporterConsole = ConsoleToBuffer(self.view, buffer)
			JythonExecutor.getExecutor().execBuffer(self.view, buffer, self.exporterConsole)

	def tabnanny(self, event):
		import jitabnanny
		buffer = self.__buffer()
		jitabnanny.checkBuffer(buffer)

	def importBuffer(self, event):
		""" Imports the current buffer """
		import os.path
		import imp
		b = self.__buffer()
		if b:
			modname, ext = os.path.splitext(os.path.basename(b.path))
			if sys.modules.has_key(modname):
				mod = sys.modules[modname]
			else:
				mod = imp.new_module(modname)
			sys.modules[modname] = mod
			mod.__file__ = b.path
			setattr(sys.modules['__main__'], modname, mod)

			dir = os.path.dirname(b.path)
			dir = os.path.normpath(os.path.abspath(dir))
			if dir not in sys.path:
				sys.path.insert(0, dir)
			JythonExecutor.getExecutor().importBuffer(self.view, b, self)

	def restart(self, event):
		""" Reinitializes the interpreter """
		from javax.swing import JOptionPane
		result = JOptionPane.showConfirmDialog(self.panel, \
			jEdit.getProperty("jython.console.clean-confirm"), "Confirm", JOptionPane.YES_NO_CANCEL_OPTION)
		if result == JOptionPane.YES_OPTION:
			JythonExecutor.getExecutor().interrupt()
			self.__inittext()

	def inLastLine(self, include = 1):
		""" Determines whether the cursor is in the last line """
		limits = self.__lastLine()
		caret = self.output.caretPosition
		if self.output.selectedText:
			caret = self.output.selectionStart
		if include:
			return (caret >= limits[0] and caret <= limits[1])
		else:
			return (caret > limits[0] and caret <= limits[1])

	def enter(self, event):
		""" Triggered when enter is pressed """
		offsets = self.__lastLine()
		text = self.doc.getText(offsets[0], offsets[1]-offsets[0])
		# Detects special keywords
		if text != "\n":
			if text[:-1] == "copyright":
				self.printResult(jEdit.getProperty("jython.interpreter.copyright") + sys.copyright)
				self.printPrompt()
				return
			if text[:-1] == "credits":
				import site
				self.printResult(jEdit.getProperty("jython.interpreter.credits") + "\n" + str(site.__builtin__.credits))
				self.printPrompt()
				return
			if text[:-1] == "license":
				import site
				self.printResult(jEdit.getProperty("jython.interpreter.license"))
				self.printPrompt()
				return
			text = text[:-1]
			if self.indenter.isPushable(text):
				JythonExecutor.getExecutor().execute(self.view, text, self)
				self.history.append(text)
			else:
				self.printOnProcess()
		else:
			JythonExecutor.getExecutor().execute(self.view, text, self)

	def home(self, event):
		""" Triggered when HOME is pressed """
		if self.inLastLine():
			self.output.caretPosition = self.__lastLine()[0]
		else:
			lines = self.doc.rootElements[0].elementCount
			for i in xrange(0,lines-1):
				offsets = (self.doc.rootElements[0].getElement(i).startOffset, \
					self.doc.rootElements[0].getElement(i).endOffset)
				line = self.doc.getText(offsets[0], offsets[1]-offsets[0])
				if self.output.caretPosition >= offsets[0] and \
					self.output.caretPosition <= offsets[1]:
					if line.startswith(jiconsole.PROMPT) or line.startswith(jiconsole.PROCESS):
						self.output.caretPosition = offsets[0] + len(jiconsole.PROMPT)
					else:
						self.output.caretPosition = offsets[0]

	def replaceRow(self, text):
		""" Replaces the last line of the textarea with text """
		offset = self.__lastLine()
		last = self.doc.getText(offset[0], offset[1]-offset[0])
		if last != "\n":
			self.doc.remove(offset[0], offset[1]-offset[0]-1)
		self.__addOutput(self.infoColor, text)

	def delete(self, event):
		""" Intercepts delete events only allowing it to work in the last line """
		if self.inLastLine():
			if self.output.selectedText:
				self.doc.remove(self.output.selectionStart, self.output.selectionEnd - self.output.selectionStart)
			elif self.output.caretPosition < self.doc.length:
				self.doc.remove(self.output.caretPosition, 1)

	def keyReleased(self, event):
		""" Experimental code completion, disabled for now """
		if event.keyChar == '.':
			offsets = self.__lastLine()
			text = self.doc.getText(offsets[0], offsets[1]-offsets[0]-1)
			#completions = jintrospect.getAutoCompleteList(text, globals())
			completions = 0
			if completions:
				CompletionPopup(self.view, completions, self.output, self)

	def keyTyped(self, event):
		if not self.inLastLine():
			event.consume()
		if self.bs:
			event.consume()
		self.bs=0

	def keyPressed(self, event):
		if event.keyCode == KeyEvent.VK_BACK_SPACE:
			offsets = self.__lastLine()
			if not self.inLastLine(include=0):
				self.bs = 1
			else:
				self.bs = 0

	def printResult(self, msg):
		""" Prints the results of an operation """
		self.__addOutput(self.output.foreground, "\n" + str(msg))

	def printOnProcess(self):
		""" Prints the process symbol """
		self.__addOutput(self.infoColor, "\n" + jiconsole.PROCESS)

	def printPrompt(self):
		""" Prints the prompt """
		self.__addOutput(self.infoColor, "\n" + jiconsole.PROMPT)

	def printErrorMsg(self, msg, file, line):
		self.__addOutput(self.errorColor, "\n" + str(msg))
		self.__addErrorButton((file, line))

	def printError(self, e):
		import org
		self.__addOutput(self.errorColor, "\n%s" % str(e)[:-1])

		if isinstance(e, org.python.core.PySyntaxError):
			self.__addErrorButton((e.value[1][0], e.value[1][1]))
		elif isinstance(e, org.python.core.PyException):
			import traceback
			self.__addErrorButton((traceback.extract_tb(e.traceback)[-1][0], traceback.extract_tb(e.traceback)[-1][1]))

	def _completeToken(self, token):
		""" Adds a token to be completed """
		style = SimpleAttributeSet()

		style.addAttribute(StyleConstants.Foreground, self.infoColor)

		self.doc.insertString(self.output.caretPosition, token, style)

	def __addOutput(self, color, msg):
		""" Adds the output to the text area using a given color """
		style = SimpleAttributeSet()

		if color:
			style.addAttribute(StyleConstants.Foreground, color)

		self.doc.insertString(self.doc.length, msg, style)
		self.output.caretPosition = self.doc.length

	def __propertiesChanged(self):
		""" Detects when the properties have changed """
		self.output.background = jEdit.getColorProperty("jython.bgColor")
		self.output.foreground = jEdit.getColorProperty("jython.resultColor")
		self.infoColor = jEdit.getColorProperty("jython.textColor")
		self.errorColor = jEdit.getColorProperty("jython.errorColor")

		family = jEdit.getProperty("jython.font", "Monospaced")
		size = jEdit.getIntegerProperty("jython.fontsize", 14)
		style = jEdit.getIntegerProperty("jython.fontstyle", Font.PLAIN)
		self.output.setFont(Font(family,style,size))

	def __inittext(self):
		""" Inserts the initial text with the jython banner """
		self.doc.remove(0, self.doc.length)
		for line in "\n".join(jiconsole.BANNER):
			self.__addOutput(self.infoColor, line)
		self.printPrompt()
		self.output.requestFocus()

	def __lastLine(self):
		""" Returns the char offsets of the last line """
		lines = self.doc.rootElements[0].elementCount
		offsets = (self.doc.rootElements[0].getElement(lines-1).startOffset, \
		self.doc.rootElements[0].getElement(lines-1).endOffset)
		line = self.doc.getText(offsets[0], offsets[1]-offsets[0])
		if len(line) >= 4 and (line[0:4]==jiconsole.PROMPT or line[0:4]==jiconsole.PROCESS):
			return (offsets[0] + len(jiconsole.PROMPT), offsets[1])
		return offsets

	def __addErrorButton(self, parsed):
		from error import ErrorButton
		if parsed[0] != "<console>":
			self.__addOutput(self.errorColor, "   ")
			self.output.insertComponent(ErrorButton(self.view, parsed))

def _createConsole(view, docker):
	docker.removeAll()
	docker.add(jiconsole(view).panel)
	docker.revalidate()

if __name__ in ("__main__","main"):
	from javax.swing import JFrame
	v = jiconsole(jEdit.getLastView())
	frame = JFrame()
	frame.getContentPane().add(v.panel)
	frame.pack()
	frame.visible = 1

# :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:
