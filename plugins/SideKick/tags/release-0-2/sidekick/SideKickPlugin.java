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
import java.util.*;
import org.gjt.sp.jedit.buffer.FoldHandler;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.syntax.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;
//}}}

public class SideKickPlugin extends EBPlugin
{
	//{{{ Some constants
	public static final String PARSER_PROPERTY = "sidekick.parser";
	public static final String PARSED_DATA_PROPERTY = "sidekick.parsed-data";
	public static final String PARSE_COUNT = "sidekick.parse-count";
	//}}}

	//{{{ start() method
	public void start()
	{
		FoldHandler.registerFoldHandler(new SideKickFoldHandler());
	} //}}}

	//{{{ createMenuItems() method
	public void createMenuItems(Vector menuItems)
	{
		menuItems.addElement(GUIUtilities.loadMenu("sidekick-menu"));
	} //}}}

	//{{{ createOptionPanes() method
	public void createOptionPanes(OptionsDialog dialog)
	{
		dialog.addOptionPane(new SideKickOptionPane());
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
		else if(msg instanceof PropertiesChanged)
		{
			updateKeyBindings();
			SideKickActions.propertiesChanged();
		}
	} //}}}

	//{{{ registerParser() method
	public static void registerParser(SideKickParser parser)
	{
		parsers.put(parser.getName(),parser);
	} //}}}

	//{{{ unregisterParser() method
	public static void unregisterParser(SideKickParser parser)
	{
		parsers.remove(parser.getName());
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

	//{{{ parse() method
	/**
	 * Immediately begins parsing the current buffer in a background thread.
	 * @param view The view
	 * @param showParsingMessage Clear the tree and show a status message
	 * there?
	 */
	public static void parse(View view, boolean showParsingMessage)
	{
		((SideKick)sidekicks.get(view)).parse(showParsingMessage);
	} //}}}

	//{{{ addWorkRequest() method
	public static void addWorkRequest(Runnable run, boolean inAWT)
	{
		if(worker == null)
		{
			worker = new WorkThreadPool("SideKick",1);
			worker.start();
			for(int i = 0; i < worker.getThreadCount(); i++)
			{
				worker.getThread(i).setPriority(Thread.MIN_PRIORITY);
			}
		}
		worker.addWorkRequest(run,inAWT);
	} //}}}

	//{{{ isParsingBuffer()
	static boolean isParsingBuffer(Buffer buffer)
	{
		return parsedBufferSet.contains(buffer);
	} //}}}

	//{{{ startParsingBuffer()
	static void startParsingBuffer(Buffer buffer)
	{
		parsedBufferSet.add(buffer);
	} //}}}

	//{{{ finishParsingBuffer()
	static void finishParsingBuffer(Buffer buffer)
	{
		parsedBufferSet.remove(buffer);
	} //}}}

	//{{{ Private members
	private static HashMap sidekicks = new HashMap();
	private static HashMap parsers = new HashMap();
	private static WorkThreadPool worker;
	private static HashSet parsedBufferSet = new HashSet();

	private void updateKeyBindings()
	{
		InputHandler ih = jEdit.getInputHandler();

		Iterator iter = parsers.values().iterator();
		while(iter.hasNext())
		{
			SideKickParser parser = (SideKickParser)iter.next();
			if(!parser.supportsCompletion())
				continue;

			String delayPopupTriggerKeys = parser.getDelayCompletionTriggers();
			if(delayPopupTriggerKeys != null)
			{
				for(int i = 0; i < delayPopupTriggerKeys.length(); i++)
				{
					char ch = delayPopupTriggerKeys.charAt(i);
					ih.addKeyBinding(String.valueOf(ch),
						new SideKickActions.CompleteAction(ch));
				}
			}

			String instantPopupTriggerKeys = parser.getInstantCompletionTriggers();
			if(instantPopupTriggerKeys != null)
			{
				for(int i = 0; i < instantPopupTriggerKeys.length(); i++)
				{
					char ch = instantPopupTriggerKeys.charAt(i);
					ih.addKeyBinding(String.valueOf(ch),
						new SideKickActions.CompleteAction(ch));
				}
			}
		}
	} //}}}

	//}}}
}
