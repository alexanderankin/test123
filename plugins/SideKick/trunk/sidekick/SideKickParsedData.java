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
	public DefaultTreeModel tree;
	public DefaultMutableTreeNode root;

	//{{{ SideKickParsedData constructor
	public SideKickParsedData(String fileName)
	{
		root = new DefaultMutableTreeNode(fileName);
		tree = new DefaultTreeModel(root);
	} //}}}

	//{{{ getParsedData() method
	public static SideKickParsedData getParsedData(EditPane editPane)
	{
		return (SideKickParsedData)editPane.getClientProperty(
			SideKickPlugin.PARSED_DATA_PROPERTY);
	} //}}}
}
