/**
 * 
 */
package ctags.sidekick;

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

public class CtagsSideKickTreeNode
{
	private Vector<CtagsSideKickTreeNode> children =
		new Vector<CtagsSideKickTreeNode>();
	private Object object = null;
	void setUserObject(Object obj)
	{
		object = obj;
	}
	public void addChildCounts()
	{
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
	CtagsSideKickTreeNode add(Object obj)
	{
		CtagsSideKickTreeNode node = new CtagsSideKickTreeNode();
		node.setUserObject(obj);
		children.add(node);
		return node;
	}
	public boolean hasChildren()
	{
		return (children.size() > 0);
	}
	Object findChild(Object obj)
	{
		Enumeration children = this.children.elements();
		while (children.hasMoreElements())
		{
			CtagsSideKickTreeNode child =
				(CtagsSideKickTreeNode) children.nextElement();
			if (child.getUserObject().equals(obj))
				return child;
		}
		return null;			
	}
	void addToTree(DefaultMutableTreeNode root)
	{
		addChildrenToTree(root);
	}
	void addChildrenToTree(DefaultMutableTreeNode node)
	{
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
		Collections.sort(children, sorter);
		Enumeration children = this.children.elements();
		while (children.hasMoreElements())
		{
			CtagsSideKickTreeNode child =
				(CtagsSideKickTreeNode) children.nextElement();
			child.sort(sorter);
		}			
	}
}