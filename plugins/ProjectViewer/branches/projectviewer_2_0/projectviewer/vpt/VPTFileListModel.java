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
import java.util.HashMap;
import java.util.Vector;
import java.util.Collections;

import javax.swing.tree.TreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.gjt.sp.util.Log;
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

	private HashMap fileLists;
	
	/** 
	 *	Create a new <code>VPTFileListModel</code>.
	 *
	 *	@param rootNode	The root node of the tree.  
	 */
	public VPTFileListModel(VPTNode rootNode) {
		super(rootNode, true);
		fileLists = new HashMap();
	}
	
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
			return ((VPTProject)node).files.size();
		}
		Log.log(Log.WARNING, this, "Reached the supposedly unreachable! parent = " + parent);
		return 0; // shouldn't reach here
	}

	/**
	 *	Returns the child at the given index of the given parent. If the parent
	 *	is a project, treats the children in such a way to allow all files in the
	 *	project to be displayed in a flat list.
	 */
	public Object getChild(Object parent, int index) {
		VPTNode node = (VPTNode) parent;
		if (node.isRoot()) {
			return node.getChildAt(index);
		} else if (node.isProject()) {
			VPTProject p = (VPTProject) node;
			Vector lst = (Vector) fileLists.get(p);
			if (lst == null) {
				lst = new Vector(p.files.values());
				Collections.sort(lst, new VPTNode.VPTNodeComparator());
				fileLists.put(node, lst);
			}
			if (index >= lst.size()) return null;
			return lst.get(index);
		}
		Log.log(Log.WARNING, this, "Reached the supposedly unreachable! parent = " + parent);
		return null; // shouldn't reach here
	}
	
	/**
	 *	Called when some node in the tree is changed. If not the root, then 
	 *	tracks down which project was changed and updates the child list.
	 */
	public void nodeStructureChanged(TreeNode node) {
		VPTNode n = (VPTNode) node;
		if (!n.isRoot()) {
			while (!n.isProject()) {
				n = (VPTNode) n.getParent();
			}
			Vector lst = new Vector(((VPTProject)n).files.values());
			Collections.sort(lst, new VPTNode.VPTNodeComparator());
			fileLists.put(n, lst);
		}
		super.nodeStructureChanged(node);
	}
	
	//{{{ removeRef(VPTProject) method
	/**
	 *	Removes any reference to the given project stored internally. This does
	 *	not update the tree! To update the tree one of the usual methods (setRoot,
	 *	nodeStructureChanged, etc) should be called.
	 */
	public void removeRef(VPTProject p) {
		fileLists.remove(p);
	} //}}}
	
	
}

