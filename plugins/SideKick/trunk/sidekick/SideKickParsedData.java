/*
 * SideKickParsedData.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003 Slava Pestov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package sidekick;

//{{{ Imports
import javax.swing.tree.*;
import java.util.*;
import org.gjt.sp.jedit.EditPane;
//}}}

/**
 * Encapsulates the results of parsing a buffer.
 */
public class SideKickParsedData
{
	//{{{ getParsedData() method
	public static SideKickParsedData getParsedData(EditPane editPane)
	{
		return (SideKickParsedData)editPane.getClientProperty(
			SideKickPlugin.PARSED_DATA_PROPERTY);
	} //}}}

	public DefaultTreeModel tree;
	public DefaultMutableTreeNode root;

	//{{{ SideKickParsedData constructor
	public SideKickParsedData(String fileName)
	{
		root = new DefaultMutableTreeNode(fileName);
		tree = new DefaultTreeModel(root);
	} //}}}

	//{{{ getTreePathForPosition() method
	public TreePath getTreePathForPosition(int dot)
	{
		if(root.getChildCount() == 0)
			return null;

		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
			root.getChildAt(0);
		if(node.getUserObject() instanceof Asset)
		{
			ArrayList _path = new ArrayList();
			getTreePathForPosition(node,dot,_path);
			_path.add(node);
			_path.add(root);

			Object[] path = new Object[_path.size()];
			for(int i = 0; i < path.length; i++)
				path[i] = _path.get(path.length - i - 1);

			TreePath treePath = new TreePath(path);
			return treePath;
		}
		else
			return null;
	} //}}}

	//{{{ getTreePathForPosition() method
	private boolean getTreePathForPosition(TreeNode node, int dot, List path)
	{
		int childCount = node.getChildCount();
		Object userObject = ((DefaultMutableTreeNode)node).getUserObject();
		Asset asset = (Asset)userObject;

		if(childCount != 0)
		{
			// check if any of our children contain the caret
			for(int i = childCount - 1; i >= 0; i--)
			{
				TreeNode _node = node.getChildAt(i);
				if(getTreePathForPosition(_node,dot,path))
				{
					path.add(_node);
					return true;
				}
			}
		}

		// check if the caret in inside this tag
		if(dot >= asset.start.getOffset() && (asset.end == null
			|| dot < asset.end.getOffset()))
		{
			//path.add(node);
			return true;
		}
		else
			return false;
	} //}}}
}
