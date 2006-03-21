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
