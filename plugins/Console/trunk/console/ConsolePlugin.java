/*
 * ConsolePlugin.java - Console plugin
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999, 2000, 2001 Slava Pestov
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
import gnu.regexp.REException;
import java.io.*;
import java.net.URL;
import java.util.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;
import errorlist.*;
//}}}

public class ConsolePlugin extends EBPlugin
{
	public static final Shell SYSTEM_SHELL = new SystemShell();
	public static final Shell BEAN_SHELL = new ConsoleBeanShell();

	//{{{ start() method
	public void start()
	{
		// register shells
		Shell.registerShell(SYSTEM_SHELL);
		Shell.registerShell(BEAN_SHELL);

		// load script with useful runCommandInConsole() method
		BufferedReader in = new BufferedReader(
			new InputStreamReader(
			getClass().getResourceAsStream(
			"/console/console.bsh")));

		BeanShell.runScript(null,"console.bsh",in,false,false);

		String settings = jEdit.getSettingsDirectory();
		if(settings != null)
		{
			consoleDirectory = MiscUtilities.constructPath(settings,"console");
			commandoDirectory = MiscUtilities.constructPath(consoleDirectory,"commando");
			File file = new File(commandoDirectory);
			if(!file.exists())
				file.mkdirs();
		}

		consoleToolBarMap = new Hashtable();
		commandoToolBarMap = new Hashtable();
	} //}}}

	//{{{ createMenuItems() method
	public void createMenuItems(Vector menuItems)
	{
		menuItems.addElement(new ConsoleMenu());
	} //}}}

	//{{{ createOptionPanes() method
	public void createOptionPanes(OptionsDialog dialog)
	{
		OptionGroup grp = new OptionGroup("console");
		grp.addOptionPane(new GeneralOptionPane());
		grp.addOptionPane(new ErrorsOptionPane());
		dialog.addOptionGroup(grp);
	} //}}}

	//{{{ handleMessage() method
	public void handleMessage(EBMessage msg)
	{
		if(msg instanceof ViewUpdate)
		{
			ViewUpdate vmsg = (ViewUpdate)msg;
			View view = vmsg.getView();
			if(vmsg.getWhat() == ViewUpdate.CREATED)
			{
				if(jEdit.getBooleanProperty("console.toolbar.enabled"))
				{
					ConsoleToolBar toolBar = new ConsoleToolBar(view);
					consoleToolBarMap.put(view,toolBar);
					view.addToolBar(toolBar);
				}

				if(jEdit.getBooleanProperty("commando.toolbar.enabled"))
				{
					CommandoToolBar toolBar = new CommandoToolBar(view);
					commandoToolBarMap.put(view,toolBar);
					view.addToolBar(toolBar);
				}
			}
			else if(vmsg.getWhat() == ViewUpdate.CLOSED)
			{
				consoleToolBarMap.remove(view);
				commandoToolBarMap.remove(view);
			}
		}
		else if(msg instanceof PropertiesChanged)
			propertiesChanged();
	} //}}}

	//{{{ getConsoleSettingsDirectory() method
	public static String getConsoleSettingsDirectory()
	{
		return consoleDirectory;
	} //}}}

	//{{{ getCommandoDirectory() method
	public static String getCommandoDirectory()
	{
		return commandoDirectory;
	} //}}}

	//{{{ rescanCommandoDirectory() method
	public static void rescanCommandoDirectory()
	{
		commands = null;
		EditBus.send(new CommandoCommandsChanged());
	} //}}}

	//{{{ getCommandoCommands() method
	public static CommandoCommand[] getCommandoCommands()
	{
		if(commands != null)
			return commands;

		Vector vector = new Vector();

		StringTokenizer st = new StringTokenizer(jEdit.getProperty(
			"commando.default"));
		while(st.hasMoreTokens())
		{
			String name = st.nextToken();
			vector.addElement(new CommandoCommand(name.replace('_',' '),
				ConsolePlugin.class.getResource(
				"/console/commando/" + name + ".xml")));
		}

		if(commandoDirectory != null)
		{
			String[] files = new File(commandoDirectory).list();
			if(files != null)
			{
				for(int i = 0; i < files.length; i++)
				{
					String file = files[i];
					if(!file.endsWith(".xml"))
						continue;

					vector.addElement(new CommandoCommand(
						file.substring(0,file.length() - 4)
						.replace('_',' '),
						MiscUtilities.constructPath(
						commandoDirectory,file)));
				}
			}
		}

		commands = new CommandoCommand[vector.size()];
		vector.copyInto(commands);
		MiscUtilities.quicksort(commands,new CommandCompare());

		return commands;
	} //}}}

	//{{{ CommandCompare class
	static class CommandCompare implements MiscUtilities.Compare
	{
		public int compare(Object obj1, Object obj2)
		{
			CommandoCommand cmd1 = (CommandoCommand)obj1;
			CommandoCommand cmd2 = (CommandoCommand)obj2;
			return cmd1.name.compareTo(cmd2.name);
		}
	} //}}}

	//{{{ parseLine() method
	/**
	 * Parses the specified line for errors, and if it contains one,
	 * adds an error to the specified error source.
	 * @param text The line text
	 * @param directory The directory to base relative path names in the
	 * error on
	 * @param errorSource The error source
	 */
	public static synchronized int parseLine(String text, String directory,
		DefaultErrorSource errorSource)
	{
		if(errorMatchers == null)
			loadMatchers();

		if(lastError != null)
		{
			String message = null;
			if(lastMatcher != null &&
				lastMatcher.match(text,directory,errorSource) == null)
				message = lastMatcher.matchExtra(text,directory,errorSource);
			if(message != null)
			{
				lastError.addExtraMessage(message);
				return lastError.getErrorType();
			}
			else
			{
				errorSource.addError(lastError);
				lastMatcher = null;
				lastError = null;
			}
		}

		for(int i = 0; i < errorMatchers.length; i++)
		{
			ErrorMatcher m = errorMatchers[i];
			DefaultErrorSource.DefaultError error
				= m.match(text,directory,errorSource);
			if(error != null)
			{
				lastError = error;
				lastMatcher = m;
				return error.getErrorType();
			}
		}

		return -1;
	} //}}}

	//{{{ finishErrorParsing() method
	/**
	 * This should be called after all lines to parse have been handled.
	 * It handles the corner case where the last line parsed was an
	 * extra message.
	 * @param errorSource The error source
	 */
	public static synchronized void finishErrorParsing(DefaultErrorSource errorSource)
	{
		if(lastError != null)
		{
			errorSource.addError(lastError);
			lastError = null;
			lastMatcher = null;
		}
	} //}}}

	//{{{ Package-private members

	//{{{ getErrorMatchers() method
	static ErrorMatcher[] getErrorMatchers()
	{
		if(errorMatchers == null)
			loadMatchers();

		return errorMatchers;
	} //}}}

	//{{{ loadMatchers() method
	static void loadMatchers()
	{
		lastMatcher = null;

		Vector vec = new Vector();

		loadMatchers(true,jEdit.getProperty("console.error.user"),vec);
		loadMatchers(false,jEdit.getProperty("console.error.default"),vec);

		errorMatchers = new ErrorMatcher[vec.size()];
		vec.copyInto(errorMatchers);
	} //}}}

	//{{{ loadMatchers() method
	static void loadMatchers(boolean user, String list, Vector vec)
	{
		if(list == null)
			return;

		StringTokenizer st = new StringTokenizer(list);

		while(st.hasMoreTokens())
		{
			loadMatcher(user,st.nextToken(),vec);
		}
	} //}}}

	//{{{ loadMatcher() method
	static void loadMatcher(boolean user, String internalName, Vector vec)
	{
		String name = jEdit.getProperty("console.error." + internalName + ".name");
		String match = jEdit.getProperty("console.error." + internalName + ".match");
		String warning = jEdit.getProperty("console.error." + internalName + ".warning");
		String extra = jEdit.getProperty("console.error." + internalName + ".extra");
		String filename = jEdit.getProperty("console.error." + internalName + ".filename");
		String line = jEdit.getProperty("console.error." + internalName + ".line");
		String message = jEdit.getProperty("console.error." + internalName + ".message");

		try
		{
			ErrorMatcher matcher = new ErrorMatcher(user,internalName,
				name,match,warning,extra,filename,line,message);
			vec.addElement(matcher);
		}
		catch(Exception re)
		{
			Log.log(Log.ERROR,ConsolePlugin.class,
				"Invalid regexp in matcher " + internalName);
			Log.log(Log.ERROR,ConsolePlugin.class,re);
		}
	} //}}}

	//}}}

	//{{{ Private members

	//{{{ Instance variables
	private static ErrorMatcher[] errorMatchers;
	private static ErrorMatcher lastMatcher;
	private static DefaultErrorSource.DefaultError lastError;
	private static String consoleDirectory;
	private static String commandoDirectory;
	private static CommandoCommand[] commands;

	private Hashtable consoleToolBarMap;
	private Hashtable commandoToolBarMap;
	//}}}

	//{{{ propertiesChanged() method
	private void propertiesChanged()
	{
		// lazily load the matchers the next time they are
		// needed
		errorMatchers = null;

		//{{{ Show console tool bar...
		if(jEdit.getBooleanProperty("console.toolbar.enabled"))
		{
			View[] views = jEdit.getViews();
			for(int i = 0; i < views.length; i++)
			{
				View view = views[i];
				if(!consoleToolBarMap.containsKey(view))
				{
					ConsoleToolBar toolBar = new ConsoleToolBar(view);
					consoleToolBarMap.put(view,toolBar);
					view.addToolBar(toolBar);
				}
			}
		}
		//}}}
		//{{{ Hide console tool bar...
		else
		{
			Enumeration enum = consoleToolBarMap.keys();
			while(enum.hasMoreElements())
			{
				View view = (View)enum.nextElement();
				ConsoleToolBar toolBar = (ConsoleToolBar)
					consoleToolBarMap.get(view);
				view.removeToolBar(toolBar);
			}

			consoleToolBarMap.clear();
		} //}}}

		//{{{ Show commando tool bar...
		if(jEdit.getBooleanProperty("commando.toolbar.enabled"))
		{
			View[] views = jEdit.getViews();
			for(int i = 0; i < views.length; i++)
			{
				View view = views[i];
				if(!commandoToolBarMap.containsKey(view))
				{
					CommandoToolBar toolBar = new CommandoToolBar(view);
					commandoToolBarMap.put(view,toolBar);
					view.addToolBar(toolBar);
				}
			}
		} //}}}
		//{{{ Hide commando tool bar...
		else
		{
			Enumeration enum = commandoToolBarMap.keys();
			while(enum.hasMoreElements())
			{
				View view = (View)enum.nextElement();
				CommandoToolBar toolBar = (CommandoToolBar)
					commandoToolBarMap.get(view);
				view.removeToolBar(toolBar);
			}

			commandoToolBarMap.clear();
		} //}}}

		// lazily load aliases and variables next time system
		// shell is used
		SystemShell.propertiesChanged();
	} //}}}

	//}}}
}
