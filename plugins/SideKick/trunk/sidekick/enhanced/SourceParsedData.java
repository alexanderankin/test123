/*
 * PerlParsedData.java
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2005 by Martin Raspe
 * (hertzhaft@biblhertz.it)
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
package sidekick.enhanced;

import javax.swing.tree.*;
import javax.swing.text.Position;
import java.util.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;

import sidekick.*;


/**
 * SourceParsedData:
 * extends SidekickParsedData because we need a special getTreeForPosition method 
 *
 * @author     Martin Raspe
 * @created    Oct 15, 2005
 * @modified   $Id$
 * @version    $Revision$
 */

public class SourceParsedData extends SideKickParsedData {
	//{{{ constructor
/**	 * Constructs a new SourceParsedData object
	 *
	 * @param name See sidekick.SidekickParsedData.
	 */
	public SourceParsedData(String name) {
		super(name);
	} //}}}
	
	//{{{ getTreePathForPosition() method
/**	 * gets the tree path for a text position
	 * largely copied from SidekickParsedData
	 * @param dot See sidekick.SidekickParsedData.
	 */
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
	public boolean getTreePathForPosition(TreeNode node, int dot, List path)
	{
		int childCount = node.getChildCount();
		Object userObject = ((DefaultMutableTreeNode)node).getUserObject();
		if(!(userObject instanceof Asset))
			return false;

		Asset asset = (Asset)userObject;

		// return true if any of our children contain the caret
		for(int i = childCount - 1; i >= 0; i--)
		{
			TreeNode _node = node.getChildAt(i);
			if(getTreePathForPosition(_node,dot,path))
			{
				path.add(_node);
				return true;
			}
		}
		// otherwise return true if we contain the caret
		return (dot >= asset.start.getOffset() && dot < asset.end.getOffset());
	} //}}}

}
