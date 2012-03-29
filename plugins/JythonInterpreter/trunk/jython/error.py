"""
 error.py - The Error Button
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

from javax.swing import JButton
from java.awt import Cursor, Insets
from org.gjt.sp.jedit import jEdit, GUIUtilities
from java.util.zip import ZipFile
import os.path
import sys

class ErrorButton(JButton):
	"""
		Class error button, it disaplys a little button which can be used to
		jump to the error source
	"""
	def __init__(self, view, handler):
		JButton.__init__(self)
		self.view = view
		self.icon = GUIUtilities.loadIcon("Find.png")
		self.margin = Insets(0,0,0,0)
		self.handler = handler
		self.alignY = 1
		self.actionPerformed = self.action
		self.cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
		self.toolTipText = "Jump to error file %s:%s" % (str(handler[0]), str(handler[1]))

	def action(self, event):
		if self.handler.list[0] != "<console>":
			buffer = None
			if (os.path.isfile(self.handler[0])):
				buffer = jEdit.openFile(self.view, self.handler[0])
			else:
				for i in sys.path:
					if buffer:
						break
					split = os.path.splitext(i)
					ext = split[1]
					if len(split[1].split("!"))>1:
						ext = split[1].split("!")[0]
					if ext=='.jar' or ext=='.zip':
						zip = ZipFile(split[0] + ext)
						entries = zip.entries()
						for j in xrange(0, zip.size()):
							zipEntry = entries.nextElement()
							if zipEntry.name == self.handler[0]:
								name = "archive:" + split[0] + ext + "!/" + self.handler[0]
								buffer = jEdit.openFile(self.view, name)
								break
			if buffer:
				self.view.buffer = buffer
				lineNo = int(self.handler[1]) - 1
				line = buffer.defaultRootElement.getElement(lineNo)
				if line:
					end = line.startOffset
					self.view.textArea.moveCaretPosition(end)

# :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:
