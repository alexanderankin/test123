/*
 * ConsoleProcessTask.java - Running process
 *
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999-2012 Slava Pestov, Alan Ezust, Artem Bryantsev
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
import java.awt.Color;
import java.io.IOException;
import java.io.PipedOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.StringList;
import org.gjt.sp.util.Task;
// }}}

// {{{ ConsoleProcessTask class
class ConsoleProcessTask extends Task
{
	// {{{ private data members
	/** The running subprocess */
	private Process process;
	
	private Console console;
	private SystemShell.ConsoleState consoleState;
	private String currentDirectory;
	private Output output;
	private Output error;
	private Output newError; // used while detaching
	private String[] args;
	private Map<String, String> env;
	private String input;
	
	private Thread currentThread;
	private InputStreamTask stdin;
	private OutputStreamTask stdout;
	private ErrorStreamTask stderr;
	private PipedOutputStream userInput;
	
	private int exitCode;
	private int failedStreamNumber;
	private boolean foreground;
	private boolean stopFlag;
	private boolean detachFlag;
	private int taskState;
	
	private Color errorColor;
	private Color plainColor;
	private Color infoColor;
	
	private final int DEFAULT_EXIT_CODE = 834;
	private final int MAX_CMDNAME_LENGTH = 12;
	private final char SYMBOL_BACKGROUND   = '&';
	private final char SYMBOL_CLOSED_INPUT = '#';
	// }}}
	
	/** Some trouble has appeared  */
	public static final int STATE_FAILURE  = 1;
	/** Task is working */
	public static final int STATE_WORKING  = 2;
	/** Task has finished */
	public static final int STATE_FINISHED = 3;
	/** Task is updating its state */
	public static final int STATE_UPDATE   = 4;
	
	// {{{ toBytes() method
	private byte[] toBytes(String str)
	{
		try
		{
			return str.getBytes(jEdit.getProperty("console.encoding"));
		}
		catch (UnsupportedEncodingException e)
		{
			Log.log (Log.ERROR, ConsoleProcessTask.class, "toBytes()", e);
			return null;
		}
	} // }}}
	
	// {{{ doDetach() method
	private void doDetach()
				 throws InterruptedException, IOException
	{
		// close InputStream and OutputStream (keep ErrorStream if any)
		if (stdin  != null) stdin.abort();
		if (stdout != null) stdout.abort();
		if (stdin  != null) stdin.join();
		if (stdout != null) stdout.join();
		
		// "detach" current Process from Console but keep reference to one:
		// 	this.console != null 
		consoleState.setProcess(null);
		consoleState = null;
		
		// notify user
		error.print(errorColor, jEdit.getProperty("console.shell.detached", new Object[] { args[0] }));
		error.commandDone();
		output.commandDone();
		
		// change recipient of error messages
		error  = newError;
		output = null;
		
		foreground = false;
		detachFlag = false;
		
	} // }}}
	
	// {{{ doStop() method
	private void doStop(boolean waitForStreams)
	{
		// close child threads
		try
		{
			if (stdin != null) stdin.abort();
			
			// may be these threads are still outputting  
			if (waitForStreams)
			{
				if (stdout != null) stdout.finish();
				if (stderr != null) stderr.finish();
			}
			else
			{
				if (stdout != null)	stdout.abort();
				if (stderr != null)	stderr.abort();
			}
			
			if (stdout != null)	stdout.join();
			if (stderr != null)	stderr.join();
		}
		catch(Exception e)
		{
			Log.log(Log.WARNING, this, e.getMessage());
		}
		
		// may be it's a forced cancelation: the process is still working
		if (process != null)
		{
			try
			{
				process.destroy();
			}
			catch (Exception e)
			{
				Log.log(Log.WARNING, this, e.getMessage());
			}
			
			process = null;
		}

		// see waitFor() method
		synchronized(this)
		{
			stopFlag = true;
			
			notifyAll();
		}
		
		// print exit code BEFORE output's completing
		boolean showExitStatus =
					jEdit.getBooleanProperty("console.processrunner.showExitStatus", true);
		
		/* If subprocess worked in the background - it's useful to know *
		 * what's happened when it stopped.                             */
		if ( showExitStatus || !foreground )
		{
			String msg = jEdit.getProperty("console.shell.exited",
										   new Object[] { args[0], Integer.valueOf(exitCode) }
			);
			
			if (taskState == STATE_FINISHED)
			{
				errorNotification(msg, infoColor);
			}
			else
			{
				errorNotification(msg);
			}
		}
		
		// complete outputing
		if (output != null) output.commandDone();
		
	} // }}}
	
	// {{{ closeCommandChannel() method
	private void closeCommandChannel()
	{
		try
		{
			try
			{
				if (userInput != null) userInput.close();
			}
			finally
			{
				userInput = null;
				
				if (stdin != null) stdin.finish();
				
				setTaskState(STATE_UPDATE);
			}
		}
		catch (IOException ioe)
		{
			Log.log(Log.ERROR, this, ioe);
		}
	} // }}}
	
	// {{{ setTaskState() method
	private void setTaskState(int newState)
	{
		int prevState = taskState;
		taskState = newState;
		
		// if command name is too long (f.e. full file name of script)
		// - try to truncate it
		int lastFileSep = args[0].lastIndexOf( System.getProperty("file.separator") );
		
		String cmdName  = args[0].length() < MAX_CMDNAME_LENGTH ?
							 args[0] :
							 ( lastFileSep == -1 || lastFileSep >= args[0].length() ?
							 	 	args[0] :
							 	 	String.format("...%s%s",
							 	 			System.getProperty("file.separator"),
							 	 			args[0].substring(lastFileSep + 1)
							 	 	)
							 )
		;
		
		// state's marks
		String marks = ( !foreground ? SYMBOL_BACKGROUND + " " : "")
					 + ( userInput == null ? SYMBOL_CLOSED_INPUT + " " : "")
		;
		
		
		setStatus( marks + cmdName );
		
		setLabel( StringList.join(args, " ") );
		
		
		// it means "Update internal task's state _only_"
		if (taskState == STATE_UPDATE)
		{
			taskState = prevState;
		}
		
	} // }}}
	
	// {{{ ConsoleProcess constructor
	ConsoleProcessTask(Console console,
					   Output output,
					   Output error,
					   String[] args,
					   Map<String, String> env,
					   SystemShell.ConsoleState consoleState,
					   boolean foreground,
					   String input
	)
	{
		this.args = args;
		this.currentDirectory = consoleState.currentDirectory;
		this.env = env;
		this.input = input;
		this.foreground = foreground;
		this.console = console;
		this.error = error;
		
		this.errorColor = console.getErrorColor();
		this.plainColor = console.getPlainColor();
		this.infoColor  = console.getInfoColor();
		
		if (foreground)
		{
			this.output = output;
			this.consoleState = consoleState;
			this.userInput = new PipedOutputStream();
			
			failedStreamNumber = 3;
		}
		else
		{
			failedStreamNumber = 1;
		}
		
		currentThread = Thread.currentThread();
		exitCode = DEFAULT_EXIT_CODE;
	} // }}}

	// {{{ methods
	
	// {{{ _run() method
	public void _run()
	{
		try
		{
			boolean waitForStreams = true;
			
			try
			{
				/* == initialization == */
				
				boolean merge = jEdit.getBooleanProperty("console.processrunner.mergeError", true);
				
				// start the working process
				try
				{
					process = ProcessRunner.getProcessRunner().exec(args, env, currentDirectory, merge && foreground);
				}
				catch (IOException ioe)
				{
					waitForStreams = false;
					String msg = "unrecognized command: " + StringList.join(args, " ");
					
					errorNotification(msg);
					throw new Exception(msg, ioe);
				}
	
				setTaskState(STATE_WORKING);
				
				if (foreground) // we need stdin, stdout and probably separate stderr
				{
					if (merge)
					{
						failedStreamNumber--;
					}
					else
					{
						stderr = new ErrorStreamTask(this,
													 process.getErrorStream(),
													 error,
													 errorColor
						);
					}
					
					stdout = new OutputStreamTask(this,
												  process.getInputStream(),
												  output,
												  plainColor,
												  console,
												  currentDirectory
					);
					
					stdin = new InputStreamTask(this,
											    process.getOutputStream(),
											    userInput
					);
					
					// run child threads
					if (!merge) stderr.start();
					stdout.start();
					stdin.start();
				}
				else // we need stderr only
				{
					stderr = new ErrorStreamTask(this,
												 process.getErrorStream(),
												 error,
												 errorColor
					);
					// run child thread
					stderr.start();
				}

				// waiting for child threads
				try
				{
					synchronized(this)
					{
						int attempt = 0;
						while (attempt < 10 && failedStreamNumber > 0)
						{
							wait(10);
							attempt++;
						}
					}
				}
				catch (InterruptedException ie)
				{
					waitForStreams = false;
				}
				
				if (failedStreamNumber != 0)
				{
					waitForStreams = false;
					throw new Exception("Some child thread has not started");
				}
				
				if (input != null)
				{
					sendCommand(input);
				}
				
				/* == working == */
				
				// main waiting loop
				boolean working = true;
				
				do
				{
					// check an exit code of the working process
					try
					{
						exitCode = process.exitValue();
						
						// process has finished its work
						working = false;
					}
					catch (IllegalThreadStateException itse)
					{
						// process is still working
						working = true;
					}
					
					// check state's flags of the current thread 
					working &= !currentThread.isInterrupted() && !stopFlag;
					
					if (working)
					{
						if (detachFlag && foreground)
						{
							doDetach();
							setTaskState(STATE_UPDATE);
						}
						
						// pause 100 ms
						try
						{
							Thread.sleep(100);
						}
						catch(InterruptedException ie)
						{
							waitForStreams = false;
							throw new Exception( ie.toString(), ie ); 
						}
					}
					
				} while (working);
				
				/* waitForStreams == false -> some error has appeared        *
				 * isInterrupted  == true  -> current thread is interrupted, *
				 *    but maybe process is still working - interrupt one and *
				 *    child threads too                                      *
				 * stopFlag       == true  -> user stops (cancels) task      */
				 
				waitForStreams &= !currentThread.isInterrupted() && !stopFlag;
				
			}
			finally
			{
				/* == finalization == */
				
				doStop(waitForStreams);
			}

			setTaskState(STATE_FINISHED);
			
		}
		catch (Exception e)
		{
			/* == abnormal termination == */
			
			String msg = jEdit.getProperty("console.shell.error",
										   new Object[] { e.getMessage() }
			);
			
			Log.log(Log.ERROR, this, msg);
			errorNotification(msg);
			
			setTaskState(STATE_FAILURE);
		}
		
	} // }}}
	
	// {{{ streamStart() method
	public synchronized void streamStart(StreamTask stream)
	{
		if (stream == null) return;
		
		failedStreamNumber--;
				
		notifyAll();
	} // }}}
	
	// {{{ streamFinish() method
	public void streamFinish(StreamTask stream)
	{
		if (stream == null) return;
		
		if (stream == stderr) stderr = null;
		if (stream == stdout) stdout = null;
		if (stream == stdin )
		{
			stdin  = null;
			
			// close the "command" channel because receiver is closed already
			closeCommandChannel();
		}
	} // }}}
	
	// {{{ detach() method
	/* The parameter "newError" must be installed;							 *
	 * it will be same "error" (f.e. this.getErrorOutput() ) or a new output *
	 * (f.e. new ErrorOutput(null) ), but it cannot be installed "null".     */
	boolean detach(Output newErrorOutput)
	{
		this.newError = newErrorOutput;
		
		return detachFlag = (newErrorOutput != null);
	} // }}}

	// {{{ stop() method
	void stop()
	{
		stopFlag = true;
	} // }}}
	
	// {{{ cancel() method
	@Override
	public void cancel()
	{
		stop();
	} // }}}
	
	// {{{ waitFor() method
	public synchronized int waitFor()
							throws InterruptedException
	{
		while ( !stopFlag )
		{
			wait(100);
		}
		
		return exitCode;
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

	// {{{ isForeground() method
	public boolean isForeground()
	{
		return foreground;
	}
	// }}}
	
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

	// {{{ getArgs() method
	String[] getArgs()
	{
		String[] result = new String[args.length];
		System.arraycopy(args, 0, result, 0, args.length);
		return result;
	} // }}}
	
	// {{{ getTaskState() method
	int getTaskState()
	{
		return taskState;
	} // }}}
	
	// {{{ sendCommand() method
	public boolean sendCommand(String command)
	{
		if (userInput != null && command != null)
		{
			try
			{
				userInput.write( toBytes(command) );
				userInput.flush();
				
				return true;
			}
			catch (IOException e)
			{
				Log.log(Log.ERROR, this, e);
				Log.log(Log.ERROR, this, "'"+command+"'");
			}
		}
		
		return false;
	} // }}}
	
	// {{{ endOfFile() method
	public void endOfFile()
	{
		/* user (keyboard)                               *
		 *        |                                      *
		 * channel(pipe, userInput)                      *
		 *        |                                      *
		 * input handler (thread, stdin)                 *
		 *        |                                      *
		 * input of process (stream, getOutputStream() ) */
		
		closeCommandChannel();
		
		try
		{
			process.getOutputStream().close();
		}
		catch (IOException ioe)
		{
			Log.log(Log.ERROR, this, ioe);
		}
	} // }}}
	
	// {{{ errorNotification() methods
	public void errorNotification(Exception e)
	{
		errorNotification(e.toString());
	}
	
	public void errorNotification(String s)
	{
		errorNotification(s, errorColor);
	}
		
	public void errorNotification(String s, Color color)
	{
		error.print(color, s);
	}
	// }}}
	
	// }}}
	
} // }}}
