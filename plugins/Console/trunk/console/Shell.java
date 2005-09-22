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

import java.util.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;


/**
 * A shell executes commands.
 * @author Slava Pestov
 */
public abstract class Shell
{
	public static final String SERVICE = "console.Shell";

	//{{{ registerShell() method
	/**
	 * @deprecated Write a <code>services.xml</code> file instead.
	 */
	public static void registerShell(Shell shell)
	{
		shells.addElement(shell);
	} //}}}

	//{{{ unregisterShell() method
	/**
	 * @deprecated Write a <code>services.xml</code> file instead.
	 */
	public static void unregisterShell(Shell shell)
	{
		shells.removeElement(shell);
	} //}}}

	//{{{ getShells() method
	/**
	 * Returns an array of all registered shells.
	 */
	public static String[] getShellNames()
	{
		ArrayList retVal = new ArrayList();
		for(int i = 0; i < shells.size(); i++)
		{
			retVal.add(((Shell)shells.get(i)).getName());
		}

		String[] newAPI = ServiceManager.getServiceNames(SERVICE);
		for(int i = 0; i < newAPI.length; i++)
		{
			retVal.add(newAPI[i]);
		}

		return (String[])retVal.toArray(new String[retVal.size()]);
	} //}}}

	//{{{ getShell() method
	/**
	 * Returns the shell with the specified name.
	 * @param name The shell name
	 */
	public static Shell getShell(String name)
	{
		// old API
		for(int i = 0; i < shells.size(); i++)
		{
			Shell shell = (Shell)shells.get(i);
			if(shell.getName().equals(name))
			{
				return shell;
			}
		}

		// new API
		return (Shell)ServiceManager.getService(SERVICE,name);
	} //}}}

	//{{{ Shell constructor
	public Shell(String name)
	{
		this.name = name;
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
		console.scrollRectToVisible(null);
//		output.writeAttrs(ConsolePane.colorAttributes(console.getPlainColor()), "\n" + promptString); 
	} //}}}

	//{{{ execute() method
	/**
	 * 
	 * @deprecated - use execute(Console, Output, String command)
	 * 
	 * Executes a command.
	 * @param console The console
	 * @param input Standard input
	 * @param output Standard output
	 * @param error Standard error
	 * @param command The command
	 * @since Console 3.5
	 */
	final private void execute(Console console, String input,
		Output output, Output error, String command)
	{
		
	} //}}}

	public void waitUntilDone() {
		
	}
	
	//{{{ execute() method
	/**
	 * Executes a command.
	 * @param console The console
	 * @param output The output
	 * @param command The command
	 */
	abstract public void execute(Console console, Output output, String command);
	
	//{{{ stop() method
	/**
	 * Stops the currently executing command, if any.
	 */
	public void stop(Console console)
	{
	} //}}}

	//{{{ waitFor() method
	/**
	 * Waits until any currently executing commands finish.
	 * @return True if the most recent command exited successfully,
	 * false otherwise
	 */
	public boolean waitFor(Console console)
	{
		return true;
	} //}}}

	//{{{ endOfFile() method
	/**
	 * Sends an end of file.
	 * @param console The console
	 */
	public void endOfFile(Console console)
	{
	} //}}}

	//{{{ detach() method
	/**
	 * Detaches the currently running process.
	 * @param console The console
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

	//{{{ CompletionInfo class
	public static class CompletionInfo
	{
		// remove from offset to command.length()
		public int offset;

		// possible values to insert
		public String[] completions;
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

	//{{{ Private members
	private static Vector shells = new Vector();
	private String name;
	//}}}
}
