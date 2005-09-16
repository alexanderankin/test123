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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.ActionSet;
import org.gjt.sp.jedit.BeanShell;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.DynamicMenuChanged;
import org.gjt.sp.jedit.msg.ViewUpdate;
import org.gjt.sp.util.Log;

import console.commando.CommandoCommand;
import console.commando.CommandoToolBar;
import console.options.ToolBarOptionPane;
import console.utils.StringList;
import errorlist.DefaultErrorSource;

// }}}

/**
 * 
 * @version 4.3
 */

public class ConsolePlugin extends EBPlugin
{
	public static final String MENU = "plugin.console.ConsolePlugin.menu";

	public static final String CMD_PATH = "/console/bsh/";

	
	/**
	 * Return value of {@link #parseLine()} if the text does not match a known
	 * error pattern.
	 */
	public static final int NO_ERROR = -1;

	// {{{ getSystemShell() method
	public static SystemShell getSystemShell()
	{
		return (SystemShell) ServiceManager.getService("console.Shell",
				"System");
	} // }}}

	// {{{ start() method
	public void start()
	{
		BeanShell.getNameSpace().addCommandPath(CMD_PATH, getClass());
		// systemCommandDirectory = MiscUtilities.constructPath(".",
		// "commando");

		String settings = jEdit.getSettingsDirectory();
		if (settings != null)
		{
			consoleDirectory = MiscUtilities.constructPath(settings, "console");

			userCommandDirectory = MiscUtilities.constructPath(
					consoleDirectory, "commando");
			File file = new File(userCommandDirectory);
			if (!file.exists())
				file.mkdirs();
		}

		selectedCommands = new ActionSet(jEdit
				.getProperty("action-set.commando.label"));
		allCommands = new ActionSet("default commands");
		jEdit.addActionSet(selectedCommands);
		ToolBarOptionPane top = new ToolBarOptionPane();

		String selectedCommands = jEdit.getProperty("commando.toolbar.list");
		ConsolePlugin.setSelectedActions(selectedCommands);
		CommandoToolBar.init();
	} // }}}

	// {{{ stop() method
	public void stop()
	{
		BeanShell.getNameSpace().addCommandPath(CMD_PATH, getClass());
		CommandoToolBar.remove();
		jEdit.removeActionSet(selectedCommands);
	} // }}}

	// {{{ handleMessage() method
	public void handleMessage(EBMessage msg)
	{
		if (msg instanceof ViewUpdate)
		{
			ViewUpdate vmsg = (ViewUpdate) msg;
			if (vmsg.getWhat() == ViewUpdate.CREATED)
			{
				CommandoToolBar.init();
			} else if (vmsg.getWhat() == ViewUpdate.CLOSED)
			{
				CommandoToolBar.remove();
			}
		}
	} // }}}

	// {{{ getConsoleSettingsDirectory() method
	public static String getConsoleSettingsDirectory()
	{
		return consoleDirectory;
	} // }}}

