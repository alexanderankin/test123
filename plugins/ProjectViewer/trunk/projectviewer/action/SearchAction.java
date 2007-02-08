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
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;

import java.util.HashSet;
import java.util.Enumeration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.search.SearchDialog;
import org.gjt.sp.jedit.search.DirectoryListSet;
import org.gjt.sp.jedit.search.SearchAndReplace;

import org.gjt.sp.util.Log;

import projectviewer.ProjectManager;
import projectviewer.ProjectViewer;
import projectviewer.config.ProjectViewerConfig;

import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTNode;
//}}}

/**
 *	Opens the search dialog for the selected directory/project.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class SearchAction extends Action {

	//{{{ +SearchAction() : <init>
	public SearchAction() {
		super("projectviewer_wrapper_search");
	} //}}}

	//{{{ +getText() : String
	/** Returns the text to be shown on the button and/or menu item. */
	public String getText() {
		return jEdit.getProperty("projectviewer.action.hypersearch");
	} //}}}

	//{{{ +actionPerformed(ActionEvent) : void
	/** Creates a new project. */
	public void actionPerformed(ActionEvent e) {
		VPTNode node = null;
		if (viewer != null) {
			node = viewer.getSelectedNode();
		}
		if (node == null) {
			node = ProjectViewer.getActiveProject(jEdit.getActiveView());
		}
		if (node != null) {
			if (node.isProject()) {
				ProjectManager mgr = ProjectManager.getInstance();
				if (!mgr.isLoaded(node.getName())) {
					node = mgr.getProject(node.getName());
				}
			} else if (node.isLeaf()) {
				node = (VPTNode) node.getParent();
			}

			SearchAndReplace.setSearchFileSet(new NodeFileSet(node));
			SearchDialog.showSearchDialog(jEdit.getActiveView(), null, SearchDialog.DIRECTORY);
		} else {
			JOptionPane.showMessageDialog(
					(viewer != null) ? (Component) viewer : (Component) jEdit.getActiveView(),
					jEdit.getProperty("projectviewer.action.search.error"),
					jEdit.getProperty("projectviewer.error"),
					JOptionPane.ERROR_MESSAGE);
		}
	} //}}}

	//{{{ +prepareForNode(VPTNode) : void
	/** Enable action only for the root node. */
	public void prepareForNode(VPTNode node) {
		cmItem.setVisible(true);
		if (node.isDirectory()) {
			((JMenuItem)cmItem).setText(
				jEdit.getProperty("projectviewer.action.hypersearch_dir"));
		} else if (node.isProject()) {
			((JMenuItem)cmItem).setText(
				jEdit.getProperty("projectviewer.action.hypersearch_project"));
		} else {
			((JMenuItem)cmItem).setText(
				jEdit.getProperty("projectviewer.action.hypersearch_parent"));
		}
	} //}}}

	//{{{ +class _NodeFileSet_
	/**
	 *	Implements a SearchFileSet representing files that are children of a given
	 *	node and its children.
	 *
	 *	@since	PV 2.1.0 (was private before)
	 */
	public static class NodeFileSet extends DirectoryListSet {

		//{{{ Private Members
		private boolean skipBinary;
		private Pattern pFilter;
		private VPTNode node;
		//}}}

		//{{{ +NodeFileSet(VPTNode) : <init>
		public NodeFileSet(VPTNode node) {
			super(null, "*", true);
			this.node = node;
			this.skipBinary = jEdit.getBooleanProperty("search.skipBinary.toggle", false)
							&& ProjectViewerConfig.getInstance().hasBinaryFileCheck();
		}
		//}}}

		//{{{ +getDirectory() : String
		/** Returns the path to the node. */
		public String getDirectory() {
			return node.getNodePath();
		} //}}}

		//{{{ #_getFiles(Component) : String[]
		/** Returns an array with the files to be searched. */
		protected String[] _getFiles(Component comp) {
			String filter = getFileFilter();
			if (filter != null && filter.length() > 0 && !filter.equals("*")) {
				pFilter = Pattern.compile(MiscUtilities.globToRE(filter));
			}

			HashSet fileset = new HashSet();
			addFiles(node, fileset);
			return (String[]) fileset.toArray(new String[fileset.size()]);
		}
		//}}}

		//{{{ -addFiles(VPTNode, HashSet) : void
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
					if (pFilter != null &&
						!pFilter.matcher(n.getNodePath()).matches())
					{
						// filtered out.
						continue;
					}

					if (!((VPTFile)n).getFile().exists()) {
						continue;
					}

					if (skipBinary) {
						InputStream is = null;
						Reader r = null;
						try {
							is = new FileInputStream(n.getNodePath());
							r = MiscUtilities.autodetect(is, null);
							if (MiscUtilities.isBinary(r)) {
								continue;
							}
						} catch (IOException ioe) {
							Log.log(Log.ERROR, this, ioe);
							continue;
						} finally {
							if (r != null)  try { r.close(); } catch (Exception ex) { }
							if (is != null)  try { is.close(); } catch (Exception ex) { }
						}
					}
					fileset.add(n.getNodePath());
				} else if (n.getAllowsChildren() && isRecursive()) {
					addFiles(n, fileset);
				}
			}
		} //}}}

	} //}}}

}

