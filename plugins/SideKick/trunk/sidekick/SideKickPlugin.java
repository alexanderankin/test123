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
import errorlist.DefaultErrorSource;
import javax.swing.*;
import java.util.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.syntax.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;
//}}}

/**
 * SideKick plugin core which tracks registered parsers.
 */
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
		View view = jEdit.getFirstView();
		while(view != null)
		{
			initView(view);
			EditPane[] panes = view.getEditPanes();
			for(int i = 0; i < panes.length; i++)
				initTextArea(panes[i].getTextArea());
			view = view.getNext();
		}
		SideKickActions.propertiesChanged();
	} //}}}

	//{{{ stop() method
	public void stop()
	{
		View view = jEdit.getFirstView();
		while(view != null)
		{
			uninitView(view);
			SideKickParsedData.setParsedData(view,null);

			EditPane[] panes = view.getEditPanes();
			for(int i = 0; i < panes.length; i++)
				uninitTextArea(panes[i].getTextArea());
			view = view.getNext();
		}
	} //}}}

	//{{{ handleMessage() method
	public void handleMessage(EBMessage msg)
	{
		if(msg instanceof ViewUpdate)
		{
			ViewUpdate vu = (ViewUpdate)msg;
			View view = vu.getView();

			if(vu.getWhat() == ViewUpdate.CREATED)
				initView(view);
			else if(vu.getWhat() == ViewUpdate.CLOSED)
				uninitView(view);
		}
		else if(msg instanceof EditPaneUpdate)
		{
			EditPaneUpdate epu = (EditPaneUpdate)msg;
			EditPane editPane = epu.getEditPane();

			if(epu.getWhat() == EditPaneUpdate.CREATED)
				initTextArea(editPane.getTextArea());
			else if(epu.getWhat() == EditPaneUpdate.DESTROYED)
				uninitTextArea(editPane.getTextArea());
		}
		else if(msg instanceof PropertiesChanged)
			SideKickActions.propertiesChanged();
	} //}}}

	//{{{ registerParser() method
	/**
	 * @deprecated Write a <code>services.xml</code> file instead.
	 * @see SideKickParser
	 */
	public static void registerParser(SideKickParser parser)
	{
		parsers.put(parser.getName(),parser);
	} //}}}

	//{{{ unregisterParser() method
	/**
	 * @deprecated Write a <code>services.xml</code> file instead.
	 * @see SideKickParser
	 */
	public static void unregisterParser(SideKickParser parser)
	{
		parsers.remove(parser.getName());
	} //}}}

	//{{{ getParser() method
	public static SideKickParser getParser(String name)
	{
		SideKickParser parser = (SideKickParser)ServiceManager
			.getService(SideKickParser.SERVICE,name);
		if(parser != null)
			return parser;
		else
			return (SideKickParser)parsers.get(name);
	} //}}}

	//{{{ getParserForView() method
	public static SideKickParser getParserForView(View view)
	{
		SideKick sidekick = (SideKick)sidekicks.get(view);
		if(sidekick == null)
			return null;
		else
			return sidekick.getParser();
	} //}}}

	//{{{ getParserForBuffer() method
	public static SideKickParser getParserForBuffer(Buffer buffer)
	{
		String parserName = buffer.getStringProperty(PARSER_PROPERTY);
		if(parserName == null)
			return null;
		else
			return getParser(parserName);
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

	//{{{ getErrorSource() method
	/**
	 * Returns the error source used by the given view.
	 * @param view The view
	 * @since SideKick 0.3
	 */
	public static DefaultErrorSource getErrorSource(View view)
	{
		return ((SideKick)sidekicks.get(view)).getErrorSource();
	} //}}}

	//{{{ addWorkRequest() method
	public static void addWorkRequest(Runnable run, boolean inAWT)
	{
		if(worker == null)
		{
			worker = new WorkThreadPool("SideKick",1);
			worker.start();
		}
		worker.addWorkRequest(run,inAWT);
	} //}}}

	//{{{ Package-private members

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

	//}}}

	//{{{ Private members
	private static HashMap sidekicks = new HashMap();
	private static HashMap parsers = new HashMap();
	private static WorkThreadPool worker;
	private static HashSet parsedBufferSet = new HashSet();

	//{{{ initView() method
	private void initView(View view)
	{
		sidekicks.put(view,new SideKick(view));
	} //}}}

	//{{{ uninitView() method
	private void uninitView(View view)
	{
		SideKick sidekick = (SideKick)sidekicks.get(view);
		sidekick.dispose();
		sidekicks.remove(view);
	} //}}}

	//{{{ initTextArea() method
	private void initTextArea(JEditTextArea textArea)
	{
		SideKickBindings b = new SideKickBindings();
		textArea.putClientProperty(SideKickBindings.class,b);
		textArea.addKeyListener(b);
	} //}}}

	//{{{ uninitTextArea() method
	private void uninitTextArea(JEditTextArea textArea)
	{
		SideKickBindings b = (SideKickBindings)
			textArea.getClientProperty(
			SideKickBindings.class);
		textArea.putClientProperty(SideKickBindings.class,null);
		textArea.removeKeyListener(b);
	} //}}}

	//}}}
}
