/*
 * ConsolePlugin.java - Console plugin
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999, 2004 Slava Pestov
 * Portions copyright (C) 1999, 2000 Kevin A. Burton
 * Revised  (c) 2005 by Alan Ezust
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

// {{{ Imports
import java.io.File;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.msg.DynamicMenuChanged;
import org.gjt.sp.jedit.msg.ViewUpdate;
import org.gjt.sp.util.Log;

import console.commando.CommandoCommand;
import console.commando.CommandoToolBar;
import console.utils.StringList;
import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;

// }}}

/**
 * ConsolePlugin 
 *
 * @version  $Id$
 */

public class ConsolePlugin extends EBPlugin
{
	// {{{ static final Members
	public static final String MENU = "plugin.console.ConsolePlugin.menu";

	public static final String CMD_PATH = "/console/bsh/";

	/**
	 * Return value of {@link #parseLine()} if the text does not match a
	 * known error pattern.
	 */
	public static final int NO_ERROR = -1;
	// }}} static final Members

	// {{{ getSystemShell() method
	public static SystemShell getSystemShell()
	{
		return (SystemShell) ServiceManager.getService("console.Shell", "System");
	} // }}}

	// {{{ getAllCommands() 
	/**
	   @return all commands that are represented as
	           .xml commando files. 
	   */ 
	public static ActionSet getAllCommands()
	{
		return allCommands;
	}
	// }}}
	
	// {{{ start() method
	public void start()
	{
		projectTreeListener = null;
		BeanShell.getNameSpace().addCommandPath(CMD_PATH, getClass());
		// systemCommandDirectory = MiscUtilities.constructPath(".",
		// "commando");

		String settings = jEdit.getSettingsDirectory();
		if (settings != null)
		{
			consoleDirectory = MiscUtilities.constructPath(settings, "console");

			userCommandDirectory = MiscUtilities.constructPath(consoleDirectory,
				"commando");
			File file = new File(userCommandDirectory);
			if (!file.exists())
				file.mkdirs();
		}
		allCommands = new ActionSet("Commando Commands");
		rescanCommands();
		jEdit.addActionSet(allCommands);
		CommandoToolBar.init();


	} // }}}
	
	//{{{
	/** parseLine() 
	 * Publicly documented class for parsing output of user defined
	 * shells and obtaining the error color. 
	 * 
	 * @return -1 if no error/warning, or an ErrorType. 
	 *      Possible values are:
	 * 	@ref ErrorSource.ERROR
	 * 	@ref ErrorSource.WARNING
	 * 	
	 * Although it is possible derived ErrorSources will return custom error codes.
	 *  
	 */
	public static synchronized int parseLine(View view,
		String text, String directory, DefaultErrorSource errorSource)
	{
		if (errorMatchers == null) loadErrorMatchers();
		Console c = getConsole(view);
		CommandOutputParser parser = getParser(view, directory, errorSource);
		return parser.processLine(text, false); // CHECK - should this be false or true?
	} //}}}

	private static HashMap<View, CommandOutputParser> sm_parsers;
	
	private static CommandOutputParser getParser(View v, String dir, DefaultErrorSource es) {
		if (sm_parsers == null) {
			sm_parsers = new HashMap<View, CommandOutputParser>();
		}
		CommandOutputParser retval = sm_parsers.get(v);
		if (retval == null) {
			retval = new CommandOutputParser(v, es);
			sm_parsers.put(v, retval);
		}
		retval.setDirectory(dir);
		return retval;
	}
	
	// {{{ stop() method
	public void stop()
	{
		BeanShell.getNameSpace().addCommandPath(CMD_PATH, getClass());
		CommandoToolBar.remove();
		jEdit.removeActionSet(allCommands);
	} // }}}

