/*
 * ConsoleMenu.java - Console menu
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2001 Slava Pestov
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

package console;

//{{{ Imports
import console.commando.CommandoCommandsChanged;
import javax.swing.event.MenuEvent;
import javax.swing.*;
import java.awt.event.*;
import org.gjt.sp.jedit.gui.EnhancedMenu;
import org.gjt.sp.jedit.*;
//}}}

class ConsoleMenu extends EnhancedMenu
{
	//{{{ ConsoleMenu
	ConsoleMenu()
	{
		super("console-menu");
	} //}}}

	//{{{ menuSelected() method
	public void menuSelected(MenuEvent evt)
	{
		super.menuSelected(evt);
		updateMenu();
	} //}}}

	//{{{ updateMenu() method
	private void updateMenu()
	{
		for(int i = getMenuComponentCount() - 1; i >= 0; i--)
		{
			if(getMenuComponent(i) instanceof JSeparator)
				break;
			else
				remove(i);
		}

		EditAction[] commands = ConsolePlugin.getCommandoCommands();
		for(int i = 0; i < commands.length; i++)
		{
			add(GUIUtilities.loadMenuItem(commands[i].getName()));
		}
	} //}}}
}
