/*
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package projectviewer.action;

//{{{ Imports
import java.util.HashSet;
import java.util.Iterator;
import java.util.Enumeration;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.search.SearchDialog;
import org.gjt.sp.jedit.search.DirectoryListSet;
import org.gjt.sp.jedit.search.SearchAndReplace;

import projectviewer.vpt.VPTNode;
//}}}

/**
 *	Opens the search dialog for the selected directory/project.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class SearchAction extends Action {

	//{{{ getText() method
	/** Returns the text to be shown on the button and/or menu item. */
	public String getText() {
		return jEdit.getProperty("projectviewer.action.hypersearch");
	} //}}}

	//{{{ actionPerformed(ActionEvent) method
	/** Creates a new project. */
	public void actionPerformed(ActionEvent e) {
		VPTNode node = viewer.getSelectedNode();
		SearchAndReplace.setSearchFileSet(new NodeFileSet(node));
		SearchDialog.showSearchDialog(viewer.getView(), null, SearchDialog.DIRECTORY);
	} //}}}

	//{{{ prepareForNode(VPTNode) method
	/** Enable action only for the root node. */
	public void prepareForNode(VPTNode node) {
		if (node != null && (node.isDirectory() || node.isProject())) {
			cmItem.setVisible(true);
			if (node.isDirectory()) {
				((JMenuItem)cmItem).setText(
					jEdit.getProperty("projectviewer.action.hypersearch_dir"));
			} else {
				((JMenuItem)cmItem).setText(
					jEdit.getProperty("projectviewer.action.hypersearch_project"));
			}
		} else {
			cmItem.setVisible(false);
		}
	} //}}}

	//{{{ NodeFileSet class
	/**
	 *	Implements a SearchFileSet representing files that are children of a given
	 *	node and its children.
	 *
	 *	@author
	 */
	private static class NodeFileSet extends DirectoryListSet {

		//{{{ Private Members
		private VPTNode node;
		//}}}

		//{{{ Constructor
		public NodeFileSet(VPTNode node) {
			super(null, "*", true);
			this.node = node;
		}
		//}}}

		//{{{ getDirectory() method
		/** Returns the path to the node. */
		public String getDirectory() {
			return node.getNodePath();
		} //}}}

		//{{{ _getFiles(Component) method
		/** Returns an array with the files to be searched. */
		protected String[] _getFiles(Component comp) {
			HashSet fileset = new HashSet();
			addFiles(node, fileset);
			return (String[]) fileset.toArray(new String[fileset.size()]);
		}
		//}}}

		//{{{ addFiles(VPTNode) method
		/**
		 *	Adds all the files below the given node to the list of search files,
		 *	recursively.
		 */
		private void addFiles(VPTNode node, HashSet fileset) {
			Enumeration e = node.children();
			if (e != null)
			while(e.hasMoreElements()) {
				VPTNode n = (VPTNode) e.nextElement();
				if (n.isFile()) {
					fileset.add(n.getNodePath());
				} else if (n.getAllowsChildren() && isRecursive()) {
					addFiles(n, fileset);
				}
			}
		} //}}}

	} //}}}

}