	// {{{ handleMessage() method
	public void handleMessage(EBMessage msg)
	{
		if (msg instanceof ViewUpdate)
		{
			ViewUpdate vmsg = (ViewUpdate) msg;
			if (vmsg.getWhat() == ViewUpdate.CREATED)
			{
				View v = vmsg.getView();
				CommandoToolBar.create(v);
			}
		}
	}
	// }}}

	
	// {{{ getConsoleSettingsDirectory() method
	public static String getConsoleSettingsDirectory()
	{
		return consoleDirectory;
	} 
	// }}}

	// {{{ scanDirectory
	/**
	 * Given a filename, performs translations so that it's a command name
	 */

	public static void scanDirectory(String directory)
	{
		if (directory != null)
		{
			File[] files = new File(directory).listFiles();
			if (files != null)
			{
				for (int i = 0; i < files.length; i++)
				{
					File file = files[i];
					String fileName = file.getAbsolutePath();
					if (!fileName.endsWith(".xml") || file.isHidden())
						continue;
					EditAction action = CommandoCommand.create(fileName);
					allCommands.addAction(action);
				}
			}
		}
	}
	// }}}

	//{{{ scanJarFile() 

	public static void scanJarFile()
	{
		
		// TODO: scan contents of a resource directory instead of using this property
		String defaultCommands = jEdit.getProperty("commando.default");
		StringList sl = StringList.split(defaultCommands, " "); 
		for (String name: sl) {
			String key = "commando." + name;
			if (allCommands.contains(key)) 
				continue;

			String resourceName = "/console/commands/" + name + ".xml";
			// System.out.println ("GetResource: " + resourceName);
			URL url = Console.class.getResource(resourceName);
			if (url != null)
			{
				EditAction action = CommandoCommand.create(url);
				allCommands.addAction(action);
			}
			else
			{
				Log.log(Log.ERROR, "ConsolePlugin", "Unable to access resource: "
					+ resourceName);
			}
		}
	}
	// }}}
	
	// {{{ rescanCommands() 
	/** Grabs the commando files from the jar file as well as user settings.
	 * 
	 */
	public static void rescanCommands()
	{
		/*
		if (allCommands.size() > 1)
			return; */
		allCommands.removeAllActions();
		scanDirectory(userCommandDirectory);
		scanJarFile();

		/* Code duplication from jEdit.initKeyBindings() is bad, but
		   otherwise invoking 'rescan commando directory' will leave
		   old actions in the input handler 
		   */
		EditAction[] ea = allCommands.getActions();
		for (int i = 0; i < ea.length; ++i)
		{
			String shortcut1 = jEdit.getProperty(ea[i].getName() + ".shortcut");
			if (shortcut1 != null)
				jEdit.getInputHandler().addKeyBinding(shortcut1, ea[i]);

			String shortcut2 = jEdit.getProperty(ea[i].getName() + ".shortcut2");
			if (shortcut2 != null)
				jEdit.getInputHandler().addKeyBinding(shortcut2, ea[i]);
		}
		Log.log(Log.DEBUG, ConsolePlugin.class, "Loaded " + allCommands.size()
				+ " Actions");
		EditBus.send(new DynamicMenuChanged(MENU));
	} // }}}

	// {{{ getCommandoCommands() method
	public static EditAction[] getCommandoCommands()
	{
		EditAction[] commands = allCommands.getActions();
		MiscUtilities.quicksort(commands, new ActionCompare());
		return commands;
	} // }}}

	// {{{ compile() method
	public static void compile(View view, Buffer buffer)
	{
		String compiler = buffer.getStringProperty("commando.compile");
		if (compiler == null || compiler.length() == 0)
		{
			GUIUtilities.error(view, "commando.no-compiler", null);
			return;
		}

		CommandoCommand command = (CommandoCommand) allCommands.getAction("commando."
			+ compiler);
		if (command == null)
		{
			GUIUtilities.error(view, "commando.no-command", new String[] { compiler });
		}
		else
		{
			if (buffer.isDirty())
			{
				Object[] args = { buffer.getName() };
				int result = GUIUtilities.confirm(view,
					"commando.not-saved-compile", args,
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE);
				if (result == JOptionPane.YES_OPTION)
				{
					if (!buffer.save(view, null, true))
						return;
				}
				else if (result != JOptionPane.NO_OPTION)
					return;
			}

			command.invoke(view);
		}
	} // }}}

