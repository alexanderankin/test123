/*
 * SideKickPlugin.java
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Component;
import java.util.HashMap;
import java.util.Vector;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.syntax.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.*;
//}}}

public class SideKickPlugin extends EBPlugin
{
	//{{{ Some constants
	public static final String PARSER_PROPERTY = "sidekick.parser";
	public static final String PARSED_DATA_PROPERTY = "sidekick.parsed-data";
	//}}}

	//{{{ createMenuItems() method
	public void createMenuItems(Vector menuItems)
	{
		menuItems.addElement(GUIUtilities.loadMenu("sidekick-menu"));
	} //}}}

	//{{{ createOptionPanes() method
	public void createOptionPanes(OptionsDialog dialog)
	{
		//grp.addOptionPane(new SideKickOptionPane());
	} //}}}

	//{{{ handleMessage() method
	public void handleMessage(EBMessage msg)
	{
		if(msg instanceof ViewUpdate)
		{
			ViewUpdate vu = (ViewUpdate)msg;
			View view = vu.getView();

			if(vu.getWhat() == ViewUpdate.CREATED)
				sidekicks.put(view,new SideKick(view));
			else if(vu.getWhat() == ViewUpdate.CLOSED)
			{
				SideKick sidekick = (SideKick)sidekicks.get(view);
				sidekick.dispose();
				sidekicks.remove(view);
			}
		}
	} //}}}

	//{{{ getInstance() method
	public static SideKick getInstance(View view)
	{
		return (SideKick)sidekicks.get(view);
	} //}}}

	//{{{ registerParser() method
	public static void registerParser(String name, SideKickParser parser)
	{
		parsers.put(name,parser);
	} //}}}

	//{{{ unregisterParser() method
	public static void unregisterParser(String name)
	{
		parsers.remove(name);
	} //}}}

	//{{{ getParser() method
	public static SideKickParser getParser(String name)
	{
		return (SideKickParser)parsers.get(name);
	} //}}}

	//{{{ getParserForBuffer() method
	public static SideKickParser getParserForBuffer(Buffer buffer)
	{
		String parserName = buffer.getStringProperty(PARSER_PROPERTY);
		if(parserName == null)
			return null;
		else
			return (SideKickParser)parsers.get(parserName);
	} //}}}

	//{{{ Private members
	private static HashMap sidekicks = new HashMap();
	private static HashMap parsers = new HashMap();
	//}}}
}
