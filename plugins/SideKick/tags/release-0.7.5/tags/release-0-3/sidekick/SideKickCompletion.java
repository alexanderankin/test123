/*
 * SideKickCompletion.java
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
import javax.swing.*;
import java.util.*;
//}}}

/**
 * A code completion instance.<p>
 *
 * This is a wrapper around a collection of possible completions, with callbacks
 * for inserting and displaying the completions in a popup menu.
 *
 * @author Slava Pestov
 * @version $Id$
 * @since SideKick 0.3
 */
public abstract class SideKickCompletion
{
	public int size()
	{
		return items.size();
	}

	public String getLongestPrefix()
	{
		return "";
	}

	public Object get(int index)
	{
		return items.get(index);
	}

	public String getCompletionDescription(int index)
	{
		return null;
	}

	public boolean isCompletionSelectable(int index)
	{
		return true;
	}

	public ListCellRenderer getRenderer()
	{
		return new DefaultListCellRenderer();
	}

	public abstract void insert(int index);

	/**
	 * The length of the text being completed (popup will be positioned there).
	 */
	public abstract int getTokenLength();

	/**
	 * @param selectedIndex -1 if the popup is empty, otherwise the index of
	 * the selected completion.
	 * @param keyChar the character typed by the user.
	 */
	public abstract boolean handleKeystroke(int selectedIndex, char keyChar);

	protected List items = new ArrayList();
}
