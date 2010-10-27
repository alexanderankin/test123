/*
 * Shell.java - Shell interface
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999, 2005 Slava Pestov
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

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.StringList;



/**
 * <p>
 * Console has a single dockable window, which can contain multiple
 * Shells. Each Shell is named, and executes commands in a different language, and
 * can be selected via a JComboBox in the upper left corner of the Console.
 * </p>
 * <p>
 * By default, each Console has two shells: A SystemShell and a BeanShell. Each
 * of these shells was defined in services.xml, which looks like this:
 * </p>
 *
 * <pre>
  &lt;SERVICES&gt;
        &lt;SERVICE CLASS=&quot;console.Shell&quot; NAME=&quot;System&quot;&gt;
                new console.SystemShell();
        &lt;/SERVICE&gt;
        &lt;SERVICE CLASS=&quot;console.Shell&quot; NAME=&quot;BeanShell&quot;&gt;
                new console.ConsoleBeanShell();
        &lt;/SERVICE&gt;
&lt;/SERVICES&gt;
</pre>
<p> To define a new Shell for your own plugin 
 *  extend from this class, and return an instance of it 
 *  from the beanshell code in services.xml.
 *  </p><p> 
 *  Because it is a service, it is a singleton instance, shared by all
 *  for all Views and Console instances, and created only once while the plugin
 *  is loaded.
 * 
</p>
<p>
 * @author Slava Pestov
 * @version $Id$
 */
public abstract class Shell
{
	public static final String SERVICE = "console.Shell";

	//{{{ Private members
//	/** @deprecated list of shells defined in the old API */
//	private static Vector<Shell> shells = new Vector<Shell>();
	private String name;
	//}}}

	//{{{ Shell constructor
	public Shell(String name)
	{
		this.name = name;
	} //}}}

	// {{{ Member functions

	//{{{ getShellNames() method
	/**
	 * Returns an array of all registered shells.
	 */
	public static String[] getShellNames()
	{
		StringList retVal = new StringList();

		String[] newAPI = ServiceManager.getServiceNames(SERVICE);
		for(int i = 0; i < newAPI.length; i++)
		{
			retVal.add(newAPI[i]);
		}

		return retVal.toArray();
	} //}}}

	//{{{ getShell() method
	/**
	 * Returns the shell with the specified name
	 * @param name The shell name. Common values are:
	 *     "System", "BeanShell", "Factor", "Ant", "Python", etc....
	 */
	public static Shell getShell(String name)
	{
		// new API
		return (Shell)ServiceManager.getService(SERVICE,name);
	} //}}}

	
	//{{{ openConsole() method
	/**
	 * Called when a Console dockable first selects this shell.
	 * @since Console 4.0.2
	 */
	public void openConsole(Console console)
	{
	} //}}}

	//{{{ closeConsole() method
	/**
	 * Called when a Console dockable is closed.
	 * @since Console 4.0.2
	 */
	public void closeConsole(Console console)
	{
	} //}}}

	//{{{ printInfoMessage() method
	/**
	 * Prints a 'info' message to the specified console.
	 * @param output The output
	 */
	public void printInfoMessage(Output output)
	{
	} //}}}

	//{{{ printPrompt() method
	/**
	 * Prints a prompt to the specified console.
	 * @param console The console instance
	 * @param output The output
	 * @since Console 3.6
	 */
	public void printPrompt(Console console, Output output)
	{
		String promptString =jEdit.getProperty("console.prompt", new String[] { getName() });
		Log.log(Log.ERROR, Shell.class, promptString);
		output.print(console.getPlainColor(), "\n" + promptString);
	} //}}}

	//{{{ execute() methods
	/**
	 *
	 * Executes a command. Override this abstract method in custom
	 * derived classes.
	 *
	 * @param console The Console instance, to distinguish it from others when there are
	 *   multiple View or Console instances. 
	 * @param input optional string to feed into the command's Standard input
	 * @param output Standard output - the destination to send output 
	 * @param error Standard error - the destionation to send error messages
	 * @param command The command
	 * @since Console 3.5
	 */
	abstract public void execute(Console console, String input,
		Output output, Output error, String command);


