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
import java.util.*;

import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;
//}}}

/**
 * SideKick plugin core which tracks registered parsers.
 * 
 * @version $Id$
 */
public class SideKickPlugin extends EBPlugin
{
	/** The name of the dockable */
	public static final String NAME = "sidekick-tree";
	
	//{{{ Some constants
	public static final String PARSER_PROPERTY = "sidekick.parser";
	public static final String PARSED_DATA_PROPERTY = "sidekick.parsed-data";
	public static final String PARSE_COUNT = "sidekick.parse-count";
	//}}}
	public static final String NONE="None";
	public static final String DEFAULT = "default parser";

	//{{{ Private members
	private static Map<View, SideKick> sidekicks;
	private static Map<String, SideKickParser> parsers;
	private static WorkThreadPool worker;
	private static Set<Buffer> parsedBufferSet;
	
	
	//{{{ start() method
	public void start()
	{
		sidekicks = new HashMap<View, SideKick>();
		parsers = new HashMap<String, SideKickParser>();
		parsedBufferSet = new HashSet<Buffer>();
		View view = jEdit.getFirstView();
		while(view != null)
		{
			initView(view);
			EditPane[] panes = view.getEditPanes();
			for(int i = 0; i < panes.length; i++)
				initTextArea(panes[i].getTextArea());
			view = view.getNext();
		}
		jEdit.addActionSet(SideKickMenuProvider.getParserSwitchers());
		SideKickActions.propertiesChanged();
	} //}}}

	//{{{ stop() method
	public void stop()
	{
		jEdit.removeActionSet(SideKickMenuProvider.getParserSwitchers());
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
		
		Buffer buffer = jEdit.getFirstBuffer();
		while(buffer != null)
		{
			buffer.setProperty(PARSED_DATA_PROPERTY,null);
			buffer = buffer.getNext();
		}
		sidekicks = null;
		parsers = null;
		parsedBufferSet = null;
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
		else if(msg instanceof BufferUpdate)
		{
			BufferUpdate bu = (BufferUpdate)msg;
			if(bu.getWhat() == BufferUpdate.CLOSED)
				finishParsingBuffer(bu.getBuffer());
		}
		else if(msg instanceof PropertiesChanged)
			SideKickActions.propertiesChanged();
		
	} //}}}

	/**
	 * Returns the parser for the given mode.
	 *
	 * @param m the mode (it must not be null)
	 * @return the parser associated to this mode (or null if there is no parser)
	 */
	public static SideKickParser getParserForMode(Mode m) {
		String modeStr = m.getName();
		String propName = "mode." + modeStr + '.' + SideKickPlugin.PARSER_PROPERTY;
		String parserName = jEdit.getProperty(propName);

		if (parserName == null)
			return null;
		
		SideKickParser parser = (SideKickParser) ServiceManager.getService(
			SideKickParser.SERVICE, parserName);
		return parser;
	}
	

	//{{{ getParser() method
	/**
	 * @param name - the name of the parser, as defined in services.xml
	 */
	public static SideKickParser getParser(String name)
	{
		SideKickParser parser = (SideKickParser)ServiceManager
			.getService(SideKickParser.SERVICE,name);
		if(parser != null)
			return parser;
		else
			return parsers.get(name);
	} //}}}

	//{{{ getParserForView() method
	public static SideKickParser getParserForView(View view)
	{
		SideKick sidekick = sidekicks.get(view);
		if(sidekick == null)
			return null;
		else
			return sidekick.getParser();
	} //}}}

	/**
	 * 
	 * @param buffer
	 * @param parserName the new parser we want to use
	 * @since Sidekick 0.6
	 */
	private static String oldName = null;
	public static void setParserForBuffer(Buffer buffer, String parserName) 
	{
		
		oldName = parserName;
		if (parserName.equals(NONE) ) {
			buffer.setStringProperty(PARSER_PROPERTY, parserName);
			return;
		}
		if (parserName.equals(DEFAULT)) {
			buffer.unsetProperty(PARSER_PROPERTY);
			return;
		}
		SideKickParser newParser = getParser(parserName);
		if (newParser != null) {
			buffer.setStringProperty(PARSER_PROPERTY, parserName);
		}
		else throw new RuntimeException("Unknown parser: " + parserName);
	}
	
	//{{{ getParserForBuffer() method
	public static SideKickParser getParserForBuffer(Buffer buffer)
	{
		String parserName = buffer.getStringProperty(PARSER_PROPERTY);
		if(parserName == null || parserName.equals(DEFAULT) || parserName.length() == 0) {
			Mode mode = buffer.getMode();
			if (mode != null) 
				return getParserForMode(mode);
			else return null;
		}
		if (parserName.equals(NONE)) {
			return null;
		}
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
		SideKick sidekick = sidekicks.get(view);
		if (sidekick == null) return;
		// Had to remove this 
		sidekick.setParser(view.getBuffer());
		sidekick.parse(showParsingMessage);
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

	//{{{ isParsingBuffer()
	public static boolean isParsingBuffer(Buffer buffer)
	{
		return parsedBufferSet.contains(buffer);
	} //}}}

	//{{{ Package-private members

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


	//{{{ initView() method
	private static void initView(View view)
	{
		SideKick sideKick = new SideKick(view);
		sidekicks.put(view, sideKick);
		sideKick.parse(true);
	} //}}}

	// {{{ getSideKick() method
	static SideKick getSideKick(View v) {
		return sidekicks.get(v);
	}
	// }}}
	
	//{{{ uninitView() method
	private static void uninitView(View view)
	{
		SideKick sidekick = sidekicks.get(view);
		sidekick.dispose();
		sidekicks.remove(view);
	} //}}}

	
	
	//{{{ initTextArea() method
	private static void initTextArea(JEditTextArea textArea)
	{
		SideKickBindings b = new SideKickBindings();
		textArea.putClientProperty(SideKickBindings.class,b);
		textArea.addKeyListener(b);
	} //}}}

	//{{{ uninitTextArea() method
	private static void uninitTextArea(JEditTextArea textArea)
	{
		SideKickBindings b = (SideKickBindings)
			textArea.getClientProperty(
			SideKickBindings.class);
		textArea.putClientProperty(SideKickBindings.class,null);
		textArea.removeKeyListener(b);
	} //}}}

	//}}}
}
