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
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Enumeration;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JMenuItem;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.search.SearchDialog;
import org.gjt.sp.jedit.search.SearchFileSet;
import org.gjt.sp.jedit.search.SearchAndReplace;

import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTFile;
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
		return jEdit.getProperty("projectviewer.launcher.hypersearch");
	} //}}}
	
	//{{{ getIcon() method
	/** Returns null. Shouldn't be on the toolbar. */
	public Icon getIcon() {
		return null;
	} //}}}
	
	//{{{ actionPerformed(ActionEvent) method
	/** Creates a new project. */
	public void actionPerformed(ActionEvent e) {
		VPTNode node = viewer.getSelectedNode();
		SearchAndReplace.setSearchFileSet(new NodeFileSet(node));
		SearchDialog.showSearchDialog(jEdit.getLastView(), null, SearchDialog.DIRECTORY);
	} //}}}

	//{{{ prepareForNode(VPTNode) method
	/** Enable action only for the root node. */
	public void prepareForNode(VPTNode node) {
		if (node != null && (node.isDirectory() || node.isProject())) {
			cmItem.setVisible(true);
			if (node.isDirectory()) {
				((JMenuItem)cmItem).setText(jEdit.getProperty("projectviewer.launcher.hypersearch_dir"));
			} else {
				((JMenuItem)cmItem).setText(jEdit.getProperty("projectviewer.launcher.hypersearch_project"));
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
	private static class NodeFileSet implements SearchFileSet {
		
		//{{{ Private members
		private ArrayList fileset;
		private Iterator it;
		//}}}
	
		//{{{ Constructors
		
		public NodeFileSet(VPTNode node) {
			fileset = new ArrayList();
			addFiles(node);
			it = fileset.iterator();
		}
		
		//}}}
		
		//{{{ addFiles(VPTNode) method
		/**
		 *	Adds all the files below the given node to the list of search files,
		 *	recursively.
		 */
		private void addFiles(VPTNode node) {
			Enumeration e = node.children();
			if (e != null)
			while(e.hasMoreElements()) {
				VPTNode n = (VPTNode) e.nextElement();
				if (n.isFile()) {
					fileset.add(n); 
				} else if (n.getAllowsChildren()) {
					addFiles(n);	
				}
			}
		} //}}}
	
		//{{{ SearchFileSet implementation
		
		public String getCode() {
			return(null);
		}
	
		public int getFileCount(View view) {
			return(fileset.size());
		}
	
		public String[] getFiles(View view) {
			return (String[]) fileset.toArray(new String[fileset.size()]);	
		}
	
		public String getFirstFile(View view) {
			if (fileset.size() == 0) return null;
			return (String) fileset.get(0);
		}
	
		public String getNextFile(View view, String path) {
			if (fileset.size() == 0) return null;
			return (String) it.next();
		} 
		
		//}}}
	
	} //}}}

}

