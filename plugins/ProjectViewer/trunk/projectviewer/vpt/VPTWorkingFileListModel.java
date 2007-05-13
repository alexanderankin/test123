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
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package projectviewer.vpt;

//{{{ Imports
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.WeakHashMap;

import javax.swing.tree.TreeNode;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;
//}}}

/**
 *	A tree model that shows all files currently opened in jEdit in a flat list.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class VPTWorkingFileListModel extends ProjectTreeModel {

	//{{{ Private members
	private WeakHashMap fileLists;

	private Object lastParent;
	private ArrayList lastList;
	private ArrayList pathBuilder;
	//}}}

	//{{{ +VPTWorkingFileListModel(VPTNode) : <init>
	/**
	 *	Create a new <code>VPTFileListModel</code>.
	 *
	 *	@param rootNode	The root node of the tree.
	 */
	public VPTWorkingFileListModel(VPTNode rootNode) {
		super(rootNode);
		fileLists = new WeakHashMap();
		pathBuilder = new ArrayList();
		checkOpenFiles();
	} //}}}

	//{{{ +getChildCount(Object) : int
	/**
	 *	Returns the child at the given index of the given parent. If the parent
	 *	is a project, returns the number of files in the project, not just the
	 *	files that are direct children of the project.
	 */
	public int getChildCount(Object parent) {
		if (parent == lastParent) {
			return (lastList != null) ? lastList.size() : 0;
		}

		VPTNode node = (VPTNode) parent;
		if (node.isGroup()) {
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

	//{{{ +getChild(Object, int) : Object
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
		if (node.isGroup()) {
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

	//{{{ +nodeStructureChanged(TreeNode) : void
	/**
	 *	Called when some node in the tree is changed. If not the root, then
	 *	tracks down which project was changed and updates the child list.
	 */
	public void nodeStructureChanged(TreeNode node) {
		VPTNode n = (VPTNode) node;
		if (!n.isGroup()) {
			node = VPTNode.findProjectFor(n);
			checkOpenFiles((VPTProject) n);
			lastParent = null;
			lastList = null;
		}
		super.nodeStructureChanged(node);
	} //}}}

	//{{{ -checkOpenFiles() : void
	/**
	 *	Checks what files currently opened in jEdit belong to some project being
	 *	showns.
	 */
	private void checkOpenFiles() {
		Buffer[] bufs = jEdit.getBuffers();
		VPTProject[] projs = getProjects();

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
					Collections.sort(lst);
				}
			}
		}
	} //}}}

	//{{{ -checkOpenFiles(VPTProject) : void
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

		Collections.sort(lst);
	} //}}}

	//{{{ +fileOpened(VPTNode) : void
	/**
	 *	Adds an open file to the list of open files of the projects to which
	 *	it belongs.
	 */
	public void fileOpened(VPTNode child) {
		String path = child.getNodePath();
		VPTProject[] projs = getProjects();

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
					Collections.sort(lst);
					super.nodeStructureChanged(projs[j]);
				}
			}
		}
	} //}}}

	//{{{ +fileClosed(VPTNode) : void
	/**
	 *	Removes an open file from the list of open files of the projects to
	 *	which it belongs.
	 */
	public void fileClosed(VPTNode child) {
		String path = child.getNodePath();
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

	//{{{ -getProjects() : VPTProject[]
	/** Returns the projects currently being shown. */
	private VPTProject[] getProjects() {
		VPTNode root = (VPTNode) getRoot();
		VPTProject[] projs;
		if (root.isProject()) {
			projs = new VPTProject[1];
			projs[0] = (VPTProject) root;
		} else {
			ArrayList lst = new ArrayList();
			findProjects((VPTGroup)root, lst);
			projs = (VPTProject[]) lst.toArray(new VPTProject[lst.size()]);
		}

		return projs;
	} //}}}

	//{{{ -findProjects(VPTGroup, ArrayList) : void
	private void findProjects(VPTGroup grp, ArrayList projs) {
		for (Enumeration e = grp.children(); e.hasMoreElements(); ) {
			VPTNode next = (VPTNode) e.nextElement();
			if (next.isProject()) {
				projs.add(next);
			} else {
				findProjects((VPTGroup)next, projs);
			}
		}
	} //}}}

	//{{{ +getPathToRoot(TreeNode) : TreeNode[]
	public TreeNode[] getPathToRoot(TreeNode aNode) {
		VPTNode n = (VPTNode) aNode;
		if (n.isGroup() || n.isProject()) {
			return buildPathToRoot(aNode, null);
		} else {
			VPTProject p = VPTNode.findProjectFor(n);
			return buildPathToRoot(p, aNode);
		}
	} //}}}

	//{{{ -buildPathToRoot(TreeNode, TreeNode) : TreeNode[]
	private TreeNode[] buildPathToRoot(TreeNode aNode, TreeNode child) {
		pathBuilder.clear();
		if (child != null)
			pathBuilder.add(child);
		while (aNode != getRoot()) {
			pathBuilder.add(0, aNode);
			aNode = aNode.getParent();
		}
		pathBuilder.add(0, aNode);
		return (TreeNode[]) pathBuilder.toArray(new TreeNode[pathBuilder.size()]);
	} //}}}

	//{{{ +nodeChanged(TreeNode) : void
	/** Handles a node changed request. */
	public void nodeChanged(TreeNode node) {
		VPTNode n = (VPTNode) node;
		if (!n.isGroup() && !n.isProject()) {
			n = VPTNode.findProjectFor(n);
		}
		fireTreeNodesChanged(n, getPathToRoot(n), null, null);
	} //}}}


	public void projectClosed(VPTProject p)
	{
		fileLists.remove(p);
	}


	protected String getName()
	{
        return "projectviewer.workingfilestab";
	}


	public boolean isFlat()
	{
		return true;
	}

}

