/*
 * ConsoleProcess.java - A running process
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999, 2004 Slava Pestov
 * With modifications Copyright (C) 2005,2007 Alan Ezust
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
import java.util.Map;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.options.GeneralOptionPane;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.StandardUtilities;
import org.gjt.sp.util.StringList;

// }}}

/**
 * @deprecated use ConsoleProcessTask
 */

// {{{ ConsoleProcess class
class ConsoleProcess
{
	// {{{ Data members
	/** The running subprocess */
	Process process;
	private SystemShell.ConsoleState consoleState;
	private String currentDirectory;
	private Console console;
	private Output output;
	private Output error;
	private String[] args;
	// {{{ Threads for handling the streams of running subprocesses
	private InputThread stdin;
	private OutputThread stdout;
	private OutputThread stderr;
	// }}}
	private int threadDoneCount = 0;
	private int exitCode = 834;
	private boolean stopped;
	private boolean foreground;

	/*
	 * AWT thread writes stdin to this pipe, and the input thread writes it to
	 * the process.
	 */
	private PipedInputStream pipeIn;

	private PipedOutputStream pipeOut;
	// }}}

	// {{{ ConsoleProcess constructor
	ConsoleProcess(final Console console, final Output output, final String[] args,
			Map<String, String> env,  SystemShell.ConsoleState consoleState,
			boolean foreground)
	{
		this.args = args;
		this.currentDirectory = consoleState.currentDirectory;
		this.console = console;
		this.foreground = foreground;
		
		if (foreground)
		{
			this.output = output;
			this.error = new ErrorOutput(console);
			this.consoleState = consoleState;
		}
		else
		{
			/* There is no necessity keep any output, because this process works in the background.
			   For same reason ErrorOutput instance is turned to Log. */
			this.error = new ErrorOutput(null);
		}

		try
		{
			boolean merge = jEdit.getBooleanProperty("console.processrunner.mergeError", true);
			
			process = ProcessRunner.getProcessRunner().exec(args, env, currentDirectory, merge && foreground);
			if (process == null)
			{
				String str = StringList.join(args, " ");
				throw new RuntimeException( "Unrecognized command: " + str );
			}

			if (foreground)
			{
				// Streams for getting user input
				pipeIn = new PipedInputStream();
				pipeOut = new PipedOutputStream(pipeIn);
				
				console.startAnimation();
				/* Yes, there is one more thread we created but do not "count" - otherwise it would be (merge ? 4 : 3) 
				   Console does not wait for the stdin stream/thread to be closed before considering the process
				   "stopped". However, if the user does signal an EOF, that will still cause the stdin thread to
				   terminate, and the count to decrease, which means that sometimes we may miss some trailing output
				   from stdout or stderr (whichever stream is closed last). */
				threadDoneCount = merge ? 2 : 3;
			}
			else
			{
				/* There is only one thread processing an error output. */
				threadDoneCount = 1;
			}
			
			new Thread() {
				public void run() {
					try
					{
						exitCode = process.waitFor();
						showExit();
						// ConsoleProcess.this.stop();
						// console.getShell().printPrompt(console, output);
						threadDone();
					} catch (InterruptedException e)
					{
						exitCode = 1;
						Log.log(Log.ERROR, this, e);
					}
				}
			}.start();

			if (foreground) // we need stdin, stdout and probably separate stderr
			{
				stdout = new StreamThread(this, process.getInputStream(), console.getPlainColor());
				stdout.start();
				if (merge)
				{
					stderr = null;
				}
				else
				{
					stderr = new StreamThread(this, process.getErrorStream(), console.getErrorColor());
					stderr.start();
				}
	
				stdin = new InputThread(this, process.getOutputStream());
				stdin.start();
			}
			else // we need stderr only
			{
				stderr = new ErrorThread(this, process.getErrorStream(), console.getErrorColor());
				stderr.start();
			}
		}
		catch (Exception ioe)
		{
			Log.log(Log.ERROR, ioe, "ConsoleProcess()");
		}

	} // }}}

