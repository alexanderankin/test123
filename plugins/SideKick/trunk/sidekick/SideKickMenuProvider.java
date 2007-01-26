/*
 * SideKickMenuProvider.java - SideKick Parser Menu
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2006 Alan Ezust
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
import org.gjt.sp.jedit.menu.DynamicMenuProvider;
import org.gjt.sp.jedit.*;
//}}}

// {{{ SideKickMenuProvider class
public class SideKickMenuProvider implements DynamicMenuProvider
{
	static ActionSet parserSwitchers = null;
	
	// {{{ getParserSwitchers() method
	/**
	  @return an ActionSet for switching the to each of 
	  the currently available SideKick parsers via the
	  combo box.  */
	static public ActionSet getParserSwitchers() {
		if (parserSwitchers == null) {
			parserSwitchers = new ActionSet("Plugin: SideKick - Parsers");
		}
		parserSwitchers.removeAllActions();
		String[] serviceNames = ServiceManager.getServiceNames(SideKickParser.SERVICE);
		for (String svcName: serviceNames) {
			EditAction ea = new ParserSwitchAction(svcName);
			parserSwitchers.addAction(ea);
		}
		return parserSwitchers;
	} // }}}

	//{{{ update() method	
	public void update(JMenu superMenu)
	{
		JMenu menu = new JMenu("Parsers");
		for (EditAction ea: getParserSwitchers().getActions()) {
			menu.add(GUIUtilities.loadMenuItem(ea, false));
		}
		superMenu.add(menu);
	} //}}}
		
	//{{{ updateEveryTime() method
	public boolean updateEveryTime()
	{
		return false;
	} //}}}

} // }}}