	static public Console getConsole(View v) {
		DockableWindowManager dwm = v.getDockableWindowManager();
		return (Console) dwm.getDockable("console");
	}
	
	// {{{ run() method
	public static void run(View view, Buffer buffer)
	{
		String interpreter = buffer.getStringProperty("commando.run");
		if (interpreter == null || interpreter.length() == 0)
		{
			GUIUtilities.error(view, "commando.no-interpreter", null);
			return;
		}

		CommandoCommand command = (CommandoCommand) allCommands.getAction("commando."
			+ interpreter);
		if (command == null)
		{
			GUIUtilities.error(view, "commando.no-command",
				new String[] { interpreter });
		}
		else
		{
			if (buffer.isDirty())
			{
				Object[] args = { buffer.getName() };
				int result = GUIUtilities.confirm(view, "commando.not-saved-run",
					args, JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE);
				if (result == JOptionPane.YES_OPTION)
				{
					if (!buffer.save(view, null, true))
						return;
				}
				else if (result != JOptionPane.NO_OPTION)
					return;
			}

			command.invoke(view);
		}
	} // }}}

	// {{{ getPackageName() method
	/**
	 * A utility method that returns the name of the package containing the
	 * current buffer.
	 * note: these might not be needed anymore as of 4.3pre3
	 * @param buffer The buffer
	 */
	public static String getPackageName(Buffer buffer)
	{
		StringReader in = new StringReader(buffer.getText(0, buffer.getLength()));

		try
		{
			StreamTokenizer stok = new StreamTokenizer(in);

			// set tokenizer to skip comments
			stok.slashStarComments(true);
			stok.slashSlashComments(true);

			while (stok.nextToken() != StreamTokenizer.TT_EOF)
			{
				if (stok.sval == null)
					continue;
				if (stok.sval.equals("package"))
				{
					stok.nextToken();
					in.close();
					return stok.sval;
				}
				else if (stok.sval.equals("class"))
				{
					in.close();
					return null;
				}
			}

			in.close();
		}
		catch (IOException io)
		{
			// can't happen
			throw new InternalError();
		}

		return null;
	} // }}}

	// {{{ getClassName() method
	
	/**
	 * Returns the name of the specified buffer without the extension,
	 * appended to the buffer's package name.
	 * note: this might not be needed with the new JARClassloader 
	 * 
	 * @param buffer
	 *                The buffer
	 */
	public static String getClassName(Buffer buffer)
	{
		String pkg = getPackageName(buffer);
		String clazz = MiscUtilities.getFileNameNoExtension(buffer.getPath());
		if (pkg == null)
			return clazz;
		else
			return pkg + '.' + clazz;
	} // }}}

	// {{{ getPackageRoot() method
	/**
	 * Returns the directory containing the root of the package of the
	 * current buffer. For example, if the buffer is located in
	 * <code>/home/slava/Stuff/example/Example.java</code> and contains a
	 * <code>package example</code> statement, this method will return
	 * <code>/home/slava/Stuff</code>.
	 * 
	 * @param buffer
	 *                The buffer
	 */
	public static String getPackageRoot(Buffer buffer)
	{
		String pkg = getPackageName(buffer);
		String path = MiscUtilities.getParentOfPath(buffer.getPath());
		if (path.endsWith(File.separator))
			path = path.substring(0, path.length() - 1);

		if (pkg == null)
			return path;

		pkg = pkg.replace('.', File.separatorChar);
		if (path.endsWith(pkg))
			return path.substring(0, path.length() - pkg.length());
		else
			return path;
	} // }}}

