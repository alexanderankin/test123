/*
 * Asset.java
 *
 * IMPORTED FROM JEDIT SIDEKICK FOR CROSS IDE COMPATIBILITY
 * PURPOSES
 *
 * Copyright (C) 2000, 2003 Slava Pestov
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

package org.jymc.jpydebug.jedit ;

//{{{ Imports
import javax.swing.text.Position;
import javax.swing.Icon;
//}}}

/**
 * A block of code within a file.  Assets correspond to nodes in the 
 * Structure Browser and folds in the SideKick folding mode.
 */
public abstract class JpyDbgAsset
{
	//{{{ Instance variables
	public String name;
	public Position start, end;
	//}}}

	//{{{ Asset constructor
	public JpyDbgAsset(String name)
	{
		this.name = name;
	} //}}}

	/**
	 * Returns the icon to be shown for the asset in the structure tree.
	 */
	public abstract Icon getIcon();

	/**
	 * Returns a brief description of the asset to be shown in the tree.
	 */
	public abstract String getShortString();

	/**
	 * Returns a full description of the asset to be shown in the view's
	 * status bar on when the mouse is over the asset in the tree.
	 */
	public abstract String getLongString();
}
