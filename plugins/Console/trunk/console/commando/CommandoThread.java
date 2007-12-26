/*
 * CommandoThread.java - Thread that runs commando commands
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
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

package console.commando;

//{{{ Imports
import console.*;
import javax.swing.*;
import java.util.Vector;
import org.gjt.sp.jedit.*;
//}}}
// {{{ commandothread class
class CommandoThread extends Thread
{
	//{{{ CommandoThread constructor
	CommandoThread(Console console, Vector commands)
	{
		this.console = console;
		this.commands = commands;
	} //}}}

	//{{{ run() method
	public void run()
	{
		final View view = console.getView();

		final CommandoHandler.Command[] lastCommand = new CommandoHandler.Command[1];
		final boolean[] returnValue = new boolean[] { true };

		for(int i = 0; i < commands.size(); i++)
		{
			final CommandoHandler.Command command =
				(CommandoHandler.Command)commands.elementAt(i);
			final Shell shell = console.setShell(command.shell);
			// final Shell shell = Shell.getShell(command.shell);
			if(shell == null)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						GUIUtilities.error(view,"commando.bad-shell",
							new String[] { command.shell });
					}
				});

				return;
			}

			try
			{
				SwingUtilities.invokeAndWait(new Runnable()
				{
					public void run()
					{
						//{{{ Get confirmation if bad exit status...
						if(!returnValue[0])
						{
							if(GUIUtilities.confirm(view,
								"commando.exit-status",
								new String[] { lastCommand[0].command },
								JOptionPane.YES_NO_OPTION,
								JOptionPane.ERROR_MESSAGE)
								!= JOptionPane.YES_OPTION)
								return;
						} //}}}

						//{{{ Get confirmation if user specified...
						if(command.confirm)
						{
							if(GUIUtilities.confirm(view,
								"commando.confirm",
								new String[] { command.command },
								JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE)
								!= JOptionPane.YES_OPTION)
								return;
						} //}}}

						Output out = console.getOutput();
						if (command.toBuffer)
							out = new BufferOutput(console, command.mode);
						console.run(shell, null,  out, null, command.command);
					}
				});
			}
			catch(Exception e)
			{
			}

			returnValue[0] = shell.waitFor(console);
			lastCommand[0] = command;
		}
	} //}}}
	// {{{ Data Members
	private Console console;
	private Vector commands;
	// }}}
}// }}}
