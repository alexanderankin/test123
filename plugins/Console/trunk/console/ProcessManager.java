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
			currentDirectory = System.getProperty("user.dir");
		}

		void setForegroundProcess(ConsoleProcess process)
		{
			if(foreground != null)
				foreground.viewState = null;

			foreground = process;

			if(process != null)
				foreground.viewState = this;
		}
	}
}
