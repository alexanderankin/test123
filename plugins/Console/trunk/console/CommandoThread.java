/*
 * CommandoThread.java - Thread that runs commando commands
 * Copyright (C) 2001 Slava Pestov
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

import javax.swing.SwingUtilities;
import java.util.Vector;

class CommandoThread extends Thread
{
	CommandoThread(Console console, Vector commands)
	{
		this.console = console;
		this.commands = commands;
	}

	public void run()
	{
		final CommandoDialog.Command[] lastCommand = new CommandoDialog.Command[1];
		final boolean[] returnValue = new boolean[] { true };

		for(int i = 0; i < commands.size(); i++)
		{
			final CommandoDialog.Command command =
				(CommandoDialog.Command)commands.elementAt(i);
			final Shell shell = Shell.getShell(command.shell);
			if(shell == null)
			{
				// TO DO
			}

			try
			{
				SwingUtilities.invokeAndWait(new Runnable()
				{
					public void run()
					{
						if(!returnValue[0])
						{
							// complain that the last command failed
						}

						if(command.confirm)
						{
							// ask for confirmation
						}

						console.run(shell,
							console,
							command.command);
					}
				});
			}
			catch(Exception e)
			{
			}

			returnValue[0] = shell.waitFor(console);
			lastCommand[0] = command;
		}
	}

	// private members
	private Console console;
	private Vector commands;
}
