"""
 history.py - Handles the History of the jython console
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
from org.gjt.sp.jedit.gui import HistoryModel

class History(object):
	"""
		The class history handles the history management basically wrapping the
		built-in jEdit's history capabilities
	"""
	def __init__(self, console):
		self.history = HistoryModel.getModel("jython")
		self.console = console
		self.index = 0

	def append(self, line):
		self.history.addItem(line)
		self.index = 0

	def historyUp(self, event):
		if self.history.size and self.console.inLastLine():
			self.console.replaceRow(self.history.getItem(self.index))
			self.index = min(self.index + 1, self.history.size - 1)

	def historyDown(self, event):
		if self.history.size and self.console.inLastLine():
			if self.index <= 1:
				self.console.replaceRow("")
			else:
				self.index -= 1
				self.console.replaceRow(self.history.getItem(self.index-1))

# :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:
