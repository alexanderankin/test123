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
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;
//}}}

/**
 * Stores a buffer structure tree.
 *
 * Plugins can extend this class to persist plugin-specific information.
 * For example, the XML plugin stores code completion-related structures using
 * a subclass.
 */
public class SideKickParsedData
{
	//{{{ getParsedData() method
	/**
	 * Returns an instance of this class for the specified view.
	 *
	 * Note that this will only return a valid object after the
	 * <code>SideKick.parse()</code> method is called.
	 *
	 * @param view The view.
	 */
	public static SideKickParsedData getParsedData(View view)
	{
		return (SideKickParsedData)view.getRootPane().getClientProperty(
			SideKickPlugin.PARSED_DATA_PROPERTY);
	} //}}}

	//{{{ setParsedData() method
	/**
	 * Sets the instance of this class for the specified view.
	 *
	 * @param view The view.
	 * @param data The instance.
	 */
	public static void setParsedData(View view, SideKickParsedData data)
	{
		view.getRootPane().putClientProperty(
			SideKickPlugin.PARSED_DATA_PROPERTY,
			data);
	} //}}}

	public DefaultTreeModel tree;

	/**
	 * Plugin parsers should add nodes to the root node.
	 */
	public DefaultMutableTreeNode root;

	//{{{ SideKickParsedData constructor
	/**
	 * @param fileName The file name being parsed, used as the root of the
	 * tree.
	 */
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

		ArrayList _path = new ArrayList();
		for(int i = root.getChildCount() - 1; i >= 0; i--)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)
				root.getChildAt(i);
			if(getTreePathForPosition(node,dot,_path))
			{
				_path.add(node);
				break;
			}
		}

		if(_path.size() == 0)
		{
			// nothing found
			return null;
		}
		else
		{
			Object[] path = new Object[_path.size() + 1];
			path[0] = root;
			int len = _path.size();
			for(int i = 0; i < len; i++)
				path[i + 1] = _path.get(len - i - 1);

			TreePath treePath = new TreePath(path);
			return treePath;
		}
	} //}}}

	//{{{ getTreePathForPosition() method
	private boolean getTreePathForPosition(TreeNode node, int dot, List path)
	{
		int childCount = node.getChildCount();
		Object userObject = ((DefaultMutableTreeNode)node).getUserObject();
		if(!(userObject instanceof Asset))
			return false;

		Asset asset = (Asset)userObject;

		// check if the caret in inside this tag
		if(dot >= asset.start.getOffset() && dot <= asset.end.getOffset())
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

			//path.add(node);
			return true;
		}
		else
			return false;
	} //}}}
}
