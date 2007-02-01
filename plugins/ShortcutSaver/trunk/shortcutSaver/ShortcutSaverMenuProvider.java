/*
 * ShortcutSaverMenuProvider.java - ShortcutSaver menu
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2007 Carmine Lucarelli
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

package shortcutSaver;

//{{{ Imports
import javax.swing.*;
import org.gjt.sp.jedit.menu.DynamicMenuProvider;
import org.gjt.sp.jedit.menu.EnhancedMenuItem;
import org.gjt.sp.jedit.*;
//}}}

public class ShortcutSaverMenuProvider implements DynamicMenuProvider
{
	//{{{ updateEveryTime() method
	public boolean updateEveryTime()
	{
		return false;
	} //}}}

	//{{{ update() method
	public void update(JMenu superMenu)
	{
		for(int i = 1; i <= 20; i++)
		{
			String item = jEdit.getProperty("shortcutSaver.ActionLabel" + i);
			if(item != null)
				superMenu.add(new EnhancedMenuItem(item, "shortcutSaver-action" + i,
					jEdit.getActionContext()));
			else
				superMenu.add(GUIUtilities.loadMenuItem("shortcutSaver-action" + i));
		}
	} //}}}
}