	// {{{ expandSystemShellVariables() method
	/**
	 * Expands embedded environment variables in the same manner as the
	 * system shell.
	 * 
	 * @param view
	 *                The view
	 * @param text
	 *                The string to expand
	 */
	public static String expandSystemShellVariables(View view, String text)
	{
		return getSystemShell().expandVariables(view, text);
	} // }}}

	// {{{ getSystemShellVariableValue() method
	/**
	 * Returns the value of the specified system shell environment variable.
	 * 
	 * @param view
	 *                The view
	 * @param var
	 *                The variable name
	 */
	public static String getSystemShellVariableValue(View view, String var)
	{
		return getSystemShell().getVariableValue(view, var);
	} // }}}

	// {{{ setSystemShellVariableValue() method
	/**
	 * Sets the value of the specified system shell environment variable.
	 * 
	 * @param view
	 *                The view
	 * @param var
	 *                The variable name
	 * @param value
	 *                The value
	 */
	public static void setSystemShellVariableValue(String var, String value)
	{
		getSystemShell().getVariables().put(var, value);
	} // }}}

	// {{{ ActionCompare class
	static class ActionCompare implements MiscUtilities.Compare
	{
		public int compare(Object obj1, Object obj2)
		{
			EditAction a1 = (EditAction) obj1;
			EditAction a2 = (EditAction) obj2;
			return a1.getLabel().compareTo(a2.getLabel());
		}
	} // }}}
	 
	// {{{ getErrorMatchers() method
	public static ErrorMatcher[] getErrorMatchers()
	{
		if (errorMatchers == null)
		{
			loadErrorMatchers();
		}
		return errorMatchers;
	} // }}}

	// {{{ getUserCommandDirectory()
	public static String getUserCommandDirectory()
	{
		return userCommandDirectory;
	}
	// }}}

	// {{{ public static loadErrorMatchers() method
	public static ErrorMatcher[] loadErrorMatchers()
	{
		// lastMatcher = null;
		// Vector vec = new Vector();
		LinkedHashMap<String, ErrorMatcher> map = new LinkedHashMap<String, ErrorMatcher>();
		StringList userMatchers = StringList.split(jEdit.getProperty("console.error.user",
			""), "\\s+");
		StringList defaultMatchers = StringList.split(jEdit.getProperty(
			"console.error.default", ""), "\\s+");
		loadMatchers(true, userMatchers, map);
		loadMatchers(false, defaultMatchers, map);
		int i = 0;
		errorMatchers = new ErrorMatcher[map.size()];
		for (ErrorMatcher m : map.values())
		{
			errorMatchers[i++] = m;
		}
		return errorMatchers;
		// errorMatchers = new ErrorMatcher[values.length];
		// vec.copyInto(errorMatchers);
	} // }}}
	
	// {{{ Instance and static variables
	private static ErrorMatcher[] errorMatchers = null;

	private static String consoleDirectory;

	private static String userCommandDirectory;

	private static ActionSet allCommands;
	
	static View view = null;
	private static ErrorMatcher lastMatcher;
	private static DefaultErrorSource.DefaultError lastError;

	private Class projectTreeListener;
	static CommandoToolBar toolBar = null;

	// }}}
	

	// {{{ private loadMatchers()
	/**
	 * Loads and tests matchers from values in jEdit properties.
	 * 
	 * @param user
	 *                a flag to determine if the matcher is userdefined or
	 *                not
	 * @param names
	 *                a list of error matcher names to load
	 * @param map
	 *                a map a map of error matchers to load into
	 */
	private static void loadMatchers(boolean user, StringList names,
		Map<String, ErrorMatcher> map)
	{
		if ((names == null) || names.size() == 0)
			return;
		for (String key : names)
		{
			if (key == null || key.equals("null"))
				continue;
			if (map.containsKey(key))
				continue;
			ErrorMatcher newMatcher = ErrorMatcher.bring(key);
			if (!newMatcher.isValid())
				continue;
			newMatcher.user = user;
			map.put(key, newMatcher);
		}
	}
	// }}}
} // }}}

