/*
 * DefaultShellBuiltIns.java - A few commands built in to the console
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

import java.io.File;
import java.util.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.HelpViewer;

class DefaultShellBuiltIns
{
	static void executeBuiltIn(View view, String name,
		Vector args, Console console)
	{
		if(name.equals("bg"))
		{
			if(checkArgs("bg",args,1,console))
			{
				ProcessManager.ViewState state = ProcessManager.getViewState(view);
				if(state.foreground == null)
				{
					console.printError(jEdit.getProperty("console.shell.bg.noproc"));
					return;
				}

				Object[] pp = { new Integer(state.foreground.pid) };
				state.setForegroundProcess(null);
				console.printPlain(jEdit.getProperty("console.shell.bg.ok",pp));
			}
		}
		else if(name.equals("cd"))
		{
			if(!ProcessManager.cdCommandAvailable())
			{
				console.printError(jEdit.getProperty("console.shell.cd.unsup"));
				return;
			}
			else if(checkArgs("cd",args,2,console))
			{
				ProcessManager.ViewState state = ProcessManager.getViewState(view);

				String newDir = (String)args.elementAt(1);
				if(newDir.equals(".."))
				{
					newDir = MiscUtilities.getParentOfPath(
						state.currentDirectory);
				}
				else
				{
					newDir = MiscUtilities.constructPath(
						state.currentDirectory,newDir);
				}

				String[] pp = { newDir };
				if(new File(newDir).exists())
				{
					state.currentDirectory = newDir;
					console.printPlain(jEdit.getProperty(
						"console.shell.cd.ok",pp));
				}
				else
				{
					console.printError(jEdit.getProperty(
						"console.shell.cd.error",pp));
				}
			}
		}
		else if(name.equals("clear"))
		{
			if(checkArgs("clear",args,1,console))
				console.clear();
		}
		else if(name.equals("echo"))
		{
			if(checkArgs("echo",args,-2,console))
			{
				for(int i = 1; i < args.size(); i++)
				{
					console.printPlain((String)args.elementAt(i));
				}
			}
		}
		else if(name.equals("fg"))
		{
			if(checkArgs("fg",args,2,console))
			{
				ProcessManager.ViewState state = ProcessManager.getViewState(view);
				try
				{
					int pid = Integer.parseInt((String)args.elementAt(1));
					ConsoleProcess process = ProcessManager.getProcess(pid);
					if(process == null)
					{
						Object[] pp = { new Integer(pid) };
						console.printError(jEdit.getProperty("console.shell.bad-pid",pp));
						return;
					}

					state.setForegroundProcess(process);

					Object[] pp = { new Integer(process.pid) };
					console.printPlain(jEdit.getProperty("console.shell.fg.ok",pp));
				}
				catch(NumberFormatException nf)
				{
					console.printError(jEdit.getProperty("console.shell.fg.usage"));
					return;
				}
			}
		}
		else if(name.equals("help"))
		{
			if(checkArgs("help",args,1,console))
			{
				new HelpViewer(DefaultShellBuiltIns.class
					.getResource("/console/Console.html")
					.toString());
			}
		}
		else if(name.equals("kill"))
		{
			if(checkArgs("kill",args,2,console))
			{
				ProcessManager.ViewState state = ProcessManager.getViewState(view);
				try
				{
					int pid = Integer.parseInt((String)args.elementAt(1));
					ConsoleProcess process = ProcessManager.getProcess(pid);
					if(process == null)
					{
						Object[] pp = { new Integer(pid) };
						console.printError(jEdit.getProperty("console.shell.bad-pid",pp));
						return;
					}

					process.kill();
				}
				catch(NumberFormatException nf)
				{
					console.printError(jEdit.getProperty("console.shell.kill.usage"));
					return;
				}
			}
		}
		else if(name.equals("ps"))
		{
			if(checkArgs("ps",args,1,console))
			{
				ProcessManager.ViewState state = ProcessManager.getViewState(view);
				Vector processes = ProcessManager.getRunningProcesses();

				console.printPlain(jEdit.getProperty("console.shell.ps.header"));

				for(int i = 0; i < processes.size(); i++)
				{
					ConsoleProcess process = (ConsoleProcess)processes
						.elementAt(i);

					Object[] pp = {
						new Integer(process.pid),
						process.command,
						new Integer(state.foreground == process ? 1 : 0)
					};

					console.printPlain(jEdit.getProperty(
						"console.shell.ps.output",pp));
				}
			}
		}
		else if(name.equals("pwd"))
		{
			if(checkArgs("pwd",args,1,console))
			{
				console.printPlain(ProcessManager.getViewState(view)
					.currentDirectory);
			}
		}
		else if(name.equals("version"))
		{
			if(checkArgs("version",args,1,console))
			{
				console.printPlain(jEdit.getProperty(
					"plugin.console.ConsolePlugin.version"));
			}
		}
		else
		{
			String[] pp = { name };
			console.printError(jEdit.getProperty("console.shell.unknown-builtin",pp));
		}
	}

	// private members
	private DefaultShellBuiltIns() {}

	// if count < 0, then at least -count arguments must be specified
	// if count > 0, then exactly count arguments must be specified
	private static boolean checkArgs(String command, Vector args, int count,
		Console console)
	{
		if(count < 0)
		{
			if(args.size() >= -count)
				return true;
		}
		else if(count > 0)
		{
			if(args.size() == count)
				return true;
		}

		console.printError(jEdit.getProperty("console.shell." + command + ".usage"));
		return false;
	}
}
