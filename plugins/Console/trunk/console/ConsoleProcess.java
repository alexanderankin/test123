/*
 * ConsoleProcess.java - A running process
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999, 2004 Slava Pestov
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

// {{{ Imports
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import console.Console.ShellState;
import console.utils.StringList;

// }}}

class ConsoleProcess
{
	// {{{ Private members
	private SystemShell.ConsoleState consoleState;

	private String currentDirectory;

	private Console console;

	private Output output;

	private Output error;

	private String[] args;

	private String[] env;

	private Process process;

	private InputThread stdin;

	// private StreamThread stdout;
	// private StreamThread stderr;
	private CommandOutputParserThread parserThread;

	private int threadDoneCount;

	private int exitCode;

	private boolean stopped;

	/*
	 * AWT thread writes stdin to this pipe, and the input thread writes it to
	 * the process.
	 */
	private PipedInputStream pipeIn;

	private PipedOutputStream pipeOut;
	
	// {{{ ConsoleProcess constructor
	ConsoleProcess(Console console, Output output, String[] args,
			ProcessBuilder pBuilder,  SystemShell.ConsoleState consoleState,
			boolean foreground)
	{
		this.args = args;
		this.currentDirectory = consoleState.currentDirectory;

		if (foreground)
		{
			this.console = console;
			this.output = output;
			this.consoleState = consoleState;
		}

		try
		{
			// Streams for getting user input
			pipeIn = new PipedInputStream();
			pipeOut = new PipedOutputStream(pipeIn);
			process = ProcessRunner.getProcessRunner().exec(args, pBuilder,
					currentDirectory);
			console.startAnimation();
			parserThread = new CommandOutputParserThread(console.getView(),
					this, console.getErrorSource());
			parserThread.setDirectory(currentDirectory);
			parserThread.start();

			stdin = new InputThread(this, process.getOutputStream());
			stdin.start();
			process.waitFor();
			Shell shell = console.getShell();
			shell.printPrompt(console, console.getOutput());
			/*
			 * stdout = new StreamThread(this, process.getInputStream(),null);
			 * stdout.start();
			 */
			/*
			 * stderr = new StreamThread(this, process.getErrorStream(),
			 * console.getErrorColor()); stderr.start();
			 */

		} catch (InterruptedException ie)
		{
		} catch (IOException ioe)
		{ /*
			 * StackTraceElement[] els = ioe.getStackTrace(); StringList sl =
			 * new StringList(els); sl.add(ioe.getMessage());
			 * error.print(console.getErrorColor(),
			 * jEdit.getProperty("console.shell.error",sl.toArray()));
			 * output.commandDone(); error.commandDone();
			 */
		} finally
		{
			stop();
		}
	} // }}}

	// {{{ detach() method
	void detach()
	{
		if (console != null)
		{
			Object[] pp = { args[0] };
			error.print(console.getErrorColor(), jEdit.getProperty(
					"console.shell.detached", pp));
			output.commandDone();
			// error.commandDone();
		}

		consoleState.process = null;
		consoleState = null;
		console = null;
	} // }}}

	// {{{ stop() method
	void stop()
	{
		if (process != null)
		{
			stopped = true;

			if (stdin != null) stdin.abort();
			// stdout.abort();
			// stderr.abort();
			if (parserThread!= null) parserThread.finishErrorParsing();
			try
			{
				pipeOut.close();
			} catch (IOException e)
			{
				throw new RuntimeException(e);
			}

			process.destroy();
			process = null;

			if (console != null)
			{
				Object[] pp = { args[0] };
/*				error.print(console.getErrorColor(), jEdit.getProperty(
						"console.shell.killed", pp)); */
			}

			output.commandDone();
//			notifyAll();
			// error.commandDone();
		}

		if (consoleState != null)
			consoleState.process = null;
	} // }}}

	// {{{ isRunning() method
	boolean isRunning()
	{
		if (process == null)
			return false;
		// TODO: how to get the status of the running process?

		return true;
	} // }}}

	// {{{ getExitStatus() method
	boolean getExitStatus()
	{
		return (exitCode == 0);
	} // }}}

	// {{{ getConsole() method
	Console getConsole()
	{
		return console;
	} // }}}

	// {{{ getOutput() method
	Output getOutput()
	{
		return output;
	} // }}}

	// {{{ getErrorOutput() method
	Output getErrorOutput()
	{
		return error;
	} // }}}

	// {{{ getCurrentDirectory() method
	String getCurrentDirectory()
	{
		return currentDirectory;
	} // }}}

	// {{{ getPipeInput() method
	public PipedInputStream getPipeInput()
	{
		return pipeIn;
	} // }}}

	/**
	 * @return The standard output/error stream as a readable input stream (with
	 *         merged channels)
	 */
	public InputStream getMergedOutputs()
	{
		return process.getInputStream();
	}

	// {{{ getPipeOutput() method
	public PipedOutputStream getPipeOutput()
	{
		return pipeOut;
	} // }}}

	// {{{ threadDone() method
	synchronized void threadDone()
	{

		threadDoneCount++;
		if (process == null)
			return;

		if (!stopped)
		{
			// we don't want unkillable processes to hang
			// jEdit

			try
			{
				exitCode = process.waitFor();
			} catch (InterruptedException e)
			{
				Log.log(Log.ERROR, this, e);
				notifyAll();
				return;
			}
			stop();

			if (threadDoneCount == 3)
			{
				if (console != null && output != null && error != null)
				{
					Object[] pp = { args[0], new Integer(exitCode) };

					String msg = jEdit.getProperty("console.shell.exited", pp);

					if (exitCode == 0)
						error.print(console.getInfoColor(), msg);
					else
						error.print(console.getErrorColor(), msg);

					/*
					 * ConsolePlugin.finishErrorParsing(
					 * console.getErrorSource());
					 */

					// output.commandDone();
					// error.commandDone();
					jEdit.checkBufferStatus(jEdit.getActiveView());
				}

				process = null;

				if (consoleState != null)
					consoleState.process = null;

			}
		} // }}}
	}


	// }}}
}
