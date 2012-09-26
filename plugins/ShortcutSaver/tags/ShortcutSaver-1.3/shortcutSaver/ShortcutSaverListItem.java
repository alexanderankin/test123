/*
 *  ShortcutSaverListItem.java - ShortcutSaver plugin
 *  Copyright (C) 2003 Carmine Lucarelli
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package shortcutSaver;

import common.gui.ListItem;

import org.gjt.sp.jedit.EditAction;

import javax.swing.Icon;

/**
 *  Implementation of CommonControls' ListItem interface
 *
 *@author    <A HREF="mailto:carmine.lucarelli@lombard.ca">Carmine Lucarelli</A>
 */
public class ShortcutSaverListItem implements ListItem
{
	private EditAction action;


	/**
	 *  Constructor for the ShortcutSaverListItem object
	 *
	 *@param  action  The EditAction we're wrapping
	 */
	public ShortcutSaverListItem(EditAction action)
	{
		this.action = action;
	}


	/**
	 *  Returns the label for the item.
	 *
	 *@return    The label value
	 */
	public String getLabel()
	{
		return action.getLabel();
	}


	/**
	 *  Returns the icon for the item.
	 *
	 *@return    The icon value
	 */
	public Icon getIcon()
	{
		return null;
	}

	/**
	 *  Returns the actual item.
	 *
	 *@return    The actualItem value
	 */
	public Object getActualItem()
	{
		return action;
	}
}

