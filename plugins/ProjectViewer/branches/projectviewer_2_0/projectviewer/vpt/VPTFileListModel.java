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
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.tree.TreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.gjt.sp.util.Log;
//}}}

/**
 *	A tree model that represents all files in a project without any relationship
 *	to the nodes they are a child of.
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
			ArrayList lst = (ArrayList) fileLists.get(p);
			if (lst == null) {
				lst = new ArrayList(p.files.values());
				Collections.sort(lst, new VPTNode.VPTNodeComparator());
				fileLists.put(node, lst);
			}
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
			ArrayList lst = new ArrayList(((VPTProject)n).files.values());
			Collections.sort(lst, new VPTNode.VPTNodeComparator());
			fileLists.put(n, lst);
		}
		super.nodeStructureChanged(node);
	}
}

