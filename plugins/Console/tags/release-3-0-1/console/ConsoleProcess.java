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

import java.awt.Color;
import java.io.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;

class ConsoleProcess
{
	ConsoleProcess(Console console, Output output, String[] args, String[] env,
		boolean foreground)
	{
		SystemShell.ConsoleState consoleState
			= SystemShell.getConsoleState(console);

		this.args = args;
		this.env = env;
		this.currentDirectory = consoleState.currentDirectory;

		if(foreground)
		{
			console.getErrorSource().clear();

			this.console = console;
			this.output = output;
			this.consoleState = consoleState;

			ConsoleProcess runningProc = consoleState.process;
			if(runningProc != null)
				runningProc.stop();

			consoleState.process = this;
		}

		try
		{
			process = OperatingSystem.getOperatingSystem()
				.exec(args,env,currentDirectory);
			stdout = new StreamThread(process.getInputStream());
			stderr = new StreamThread(process.getErrorStream());
			stdout.start();
			stderr.start();
		}
		catch(Exception e)
		{
			String[] pp = { e.toString() };
			console.print(console.getErrorColor(),
				jEdit.getProperty("console.shell.error",pp));
			output.commandDone();
			stop();
		}
	}

	void detach()
	{
		if(console != null)
		{
			Object[] pp = { args[0] };
			console.print(console.getErrorColor(),
				jEdit.getProperty("console.shell.detached",pp));
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
				Object[] pp = { args[0] };
				console.print(console.getErrorColor(),
					jEdit.getProperty("console.shell.killed",pp));
			}

			ConsolePlugin.finishErrorParsing(console.getErrorSource());

			output.commandDone();
		}

		if(consoleState != null)
			consoleState.process = null;
	}

	boolean getExitStatus()
	{
		return (exitCode == 0);
	}

	// private members
	private SystemShell.ConsoleState consoleState;
	private String currentDirectory;
	private Console console;
	private Output output;
	private String[] args;
	private String[] env;
	private Process process;
	private StreamThread stdout;
	private StreamThread stderr;
	private int threadDoneCount;
	private int exitCode;

	private synchronized void threadDone()
	{
		threadDoneCount++;
		if(process != null && threadDoneCount == 2)
		{
			try
			{
				exitCode = process.waitFor();
			}
			catch(InterruptedException e)
			{
				Log.log(Log.ERROR,this,e);
				return;
			}

			if(console != null && output != null)
			{
				Object[] pp = { args[0], new Integer(exitCode) };

				String msg = jEdit.getProperty("console.shell.exited",pp);

				if(exitCode == 0)
					console.print(console.getInfoColor(),msg);
				else
					console.print(console.getErrorColor(),msg);

				ConsolePlugin.finishErrorParsing(
					console.getErrorSource());

				output.commandDone();
			}

			process = null;
		}

		if(consoleState != null)
			consoleState.process = null;

		notifyAll();
	}

	class StreamThread extends Thread
	{
		InputStream inputStream;

		StreamThread(InputStream inputStream)
		{
			setName("" + StreamThread.class + args);
			setPriority(Thread.MIN_PRIORITY);
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
					if(console != null && output != null)
					{
						int type = ConsolePlugin.parseLine(
							line,currentDirectory,
							console.getErrorSource());

						Color color;

						switch(type)
						{
						case ErrorSource.ERROR:
							color = console.getErrorColor();
							break;
						case ErrorSource.WARNING:
							color = console.getWarningColor();
							break;
						default:
							color = null;
							break;
						}

						output.print(color,line);
					}
				}
				in.close();
			}
			catch(Exception e)
			{
				Log.log(Log.ERROR,this,e);

				if(console != null)
				{
					String[] args = { e.toString() };
					console.print(console.getErrorColor(),
						jEdit.getProperty(
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
