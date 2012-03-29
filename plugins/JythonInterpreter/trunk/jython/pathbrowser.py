"""
 pathbrowser.py - The path browser. It consists of a window containig
 the path entries and allowing for opening those files

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

import os
import sys
import imp

from javax.swing import JTree, JPanel, JScrollPane
from java.awt import BorderLayout
from javax.swing.tree import DefaultTreeModel, DefaultMutableTreeNode, DefaultTreeCellRenderer
from org.gjt.sp.jedit import jEdit, GUIUtilities


class BrowserCellRenderer(DefaultTreeCellRenderer):
	def __init__(self):
		DefaultTreeCellRenderer.__init__(self)
		self.__closedIcon = GUIUtilities.loadIcon("New.png")
		self.__leafIcon = GUIUtilities.loadIcon("New.png")
		self.__openIcon = GUIUtilities.loadIcon("NewDir.png")
		self.__minusIcon = GUIUtilities.loadIcon("NewDir.png")
		self.__homeIcon = GUIUtilities.loadIcon("Home.png")

	def getTreeCellRendererComponent(self, tree, value, sel, expanded, leaf, row, hasFocus):
		if isinstance(value, PathBrowserTreeItem):
			stringValue = tree.convertValueToText(value, sel, \
						expanded, leaf, row, hasFocus);
			self.text = stringValue
			self.icon = self.__homeIcon
			return self
		else:
			return DefaultTreeCellRenderer.getTreeCellRendererComponent(self, tree, value, sel,\
				expanded, leaf, row, hasFocus)

	def getClosedIcon(self):
		return self.__minusIcon

	def getDefaultClosedIcon(self):
		return self.__minusIcon

	def getDefaultLeafIcon(self):
		return self.__closedIcon

	def getDefaultOpenIcon(self):
		return self.__closedIcon

	def getLeafIcon(self):
		return self.__leafIcon

	def getOpenIcon(self):
		return self.__openIcon


class PathBrowser:
	def __init__(self, view):
		model = DefaultTreeModel(PathBrowserTreeItem())
		self.tree = JTree(model, mousePressed = self.__mousePressed)
		self.tree.cellRenderer = BrowserCellRenderer()
		self.panel = JScrollPane(self.tree)

	def __mousePressed(self, event):
		selPath = self.tree.getPathForLocation(event.x, event.y);
		if selPath and isinstance(selPath.lastPathComponent, ModuleBrowserTreeItem):
			if event.clickCount == 2:
				jEdit.openFile(jEdit.getLastView(), selPath.lastPathComponent.file)
			if event.clickCount == 1:
				buffers = jEdit.getBuffers()
				for aBuffer in buffers:
					if aBuffer.file or (aBuffer.file.path == selPath.lastPathComponent.file):
						jEdit.getLastView().buffer = aBuffer


class PathBrowserTreeItem(DefaultMutableTreeNode):
	def __init__(self):
		DefaultMutableTreeNode.__init__(self)
		for dir in sys.path:
			self.add(DirBrowserTreeItem(dir))

	def toString(self):
		return "sys.path"

	__str__ = toString


class DirBrowserTreeItem(DefaultMutableTreeNode):
	def __init__(self, dir, packages=[]):
		DefaultMutableTreeNode.__init__(self)
		self.dir = dir
		self.packages = packages
		self.isJar = (self.dir.find(".jar") >= 0)
		self.isZip = (self.dir.find(".zip") >= 0)
		for subitem in self.getSubList():
			self.add(subitem)

	def toString(self):
		if not self.packages:
			return self.dir
		else:
			return str(self.packages[-1]) + ": package"

	__str__ = toString

	def getSubList(self):
		try:
			if self.isJar or self.isZip:
				from java.util.zip import ZipFile
				last = self.dir.find(self.isJar and ".jar" or ".zip") + 4
				self.zipfile = self.dir[:last]
				zip = ZipFile(self.zipfile)
				head = self.dir[last + 1:]
				names=[]
				for i in zip.entries():
					if str(i).startswith(head):
						names.append(i)
			else:
				names = os.listdir(self.dir)
		except os.error:
			return []
		packages = []
		for name in names:
			if self.isJar or self.isZip:
				if self.iszipdir(self.dir, name):
					file = os.path.join(self.dir, str(name))
					nn = os.path.normcase(str(name))
					packages.append((nn, name, file))
			else:
				file = os.path.join(self.dir, str(name))
				if self.ispackagedir(file):
					file = os.path.join(self.dir, str(name))
					nn = os.path.normcase(name)
					packages.append((nn, name, file))
		packages.sort()
		sublist = []
		for nn, name, file in packages:
			if self.isJar or self.isZip:
				#print self.zipfile + "/" + str(name)
				item = DirBrowserTreeItem(file + "/" + str(name), self.packages + [name])
			else:
				item = DirBrowserTreeItem(file, self.packages + [name])
			sublist.append(item)
		for nn, name in self.listmodules(names):
			if self.isJar or self.isZip:
				item = ModuleBrowserTreeItem("archive:" + self.dir[:self.dir.find(".jar")+4]+ "!/" + nn)
			else:
				item = ModuleBrowserTreeItem(os.path.join(self.dir, name))
			sublist.append(item)
		return sublist

	def ispackagedir(self, file):
		if not os.path.isdir(file):
			return 0
		init = os.path.join(file, "__init__.py")
		return os.path.exists(init)

	def iszipdir(self, file, entry):
		return entry.isDirectory()
#			return 0
#		init = os.path.join(file, "__init__.py")
#		return os.path.exists(init)

	def listmodules(self, allnames):
		modules = {}
		suffixes = imp.get_suffixes()
		sorted = []
		if self.isZip or self.isJar:
			for suff, mode, flag in suffixes:
				i = -len(suff)
				for name in allnames[:]:
					normed_name = str(name)
					if normed_name[i:] == suff:
						mod_name = normed_name[:i]
						if not modules.has_key(mod_name):
							modules[mod_name] = None
							sorted.append((normed_name, name))
							allnames.remove(name)
			sorted.sort()
		else:
			for suff, mode, flag in suffixes:
				i = -len(suff)
				for name in allnames[:]:
					normed_name = os.path.normcase(name)
					if normed_name[i:] == suff:
						mod_name = name[:i]
						if not modules.has_key(mod_name):
							modules[mod_name] = None
							sorted.append((normed_name, name))
							allnames.remove(name)
			sorted.sort()
		return sorted

class ModuleBrowserTreeItem(DefaultMutableTreeNode):
	def __init__(self, file):
		DefaultMutableTreeNode.__init__(self)
		self.file = file

	def toString(self):
		base = os.path.basename(self.file)
		if base.find("!")>0:
			base = base[base.find("!")+1:]
		return base

	__str__ = toString

	def listclasses(self):
		dir, file = os.path.split(self.file)
		name, ext = os.path.splitext(file)
		if os.path.normcase(ext) != ".py":
				return []
		try:
				dict = pyclbr.readmodule_ex(name, [dir] + sys.path)
		except ImportError, msg:
				return []
		items = []
		self.classes = {}
		for key, cl in dict.items():
				if cl.module == name:
						s = key
						if cl.super:
								supers = []
								for sup in cl.super:
										if type(sup) is type(''):
												sname = sup
										else:
												sname = sup.name
												if sup.module != cl.module:
														sname = "%s.%s" % (sup.module, sname)
										supers.append(sname)
								s = s + "(%s)" % string.join(supers, ", ")
						items.append((cl.lineno, s))
						self.classes[s] = cl
		items.sort()
		list = []
		for item, s in items:
				list.append(s)
		return list

def _createBrowser(view, docker):
	docker.removeAll()
	docker.add(PathBrowser(view).panel)

def main():
	from org.gjt.sp.jedit import jEdit
	from javax.swing import JFrame
	p = PathBrowser(jEdit.getFirstView())
	f = JFrame()
	f.contentPane.layout = BorderLayout()
	f.contentPane.add(p.panel)
	f.pack()
	f.visible = 1

if __name__ in ("__main__","main"):
	main()

# :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:
