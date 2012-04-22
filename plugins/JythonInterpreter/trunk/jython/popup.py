"""
 popup.py - The console window
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

from javax.swing.event import ListDataListener
from javax.swing import *
from java.awt.event import *
from java.awt import BorderLayout
from java.awt import Point
from java.util import *
from java.lang import Runnable
from  org.gjt.sp.jedit.gui import KeyEventWorkaround
from org.gjt.sp.jedit import *

class CompletionPopup(JWindow, Runnable):
	def __init__(self, view, tokens, textArea, console):
		JWindow.__init__(self, view)
		self.view = view
		self.tokens = tokens
		self.tokens.sort()
		self.textArea = textArea
		self.console = console
		self.length = 0

		self.list = JList(selectionMode = ListSelectionModel.SINGLE_SELECTION, mouseClicked = self.mouseClicked)
		self.list.cellRenderer = DefaultListCellRenderer()
		model = CompletionModel(tokens)
		self.list.model = model
		self.list.selectedIndex = 0

		location = self.textArea.modelToView(self.textArea.caretPosition).location
		location.y = location.y + self.textArea.getFontMetrics(self.textArea.font).height
		SwingUtilities.convertPointToScreen(location, self.textArea)

		scroller = JScrollPane(self.list, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER)

		self.contentPane.add(scroller, BorderLayout.CENTER)

		self.keyHandler = KeyHandler(self)
		self.addKeyListener(self.keyHandler)
		self.rootPane.addKeyListener(self.keyHandler)
		self.list.addKeyListener(self.keyHandler)
		self.view.keyEventInterceptor = self.keyHandler
		self.textArea.addKeyListener(self.keyHandler)

		GUIUtilities.requestFocus(self, self.list)

		self.pack()
		if location.y + self.size.height > self.view.size.height:
			location.y = max(0, self.view.size.height - self.size.height - self.textArea.getFontMetrics(self.textArea.font).height)
		self.location = location
		self.show()

	def run(self):
		self.textArea.requestFocus()

	def dispose(self):
		self.view.keyEventInterceptor = None
		self.textArea.removeKeyListener(self.keyHandler)

		JWindow.dispose(self)
		SwingUtilities.invokeLater(self)

	def mouseClicked(self, evt):
		self.complete()

	def complete(self):
		if self.list.selectedIndex>=0:
			self.console._completeToken(self.list.selectedValue)
		self.dispose()

	def matchCharacter(self, ch):
		l = [x for x in self.tokens if x[self.length] == ch]
		if l:
			selected = self.tokens.index(l[self.length])
			self.list.selectedIndex = selected
			self.list.ensureIndexIsVisible(selected)
		self.length = self.length + 1
		print self.length

class KeyHandler(KeyAdapter):
	def __init__(self, parent):
		KeyAdapter.__init__(self)
		self.parent = parent

	def keyPressed(self, evt):
		evt = KeyEventWorkaround.processKeyEvent(evt)
		if not evt:
			return

		if evt.keyCode == KeyEvent.VK_ENTER:
			self.parent.complete()
			evt.consume()
			return

		if evt.keyCode == KeyEvent.VK_ESCAPE:
			self.parent.dispose()
			evt.consume()
			return

		if evt.keyCode == KeyEvent.VK_UP:
			selected = self.parent.list.selectedIndex
			if selected == 0:
				selected = self.parent.list.model.size -1
			elif self.parent.view.getFocusOwner() == self.parent.list:
				return
			else:
				selected = selected - 1
			self.parent.list.selectedIndex = selected
			self.parent.list.ensureIndexIsVisible(selected)
			evt.consume()
			return

		if evt.keyCode == KeyEvent.VK_DOWN:
			selected = self.parent.list.selectedIndex
			if selected == self.parent.list.model.size - 1:
				selected = 0
			elif self.parent.view.getFocusOwner() == self.parent.list:
				return
			else:
				selected = selected + 1
			self.parent.list.selectedIndex = selected
			self.parent.list.ensureIndexIsVisible(selected)
			evt.consume()
			return

		if evt.keyCode == KeyEvent.VK_TAB or evt.keyCode == KeyEvent.VK_SPACE:
			return

		if evt.keyCode == KeyEvent.VK_BACK_SPACE or evt.keyCode == KeyEvent.VK_DELETE:
			self.parent.dispose()
			self.parent.view.processKeyEvent(evt)
			return

		if not (evt.isControlDown() or evt.isAltDown() or evt.isMetaDown()):
			if(not evt.isActionKey()):
				return

		self.parent.dispose()
		self.parent.view.processKeyEvent(evt);

	def keyTyped(self, evt):
		evt = KeyEventWorkaround.processKeyEvent(evt)
		if not evt:
			return

		ch = evt.keyChar
		if (ch == '\b'):
			return

		self.charKeyTyped(ch)
		#evt.consume()

	def charKeyTyped(self, ch):
		self.parent.matchCharacter(ch)
		#		EditPane editPane = view.getEditPane();
		#		int caret = editPane.getTextArea()
		#			.getCaretPosition();
		#		complete = parser.complete(editPane,caret);
		#		updateListModel();
		#	}
		#	else
		#		dispose();
		#self.parent.textArea.


class CompletionModel(ListModel):
	def __init__(self, tokens):
		self.tokens = tokens

	def getSize(self):
		return len(self.tokens)

	def getElementAt(self, index):
		return self.tokens[index]

	def addListDataListener(self, l):
		pass

	def removeListDataListener(self, l):
		pass