	/**
	 * Given a filename, performs translations so that it's a command name
	 */
	/*
	 * String path = location.getPath(); int i = path.lastIndexOf('/'); String
	 * basename = path.substring(i+1); i = path.lastIndexOf('.'); basename =
	 * path.substring(i+1); basename = basename.substring(0, basename.length() -
	 * 4);
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

	static public void setSelectedActions(String actionList)
	{
		StringList sl = StringList.split(actionList, " ");
		rescanCommands();
		selectedCommands.removeAllActions();
		EditAction[] ea = allCommands.getActions();
		for (int i = 0; i < ea.length; ++i)
		{
			CommandoCommand cc = (CommandoCommand) ea[i];
			if ((cc != null) && (sl.contains(cc.getShortLabel())))
			{
				selectedCommands.addAction(cc);
			}
		}
		CommandoToolBar.init();
	}

	public static void scanJarFile()
	{
		String defaultCommands = jEdit.getProperty("commando.default");
		StringList sl = StringList.split(defaultCommands, " ");
		for (int i = 0; i < sl.size(); i++)
		{
			String resourceName = "/console/commands/" + sl.get(i) + ".xml";
			// System.out.println ("GetResource: " + resourceName);
			URL url = Console.class.getResource(resourceName);
			if (url != null)
			{
				EditAction action = CommandoCommand.create(url);
				allCommands.addAction(action);
			} else
			{
				Log.log(Log.ERROR, "ConsolePlugin",
						"Unable to access resource: " + resourceName);
			}
		}
	}

	public static void rescanCommands()
	{
		allCommands.removeAllActions();
		scanDirectory(userCommandDirectory);
		scanJarFile();

		// Code duplication from jEdit.initKeyBindings() is bad, but
		// otherwise invoking 'rescan commando directory' will leave
		// old actions in the input handler
		EditAction[] ea = allCommands.getActions();
		for (int i = 0; i < ea.length; ++i)
		{
			String shortcut1 = jEdit.getProperty(ea[i].getName() + ".shortcut");
			if (shortcut1 != null)
				jEdit.getInputHandler().addKeyBinding(shortcut1, ea[i]);

			String shortcut2 = jEdit
					.getProperty(ea[i].getName() + ".shortcut2");
			if (shortcut2 != null)
				jEdit.getInputHandler().addKeyBinding(shortcut2, ea[i]);
		}
		Log.log(Log.DEBUG, ConsolePlugin.class, "Loaded "
				+ allCommands.size() + " Actions");
		EditBus.send(new DynamicMenuChanged(MENU));
	} // }}}

	// {{{ getCommandoCommands() method
	public static EditAction[] getCommandoCommands()
	{
		EditAction[] commands = selectedCommands.getActions();
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

		CommandoCommand command = (CommandoCommand) allCommands
				.getAction("commando." + compiler);
		if (command == null)
		{
			GUIUtilities.error(view, "commando.no-command",
					new String[] { compiler });
		} else
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
				} else if (result != JOptionPane.NO_OPTION)
					return;
			}

			command.invoke(view);
		}
	} // }}}

	// {{{ run() method
	public static void run(View view, Buffer buffer)
	{
		String interpreter = buffer.getStringProperty("commando.run");
		if (interpreter == null || interpreter.length() == 0)
		{
			GUIUtilities.error(view, "commando.no-interpreter", null);
			return;
		}

		CommandoCommand command = (CommandoCommand) allCommands
				.getAction("commando." + interpreter);
		if (command == null)
		{
			GUIUtilities.error(view, "commando.no-command",
					new String[] { interpreter });
		} else
		{
			if (buffer.isDirty())
			{
				Object[] args = { buffer.getName() };
				int result = GUIUtilities.confirm(view,
						"commando.not-saved-run", args,
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE);
				if (result == JOptionPane.YES_OPTION)
				{
					if (!buffer.save(view, null, true))
						return;
				} else if (result != JOptionPane.NO_OPTION)
					return;
			}

			command.invoke(view);
		}
	} // }}}

	// {{{ getPackageName() method
	/**
	 * A utility method that returns the name of the package containing the
	 * current buffer.
	 * 
	 * @param buffer
	 *            The buffer
	 */
	public static String getPackageName(Buffer buffer)
	{
		StringReader in = new StringReader(buffer
				.getText(0, buffer.getLength()));

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
				} else if (stok.sval.equals("class"))
				{
					in.close();
					return null;
				}
			}

			in.close();
		} catch (IOException io)
		{
			// can't happen
			throw new InternalError();
		}

		return null;
	} // }}}

	// {{{ getClassName() method
	/**
	 * Returns the name of the specified buffer without the extension, appended
	 * to the buffer's package name.
	 * 
	 * @param buffer
	 *            The buffer
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
	 * Returns the directory containing the root of the package of the current
	 * buffer. For example, if the buffer is located in
	 * <code>/home/slava/Stuff/example/Example.java</code> and contains a
	 * <code>package example</code> statement, this method will return
	 * <code>/home/slava/Stuff</code>.
	 * 
	 * @param buffer
	 *            The buffer
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
	 * Expands embedded environment variables in the same manner as the system
	 * shell.
	 * 
	 * @param view
	 *            The view
	 * @param text
	 *            The string to expand
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
	 *            The view
	 * @param var
	 *            The variable name
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
	 *            The view
	 * @param var
	 *            The variable name
	 * @param value
	 *            The value
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

	// {{{ parseLine() method
	/**
	 * @deprecated Call the other form of <code>parseLine() instead.
	 */
	public static synchronized int parseLine(String text, String directory,
			DefaultErrorSource errorSource)
	{
		return parseLine(jEdit.getLastView(), text, directory, errorSource);
	} // }}}

	// {{{ parseLine() method
	/**
	 * Parses the specified line for errors, and if it contains one, adds an
	 * error to the specified error source.
	 * 
	 * @param view
	 *            The current view
	 * @param text
	 *            The line text
	 * @param directory
	 *            The path of the current directory
	 * @param errorSource
	 *            The error source
	 * @return Returns either <code>ErrorSource.WARNING</code>,
	 *         <code>ErrorSource.ERROR</code>, or <code>NO_ERROR</code>.
	 */
	public static synchronized int parseLine(View view, String text,
			String directory, DefaultErrorSource errorSource)
	{
		if (errorMatchers == null)
			loadMatchers();

		if (lastError != null)
		{
			String message = null;
			if (lastMatcher != null
					&& lastMatcher.match(view, text, directory, errorSource) == null)
				message = lastMatcher.matchExtra(text);
			if (message != null)
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

		for (int i = 0; i < errorMatchers.length; i++)
		{
			ErrorMatcher m = errorMatchers[i];
			
			DefaultErrorSource.DefaultError error = m.match(view, text,
					directory, errorSource);
			if (error != null)
			{
				lastError = error;
				lastMatcher = m;
				return error.getErrorType();
			}
		}

		return -1;
	} // }}}

	// {{{ getErrorMatchers() method
	public static ErrorMatcher[] getErrorMatchers()
	{
		loadMatchers();
		return errorMatchers;
	} // }}}

	// {{{ finishErrorParsing() method
	/**
	 * This should be called after all lines to parse have been handled. It
	 * handles the corner case where the last line parsed was an extra message.
	 * 
	 * @param errorSource
	 *            The error source
	 */
	public static synchronized void finishErrorParsing(
			DefaultErrorSource errorSource)
	{
		if (lastError != null)
		{
			errorSource.addError(lastError);
			lastError = null;
			lastMatcher = null;
		}
	} // }}}

	// {{{ Private members

	// {{{ Instance and static variables
	private static ErrorMatcher[] errorMatchers;

	private static ErrorMatcher lastMatcher;

	private static DefaultErrorSource.DefaultError lastError;

	private static String consoleDirectory;

	private static String userCommandDirectory;

	// private static String systemCommandDirectory;

	private static ActionSet selectedCommands;

	private static ActionSet allCommands;

	public static ActionSet getAllCommands()
	{
		return allCommands;
	}

	public static ActionSet getSelectedCommands()
	{
		return selectedCommands;
	}

	/**
	 * @return a row of checkboxable buttons as a view for the two ActionSets.
	 */

	// }}}
	static View view = null;

	static CommandoToolBar toolBar = null;

	// {{{ loadMatchers() method
	private static void loadMatchers()
	{
		lastMatcher = null;
//		Vector vec = new Vector();
		LinkedHashMap map = new LinkedHashMap();
		String[] userMatchers = jEdit.getProperty("console.error.user", "").split("\\s+");
		String[] defaultMatchers = jEdit.getProperty("console.error.default", "").split("\\s+");
		loadMatchers(true, userMatchers, map);
		loadMatchers(false, defaultMatchers, map);
//		loadMatchers(false, jEdit.getProperty("console.error.default"), vec);

//		errorMatchers = new ErrorMatcher[vec.size()];
		errorMatchers = new ErrorMatcher[map.size()];		
		Iterator itr = map.values().iterator();
		int i=0;
		while (itr.hasNext()){
			lastMatcher = (ErrorMatcher) itr.next();
			errorMatchers[i++] = lastMatcher;
		}
//		errorMatchers = new ErrorMatcher[values.length];
//		vec.copyInto(errorMatchers);
	} // }}}

	/**
	 * @deprecated - use loadMatchers(boolean user, String[] list, Map map)  instead
	 * @param user
	 * @param list
	 * @param vec
	 */
	private static void loadMatchers(boolean user, String list, Vector vec)
	{
		if (list == null)
			return;
		StringTokenizer st = new StringTokenizer(list);
		while (st.hasMoreTokens())
		{
			String name = st.nextToken();
			ErrorMatcher newMatcher = ErrorMatcher.bring(name);
			newMatcher.user = user;
			vec.add(newMatcher);
		}
	} // }}}

	private static void loadMatchers(boolean user, String[] list, Map map) {
		if (list == null) return;
		for (int i=0; i<list.length; ++i) {
			String key = list[i];
			if (key==null || key.equals("null")) continue;
			if (map.containsKey(key)) continue;
			ErrorMatcher newMatcher = ErrorMatcher.bring(key);
			if (!newMatcher.isValid()) continue;
			newMatcher.user=user;
			if (user) { newMatcher.name += " (user)"; }
			map.put(key, newMatcher);
		}
	}
		
} // }}}


