/*
 * ConsolePlugin.java - Console plugin
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

import gnu.regexp.REException;
import java.io.*;
import java.net.URL;
import java.util.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;

public class ConsolePlugin extends EBPlugin
{
	public static final Shell SYSTEM_SHELL = new SystemShell();
	public static final Shell BEAN_SHELL = new ConsoleBeanShell();

	public void start()
	{
		// register shells
		Shell.registerShell(SYSTEM_SHELL);
		Shell.registerShell(BEAN_SHELL);

		// register dockable window
		EditBus.addToNamedList(DockableWindow.DOCKABLE_WINDOW_LIST,"console");

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
	}

	public void createMenuItems(Vector menuItems)
	{
		menuItems.addElement(new ConsoleMenu());
	}

	public void createOptionPanes(OptionsDialog dialog)
	{
		dialog.addOptionPane(new ConsoleOptionPane());
	}

	public void handleMessage(EBMessage msg)
	{
		if(msg instanceof CreateDockableWindow)
		{
			CreateDockableWindow cmsg = (CreateDockableWindow)msg;
			if(cmsg.getDockableWindowName().equals("console"))
				cmsg.setDockableWindow(new Console(cmsg.getView()));
		}
		else if(msg instanceof ViewUpdate)
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
	}

	public static String getConsoleSettingsDirectory()
	{
		return consoleDirectory;
	}

	public static String getCommandoDirectory()
	{
		return commandoDirectory;
	}

	public static void rescanCommandoDirectory()
	{
		commands = null;
		EditBus.send(new CommandoCommandsChanged());
	}

	public static CommandoCommand[] getCommandoCommands()
	{
		if(commands != null)
			return commands;

		Vector vector = new Vector();

		StringTokenizer st = new StringTokenizer(jEdit.getProperty(
			"commando.built-ins"));
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
	}

	static class CommandCompare implements MiscUtilities.Compare
	{
		public int compare(Object obj1, Object obj2)
		{
			CommandoCommand cmd1 = (CommandoCommand)obj1;
			CommandoCommand cmd2 = (CommandoCommand)obj2;
			return cmd1.name.compareTo(cmd2.name);
		}
	}

	// package-private members
	static synchronized int parseLine(String text, String directory,
		DefaultErrorSource errorSource)
	{
		if(errorMatchers == null)
			loadMatchers();

		for(int i = 0; i < errorMatchers.length; i++)
		{
			ErrorMatcher m = errorMatchers[i];
			int type = m.match(text,directory,errorSource);
			if(type != -1)
				return type;
		}

		return -1;
	}

	static void loadMatchers()
	{
		Vector vector = new Vector();
		int i = 0;
		String match;
		while((match = jEdit.getProperty("console.error." + i + ".match")) != null)
		{
			String name = jEdit.getProperty("console.error." + i + ".name");
			String filename = jEdit.getProperty("console.error." + i + ".filename");
			String line = jEdit.getProperty("console.error." + i + ".line");
			String message = jEdit.getProperty("console.error." + i + ".message");

			try
			{
				ErrorMatcher matcher = new ErrorMatcher(name,match,
					filename,line,message);
				vector.addElement(matcher);
			}
			catch(REException re)
			{
				Log.log(Log.ERROR,ConsolePlugin.class,
					"Invalid regexp: " + match);
				Log.log(Log.ERROR,ConsolePlugin.class,re);
			}

			i++;
		}

		errorMatchers = new ErrorMatcher[vector.size()];
		vector.copyInto(errorMatchers);
	}

	// private members
	private static ErrorMatcher[] errorMatchers;
	private static String consoleDirectory;
	private static String commandoDirectory;
	private static CommandoCommand[] commands;

	private Hashtable consoleToolBarMap;
	private Hashtable commandoToolBarMap;

	private void propertiesChanged()
	{
		// lazily load the matchers the next time they are
		// needed
		errorMatchers = null;

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
		}

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
		}
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
		}
	}
}