	// {{{ methods
	// {{{ showExit method
	synchronized void showExit () {
		// make sure error isn't null
		if (this.error == null)
		{
			this.error = this.console.getOutput();
		}
		
		boolean showExitStatus = jEdit.getBooleanProperty("console.processrunner.showExitStatus", true);
		/* if subprocess worked in the background - it's useful to know
		   what's happened when it stopped */
		if (!foreground || showExitStatus) {
			Object[] pp = { args[0], Integer.valueOf(exitCode) };
			String msg = jEdit.getProperty("console.shell.exited", pp);
			if (this.exitCode == 0)
				this.error.print(console.getInfoColor(), msg);
			else
				this.error.print(console.getErrorColor(), msg);
		}

		// console.getShell().printPrompt(console, output);
	} // }}}

	// {{{ detach() method
	void detach()
	{
		if (console != null)
		{
			Object[] pp = { args[0] };
			output.commandDone();
			if (error != null)
			{
				error.print(console.getErrorColor(),
					jEdit.getProperty("console.shell.detached", pp));
				error.commandDone();
			}
		}

		consoleState.setProcess(null);
		consoleState = null;
		console = null;
	} // }}}

	// {{{ stop() method
	synchronized void stop()
	{

		if (process != null)
		{
			if (stdin != null) stdin.abort();
			if (stdout != null) stdout.abort();
			if (stderr != null) stderr.abort();
			stopped = true;
			try
			{
				if (pipeOut != null) pipeOut.close();
			} catch (IOException e)
			{
				Log.log(Log.WARNING, this, e.getMessage());
			}

			try
			{
				this.process.destroy();
				if (this.output != null)
					this.output.commandDone();
			}
			catch (Exception e) {
				Log.log(Log.WARNING, this, e.getMessage());
			}
			
			this.process = null;
			
			if (this.console != null)
			{
/*				error.print(console.getErrorColor(), jEdit.getProperty(
						"console.shell.killed", pp)); */
			}
		}

		if (consoleState != null)
			consoleState.setProcess(null);

		// waitFor() might be waiting this.
		notifyAll();
	} // }}}

	// {{{ isRunning() method
	boolean isRunning()
	{
		if (process == null)
			return false;
		try
		{
			// should throw an exception of the thing is still running
			process.exitValue();
		}
		catch (IllegalThreadStateException itse)
		{
			return true;
		}
		return false;
	} // }}}

	// {{{ getExitStatus() method
	int getExitStatus()
	{
		return exitCode;
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

	// {{{ getArgs() method
	String[] getArgs() {
		return args;
	} // }}}

	// {{{ getPipeOutput() method
	public PipedOutputStream getPipeOutput()
	{
		return (process != null) ? pipeOut : null;
	} // }}}

	// {{{ waitFor() method
	/** @see Process.waitFor() */
	public synchronized int waitFor() throws InterruptedException
	{
		if (process != null) 
			exitCode = process.waitFor();
		while (!stopped) {
			// wait for notifyAll() in stop().
			wait(100);
		}
		return exitCode;
	} // }}}

	// {{{ threadDone() method
	synchronized void threadDone()
	{

		threadDoneCount--;
		if (threadDoneCount > 0) return;

		if ((!stopped) && (process != null))
		{

			// we don't want unkillable processes to hang
			// jEdit
			stop();
		}
		boolean doCheck = false;
		final int check = jEdit.getIntegerProperty("checkFileStatus");
		if (StandardUtilities.compareStrings(jEdit.getBuild(), "05.01.00.01", false) >= 0) {
			if (check > 0) doCheck = true;
		}
		else doCheck = true;
		if (doCheck) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						jEdit.checkBufferStatus(console.getView(), check != GeneralOptionPane.checkFileStatus_focus);
					}
			});
		}
	}
	// }}}
	
	// {{{ isForeground() method
	public boolean isForeground()
	{
		return foreground;
	}
	// }}}
	// }}}
} // }}}

