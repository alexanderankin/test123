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
import java.util.Stack;
import java.util.ArrayList;
import java.util.WeakHashMap;
import java.util.Collections;

import javax.swing.tree.TreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.MiscUtilities;
//}}}

/**
 *	A tree model that represents all files in a project without any relationship
 *	to the nodes they are a child of.
 *
 *	<p>This model, similarly to the other "flat model": VPTWorkingFileList, is
 *	a little dumb, for the sake of code simplicity. Using "insertNodeInto" or
 *	similar methods will not work for these models. The only structure change
 *	supported is the {@link #nodeStructureChanged(TreeNode) nodeStructureChanged()}
 *	method. So, classes that change the tree structure in some way should wait
 *	until all changes are made and then call this method for the project node or
 *	the root node of the tree, preferably using the "broadcast" method available
 *	in the ProjectViewer class.</p>
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class VPTFileListModel extends DefaultTreeModel {

	//{{{ Private members
	private final static VPTNode.VPTNodeComparator COMPARATOR =
		new VPTNode.VPTNodeComparator();

	private WeakHashMap fileLists;

	private Object lastParent;
	private ArrayList lastList;
	//}}}

	//{{{ +VPTFileListModel(VPTNode) : <init>

	/**
	 *	Create a new <code>VPTFileListModel</code>.
	 *
	 *	@param rootNode	The root node of the tree.
	 */
	public VPTFileListModel(VPTNode rootNode) {
		super(rootNode, true);
		fileLists = new WeakHashMap();
	}

	//}}}

	//{{{ +getChildCount(Object) : int
	/**
	 *	Returns the child at the given index of the given parent. If the parent
	 *	is a project, returns the number of files in the project, not just the
	 *	files that are direct children of the project.
	 */
	public int getChildCount(Object parent) {
		VPTNode node = (VPTNode) parent;
		if (node.isRoot()) {
			return node.getChildCount();
		} else if (node.isProject()) {
			return ((VPTProject)node).openableNodes.size();
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
		if (node.isRoot()) {
			return node.getChildAt(index);
		} else if (node.isProject()) {
			ArrayList lst = getProjectFileList((VPTProject) node);
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
		if (!n.isRoot()) {
			VPTProject p = VPTNode.findProjectFor(n);
			ArrayList lst = new ArrayList(p.openableNodes.values());
			MiscUtilities.quicksort(lst, COMPARATOR);
			fileLists.put(p, lst);

			if (lastParent == p) {
				lastList = lst;
			}
			node = p;
		}
		super.nodeStructureChanged(node);
	} //}}}

	//{{{ +getIndexOfChild(Object, Object) : int
	public int getIndexOfChild(Object parent, Object child) {
		if (parent == lastParent) {
			return Collections.binarySearch(lastList, child, COMPARATOR);
		}

		VPTNode node = (VPTNode) child;
		if (node.isRoot()) {
			return super.getIndexOfChild(parent, child);
		} else if (node.isProject()) {
			return Collections.binarySearch(getProjectFileList((VPTProject) node), child);
		}
		Log.log(Log.WARNING, this, "Reached the supposedly unreachable! parent = " + parent);
		return -1; // shouldn't reach here
	} //}}}

	//{{{ -getProjectFileList(VPTProject) : ArrayList
	/** Returns a vector with all the files of the project. */
	private ArrayList getProjectFileList(VPTProject p) {
		lastParent = p;
		ArrayList lst = (ArrayList) fileLists.get(p);
		if (lst == null) {
			lst = new ArrayList(p.openableNodes.values());
			MiscUtilities.quicksort(lst, COMPARATOR);
			fileLists.put(p, lst);
		}
		lastList = lst;
		return lst;
	} //}}}

	//{{{ +getPathToRoot(TreeNode) : TreeNode[]
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

	//{{{ +nodeChanged(TreeNode) : void
	/** Handles a node changed request. */
	public void nodeChanged(TreeNode node) {
		VPTNode n = (VPTNode) node;
		if (n.isRoot() || n.isProject()) {
			super.nodeChanged(node);
		} else {
			VPTProject p = VPTNode.findProjectFor(n);
			fireTreeNodesChanged(n, getPathToRoot(n), new int[] { -1 }, null);
		}
	} //}}}

}