	/** A convenience function - you do not override this method.
	 **
	 */
	final public void execute(Console console, String command, Output output) {
		execute(console, null, output, null, command);
	} // }}}

	// {{{ waitUntilDone() stub
	/** What is this for? It doesn't seem to be used *anywhere*.
	 * @deprecated - see waitFor(Console)
	 * */
	public void waitUntilDone() {

	}
	// }}}

	//{{{ stop() method
	/**
	 * Stops the currently executing command, if any. When the user clicks the "stop" button
	 * on the console, this method is called.
	 * @param console the same Console instance that was passed to execute()
	 */
	public void stop(Console console)
	{
	} //}}}

	//{{{ waitFor() method
	/**
	 * Waits until any currently executing commands finish.
	 * @return True if the most recent command exited successfully,
	 * false otherwise
	 * @param console the same Console instance that was passed to execute()	 
	 */
	public boolean waitFor(Console console)
	{
		return true;
	} //}}}

	//{{{ endOfFile() method
	/**
	 * Sends an end of file. Called when Ctrl-D is typed from Console.
	 * @param console the same Console instance that was passed to execute()
	 */
	public void endOfFile(Console console)
	{
	} //}}}

	//{{{ detach() method
	/**
	 * Detaches the currently running process. Called when Ctrl-Z is typed from the console. 
	 * @param console the same Console instance that was passed to execute()
	 */
	public void detach(Console console)
	{
	} //}}}

	//{{{ getCompletions() method
	/**
	 * Returns possible completions for the specified command.
	 * @param console The console instance
	 * @param command The command
	 * @since Console 3.6
	 */
	public CompletionInfo getCompletions(Console console, String command)
	{
		return null;
	} //}}}


	//{{{ getName() method
	/**
	 * Returns the name of the shell.
	 */
	public String getName()
	{
		return name;
	} //}}}

	//{{{ toString() method
	/**
	 * Returns the name of the shell.
	 */
	public String toString()
	{
		return name;
	} //}}}
	// }}}

	// {{{ Inner classes
	// {{{ ShellAction class
	/** All ShellActions select a named Shell.
	    @since Console 4.3
	*/
	public abstract static class ShellAction extends EditAction
	{
		protected String shellName;
		ShellAction(String actionName, String shellName) {
			super(actionName, new Object[] {shellName} );
			this.shellName = shellName;
		}
		public void invoke(View view) {
			Console c = ConsolePlugin.getConsole(view);
			c.setShell(shellName);
		}
	}// }}}

	// {{{ ToggleAction class
	/** A ToggleAction is a ShellAction which also toggles the
	    visibility of the Console. 
	*/
	public static class ToggleAction extends ShellAction {

		public String getLabel() {
			return shellName + " (Toggle)";
		}
		public ToggleAction(String shellName) {
			super("console.shell." + shellName + "-toggle", shellName);
			this.shellName =shellName;
		}
		public String getCode() {
			return "new console.Shell.ToggleAction(\"" + shellName + "\").invoke(view)";
		}
		public void invoke(View view)
		{
			super.invoke(view);
			jEdit.getAction("console-toggle").invoke(view);
		}


	} // }}}
	// {{{ SwitchAction class
	/**
	    A SwitchAction selects a shell and makes Console visible.
	*/
	public static class SwitchAction extends ShellAction
	{
		public SwitchAction(String shellName)
		{
			super("console.shell." + shellName + "-show", shellName);
		}
		
		public String getLabel() 
		{
			return shellName;
		}
		
		public String getCode() 
		{
			return "new console.Shell.SwitchAction(\"" + shellName + "\").invoke(view)";
		}
		public void invoke(View view)
		{
			DockableWindowManager wm = view.getDockableWindowManager();
			wm.showDockableWindow("console");
			super.invoke(view);
		}
	} // }}}
	//{{{ CompletionInfo class
	public static class CompletionInfo
	{
		// remove from offset to command.length()
		public int offset;

		// possible values to insert
		public String[] completions;
	} //}}}
	// }}}
} // }}}
