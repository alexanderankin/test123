/*
 * IAsset.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2005 Matthieu Casanova
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	 See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package sidekick;

//{{{ Imports
import javax.swing.*;
import javax.swing.text.Position;
//}}}

/**
 * A block of code within a file.  Assets correspond to nodes in the
 * Structure Browser and folds in the SideKick folding mode.
 */
public interface IAsset
{
	/**
	 * Returns the icon to be shown for the asset in the structure tree.
	 */
	Icon getIcon();

	/**
	 * Returns a brief description of the asset to be shown in the tree.
	 */
	String getShortString();

	/**
	 * Returns a full description of the asset to be shown in the view's
	 * status bar on when the mouse is over the asset in the tree.
	 */
	String getLongString();

	/**
	 * Returns the name of the Asset.
	 */
	String getName();

	/**
	 * Set the name of the asset
	 */
	void setName(String name);
	
	/**
	 * Set the start position
	 */
	void setStart(Position start);
	
	/**
	 * Returns the starting position.
	 */
	Position getStart();
	
	/**
	 * Set the end position
	 */
	void setEnd(Position end);

	/**
	 * Returns the end position.
	 */
	Position getEnd();
	
}
