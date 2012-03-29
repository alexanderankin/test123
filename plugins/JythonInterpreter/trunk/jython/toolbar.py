"""
 toolbar.py Contains utility classes for toolbar construction
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

from javax.swing import JToolBar, JButton
from java.awt import Insets
from org.gjt.sp.jedit import jEdit, GUIUtilities

class ToolbarHandler:
	""" Utility class to simplify the toolbar creation process """
	def __init__(self, actions = None):
		self.actions = actions

	def createToolbar(self):
		toolBar = JToolBar()
		margin = Insets(1,1,1,1)
		[self.createButton(toolBar, i, t ,f) for (i,t,f) in self.actions]
		return toolBar

	def createButton(self, toolBar, i, t, f):
		if i == "separator":
			toolBar.addSeparator()
		else:
			b = JButton(icon = GUIUtilities.loadIcon(i), \
			toolTipText = jEdit.getProperty(t), actionPerformed = f)
			toolBar.add(b)

# :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:
