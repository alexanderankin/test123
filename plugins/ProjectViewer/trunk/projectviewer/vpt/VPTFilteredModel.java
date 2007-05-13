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
import java.util.HashMap;
import java.util.List;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.*;

import javax.swing.tree.TreeNode;

import org.gjt.sp.util.Log;

import projectviewer.ProjectManager;
//}}}

/**
 *	A tree model that compress the view by displaying packed directories.
 *
 *	<p>A directory is displayed only if it contains at least one (non-hidden) file.
 *	The relative path is used to display these directories.
 *	</p>
 *	<p>Example:</p>
 *	<table>
 *		<tr><td>-</td><td>MyProject</td></tr>
 *		<tr><td></td><td>-</td><td>src/net/sf/myproject/ui</td></tr>
 *		<tr><td></td><td></td><td></td><td>MyFrame.java</td></tr>
 *		<tr><td></td><td></td><td></td><td>MyDialog.java</td></tr>
 *		<tr><td></td><td>-</td><td>src/net/sf/myproject/actions</td></tr>
 *		<tr><td></td><td></td><td></td><td>MyAction.java</td></tr>
 *		<tr><td></td><td>+</td><td>src/net/sf/myproject/mybigpackage</td></tr>
 *	</table>
 *
 *	@author		Rudolf Widmann
 *	@version	$Id$
 *	@since		PV 2.2.0.0
 */
public class VPTFilteredModel extends ProjectTreeModel {

	//{{{ Private members
	private static final String SEPARATOR = "/";
	private Map cache = new HashMap();
	//}}}

	//{{{ +VPTFilteredModel(VPTNode) : <init>
	/**
	*	Create a new <code>VPTFilteredModel</code>.
	*
	*	@param rootNode	The root node of the tree.
	*/
	public VPTFilteredModel(VPTNode rootNode) {
		super(rootNode);
	}
	//}}}

	//{{{ +getChildCount(Object) : int
	public int getChildCount(Object parent) {
		VPTNode node = (VPTNode) parent;
		if (node.isGroup()) {
			return node.getChildCount();
		} else if (node.isProject()) {
			return getProjectChildren((VPTProject)node).size();
		} else if (node instanceof FilteredDirectoryNode) {
			FilteredDirectoryNode cd = (FilteredDirectoryNode) node;
			return cd.getFiles().size();
		}
		Log.log(Log.WARNING, this, "Reached the supposedly unreachable! parent.getClass() = " + parent.getClass());
		return 0; // shouldn't reach here
	} //}}}

	//{{{ +getChild(Object, int) : Object
	public Object getChild(Object parent, int index) {
		VPTNode node = (VPTNode) parent;
		if (node.isGroup()) {
			return node.getChildAt(index);
		} else if (node.isProject()) {
			return getProjectChildren((VPTProject)node).get(index);
		} else if (node instanceof FilteredDirectoryNode) {
			FilteredDirectoryNode cd = (FilteredDirectoryNode) node;
			return cd.getFiles().get(index);
		}
		Log.log(Log.WARNING, this, "Reached the supposedly unreachable! parent.getClass() = " + parent.getClass());
		return null; // shouldn't reach here
	} //}}}

	//{{{ +getIndexOfChild(Object, Object) : int
	public int getIndexOfChild(Object parent, Object child) {
		VPTNode node = (VPTNode) parent;
		if (node.isGroup()) {
			return super.getIndexOfChild(parent, child);
		} else if (node.isProject()) {
			List l = getProjectChildren((VPTProject)node);
			return l.indexOf(child);
		} else if (node instanceof FilteredDirectoryNode) {
			FilteredDirectoryNode cd = (FilteredDirectoryNode) node;
			return cd.getFiles().indexOf(child);
		}
		Log.log(Log.WARNING, this, "Reached the supposedly unreachable! parent.getClass() = " + parent.getClass());
		return -1; // shouldn't reach here
	} //}}}

	//{{{ +nodeStructureChanged(TreeNode) : void
	public void nodeStructureChanged(TreeNode node) {
		cache.clear();
		super.nodeStructureChanged(node);
	} //}}}

	//{{{ +clearCache() : void
	/** when the filter has changed, clear the cache */
	public void clearCache() {
		cache.clear();
	} //}}}

	//{{{ +nodesWereInserted(TreeNode, int[]) : void
	public void nodesWereInserted(TreeNode node, int[] childIndices) {
		nodeStructureChanged(VPTNode.findProjectFor((VPTNode)node));
	} //}}}

	//{{{ +nodesWereRemoved(TreeNode, int[], Object[]) : void
	public void nodesWereRemoved(TreeNode node, int[] childIndices, Object[] removedChildren) {
		nodeStructureChanged(VPTNode.findProjectFor((VPTNode)node));
	} //}}}

