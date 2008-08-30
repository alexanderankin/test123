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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.swing.tree.TreeNode;

/**
 * A tree model for customized trees, trees that do not follow the node
 * hierarchy as defined by the original data. This class provides some
 * useful methods for caching and doing boring tree management work
 * for these trees.
 *
 * @author     Marcelo Vanzin
 * @version    $Id$
 * @since      PV 3.0.0
 */
public abstract class ProjectCustomTreeModel extends ProjectTreeModel
{

	private Map<VPTProject,List<VPTNode>> childCache;

	private Object lastParent;
	private List<VPTNode> lastList;
	private List<TreeNode> pathBuilder;


    protected ProjectCustomTreeModel(VPTNode root)
    {
        super(root);
		childCache = new WeakHashMap<VPTProject,List<VPTNode>>();
		pathBuilder = new LinkedList<TreeNode>();
    }


	/**
	 * Returns the child at the given index of the given parent. If
     * the parent is a project, treats the children in such a way to
     * allow all files in the project to be displayed in a flat list.
	 */
	public Object getChild(Object parent,
                           int index)
    {
		if (parent == lastParent) {
			return lastList.get(index);
		}

		VPTNode node = (VPTNode) parent;
		if (node.isGroup()) {
			return node.getChildAt(index);
		} else if (node.isProject()) {
			VPTProject p = (VPTProject) node;
			List<VPTNode> lst = getCachedChildren(p);
			lastParent = parent;
			lastList = lst;
			return (index < lst.size()) ? lst.get(index) : null;
		}
        assert (false) : "should not reach here";
		return null;
	}


	/**
	 * Returns the number of children of the given node, based on the
     * internal cached data.
	 */
	public int getChildCount(Object parent)
    {
		VPTNode node = (VPTNode) parent;
		if (node.isGroup()) {
			return node.getChildCount();
		} else if (node.isProject()) {
			return getCachedChildren((VPTProject)node).size();
		}
        assert (false) : "should not reach here";
		return -1;
	}


    /**
     * Returns the index of the given child in the given parent,
     * according to the information in the internal cache.
     */
	public int getIndexOfChild(Object parent,
                               Object child)
    {
		if (parent == lastParent) {
			return Collections.binarySearch(lastList, (VPTNode) child);
		}

		VPTNode node = (VPTNode) child;
		if (node.isGroup()) {
			return super.getIndexOfChild(parent, child);
		} else if (node.isProject()) {
			lastParent = parent;
			return Collections.binarySearch(getCachedChildren((VPTProject) node),
											(VPTNode) child);
		}
        assert (false) : "should not reach here";
		return -1;
	}


    /**
     * Builds the path to the root of the tree up to the given
     * node.
     */
	public TreeNode[] getPathToRoot(TreeNode aNode)
    {
		VPTNode n = (VPTNode) aNode;
		if (n.isGroup() || n.isProject()) {
			return buildPathToRoot(aNode);
		} else {
			VPTProject p = VPTNode.findProjectFor(n);
			return buildPathToRoot(p, aNode);
		}
	}


	/** Handles a node changed request. */
	public void nodeChanged(TreeNode node)
    {
		VPTNode n = (VPTNode) node;
		if (!n.isGroup() && !n.isProject()) {
			n = VPTNode.findProjectFor(n);
		}
		fireTreeNodesChanged(n, getPathToRoot(n), null, null);
	}


	/**
	 * Called when some node in the tree is changed. If not the root,
     * then tracks down which project was changed and forces a refresh
     * of the internal cache of child nodes.
	 */
	public void nodeStructureChanged(TreeNode node)
    {
		VPTNode n = (VPTNode) node;
		if (!n.isGroup()) {
			VPTProject p = VPTNode.findProjectFor(n);
			List<VPTNode> lst = getChildren(p);
			childCache.put(p, lst);

			if (lastParent == p) {
				lastList = lst;
			}
			node = p;
		}
		super.nodeStructureChanged(node);
	}


    /** Remove data related to the project from the cache. */
	public void projectClosed(VPTProject p)
	{
        if (lastParent == p) {
            lastParent = null;
            lastList = null;
        }
		childCache.remove(p);
	}


    /**
     * Adds a child to the list of children of the given project.
     */
    protected void addChild(VPTProject proj,
                            VPTNode child)
    {
        List<VPTNode> lst = getCachedChildren(proj);
        if (!lst.contains(child)) {
            lst.add(child);
            Collections.sort(lst);
            super.nodeStructureChanged(proj);
        }
    }


    /**
     * Returns the path from the root to the given node, appending
     * an optional list of children of the node to the path.
     */
	protected TreeNode[] buildPathToRoot(TreeNode aNode,
                                         TreeNode... children)
    {
		pathBuilder.clear();
		if (children != null) {
            for (TreeNode child : children) {
                pathBuilder.add(child);
            }
        }
		while (aNode != getRoot()) {
			pathBuilder.add(0, aNode);
			aNode = aNode.getParent();
		}
		pathBuilder.add(0, aNode);
		return pathBuilder.toArray(new TreeNode[pathBuilder.size()]);
	}


    /**
     * Returns the internally cached children lists.
     */
    protected Map<VPTProject,List<VPTNode>> getCache()
    {
        return childCache;
    }


    /**
     * Retrieves the list of child nodes from the given project,
     * first trying the cache.
     */
    protected List<VPTNode> getCachedChildren(VPTProject proj)
    {
			List<VPTNode> lst = childCache.get(proj);
			if (lst == null) {
				lst = getChildren(proj);
                Collections.sort(lst);
				childCache.put(proj, lst);
			}
            return lst;
    }


    /**
     * Subclasses should implement this method to populate the list
     * of children of a project node. Whenever a "cache miss" occurs,
     * this method will be called to return the list of nodes to be
     * added to the internal cache as the "children" of the given
     * project.
     *
     * @param   proj    The project being queried.
     */
    protected abstract List<VPTNode> getChildren(VPTProject proj);


    /**
     * Overrides the parent implementation, returning "true".
     */
    protected boolean isCustom()
    {
        return true;
    }


    /**
     * Remove a child from the internal cache for the given project.
     */
    protected void removeChild(VPTProject proj,
                               VPTNode child)
    {
        List<VPTNode> lst = getCachedChildren(proj);
        if (!lst.contains(child)) {
            lst.remove(child);
            super.nodeStructureChanged(proj);
        }
    }

}

