"""
 exportconsole.py - The console used to export the results of a run
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

from jython import Console
from org.gjt.sp.jedit import jEdit, GUIUtilities
from javax.swing.text import SimpleAttributeSet
from javax.swing.text import BadLocationException
from javax.swing.text import StyleConstants
from javax.swing.text import Segment

class ConsoleToBuffer(Console):
	def __init__(self, view, buffer):
		self.target = jEdit.newFile(view)
		self.target.setProperty("jython.originator", buffer)
		self.background = GUIUtilities.parseColor(jEdit.getProperty( \
			"jython.bgColor"))
		self.foreground = GUIUtilities.parseColor(jEdit.getProperty( \
			"jython.plainColor"))
		self.infoColor = GUIUtilities.parseColor(jEdit.getProperty( \
			"jython.infoColor"))
		self.warningColor = GUIUtilities.parseColor(jEdit.getProperty( \
			"jython.warningColor"))
		self.errorColor = GUIUtilities.parseColor(jEdit.getProperty( \
			"jython.errorColor"))

	def printInfo(self, msg):
		self.addOutput(self.infoColor,msg)

	def printPlain(self, msg):
		self.addOutput(self.foreground, msg)

	def printWarning(self, msg):
		self.addOutput(self.warningColor, msg)

	def printPrompt(self):
		pass

	def printErrorMsg(self, msg, filename, lineno):
		self.addOutput(self.errorColor, u"\n" + msg)

	def printError(self, e):
		from org.python.core import PyException
		from java.io import ByteArrayOutputStream, PrintStream
		import traceback
		if isinstance(e, PyException):
			self.reflectChanges = 0
			out = ByteArrayOutputStream()
			e.printStackTrace(PrintStream(out))
			self.addOutput(self.errorColor, u"\n%s" % unicode(out)[:-1])

	def clear(self):
		pass

	def printResult(self, msg):
		self.addOutput(self.foreground, msg)

	def printOnProcess(self, msg):
		self.addOutput(self.foreground, msg)

	def clear(self):
		from utils import BufferList
		l = BufferList(self.target)
		l.clean()

	def addOutput(self, color, msg):
		# Makes no sense without Buffer.insertString()
		#style = SimpleAttributeSet()
		#if color:
		#	style.addAttribute(StyleConstants.Foreground,color)
		try:
			# Buffer.insertString() was removed in jEdit 4.5.
			# insert() shall be used.
			#self.target.insertString(self.target.length, msg, style)
			self.target.insert(self.target.length, msg)
		except BadLocationException:
			Log.log(Log.ERROR, self, "")
		if jEdit.getBooleanProperty("options.jython.cleanDirtyFlag"):
			self.target.dirty = 0

# :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:
