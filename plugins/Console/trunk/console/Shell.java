/*
 * Shell.java - Shell interface
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999, 2000, 2001, 2002 Slava Pestov
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

import java.util.Vector;
import org.gjt.sp.jedit.View;

/**
 * A shell executes commands.
 * @author Slava Pestov
 */
public abstract class Shell
{
	//{{{ registerShell() method
	/**
	 * Registers a shell with the console plugin.
	 * @param shell The shell
	 */
	public static void registerShell(Shell shell)
	{
		shells.addElement(shell);
	} //}}}

	//{{{ unregisterShell() method
	/**
	 * Unregisters a shell.
	 * @param shell The shell
	 */
	public static void unregisterShell(Shell shell)
	{
		shells.removeElement(shell);
	} //}}}

	//{{{ getShells() method
	/**
	 * Returns an array of all registered shells.
	 */
	public static Shell[] getShells()
	{
		Shell[] retVal = new Shell[shells.size()];
		shells.copyInto(retVal);
		return retVal;
	} //}}}

	//{{{ getShell() method
	/**
	 * Returns the shell with the specified name.
	 * @param name The shell name
	 */
	public static Shell getShell(String name)
	{
		Shell[] shells = Shell.getShells();
		for(int i = 0; i < shells.length; i++)
		{
			if(shells[i].getName().equals(name))
			{
				return shells[i];
			}
		}

		return null;
	} //}}}

	//{{{ Shell constructor
	public Shell(String name)
	{
		this.name = name;
	} //}}}

	//{{{ printInfoMessage() method
	/**
	 * Prints a 'info' message to the specified console.
	 * @param output The output
	 */
	public abstract void printInfoMessage(Output output); //}}}

	//{{{ execute() method
	/**
	 * Executes a command. Note that both the console and output parameters
	 * are implementations of the Output interface. Writing to the console
	 * instance will always display the text in the console, but the output
	 * can either be the console or a buffer, depending on circumstances.
	 * So the console is like 'System.err', the output is like 'System.out',
	 * in a way.
	 * @param console The console
	 * @param input Standard input
	 * @param output Standard output
	 * @param error Standard error
	 * @param command The command
	 * @since Console 3.5
	 */
	public void execute(Console console, String input,
		Output output, Output error, String command)
	{
		execute(console,output,command);
	} //}}}

	//{{{ execute() method
	/**
	 * Executes a command. Note that both the console and output parameters
	 * are implementations of the Output interface. Writing to the console
	 * instance will always display the text in the console, but the output
	 * can either be the console or a buffer, depending on circumstances.
	 * So the console is like 'System.err', the output is like 'System.out',
	 * in a way.
	 * @param console The console
	 * @param output The output
	 * @param command The command
	 */
	public void execute(Console console, Output output, String command)
	{
	} //}}}

	//{{{ stop() method
	/**
	 * Stops the currently executing command, if any.
	 */
	public abstract void stop(Console console); //}}}

	//{{{ waitFor() method
	/**
	 * Waits until any currently executing commands finish.
	 * @return True if the most recent command exited successfully,
	 * false otherwise
	 */
	public abstract boolean waitFor(Console console); //}}}

	//{{{ getCompletions() method
	/**
	 * Returns possible completions for the specified command.
	 * @param view The current view
	 * @param currentDirectory The current directory
	 * @param command The comamnd
	 */
	public CompletionInfo getCompletions(View view, String currentDirectory,
		String command)
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
