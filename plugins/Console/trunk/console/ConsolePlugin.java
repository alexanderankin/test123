/*
 * ConsolePlugin.java - Console plugin
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999, 2004 Slava Pestov
 * Portions copyright (C) 1999, 2000 Kevin A. Burton
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
import console.commando.*;
import gnu.regexp.REException;
import javax.swing.JOptionPane;
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
	public static final String MENU = "plugin.console.ConsolePlugin.menu";
	public static final String CMD_PATH = "/console/bsh/";

	/**
	 * Return value of {@link #parseLine()} if the text does not match
	 * a known error pattern.
	 */
	public static final int NO_ERROR = -1;

	//{{{ getSystemShell() method
	public static SystemShell getSystemShell()
	{
		return (SystemShell)ServiceManager.getService(
			"console.Shell","System");
	} //}}}

	//{{{ start() method
	public void start()
	{
		BeanShell.getNameSpace().addCommandPath(CMD_PATH,
			getClass());

		String settings = jEdit.getSettingsDirectory();
		if(settings != null)
		{
			consoleDirectory = MiscUtilities.constructPath(settings,"console");
			commandoDirectory = MiscUtilities.constructPath(consoleDirectory,"commando");
			File file = new File(commandoDirectory);
			if(!file.exists())
				file.mkdirs();
		}

		commandoToolBarMap = new Hashtable();

		commando = new ActionSet(jEdit.getProperty("action-set.commando.label"));
		jEdit.addActionSet(commando);

		rescanCommandoDirectory();

		View[] views = jEdit.getViews();
		for(int i = 0; i < views.length; i++)
		{
			viewOpened(views[i]);
		}

		propertiesChanged();
	} //}}}

	//{{{ stop() method
	public void stop()
	{
		BeanShell.getNameSpace().addCommandPath(CMD_PATH,
			getClass());

		View[] views = jEdit.getViews();
		for(int i = 0; i < views.length; i++)
		{
			viewClosed(views[i]);
		}

		jEdit.removeActionSet(commando);
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
				viewOpened(view);
			}
			else if(vmsg.getWhat() == ViewUpdate.CLOSED)
			{
				viewClosed(view);
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
		commando.removeAllActions();

		StringTokenizer st = new StringTokenizer(jEdit.getProperty(
			"commando.default"));
		while(st.hasMoreTokens())
		{
			String name = st.nextToken();
			commando.addAction(new CommandoCommand(name.replace('_',' '),
				ConsolePlugin.class.getResource(
				"/console/commando/" + name + ".xml")));
		}

		if(commandoDirectory != null)
		{
			File[] files = new File(commandoDirectory).listFiles();
			if(files != null)
			{
				for(int i = 0; i < files.length; i++)
				{
					File file = files[i];
					String name = file.getName();
					if(!name.endsWith(".xml")
						|| file.isHidden())
						continue;

					commando.addAction(new CommandoCommand(
						name.substring(0,name.length() - 4)
						.replace('_',' '),
						file.getPath()));
				}
			}
		}

		// Code duplication from jEdit.initKeyBindings() is bad, but
		// otherwise invoking 'rescan commando directory' will leave
		// old actions in the input handler
		EditAction[] actions = getCommandoCommands();
		for(int i = 0; i < actions.length; i++)
		{
			EditAction action = actions[i];

			String shortcut1 = jEdit.getProperty(action.getName()
				+ ".shortcut");
			if(shortcut1 != null)
				jEdit.getInputHandler().addKeyBinding(shortcut1,action);

			String shortcut2 = jEdit.getProperty(action.getName()
				+ ".shortcut2");
			if(shortcut2 != null)
				jEdit.getInputHandler().addKeyBinding(shortcut2,action);
		}

		EditBus.send(new DynamicMenuChanged(MENU));
	} //}}}

	//{{{ getCommandoCommands() method
	public static EditAction[] getCommandoCommands()
	{
		EditAction[] commands = commando.getActions();
		MiscUtilities.quicksort(commands,new ActionCompare());

		return commands;
	} //}}}

	//{{{ compile() method
	public static void compile(View view, Buffer buffer)
	{
		String compiler = buffer.getStringProperty("commando.compile");
		if(compiler == null || compiler.length() == 0)
		{
			GUIUtilities.error(view,"commando.no-compiler",null);
			return;
		}

		CommandoCommand command = (CommandoCommand)commando.getAction(
			"commando." + compiler);
		if(command == null)
		{
			GUIUtilities.error(view,"commando.no-command",
				new String[] { compiler });
		}
		else
		{
			if(buffer.isDirty())
			{
				Object[] args = { buffer.getName() };
				int result = GUIUtilities.confirm(view,
					"commando.not-saved-compile",args,
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE);
				if(result == JOptionPane.YES_OPTION)
				{
					if(!buffer.save(view,null,true))
						return;
				}
				else if(result != JOptionPane.NO_OPTION)
					return;
			}

			command.invoke(view);
		}
	} //}}}

	//{{{ run() method
	public static void run(View view, Buffer buffer)
	{
		String interpreter = buffer.getStringProperty("commando.run");
		if(interpreter == null || interpreter.length() == 0)
		{
			GUIUtilities.error(view,"commando.no-interpreter",null);
			return;
		}

		CommandoCommand command = (CommandoCommand)commando.getAction(
			"commando." + interpreter);
		if(command == null)
		{
			GUIUtilities.error(view,"commando.no-command",
				new String[] { interpreter });
		}
		else
		{
			if(buffer.isDirty())
			{
				Object[] args = { buffer.getName() };
				int result = GUIUtilities.confirm(view,
					"commando.not-saved-run",args,
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE);
				if(result == JOptionPane.YES_OPTION)
				{
					if(!buffer.save(view,null,true))
						return;
				}
				else if(result != JOptionPane.NO_OPTION)
					return;
			}

			command.invoke(view);
		}
	} //}}}

	//{{{ getPackageName() method
	/**
	 * A utility method that returns the name of the package containing
	 * the current buffer.
	 * @param buffer The buffer
	 */
	public static String getPackageName(Buffer buffer)
	{
		StringReader in = new StringReader(buffer.getText(0,
			buffer.getLength()));

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
		catch(IOException io)
		{
			// can't happen
			throw new InternalError();
		}

		return null;
	} //}}}

	//{{{ getClassName() method
	/**
	 * Returns the name of the specified buffer without the extension,
	 * appended to the buffer's package name.
	 * @param buffer The buffer
	 */
	public static String getClassName(Buffer buffer)
	{
		String pkg = getPackageName(buffer);
		String clazz = MiscUtilities.getFileNameNoExtension(buffer.getPath());
		if(pkg == null)
			return clazz;
		else
			return pkg + '.' + clazz;
	} //}}}

	//{{{ getPackageRoot() method
	/**
	 * Returns the directory containing the root of the package of the
	 * current buffer. For example, if the buffer is located in
	 * <code>/home/slava/Stuff/example/Example.java</code> and contains a
	 * <code>package example</code> statement, this method will return
	 * <code>/home/slava/Stuff</code>.
	 *
	 * @param buffer The buffer
	 */
	public static String getPackageRoot(Buffer buffer)
	{
		String pkg = getPackageName(buffer);
		String path = MiscUtilities.getParentOfPath(buffer.getPath());
		if(path.endsWith(File.separator))
			path = path.substring(0,path.length() - 1);

		if(pkg == null)
			return path;

		pkg = pkg.replace('.',File.separatorChar);
		if(path.endsWith(pkg))
			return path.substring(0,path.length() - pkg.length());
		else
			return path;
	} //}}}

	//{{{ expandSystemShellVariables() method
	/**
	 * Expands embedded environment variables in the same manner as the
	 * system shell.
	 * @param view The view
	 * @param text The string to expand
	 */
	public static String expandSystemShellVariables(View view, String text)
	{
		return getSystemShell().expandVariables(view,text);
	} //}}}

	//{{{ getSystemShellVariableValue() method
	/**
	 * Returns the value of the specified system shell environment variable.
	 * @param view The view
	 * @param var The variable name
	 */
	public static String getSystemShellVariableValue(View view, String var)
	{
		return getSystemShell().getVariableValue(view,var);
	} //}}}

	//{{{ setSystemShellVariableValue() method
	/**
	 * Sets the value of the specified system shell environment variable.
	 * @param view The view
	 * @param var The variable name
	 * @param value The value
	 */
	public static void setSystemShellVariableValue(String var, String value)
	{
		getSystemShell().getVariables().put(var,value);
	} //}}}

	//{{{ ActionCompare class
	static class ActionCompare implements MiscUtilities.Compare
	{
		public int compare(Object obj1, Object obj2)
		{
			EditAction a1 = (EditAction)obj1;
			EditAction a2 = (EditAction)obj2;
			return a1.getLabel().compareTo(a2.getLabel());
		}
	} //}}}

	//{{{ parseLine() method
	/**
	 * @deprecated Call the other form of <code>parseLine() instead.
	 */
	public static synchronized int parseLine(String text, String directory,
		DefaultErrorSource errorSource)
	{
		return parseLine(jEdit.getLastView(),text,directory,errorSource);
	} //}}}

	//{{{ parseLine() method
	/**
	 * Parses the specified line for errors, and if it contains one,
	 * adds an error to the specified error source.
	 * @param view The current view
	 * @param text The line text
	 * @param directory The path of the current directory
	 * @param errorSource The error source
	 * @return Returns either <code>ErrorSource.WARNING</code>,
	 * <code>ErrorSource.ERROR</code>, or <code>NO_ERROR</code>.
	 */
	public static synchronized int parseLine(View view,
		String text, String directory, DefaultErrorSource errorSource)
	{
		if(errorMatchers == null)
			loadMatchers();

		if(lastError != null)
		{
			String message = null;
			if(lastMatcher != null &&
				lastMatcher.match(view,text,directory,errorSource) == null)
				message = lastMatcher.matchExtra(text);
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
				= m.match(view,text,directory,errorSource);
			if(error != null)
			{
				lastError = error;
				lastMatcher = m;
				return error.getErrorType();
			}
		}

		return -1;
	} //}}}

	//{{{ getErrorMatchers() method
	public static ErrorMatcher[] getErrorMatchers()
	{
		if(errorMatchers == null)
			loadMatchers();

		return errorMatchers;
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

	//{{{ Private members

	//{{{ Instance and static variables
	private static ErrorMatcher[] errorMatchers;
	private static ErrorMatcher lastMatcher;
	private static DefaultErrorSource.DefaultError lastError;
	private static String consoleDirectory;
	private static String commandoDirectory;
	private static ActionSet commando;

	private Hashtable commandoToolBarMap;
	//}}}

	//{{{ viewOpened() method
	private void viewOpened(View view)
	{
		if(view.isPlainView())
			return;

		if(jEdit.getBooleanProperty("commando.toolbar.enabled"))
		{
			CommandoToolBar toolBar = new CommandoToolBar(view);
			commandoToolBarMap.put(view,toolBar);
			view.addToolBar(toolBar);
		}
	} //}}}

	//{{{ viewClosed() method
	private void viewClosed(View view)
	{
		CommandoToolBar toolBar = (CommandoToolBar)
			commandoToolBarMap.remove(view);
		if(toolBar != null)
			view.removeToolBar(toolBar);
	} //}}}

	//{{{ propertiesChanged() method
	private void propertiesChanged()
	{
		// lazily load the matchers the next time they are
		// needed
		errorMatchers = null;

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
		//SystemShell.propertiesChanged();
	} //}}}

	//{{{ loadMatchers() method
	private static void loadMatchers()
	{
		lastMatcher = null;

		Vector vec = new Vector();

		loadMatchers(true,jEdit.getProperty("console.error.user"),vec);
		loadMatchers(false,jEdit.getProperty("console.error.default"),vec);

		errorMatchers = new ErrorMatcher[vec.size()];
		vec.copyInto(errorMatchers);
	} //}}}

	//{{{ loadMatchers() method
	private static void loadMatchers(boolean user, String list, Vector vec)
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
	private static void loadMatcher(boolean user, String internalName, Vector vec)
	{
		String name = jEdit.getProperty("console.error." + internalName + ".name");
		String error = jEdit.getProperty("console.error." + internalName + ".match");
		String warning = jEdit.getProperty("console.error." + internalName + ".warning");
		String extra = jEdit.getProperty("console.error." + internalName + ".extra");
		String filename = jEdit.getProperty("console.error." + internalName + ".filename");
		String line = jEdit.getProperty("console.error." + internalName + ".line");
		String message = jEdit.getProperty("console.error." + internalName + ".message");

		try
		{
			ErrorMatcher matcher = new ErrorMatcher(user,internalName,
				name,error,warning,extra,filename,line,message);
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
}