	//{{{ #getFilteredNodes(VPTProject) : List
	protected List getFilteredNodes(VPTProject node) {
		if (cache.get(node)!=null)
			return (List)cache.get(node);
		Log.log(Log.DEBUG, this, "not cached: "+node);
		List filteredNodesList=new ArrayList();

		// openableNodes returns an unmodifiable Collection
		// ==> copy it into a new list
		List openableNodelist = new LinkedList();
		Iterator openableNodesIt = node.openableNodes.values().iterator();
		while (openableNodesIt.hasNext())
			openableNodelist.add(openableNodesIt.next());

		// check first project list, then global list
		boolean useProjectFilter = true;
		for (int i=0; i<2 ;i++)
		{
			Iterator it;
			if (useProjectFilter) {
				it = node.getFilterList().iterator();
			} else {
				it = ProjectManager.getInstance().getGlobalFilterList().iterator();
			}
			useProjectFilter = false;
			while (it.hasNext())
			{
				VPTFilterData fd = (VPTFilterData)it.next();
				FilteredDirectoryNode filtNode = new FilteredDirectoryNode(fd, openableNodelist);
				if (filtNode.getChildCount() > 0)
					filteredNodesList.add(filtNode);
			}
		}
		cache.put(node, filteredNodesList);
		return filteredNodesList;
	} //}}}

	//{{{ #hasFile(VPTNode) : boolean
	protected boolean hasFile(VPTNode node) {
		for (int i = 0; i < node.getChildCount(); i++) {
			VPTNode child = (VPTNode) node.getChildAt(i);
			if (child.isFile()) {
				return true;
			}
		}
		return false;
	} //}}}

	//{{{ #getProjectChildren(VPTProject) : List
	protected List getProjectChildren(VPTProject project) {
		List cd = getFilteredNodes(project);
		return cd;
	} //}}}

	//{{{ +getPathToRoot(TreeNode) : TreeNode[]
	public TreeNode[] getPathToRoot(TreeNode aNode) {
		// Three possibilities: aNode is a group, aNode is a
		// project or aNode is a leaf. In the first cases, just
		// return the normal path to root. Otherwise, need to build
		// the path...
		VPTNode node = (VPTNode) aNode;
		if (node.isGroup() || node.isProject()) {
			return super.getPathToRoot(aNode);
		} else if (node.isLeaf()) {
			VPTProject proj = VPTNode.findProjectFor(node);
			TreeNode[] proj_path = super.getPathToRoot(proj);
			// path will be project path + 2 nodes
			TreeNode[] lpath = new TreeNode[proj_path.length + 2];
			System.arraycopy(proj_path, 0, lpath, 0, proj_path.length);

			boolean found = false;
			List filters = getFilteredNodes(proj);
			for (Iterator i = filters.iterator(); i.hasNext(); ) {
				FilteredDirectoryNode dnode = (FilteredDirectoryNode) i.next();
				if (dnode.getFiles().contains(node)) {
					lpath[lpath.length - 2] = dnode;
					lpath[lpath.length - 1] = node;
					found = true;
					break;
				}
			}
			return (found) ? lpath : null;
		} else {
			// shouldn't reach here?
			return null;
		}
	} //}}}

	//{{{ +nodeChanged(TreeNode) : void
	/** Handles a node changed request. */
	public void nodeChanged(TreeNode node) {
		VPTNode n = (VPTNode) node;
		if (n.isGroup() || n.isProject()) {
			super.nodeChanged(node);
		} else {
			TreeNode[] path_to_node = getPathToRoot(n);
			if (path_to_node != null) {
				fireTreeNodesChanged(n, path_to_node, null, null);
			}
		}
	} //}}}


	protected String getName()
	{
        return "projectviewer.filteredtab";
	}


	//{{{ +class FilteredDirectoryNode
	public class FilteredDirectoryNode extends VPTDirectory {

		private VPTFilterData filterData;
		private List files = new ArrayList();

		//{{{ +FilteredDirectoryNode(VPTFilterData, List) : <init>
		public FilteredDirectoryNode(VPTFilterData filterData, List openableNodeList)
		{
			super(new java.io.File(filterData.getName()));
			this.filterData = filterData;
			Iterator it = openableNodeList.iterator();
			while (it.hasNext())
			{
				VPTNode node = (VPTNode)it.next();
				if (filterData.getPattern().matcher(node.getName()).matches())
				{
					add(node);
				}
			}
			sortFiles();

			// remove from openableNodeList
			it = files.iterator();
			while (it.hasNext())
				openableNodeList.remove(it.next());

		} //}}}

		//{{{ +getName() : String
		public String getName() {
			return filterData.getName();
		} //}}}

		//{{{ +getFiles() : List
		public List getFiles() {
			return files;
		} //}}}

		//{{{ -sortFiles() : void
		private void sortFiles() {
			Collections.sort(files);
		} //}}}

		//{{{ +getChildCount() : int
		public int getChildCount() {
			return files.size();
		} //}}}

		//{{{ +remove(VPTNode) : void
		public void remove(VPTNode node) {
			files.remove(node);
		} //}}}

		//{{{ +add(VPTNode) : void
		public void add(VPTNode node) {
			files.add(node);
		} //}}}

		//{{{ +getClipType() : int
		public int getClipType() {
			return VPTCellRenderer.CLIP_START;
		} //}}}

	} //}}}

}

