"""
 exportsession.py - Handles the session exporting
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
from javax.swing import JCheckBox, Box, JPanel, JButton
from javax.swing.border import EmptyBorder
from java.awt import BorderLayout, FlowLayout
from org.gjt.sp.jedit.gui import EnhancedDialog
from org.gjt.sp.jedit import jEdit
from utils import centerDialog

def export(view, doc):
	buffer = jEdit.newFile(view)
	root = doc.defaultRootElement
	for i in xrange(0, root.elementCount):
		element = root.getElement(i)
		limits = (element.startOffset, element.endOffset - element.startOffset)
		buffer.insertString(buffer.length, doc.getText(limits[0], limits[1]), None)

class ShowDialog(EnhancedDialog):
	def __init__(self, view, doc):
		EnhancedDialog.__init__(self, view, jEdit.getProperty("jython.export.title"), 1)
		self.view = view
		self.doc = doc
		panel = Box.createVerticalBox()
		panel.border = EmptyBorder(10, 10, 10, 10)
		self.header = JCheckBox(jEdit.getProperty("jython.export.header"), 0)
		panel.add(self.header)
		self.prompts = JCheckBox(jEdit.getProperty("jython.export.propmts"), 0)
		panel.add(self.prompts)
		self.errors = JCheckBox(jEdit.getProperty("jython.export.errors"), 0)
		panel.add(self.errors)
		self.contentPane.layout = BorderLayout()
		self.contentPane.add(panel, BorderLayout.CENTER)
		lowerPanel = JPanel(FlowLayout.LEFT)
		lowerPanel.add(JButton(jEdit.getProperty("common.ok"), actionPerformed = \
			self._ok))
		lowerPanel.add(JButton(jEdit.getProperty("common.cancel"), actionPerformed = \
			self._cancel))
		self.contentPane.add(lowerPanel, BorderLayout.SOUTH)

	def ok(self):
		self.__export()
		self.dispose()

	def cancel(self):
		self.dispose()

	def _ok(self, event):
		self.ok()

	def _cancel(self, event):
		self.cancel()

	def __export(self):
		if self.doc:
			print dir(self.doc)
			buffer = jEdit.newFile(self.view)
			doc = self.doc.document
			root = self.doc.document.defaultRootElement()
			for i in xrange(0, root.elementCount):
				element = root.getElement(i)
				limits = (element.startOffset, element.endOffset - element.startOffset)
				buffer.insertString(buffer.length, doc.getText(limits[0], limits[1], None))

if __name__ in ("__main__","main"):
	s = ShowDialog(view, None)
	s.pack()
	centerDialog(s)
	s.visible = 1

# :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:
