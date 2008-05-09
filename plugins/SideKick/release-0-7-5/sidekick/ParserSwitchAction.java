/*
 * ParserSwitchAction.java - Action to switch parsers in SideKick.
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, * USA.
 */
package sidekick;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DockableWindowManager;


/**
 * An action to switch the current SideKick parser.
 */
public class ParserSwitchAction extends EditAction
{

	String parserName ;
	public ParserSwitchAction(String parser) 
	{
		super("sidekick.parser." + parser + "-switch");
		parserName = parser;
	}
	public String getLabel() 
	{
		return parserName;
	}
	public String getCode()
	{
		return "new sidekick.ParserSwitchAction(\"" + parserName + "\").invoke(view)";
	}
	
	public void invoke(View view)
	{
		DockableWindowManager wm = view.getDockableWindowManager();
		wm.showDockableWindow("sidekick-tree");
		SideKick sk = SideKickPlugin.getSideKick(view);
		Buffer b = view.getBuffer();
		SideKickPlugin.setParserForBuffer(b, parserName);
		sk.parse(true);
		
	}

}
