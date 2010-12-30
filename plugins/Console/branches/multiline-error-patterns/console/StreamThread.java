/*
 * StreamThread.java - A running process
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999, 2005 Slava Pestov, Alan Ezust, Marcelo Vanzin
 * Copyright (C) 2010 Eric Le Lay
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import errorlist.DefaultErrorSource;

// }}}

/**
 * Thread for handing output of running sub-processes.
 *
 * @version $Id$
 */

// {{{ class StreamThread
class StreamThread extends Thread
{
	// {{{ Private members
	private ConsoleProcess process;

	private boolean aborted;

	private InputStream in;
	CommandOutputParser copt = null;

	private StringBuilder lineBuffer;
	private boolean pendingCr;
	private WatchDog watchdog;
	// }}}

	// {{{ StreamThread constructor
	/**
	 */
	StreamThread(ConsoleProcess process, InputStream in, Color defaultColor)
	{
		this.process = process;
		this.in = in;
		String currentDirectory = process.getCurrentDirectory();
		Console console = process.getConsole();
		DefaultErrorSource es = console.getErrorSource();
		copt = new CommandOutputParser(console.getView(), es, defaultColor);
		copt.setDirectory(currentDirectory);
		lineBuffer = new StringBuilder(100);
		pendingCr = false;
	} // }}}

	// {{{ run() method
	public void run()
	{
		InputStreamReader isr = null;
		try
		{
			 isr = new InputStreamReader(in, jEdit.getProperty("console.encoding") );
		}
		catch (UnsupportedEncodingException uee)
		{
			throw new RuntimeException(uee);
		}

		Output output = process.getOutput();
		copt.output = output;
		// waiting 800ms before flushing current line seems reasonable
		long watchdogPeriod = jEdit.getIntegerProperty("console.watchdog.period", 800);
		watchdog = new WatchDog(watchdogPeriod);
		watchdog.start();
		try
		{
			char[] input = new char[1024];
			while (!aborted)
			{
				int read = isr.read(input, 0, input.length);
				
				synchronized(lineBuffer){
					
					if (aborted)
					{
						break;
					}
					else if (read == -1)
					{
						if (pendingCr)
						{
							flushLine(output, "\r");
						}
						else if (lineBuffer.length() > 0)
						{
							flushLine(output, "");
						}
						break;
					}
	
					for (int i = 0; i < read; i++)
					{
						char c = input[i];
						if (c == '\n')
						{
							if (pendingCr)
							{
								flushLine(output, "\r\n");
							}
							else
							{
								flushLine(output, "\n");
							}
						}
						else
						{
							if (pendingCr)
							{
								flushLine(output, "\r");
							}
			
							if (c == '\r')
							{
								pendingCr = true;
							}
							else
							{
								lineBuffer.append(c);
							}
						}
					}
					
				} // end synchronized

			}
		}
		catch (Exception e)
		{
			if (!aborted)
			{
				Log.log(Log.ERROR, e, e);
				Console console = process.getConsole();
				Output error = process.getErrorOutput();
				if (console != null)
				{
					String[] args = { e.toString() };
					error.print(console.getErrorColor(), jEdit.getProperty(
						"console.shell.error", args));
				}
			}
		}
		finally
		{
			copt.finishErrorParsing(lineBuffer,true);
			try
			{
				in.close();
			}
			catch (IOException e2)
			{
			}

			watchdog.interrupt();
			process.threadDone();


		}
	} // }}}

	// {{{ abort() method
	void abort()
	{
		aborted = true;
		interrupt();
	} // }}}

	// {{{ flushLine() method
	private void flushLine(Output output, String eol)
	{
		// we need to write the line break to the output, but we
		// can't concatenate to the lineBuffer, otherwise
		// regexps won't recognize anything.
		// actually, thats not true for error patterns since we use lookingAt()
		// but still true for warnings...
		int newStart = copt.processLine(lineBuffer, eol, true);
		if(newStart > 0)lineBuffer.delete(0, newStart);
		pendingCr = false;
		// marking now, because a line truly has been processed
		watchdog.mark = true;
	} //}}}

	/**
	 * callback for the watchdog : tell the CommandOutputParser
	 * to flush the current lineBuffer.
	 * Synchronized on lineBuffer, so no interleaving with processing linebuffer in the StreamThread
	 * thread will happen.
	 **/
	void flushLineBuffer()
	{
		synchronized(lineBuffer)
		{
			if (lineBuffer.length() > 0)
			{
				System.err.println("flushing '"+lineBuffer+"'");
				copt.flushBuffer(lineBuffer,true);
			}			
		}
	}
	
	/**
	 * watchdog to flush buffered lines once there has been no output for
	 * some time.
	 * This is necessary to show the command prompt in interactive programs.
	 * */
	private class WatchDog extends Thread
	{
		volatile boolean mark;
		private long sleep;
		
		WatchDog(long sleep){
			if(sleep<100)throw new IllegalArgumentException("WatchDog sleep time shouldn't be less than 100ms");
			this.sleep = sleep;
		}
		
		/**
		 * while not interrupted, sleep for some time and flush the buffer if it has
		 * not been processed since last pass.
		 * marking as processed is done in flushLine() in the StreamThread thread.
		 * */
		public void run()
		{
			try
			{
				while(true)
				{
					Thread.sleep(sleep);
					if(!mark)
					{
						flushLineBuffer();
					}
					mark = false;
				}
			}
			catch(InterruptedException e)
			{
				/* clean exit */
			}
		}
	}
} // }}}

