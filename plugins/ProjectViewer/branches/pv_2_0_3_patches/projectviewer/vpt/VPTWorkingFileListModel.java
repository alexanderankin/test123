/*
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more detaProjectTreeSelectionListenerils.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package projectviewer.vpt;

//{{{ Imports
import java.util.WeakHashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import javax.swing.tree.TreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.MiscUtilities;
//}}}

/**
 *	A tree model that shows all files currently opened in jEdit in a flat list.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class VPTWorkingFileListModel extends DefaultTreeModel {

	//{{{ Private members
	private WeakHashMap fileLists;

	private Object lastParent;
	private ArrayList lastList;
	//}}}

	//{{{ Constructor
	/**
	 *	Create a new <code>VPTFileListModel</code>.
	 *
	 *	@param rootNode	The root node of the tree.
	 */
	public VPTWorkingFileListModel(VPTNode rootNode) {
		super(rootNode, true);
		fileLists = new WeakHashMap();
		checkOpenFiles();
	} //}}}

	//{{{ getChildCount(Object) method
	/**
	 *	Returns the child at the given index of the given parent. If the parent
	 *	is a project, returns the number of files in the project, not just the
	 *	files that are direct children of the project.
	 */
	public int getChildCount(Object parent) {
		if (parent == lastParent) {
			return lastList.size();
		}

		VPTNode node = (VPTNode) parent;
		if (node.isRoot()) {
			return node.getChildCount();
		} else if (node.isProject()) {
			lastParent = parent;
			ArrayList lst = (ArrayList) fileLists.get(node);
			lastList = lst;
			return (lst != null) ? lst.size() : 0;
		}
		Log.log(Log.WARNING, this, "Reached the supposedly unreachable! parent = " + parent);
		return 0; // shouldn't reach here
	} //}}}

	//{{{ getChild(Object, int) method
	/**
	 *	Returns the child at the given index of the given parent. If the parent
	 *	is a project, treats the children in such a way to allow all files in the
	 *	project to be displayed in a flat list.
	 */
	public Object getChild(Object parent, int index) {
		if (parent == lastParent) {
			return lastList.get(index);
		}

		VPTNode node = (VPTNode) parent;
		if (node.isRoot()) {
			return node.getChildAt(index);
		} else if (node.isProject()) {

			lastParent = parent;
			VPTProject p = (VPTProject) node;
			ArrayList lst = (ArrayList) fileLists.get(p);
			if (lst == null) {
				lst = new ArrayList();
				fileLists.put(node, lst);
			}
			lastList = lst;
			if (index >= lst.size()) return null;
			return lst.get(index);
		}
		Log.log(Log.WARNING, this, "Reached the supposedly unreachable! parent = " + parent);
		return null; // shouldn't reach here
	} //}}}

	//{{{ nodeStructureChanged(TreeNode) method
	/**
	 *	Called when some node in the tree is changed. If not the root, then
	 *	tracks down which project was changed and updates the child list.
	 */
	public void nodeStructureChanged(TreeNode node) {
		VPTNode n = (VPTNode) node;
		if (!n.isRoot()) {
			n = VPTNode.findProjectFor(n);
			checkOpenFiles((VPTProject) n);
		}
		super.nodeStructureChanged(node);
	} //}}}

	//{{{ checkOpenFiles() method
	/**
	 *	Checks what files currently opened in jEdit belong to some project being
	 *	showns.
	 */
	private void checkOpenFiles() {
		Buffer[] bufs = jEdit.getBuffers();
		VPTProject[] projs = getProjects();
		VPTNode.VPTNodeComparator comp = null;

		for (int i = 0; i < bufs.length; i++) {
			String path = bufs[i].getPath();

			for (int j = 0; j < projs.length; j++) {
				VPTNode n = projs[j].getChildNode(path);
				if (n != null) {
					ArrayList lst = (ArrayList) fileLists.get(projs[j]);
					if (lst == null) {
						lst = new ArrayList();
						fileLists.put(projs[j], lst);
					}
					lst.add(n);
					if (comp == null) {
						comp = new VPTNode.VPTNodeComparator();
					}
					MiscUtilities.quicksort(lst, comp);
				}
			}
		}
	} //}}}

	//{{{ checkOpenFiles(VPTProject) method
	/**
	 *	Checks what files currently opened in jEdit belong to the given project.
	 */
	private void checkOpenFiles(VPTProject p) {
		Buffer[] bufs = jEdit.getBuffers();
		VPTNode root = (VPTNode) this.root;

		ArrayList lst = new ArrayList();
		fileLists.put(p, lst);

		for (int i = 0; i < bufs.length; i++) {
			VPTNode n = p.getChildNode(bufs[i].getPath());
			if (n != null) {
				lst.add(n);
			}
		}

		MiscUtilities.quicksort(lst, new VPTNode.VPTNodeComparator());
	} //}}}

	//{{{ addOpenFile(String) method
	/**
	 *	Adds an open file to the list of open files of the projects to which
	 *	it belongs.
	 */
	public void addOpenFile(String path) {
		VPTProject[] projs = getProjects();
		VPTNode.VPTNodeComparator comp = null;

		for (int j = 0; j < projs.length; j++) {
			VPTNode n = projs[j].getChildNode(path);
			if (n != null) {
				ArrayList lst = (ArrayList) fileLists.get(projs[j]);
				if (lst == null) {
					lst = new ArrayList();
					fileLists.put(projs[j], lst);
				}
				if (!lst.contains(n)) {
					lst.add(n);
					if (comp == null) {
						comp = new VPTNode.VPTNodeComparator();
					}
					MiscUtilities.quicksort(lst, comp);
					super.nodeStructureChanged(projs[j]);
				}
			}
		}
	} //}}}

	//{{{ removeOpenFile(String) method
	/**
	 *	Removes an open file from the list of open files of the projects to
	 *	which it belongs.
	 */
	public void removeOpenFile(String path) {
		VPTProject[] projs = getProjects();

		for (int j = 0; j < projs.length; j++) {
			VPTNode n = projs[j].getChildNode(path);
			if (n != null) {
				ArrayList lst = (ArrayList) fileLists.get(projs[j]);
				if (lst != null) {
					lst.remove(n);
					super.nodeStructureChanged(projs[j]);
				}
			}
		}
	} //}}}

	//{{{ getProjects() method
	/** Returns the projects currently being shown. */
	private VPTProject[] getProjects() {
		VPTNode root = (VPTNode) this.root;
		VPTProject[] projs;
		if (root.isProject()) {
			projs = new VPTProject[1];
			projs[0] = (VPTProject) root;
		} else {
			int i = 0;
			projs = new VPTProject[root.getChildCount()];
			for (Enumeration e = root.children(); e.hasMoreElements(); i++) {
				projs[i] = (VPTProject) e.nextElement();
			}
		}

		return projs;
	} //}}}

	//{{{ getPathToRoot(TreeNode) method
	public TreeNode[] getPathToRoot(TreeNode aNode) {
		VPTNode n = (VPTNode) aNode;
		if (n.isRoot()) {
			return new TreeNode[] { n };
		} else if (n.isProject()) {
			if (n == getRoot()) {
				return new TreeNode[] { n };
			} else {
				TreeNode[] ns = new TreeNode[2];
				ns[0] = (TreeNode) getRoot();
				ns[1] = n;
				return ns;
			}
		} else {
			VPTProject p = VPTNode.findProjectFor(n);
			if (p == getRoot()) {
				TreeNode[] ns = new TreeNode[2];
				ns[0] = p;
				ns[1] = n;
				return ns;
			} else {
				TreeNode[] ns = new TreeNode[3];
				ns[0] = (TreeNode) getRoot();
				ns[1] = p;
				ns[2] = n;
				return ns;
			}
		}
	} //}}}

}

