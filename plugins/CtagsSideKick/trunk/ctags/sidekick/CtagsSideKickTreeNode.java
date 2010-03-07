/**
 * 
 */
package ctags.sidekick;

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;
import java.util.HashMap;

import javax.swing.tree.DefaultMutableTreeNode;

import ctags.sidekick.mappers.ITreeMapper.CollisionHandler;

public class CtagsSideKickTreeNode
{
	private ChildSet children = null;
	private Object object = null;
	
	void setUserObject(Object obj)
	{
		object = obj;
	}
	public void addChildCounts()
	{
		if (children == null)
		{
			return;
		}
		Enumeration<CtagsSideKickTreeNode> children = this.children.elements();
		while (children.hasMoreElements())
		{
			CtagsSideKickTreeNode child =
				(CtagsSideKickTreeNode) children.nextElement();
			Object obj = child.getUserObject();
			if (obj instanceof String)
			{
				int num = child.children.size();
				child.setUserObject((String)obj + " (" + num + ")");
			}
		}
	}
	public Object getUserObject()
	{
		return object;
	}
	/*
	 * Adds a child to this node (if necessary) and returns the child node.
	 * A child can be a Tag or a String place-holder.
	 * If the child to be added (c1) has the same name (string representation)
	 * as an existing child (c2):
	 * - If c1 is a place-holder, it is ignored and c2 is returned. c2 is
	 *   the most recently added child with the same name.
	 * - If c1 is a tag, and c2 is a place-holder, c2 is replaced by c1, and
	 *   c1 is returned.
	 * - If c1 and c2 are both tags, c1 is added and returned. This allows
	 *   multiple tags with the same name under a common parent. c1 is returned
	 *   in this case in order to preserve the original tag order in the tree,
	 *   e.g. in xml where multiple elements with the same name can exist under
	 *   a common parent element, and their children should be added below them
	 *   in-order. However, once all tags have been added to the tree, the mapper
	 *   (if exists) will re-decide the mapping of the grand-children of the
	 *   identically-named children to their parents, and can move a grand-child
	 *   from its current parent to another identically-named child.
	 */
	public CtagsSideKickTreeNode putChild(Object obj, boolean deferCollisions)
	{
		if (children == null)
		{
			children = new ChildSet();
		}
		String key =
			(obj instanceof Tag)
				? ((Tag)obj).getName()
				: obj.toString();
		CtagsSideKickTreeNode node = children.get(key);
		if ((node != null) && 
			// Let real tags take over String placeholders
			((node.getUserObject() instanceof String) &&
			 (!(obj instanceof String))))
		{
			node.setUserObject(obj);
		}
		// Do not add a placeholder if a tag with this name exists
		else if ((node == null) || (! (obj instanceof String)))
		{
			// Either no children with this name were added, or multiple
			// tags with the same name exist under this parent.
			node = new CtagsSideKickTreeNode();
			node.setUserObject(obj);
			children.put(key, node, deferCollisions);
		}
		return node;
	}
	public boolean hasChildren()
	{
		return (children != null && children.size() > 0);
	}
	/*
	 * Adds the children to the given parser tree node.
	 * For identically-named children, the mapper (if exists) re-decides
	 * the mapping of their own children to them as parents. For example,
	 * in C++, if there are two identically named tags under the same
	 * parent, where one is a variable and another is a class, then
	 * the children of the variable node will be re-mapped and moved to
	 * the class node.
	 */
	void addToTree(DefaultMutableTreeNode node, CollisionHandler ch)
	{
		if (children == null)
			return;

		// If identically-named children exist under this node, re-parent
		// their children before actually adding this node's children to
		// the real tree.
		if (ch != null)
			children.handleCollisions(ch);
		
		Enumeration<CtagsSideKickTreeNode> children = this.children.elements();
		while (children.hasMoreElements())
		{
			CtagsSideKickTreeNode child =
				(CtagsSideKickTreeNode) children.nextElement();
			DefaultMutableTreeNode newNode = 
				new DefaultMutableTreeNode(child.getUserObject());
			node.add(newNode);
			child.addToTree(newNode, ch);
		}
	}
	// Used only by the collision handler. When multiple nodes with the
	// same name exist under the same parent, the collision handler
	// re-parents their children to the right node.
	public void takeChildrenFrom(CtagsSideKickTreeNode parent)
	{
		if (parent.children == null)
			return;
		Enumeration<CtagsSideKickTreeNode> otherChildren =
			parent.children.elements();
		while (otherChildren.hasMoreElements())
		{
			putChild(otherChildren.nextElement().getUserObject(), true);
			parent.children = null;
		}
	}
	void sort(Comparator<CtagsSideKickTreeNode> sorter)
	{
		if (children == null)
		{
			return;
		}
		children.sort(sorter);
		Enumeration<CtagsSideKickTreeNode> children = this.children.elements();
		while (children.hasMoreElements())
		{
			CtagsSideKickTreeNode child = children.nextElement();
			child.sort(sorter);
		}			
	}

	/**
	 * Container which keeps nodes in both ordered(sortable) and
	 * efficiently identified.
	 */
	private static class ChildSet
	{
		private Vector<CtagsSideKickTreeNode> ordered =
			new Vector<CtagsSideKickTreeNode>();
		private HashMap<String, CtagsSideKickTreeNode> identified =
			new HashMap<String, CtagsSideKickTreeNode>();
		private HashMap<String, Vector<CtagsSideKickTreeNode>> collisions = null;

		public int size()
		{
			return ordered.size();
		}

		public Enumeration<CtagsSideKickTreeNode> elements()
		{
			return ordered.elements();
		}

		public CtagsSideKickTreeNode get(String key)
		{
			return identified.get(key);
		}

		public void put(String key, CtagsSideKickTreeNode node,
			boolean deferCollisions)
		{
			ordered.add(node);
			CtagsSideKickTreeNode prevNode = identified.put(key, node);
			if (deferCollisions && (prevNode != null))
			{
				if (collisions == null)
					collisions = new HashMap<String, Vector<CtagsSideKickTreeNode>>();

				// Adding a node with the same key as an existing node
				Vector<CtagsSideKickTreeNode> nodes = collisions.get(key);
				if (nodes == null)
				{
					nodes = new Vector<CtagsSideKickTreeNode>();
					collisions.put(key, nodes);
					nodes.add(prevNode);
				}
				nodes.add(node);
			}
		}

		public void sort(Comparator<CtagsSideKickTreeNode> sorter)
		{
			Collections.sort(ordered, sorter);
		}
		
		public void handleCollisions(CollisionHandler ch)
		{
			if (collisions == null)
				return;

			for (String key: collisions.keySet())
				ch.remapChildrenOf(collisions.get(key));
		}
	}
}
