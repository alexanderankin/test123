/*
 * :tabSize=4:indentSize=4:noTabs=true:
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
import java.util.Iterator;
import java.util.List;
import java.util.Collections;

import javax.swing.tree.TreeNode;

import projectviewer.ProjectManager;
//}}}

/**
 *	A tree model that shows nodes grouped according to configurable
 *	filters.
 *
 *	@author		Rudolf Widmann
 *	@version	$Id$
 *	@since		PV 2.2.0.0
 */
public class VPTFilteredModel extends ProjectCustomTreeModel
{

	/**
	 * Create a new <code>VPTFilteredModel</code>.
	 *
	 * @param	rootNode	The root node of the tree.
	 */
	public VPTFilteredModel(VPTNode rootNode)
	{
		super(rootNode);
	}


	public int getChildCount(Object parent)
	{
		if (parent instanceof FilteredDirectoryNode) {
			FilteredDirectoryNode node = (FilteredDirectoryNode) parent;
			return node.getFiles().size();
		}
		return super.getChildCount(parent);
	}


	public Object getChild(Object parent,
						   int index)
	{
		if (parent instanceof FilteredDirectoryNode) {
			FilteredDirectoryNode node = (FilteredDirectoryNode) parent;
			return node.getFiles().get(index);
		}
		return super.getChild(parent, index);
	}


	public int getIndexOfChild(Object parent,
							   Object child)
	{
		if (parent instanceof FilteredDirectoryNode) {
			FilteredDirectoryNode node = (FilteredDirectoryNode) parent;
			return node.getFiles().indexOf(child);
		}
		return super.getIndexOfChild(parent, child);
	}


	protected List<VPTNode> getChildren(VPTProject node)
	{
		List<VPTNode> lst = new ArrayList<VPTNode>();

		/*
		 * openableNodes returns an unmodifiable Collection, so
		 * copy it into a new list.
		 */
		List<VPTNode> openableNodelist = new ArrayList<VPTNode>();
		for (VPTNode n : node.openableNodes.values()) {
			openableNodelist.add(n);
		}

		boolean useProjectFilter = true;
		for (int i = 0; i < 2; i++)
		{
			List<VPTFilterData> filters;
			if (useProjectFilter) {
				filters = node.getFilterList();
				useProjectFilter = false;
			} else {
				filters = ProjectManager.getInstance().getGlobalFilterList();
			}
			for (VPTFilterData fd : filters) {
				FilteredDirectoryNode filtNode = new FilteredDirectoryNode(fd, openableNodelist);
				if (filtNode.getChildCount() > 0) {
					lst.add(filtNode);
				}
			}
		}
		return lst;
	}


	public TreeNode[] getPathToRoot(TreeNode aNode)
	{
		if (aNode.isLeaf() && !aNode.getAllowsChildren()) {
			VPTNode node = (VPTNode) aNode;
			VPTProject proj = VPTNode.findProjectFor(node);
			List<VPTNode> filters = getCachedChildren(proj);

			for (VPTNode _filter : filters) {
				FilteredDirectoryNode filter = (FilteredDirectoryNode) _filter;
				if (filter.getFiles().contains(node)) {
					return buildPathToRoot(filter, node);
				}
			}
			return null;
		}
		return super.getPathToRoot(aNode);
	}


	protected String getName()
	{
        return "projectviewer.filteredtab";
	}


	//{{{ class FilteredDirectoryNode
	private class FilteredDirectoryNode extends VPTDirectory
	{
		private List<VPTNode> files;

		public FilteredDirectoryNode(VPTFilterData filter,
									 List<VPTNode> nodeList)
		{
			super(filter.getName());
            files = new ArrayList<VPTNode>();

			for (Iterator<VPTNode> i = nodeList.iterator(); i.hasNext(); ) {
                VPTNode node = i.next();
				if (filter.getPattern().matcher(node.getName()).matches()) {
					files.add(node);
                    i.remove();
				}
			}
			Collections.sort(files);
		}


		public List getFiles()
		{
			return files;
		}


		public int getChildCount()
		{
			return files.size();
		}


		public int getClipType()
		{
			return VPTCellRenderer.CLIP_START;
		}
        
		public int compareTo(VPTNode node)
		{
			if (node instanceof FilteredDirectoryNode) {
				return compareName(node);
			} else {
				return -1 * node.compareTo(this);
			}
		}

	} //}}}

}

