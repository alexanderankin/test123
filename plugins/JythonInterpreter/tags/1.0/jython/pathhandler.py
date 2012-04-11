"""
 pathhandler.py - The path handler dialog
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

from javax.swing import JDialog, JPanel, JButton, JList, BorderFactory,\
	JScrollPane, DefaultListModel, ListSelectionModel, JFileChooser, JLabel, \
	JTextField, JCheckBox
from java.awt import BorderLayout, Insets, FlowLayout, GridBagLayout, GridBagConstraints
from org.gjt.sp.jedit import jEdit, GUIUtilities
from utils import FileDialogFilter, centerDialog, xmlroot
from init import _getUsersJythonDir

class PathDialog(JDialog):
	""" The PathDialog class is a small popup dialog which has an entry field
		and a button which opens a FileChooser dialog
	"""
	def __init__(self, parent, title):
		JDialog.__init__(self, parent, title, 1)
		self.contentPane.layout = BorderLayout()
		upperPanel = JPanel(FlowLayout())
		upperPanel.add(JLabel("New path:"))
		self.pathField = JTextField(columns=20)
		upperPanel.add(self.pathField)
		upperPanel.add(JButton(actionPerformed = self.openFileChooser, icon = \
			GUIUtilities.loadIcon("Open.png")))
		self.contentPane.add(upperPanel, BorderLayout.CENTER)
		ok = JButton("Ok", actionPerformed = self.ok, defaultCapable = 1)
		self.contentPane.add(upperPanel, BorderLayout.CENTER)
		lowerPanel = JPanel(FlowLayout(FlowLayout.RIGHT))
		lowerPanel.add(ok)
		self.rootPane.defaultButton = ok
		lowerPanel.add(JButton("Cancel", actionPerformed = self.cancel))
		self.contentPane.add(lowerPanel, BorderLayout.SOUTH)
		self.path = None
		self.tempPath = None

	def getFile(self):
		return self.path

	def ok(self, event):
		if len(self.pathField.text):
			self.path = self.pathField.text
		else:
			self.path = None
		self.visible = 0

	def cancel(self, event):
		self.visible = 0

	def openFileChooser(self, event):
		import sys
		theFilter = FileDialogFilter(["jar", "zip"], jEdit.getProperty("jython.pathhandler.compressedfilter"))
		chooser = JFileChooser(len(sys.path) and sys.path[0] or ".", \
			fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES, dialogTitle =jEdit.getProperty("jython.pathhandler.addtitle"), \
			fileFilter = theFilter)
		if chooser.showOpenDialog(self) == JFileChooser.APPROVE_OPTION:
			self.pathField.text = chooser.selectedFile.path

class PyPathHandler(JDialog):
	""" Dialog box which allows to modify the python path """
	def __init__(self, view, sys):
		JDialog.__init__(self, view, jEdit.getProperty("jython.pathhandler.title"))
		self.sys = sys
		content = self.contentPane
		content.layout = BorderLayout()
		upperPanel = JPanel(BorderLayout())
		leftPanel = JPanel(BorderLayout(), border = \
			BorderFactory.createTitledBorder(jEdit.getProperty("jython.pathhandler.pathborder")))
		self.model = DefaultListModel()
		for s in sys.path:
			self.model.addElement(s)
		self.pathlist = JList(self.model, selectionMode=ListSelectionModel.SINGLE_SELECTION)
		leftPanel.add(JScrollPane(self.pathlist))
		rightPanel = JPanel(GridBagLayout())
		constraints = GridBagConstraints()
		constraints.insets = Insets(5,5,5,5)
		constraints.gridy = GridBagConstraints.RELATIVE
		constraints.gridx = 0
		buttons = [("Plus.png", "New...", self.new), \
			("ButtonProperties.png", "Edit...", self.edit),
			("Minus.png", "Remove", self.remove), \
			("ArrowU.png", "Move Up", self.up), \
			("ArrowD.png", "Move down", self.down)]
		for (i, t, a) in buttons:
			rightPanel.add(JButton(icon = GUIUtilities.loadIcon(i), \
				toolTipText=t, actionPerformed=a), constraints)
		upperPanel.add(leftPanel, BorderLayout.CENTER)
		upperPanel.add(rightPanel, BorderLayout.EAST)
		content.add(upperPanel, BorderLayout.CENTER)
		lowerPanel = JPanel(FlowLayout(FlowLayout.RIGHT))
		self.saveAsk = JCheckBox(jEdit.getProperty("options.jython.saveJythonPathTitle"), \
			selected = jEdit.getBooleanProperty("options.jython.saveJythonPath"), actionPerformed = self.saveAsk)
		ok = JButton("Ok", actionPerformed=self.__ok)
		lowerPanel.add(self.saveAsk)
		lowerPanel.add(ok)
		self.rootPane.defaultButton = ok
		lowerPanel.add(JButton("Cancel", actionPerformed=self.__cancel))
		content.add(lowerPanel, BorderLayout.SOUTH)

	def saveAsk(self, event):
		jEdit.setBooleanProperty("options.jython.saveJythonPath", self.saveAsk.selected)

	def new(self, event):
		newPath = PathDialog(self, "New path entry")
		newPath.pack()
		centerDialog(newPath)
		newPath.visible = 1
		if newPath.getFile():
			index = self.pathlist.selectedIndex
			self.model.insertElementAt(newPath.getFile(), max(0, index))

	def remove(self, event):
		self.model.remove(self.pathlist.selectedIndex)

	def export(self):
		import codecs
		import os.path
		filename = os.path.join(_getUsersJythonDir(), "jython.xml")
		try:
			import sys
			f = codecs.open(filename, "w", encoding="UTF-8")
			xmlroot(f)
			f.write("\n<JythonInterpreter>\n")
			i=0
			for path in sys.path:
				i += 1
				f.write(u'\t<pathentry path="%s" order="%d"/>\n' % (path, i))
			f.write("</JythonInterpreter>\n")
			f.close()
		except IOError, msg:
			import sys
			GUIUtilities.error(self, "jython.pathandler-error-saving", [msg, sys.exc_in()[2]])
			return

	def edit(self, event):
		if self.pathlist.selectedIndex >= 0:
			newPath = PathDialog(self, "Edit path entry")
			newPath.pathField.text = self.model.elementAt(self.pathlist.selectedIndex)
			centerDialog(newPath)
			newPath.pack()
			newPath.visible = 1
			if newPath.getFile():
				index = self.pathlist.selectedIndex
				self.model.setElementAt(newPath.getFile(), self.pathlist.selectedIndex)

	def up(self, event):
		index = self.pathlist.selectedIndex
		if (index > 0):
			selected = self.model.get(index)
			self.model.remove(index)
			self.model.insertElementAt(selected, index-1)
			self.pathlist.selectedIndex = index-1

	def down(self, event):
		index = self.pathlist.selectedIndex
		if (index < self.model.size()-1):
			selected = self.model.get(index)
			self.model.remove(index)
			self.model.insertElementAt(selected, index+1)
			self.pathlist.selectedIndex = index+1

	def __cancel(self, event):
		self.visible = 0

	def __ok(self, event):
		self.sys.path[0:len(self.sys.path)]=[]
		[self.sys.path.append(self.model.get(i)) for i in xrange(0, self.model.size())]
		if jEdit.getBooleanProperty("options.jython.saveJythonPath"):
			self.export()
		self.visible = 0

if __name__ in ("__main__","main"):
	from javax.swing import JFrame
	import sys
	v = PyPathHandler(jEdit.getLastView(), sys)
	v.pack()
	centerDialog(v)
	v.visible = 1

# :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:
