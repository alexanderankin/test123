/*
 * Shell.java - Shell interface
 * Copyright (C) 1999, 2000 Slava Pestov
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
	/**
	 * Registers a shell with the console plugin.
	 * @param shell The shell
	 */
	public static void registerShell(Shell shell)
	{
		shells.addElement(shell);
	}

	/**
	 * Unregisters a shell.
	 * @param shell The shell
	 */
	public static void unregisterShell(Shell shell)
	{
		shells.removeElement(shell);
	}

	/**
	 * Returns an array of all registered shells.
	 */
	public static Shell[] getShells()
	{
		Shell[] retVal = new Shell[shells.size()];
		shells.copyInto(retVal);
		return retVal;
	}

	/**
	 * Returns the shell with the specified name.
	 * @param name The shell name
	 */
	public static Shell getShell(String name)
	{
		Shell[] shells = Shell.getShells();
		for(int i = 0; i < shells.length; i++)
		{
			if(shells[i].getName().equals(shell))
			{
				return shells[i];
			}
		}

		return null;
	}

	public Shell(String name)
	{
		this.name = name;
	}

	/**
	 * Prints a 'info' message to the specified console.
	 * @param output The output
	 */
	public abstract void printInfoMessage(Output output);

	/**
	 * Executes a command.
	 */
	public abstract void execute(Console console, Output output, String command);

	/**
	 * Stops the currently executing command, if any.
	 */
	public abstract void stop(Console console);

	/**
	 * Waits until any currently executing commands finish.
	 * @return True if the most recent command exited successfully,
	 * false otherwise
	 */
	public abstract boolean waitFor();

	/**
	 * Returns the name of the shell.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the name of the shell.
	 */
	public String toString()
	{
		return name;
	}

	// private members
	private static Vector shells = new Vector();
	private String name;
}
