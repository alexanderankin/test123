"""
 utils.py Contains some utility classes and functions
 author: Carlos Quiroz

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

from javax.swing.filechooser import FileFilter
from org.gjt.sp.jedit import EditPlugin, jEdit

class FileDialogFilter(FileFilter):
	"""
		Simple implementation of FileFilter
		Author Carlos Quiroz
	"""
	def __init__(self, extensions= None, desc = None):
		FileFilter.__init__(self)
		self.extensions = extensions
		self.desc = desc

	def accept(self, file):
		import os.path
		fullpath = file.getPath()
		return os.path.isdir(fullpath) or os.path.splitext(fullpath)[1][1:] in self.extensions

	def getDescription(self):
		return self.desc

def getIconFromPlugin(plugin, image):
	"""
		Loads an image from a plugin jar

		Author Carlos Quiroz

	"""
	from javax.swing import ImageIcon
	ePlugin = jEdit.getPlugin(plugin);
	if ePlugin:
		loader = ePlugin.getPluginJAR().getClassLoader()
		try:
			URL = loader.getResource(image)
			if URL:
				return ImageIcon(URL)
		except:
			return None
	return None

def centerDialog(dialog):
	"""
		Centers a dialog on screen
	"""
	from java.awt import Toolkit
	ssize = Toolkit.getDefaultToolkit().getScreenSize()
	size = dialog.size
	dialog.setLocation((ssize.width-size.width)/2, \
		(ssize.height-size.height)/2)

class BufferList(object):
	def __init__(self, buffer):
		self.buffer = buffer

	def __line(self, index):
		return self.buffer.getLineText(index)

	def __replace(self, index, content):
		self.__remove(index)
		ofs = self.offsets(index)
		self.buffer.insert(ofs[0], content)

	def __remove(self, index):
		ofs = self.offsets(index)
		self.buffer.remove(ofs[0], ofs[1]-ofs[0])

	def offsets(self, index):
		return (self.buffer.getLineStartOffset(index), self.buffer.getLineEndOffset(index))

	def __len__(self):
		return self.buffer.lineCount

	def __getitem__(self, index):
		import java
		import types
		indexes = range(self.buffer.lineCount)[index]
		try:
			if not isinstance(indexes, types.ListType):
				return self.__line(indexes)
			else:
				return map(self.__line, indexes)
		except java.lang.ArrayIndexOutOfBoundsException:
			raise IndexError, "index out of range; %s" % index

	def __delitem__(self, index):
		import java
		import types
		indexes = range(self.buffer.lineCount)[index]
		try:
			if not isinstance(indexes, types.ListType):
				return self.__remove(indexes)
			else:
				return map(self.__remove, indexes)
		except java.lang.ArrayIndexOutOfBoundsException:
			raise IndexError, "index out of range; %s" % index

	def __setitem__(self, index, value):
		import types
		if isinstance(index, types.SliceType):
			size = self.buffer.lineCount
			if index.step != 1:
				raise ValueError, "Step size must be 1 for setting list slice"
			map(self.__replace, value)
		else:
			self.__replace(index, value)

	def __repr__(self):
		return str(self.buffer)

	def clean(self):
		self.buffer.remove(0, self.buffer.length)

def xmlroot(file):
	""" writes the base xml declaration """
	file.write('<?xml version="1.0" encoding="UTF-8"?>')

# :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:
