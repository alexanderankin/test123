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
	int pid;
	String command;

	ConsoleProcess(String currentDirectory, Vector args)
	{
		pid = PID_COUNTER++;
		this.currentDirectory = currentDirectory;
		this.args = args;
		command = (String)args.elementAt(0);
	}

	void start(Console console)
	{
		try
		{
			process = ProcessManager.exec(currentDirectory,args);
			stdout = new StreamThread(process.getInputStream());
			stderr = new StreamThread(process.getErrorStream());
		}
		catch(Exception e)
		{
			String[] pp = { e.toString() };
			console.printError(jEdit.getProperty("console.shell.error",pp));
			ProcessManager.destroyProcess(this);
		}
	}

	synchronized void stop()
	{
		if(process != null)
		{
			process.destroy();
			process = null;
			stdout.stop();
			stderr.stop();

			if(viewState != null)
				viewState.setForegroundProcess(null);
		}
	}

	synchronized ProcessManager.ViewState getViewState()
	{
		return viewState;
	}

	synchronized void setViewState(ProcessManager.ViewState viewState)
	{
		this.viewState = viewState;
	}

	// private members
	private static int PID_COUNTER = 1;
	private ProcessManager.ViewState viewState;
	private String currentDirectory;
	private Vector args;
	private Process process;
	private StreamThread stdout;
	private StreamThread stderr;
	private int threadDoneCount;

	private synchronized void parseLine(String line)
	{
		if(viewState != null)
			viewState.console.printPlain(line);

		/* int type = ConsolePlugin.parseLine(line,dir);
		switch(type)
		{
		case ErrorSource.ERROR:
			console.printError(line);
			break;
		case ErrorSource.WARNING:
			console.printWarning(line);
			break;
		default:
			console.printPlain(line);
			break;
		} */
	}

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
				Log.log(Log.ERROR,this,"Yo Flav, what is this?");
				return;
			}

			Object[] args = { command, new Integer(exitCode) };

			String msg = jEdit.getProperty("console.shell.exited",args);

			if(viewState != null)
			{
				if(exitCode == 0)
					viewState.console.printInfo(msg);
				else
					viewState.console.printError(msg);
			}

			process = null;
		}

		ProcessManager.destroyProcess(this);
	}


	class StreamThread extends Thread
	{
		InputStream inputStream;

		StreamThread(InputStream inputStream)
		{
			setName(StreamThread.class + "[" + command + "]");
			this.inputStream = inputStream;
			start();
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
					parseLine(line);
				}
				in.close();
			}
			catch(Exception e)
			{
				synchronized(ConsoleProcess.this)
				{
					if(viewState != null)
					{
						String[] args = { e.toString() };
						viewState.console.printError(jEdit.getProperty("console.shell.error",args));
					}
				}
			}
			finally
			{
				threadDone();
			}
		}
	}
}
