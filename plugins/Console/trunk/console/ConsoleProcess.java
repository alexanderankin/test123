/*
 * ConsoleProcess.java - A running process
 * Copyright (C) 1999, 2000, 2001 Slava Pestov
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

import java.io.*;
import java.util.Vector;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

class ConsoleProcess
{
	ConsoleProcess(Console console, Vector args, boolean foreground)
	{
		SystemShell.ConsoleState consoleState
			= SystemShell.getConsoleState(console);

		this.args = args;
		this.currentDirectory = consoleState.currentDirectory;

		if(foreground)
		{
			console.getErrorSource().clear();

			this.console = console;
			this.consoleState = consoleState;

			ConsoleProcess runningProc = consoleState.process;
			if(runningProc != null)
				runningProc.stop();

			consoleState.process = this;
		}

		try
		{
			process = SystemShell.exec(currentDirectory,args);
			stdout = new StreamThread(process.getInputStream());
			stderr = new StreamThread(process.getErrorStream());
			stdout.start();
			stderr.start();
		}
		catch(Exception e)
		{
			String[] pp = { e.toString() };
			console.printError(jEdit.getProperty("console.shell.error",pp));
			stop();
		}
	}

	void detach()
	{
		if(console != null)
		{
			Object[] pp = { args.elementAt(0) };
			console.printError(jEdit.getProperty("console.shell.detached",pp));
		}

		consoleState = null;
		console = null;
	}

	void stop()
	{
		if(process != null)
		{
			process.destroy();
			process = null;
			stdout.stop();
			stderr.stop();

			if(console != null)
			{
				Object[] pp = { args.elementAt(0) };
				console.printError(jEdit.getProperty("console.shell.killed",pp));
			}
		}

		if(consoleState != null)
			consoleState.process = null;
	}

	// private members
	private SystemShell.ConsoleState consoleState;
	private String currentDirectory;
	private Console console;
	private Vector args;
	private Process process;
	private StreamThread stdout;
	private StreamThread stderr;
	private int threadDoneCount;

	private synchronized void threadDone()
	{
		threadDoneCount++;
		if(process != null && threadDoneCount == 2)
		{
			int exitCode;
			try
			{
				exitCode = process.waitFor();
			}
			catch(InterruptedException e)
			{
				Log.log(Log.ERROR,this,e);
				return;
			}

			if(console != null)
			{
				Object[] pp = { args.elementAt(0), new Integer(exitCode) };

				String msg = jEdit.getProperty("console.shell.exited",pp);

				if(exitCode == 0)
					console.printInfo(msg);
				else
					console.printError(msg);
			}

			process = null;
		}

		if(consoleState != null)
			consoleState.process = null;
	}

	class StreamThread extends Thread
	{
		InputStream inputStream;

		StreamThread(InputStream inputStream)
		{
			setName("" + StreamThread.class + args);
			this.inputStream = inputStream;
		}

		public void run()
		{
			try
			{
				BufferedReader in = new BufferedReader(
					new InputStreamReader(inputStream));

				String line;
				while((line = in.readLine()) != null)
				{
					if(console != null)
					{
						console.printAndParseError(line,
							currentDirectory);
					}
				}
				in.close();
			}
			catch(Exception e)
			{
				if(console != null)
				{
					String[] args = { e.toString() };
					console.printError(jEdit.getProperty(
						"console.shell.error",args));
				}
			}
			finally
			{
				threadDone();
			}
		}
	}
}
