/*
 * ProcessManager.java - Manages multiple running processes
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

import java.lang.reflect.*;
import java.io.File;
import java.util.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.ViewUpdate;

class ProcessManager
{
	static void consoleOpened(View view, Console console)
	{
		viewStates.put(view,new ViewState(view,console));
	}

	static void consoleClosed(View view)
	{
		getViewState(view).setForegroundProcess(null);
		viewStates.remove(view);
	}

	static ViewState getViewState(View view)
	{
		return (ViewState)viewStates.get(view);
	}

	static boolean cdCommandAvailable()
	{
		return java13exec != null;
	}

	static Vector getRunningProcesses()
	{
		return processes;
	}

	static ConsoleProcess getProcess(int pid)
	{
		for(int i = 0; i < processes.size(); i++)
		{
			ConsoleProcess process = (ConsoleProcess)processes.elementAt(i);
			if(process.pid == pid)
				return process;
		}

		return null;
	}

	static ConsoleProcess createProcess(View view, Console console,
		Vector args, boolean foreground)
	{
		ViewState state = getViewState(view);
		ConsoleProcess process = new ConsoleProcess(state.currentDirectory,args);
		processes.addElement(process);

		if(foreground)
			state.setForegroundProcess(process);

		process.start(console);

		return process;
	}

	static void destroyProcess(ConsoleProcess process)
	{
		process.stop();
		processes.removeElement(process);
	}

	static Process exec(String currentDirectory, Vector _args) throws Exception
	{
		String[] args = new String[_args.size()];
		_args.copyInto(args);

		String[] extensionsToTry;
		if(DefaultShell.DOS)
			extensionsToTry = new String[] { ".cmd", ".exe", ".bat", ".com" };
		else
			extensionsToTry = new String[] { "" };

		String commandName = args[0];

		for(int i = 0; i < extensionsToTry.length; i++)
		{
			args[0] = commandName + extensionsToTry[i];

			try
			{
				if(java13exec != null)
				{
					Object[] methodArgs = { args, null, new File(currentDirectory) };
					return (Process)java13exec.invoke(null,methodArgs);
				}
				else
				{
					return Runtime.getRuntime().exec(args);
				}
			}
			catch(Exception e)
			{
				if(i == extensionsToTry.length - 1)
					throw e;
			}
		}

		// can't happen
		return null;
	}

	// private members
	private static Hashtable viewStates;
	private static Vector processes;
	private static Method java13exec;

	private ProcessManager() {}

	static
	{
		viewStates = new Hashtable();
		processes = new Vector();

		try
		{
			Class[] classes = { String[].class, String[].class, File.class };
			java13exec = Runtime.class.getMethod("exec",classes);
		}
		catch(Exception e)
		{
			// use Java 1.1/1.2 code instead
		}
	}

	static class ViewState
	{
		View view;
		Console console;
		String currentDirectory;
		ConsoleProcess foreground;

		ViewState(View view, Console console)
		{
			this.view = view;
			this.console = console;
			currentDirectory = System.getProperty("user.dir");
		}

		void setForegroundProcess(ConsoleProcess process)
		{
			if(foreground != null)
				foreground.setViewState(null);

			foreground = process;

			if(process != null)
				foreground.setViewState(this);
		}
	}
}
