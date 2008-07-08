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

public class CtagsSideKickTreeNode
{
	private UniqueSequence children = null;
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
		Enumeration children = this.children.elements();
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
	CtagsSideKickTreeNode putChild(Object obj)
	{
		if (children == null)
		{
			children = new UniqueSequence();
		}
		String key =
			(obj instanceof Tag)
				? ((Tag)obj).getShortString()
				: obj.toString();
		CtagsSideKickTreeNode node = children.get(key);
		if (node == null)
		{
			node = new CtagsSideKickTreeNode();
			node.setUserObject(obj);
			children.put(key, node);
		}
		else
		{
			// Let real tags take over String placeholders
			if ((node.getUserObject() instanceof String) &&
				(!(obj instanceof String)))
			{
				node.setUserObject(obj);
			}
		}
		return node;
	}
	public boolean hasChildren()
	{
		return (children != null && children.size() > 0);
	}
	void addToTree(DefaultMutableTreeNode root)
	{
		addChildrenToTree(root);
	}
	void addChildrenToTree(DefaultMutableTreeNode node)
	{
		if (children == null)
		{
			return;
		}
		Enumeration children = this.children.elements();
		while (children.hasMoreElements())
		{
			CtagsSideKickTreeNode child =
				(CtagsSideKickTreeNode) children.nextElement();
			DefaultMutableTreeNode newNode = 
				new DefaultMutableTreeNode(child.getUserObject());
			node.add(newNode);
			child.addChildrenToTree(newNode);
		}
	}
	void sort(Comparator<CtagsSideKickTreeNode> sorter)
	{
		if (children == null)
		{
			return;
		}
		children.sort(sorter);
		Enumeration children = this.children.elements();
		while (children.hasMoreElements())
		{
			CtagsSideKickTreeNode child =
				(CtagsSideKickTreeNode) children.nextElement();
			child.sort(sorter);
		}			
	}

	/**
	 * Container which keeps nodes in both ordered(sortable) and
	 * efficiently identified.
	 */
	private static class UniqueSequence
	{
		private Vector<CtagsSideKickTreeNode> ordered =
			new Vector<CtagsSideKickTreeNode>();
		private HashMap<String, CtagsSideKickTreeNode> identified =
			new HashMap<String, CtagsSideKickTreeNode>();

		public int size()
		{
			assert(ordered.size() == identified.size());
			return ordered.size();
		}

		public Enumeration elements()
		{
			return ordered.elements();
		}

		public CtagsSideKickTreeNode get(String key)
		{
			return identified.get(key);
		}

		public void put(String key, CtagsSideKickTreeNode node)
		{
			ordered.add(node);
			identified.put(key, node);
		}

		public void sort(Comparator<CtagsSideKickTreeNode> sorter)
		{
			Collections.sort(ordered, sorter);
		}
	}
}
