/*
Copyright (C) 2006  Shlomy Reinstein

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package ctags.sidekick;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.gjt.sp.jedit.jEdit;

import sidekick.IAsset;
import sidekick.SideKickParsedData;


public class ParsedData extends SideKickParsedData
{
	ITreeMapper mapper = null;
	Comparator<CtagsSideKickTreeNode> sorter = null;
	CtagsSideKickTreeNode tree = new CtagsSideKickTreeNode();
	
	public ParsedData(String fileName, String lang)
	{
		super(fileName);
		String mapperName = jEdit.getProperty(OptionPane.MAPPER); 
		if (mapperName.equals(jEdit.getProperty(OptionPane.NAMESPACE_MAPPER_NAME)))
			mapper = new NamespaceTreeMapper();
		else if (mapperName.equals(jEdit.getProperty(OptionPane.FLAT_NAMESPACE_MAPPER_NAME)))
			mapper = new FlatNamespaceTreeMapper();
		else
			mapper = new KindTreeMapper();
		mapper.setLang(lang);
		if (jEdit.getBooleanProperty(OptionPane.SORT, false))
		{
			if (jEdit.getBooleanProperty(OptionPane.FOLDS_BEFORE_LEAFS, true))
				sorter = new FoldNameComparator();
			else
				sorter = new NameComparator();
		}
		else
			sorter = null;
	}
	
	void add(Tag tag)
	{
		if (mapper == null)
		{
			tree.add(tag);
			return;
		}
		Vector<Object> path = mapper.getPath(tag);
		CtagsSideKickTreeNode node = tree; 
		for (int i = 0; i < path.size(); i++)
		{
			Object obj = path.get(i);
			CtagsSideKickTreeNode child =
				(CtagsSideKickTreeNode) node.findChild(obj);
			if (child == null)
			{
				child = node.add(obj);
			}
			else
			{
				// Let real tags take over String placeholders
				if ((child.getUserObject() instanceof String) &&
					(!(obj instanceof String)))
				{
					child.setUserObject(obj);
				}
			}
			node = child;
		}
	}
	public void done()
	{
		if (sorter != null)
			tree.sort(sorter);
		tree.addToTree(root);
	}
	private static boolean assetContains(IAsset asset, int offset)
	{
		return offset >= asset.getStart().getOffset()
		    && offset < asset.getEnd().getOffset();
	}
	protected TreeNode getNodeAt(TreeNode parent, int offset)
	{
		for (int i = 0; i < parent.getChildCount(); i++)
		{
			TreeNode node = parent.getChildAt(i);
			// First check node's children recursively (DFS)
			TreeNode ret = getNodeAt(node, offset);
			if (ret != null)
				return ret;
			// If not in the children - check node
			IAsset asset = getAsset(node);
			if ((asset != null) && assetContains(asset, offset))
				return node;
		}
		IAsset asset = getAsset(parent);
		if ((asset != null) && assetContains(asset, offset))
			return parent;
		return null;
	}

	private class CtagsSideKickTreeNode
	{
		private Vector<CtagsSideKickTreeNode> children =
			new Vector<CtagsSideKickTreeNode>();
		private Object object = null;
		void setUserObject(Object obj)
		{
			object = obj;
		}
		Object getUserObject()
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
		boolean hasChildren()
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
			root.setUserObject(getUserObject());
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
	class NameComparator implements Comparator<CtagsSideKickTreeNode>	
	{
		public int compare(CtagsSideKickTreeNode a, CtagsSideKickTreeNode b)
		{
			return a.getUserObject().toString().compareTo(b.getUserObject().toString());
		}
	}
	class FoldNameComparator implements Comparator<CtagsSideKickTreeNode>	
	{
		public int compare(CtagsSideKickTreeNode a, CtagsSideKickTreeNode b)
		{
			if (a.hasChildren() == b.hasChildren())
				return a.getUserObject().toString().compareTo(b.getUserObject().toString());
			if (a.hasChildren())
				return (-1);
			return 1;
		}
	}	
}
