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

import org.gjt.sp.jedit.View;

/**
 * A shell executes commands.
 * @author Slava Pestov
 */
public abstract class Shell
{
	public Shell(String name)
	{
		this.name = name;
	}

	/**
	 * Name of named list where all available shells are stored.
	 */
	public static final String SHELLS_LIST = "SHELLS";

	/**
	 * Prints a 'info' message to the specified console.
	 * @param console The console
	 */
	public abstract void printInfoMessage(Console console);

	/**
	 * Executes a command.
	 * @param view The view
	 * @param command The command. The format of this is left entirely
	 * up to the implementation
	 * @param console The console
	 */
	public abstract void execute(View view, String command, Console console);

	/**
	 * Stops the currently executing command, if any.
	 */
	public abstract void stop();

	/**
	 * Waits until the currently executing command finishes.
	 * @return True if the command exited successfully, false otherwise
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
	private String name;
}
