/*
 * ConsoleProcess.java - A running process
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
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

//{{{ Imports
import gnu.regexp.*;
import java.awt.Color;
import java.io.*;
import java.util.Stack;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.search.RESearchMatcher;
import org.gjt.sp.util.Log;
import errorlist.*;
//}}}

class ConsoleProcess
{
	//{{{ ConsoleProcess constructor
	ConsoleProcess(Console console, Output output, String[] args, String[] env,
		boolean foreground)
	{
		SystemShell.ConsoleState consoleState
			= SystemShell.getConsoleState(console);

		this.args = args;
		this.env = env;
		this.currentDirectory = consoleState.currentDirectory;

		currentDirectoryStack = new Stack();
		currentDirectoryStack.push(currentDirectory);

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
			process = ProcessRunner.getProcessRunner()
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
	} //}}}

	//{{{ detach() method
	void detach()
	{
		if(console != null)
		{
			Object[] pp = { args[0] };
			console.print(console.getErrorColor(),
				jEdit.getProperty("console.shell.detached",pp));
			output.commandDone();
		}

		consoleState.process = null;
		consoleState = null;
		console = null;
	} //}}}

	//{{{ stop() method
	void stop()
	{
		if(process != null)
		{
			process.destroy();
			process = null;
			stdout.abort();
			stderr.abort();

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
	} //}}}

	//{{{ getExitStatus() method
	boolean getExitStatus()
	{
		return (exitCode == 0);
	} //}}}

	//{{{ Private members

	private static RE makeEntering, makeLeaving;

	static
	{
		try
		{
			makeEntering = new RE(jEdit.getProperty("console.error.make.entering"),
				0,RESearchMatcher.RE_SYNTAX_JEDIT);
			makeLeaving = new RE(jEdit.getProperty("console.error.make.leaving"),
				0,RESearchMatcher.RE_SYNTAX_JEDIT);
		}
		catch(REException re)
		{
			Log.log(Log.ERROR,ConsoleProcess.class,re);
		}
	}

	//{{{ Instance variables
	private SystemShell.ConsoleState consoleState;
	private String currentDirectory;
	private Stack currentDirectoryStack; // for make
	private Console console;
	private Output output;
	private String[] args;
	private String[] env;
	private Process process;
	private StreamThread stdout;
	private StreamThread stderr;
	private int threadDoneCount;
	private int exitCode;
	//}}}

	//{{{ threadDone() method
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
	} //}}}

	//}}}

	//{{{ StreamThread class
	class StreamThread extends Thread
	{
		boolean aborted;
		InputStream inputStream;

		//{{{ StreamThread constructor
		StreamThread(InputStream inputStream)
		{
			setName("" + StreamThread.class + args);
			//setPriority(Thread.MIN_PRIORITY + 2);
			this.inputStream = inputStream;
		} //}}}

		//{{{ abort() method
		public void abort()
		{
			aborted = true;
			try
			{
				inputStream.close();
			}
			catch(IOException io)
			{
			}
		} //}}}

		//{{{ run() method
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
						Color color = null;

						REMatch match = makeEntering.getMatch(line);
						if(match == null)
						{
							match = makeLeaving.getMatch(line);
							if(match == null)
							{
								String _currentDirectory;
								if(currentDirectoryStack.isEmpty())
								{
									// should not happen...
									_currentDirectory = currentDirectory;
								}
								else
									_currentDirectory = (String)currentDirectoryStack.peek();

								int type = ConsolePlugin.parseLine(
									console.getView(),line,
									_currentDirectory,
									console.getErrorSource());
								switch(type)
								{
								case ErrorSource.ERROR:
									color = console.getErrorColor();
									break;
								case ErrorSource.WARNING:
									color = console.getWarningColor();
									break;
								}
							}
							else if(!currentDirectoryStack.isEmpty())
								currentDirectoryStack.pop();
						}
						else
							currentDirectoryStack.push(match.toString(1));

						output.print(color,line);
					}
				}
				in.close();
			}
			catch(Exception e)
			{
				if(!aborted)
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
			}
			finally
			{
				threadDone();
			}
		} //}}}
	} //}}}
}
