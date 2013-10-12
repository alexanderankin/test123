/*
 * SimpleInputStreamTask.java - Thread for the process input handling.
 *
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999-2012 Slava Pestov, Alan Ezust, Marcelo Vanzin, Artem Bryantsev 
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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;
//}}}

/**
 * Thread for feeding input to a running subprocess.
 */
public class SimpleInputStreamTask extends StreamTask
{
	private PipedOutputStream userInput;
	private OutputStream processOutput;
	
	protected int SLEEP_DELAY = 100;
	
	// {{{ constructor
	public SimpleInputStreamTask(OutputStream processOutput, PipedOutputStream userInput)
	{
		super("Input thread");
		
		this.processOutput = processOutput;
		this.userInput = userInput;
	} // }}}

	// {{{ actionInsideWaitingLoop() method
	/**
	 * Run waiting loop inside.
	 */
	protected void actionInsideWaitingLoop(BufferedReader inr) throws Exception
	{
	} // }}}
	
	// {{{ beforeWorking() method
	/**
	 * Run BEFORE main working loop starts
	 * (under "try" section) 
	 */
	protected void beforeWorking() throws Exception
	{
	} // }}}
	
	// {{{ flushData() method
	/**
	 * Flush string of data to working process.
	 * @param _line string for flushing to working process
	 *
	 * (under "try" section) 
	 */
	protected void flushData(String _line) throws Exception
	{
		processOutput.write(_line.getBytes());
		processOutput.flush();
	} // }}}
	
	// {{{ afterWorking() method
	/**
	 * Run AFTER:
	 * - main working loop ends
	 * - "finalOutputing()" method
	 *
	 * (under "finally" section) 
	 */
	protected void afterWorking()
	{
	} // }}}
	
	// {{{ exception_dumpToLog() method
	/**
	 * By default dump data about exception to jEdit.Log
	 * BEFORE "exception_dumpToOwner()" method
	 * (under "catch" section)
	 */
	protected void exception_dumpToLog(Exception e)
	{
		Log.log(Log.ERROR, e, e);
	} // }}}
	
	// {{{ exception_dumpToOwner() method
	/**
	 * By default do nothing AFTER "exception_dumpToLog" method
	 * (under "catch" section)
	 */
	protected void exception_dumpToOwner(Exception e)
	{
	} // }}}
	
	// {{{ run() method
	public final void run()
	{
		String _line = "";
		
		try
		{
			beforeWorking();
			
			PipedInputStream pipeIn = new PipedInputStream(userInput);
			try
			{
				BufferedReader inr = new BufferedReader(new InputStreamReader(pipeIn));
				
				while ( !abortFlag )
				{
					try
					{
						/* wait until:                                           *
						 * - either the end of the input stream has been reached *
						 * - or user interrupts this thread.                     */
						while (true) // waiting loop
						{
							if (abortFlag)
							{
								throw new InterruptedException("Break the main loop: aborting");
							}
							else if ( inr.ready() )
							{
								if ((_line = inr.readLine()) == null)
								{
									throw new InterruptedException("Break the main loop: input stream is empty"); 
								}
								else
								{
									break; // break waiting loop only
								}
							}
							else if (finishFlag)
							{
								throw new InterruptedException("End the main loop.");
							}
							
							Thread.sleep(SLEEP_DELAY);
							
							actionInsideWaitingLoop(inr);
						}
					}
					catch (InterruptedException ie)
					{
						break;
					}
					
					_line += '\n'; // fixes "Press any key to continue . . ." (cjp)
					_line = new String(_line.getBytes(), jEdit.getProperty("console.encoding"));
					
					flushData(_line);
				}
			}
			finally
			{
				afterWorking();
				
				pipeIn.close(); // <-- throws IOExeption - is called last in this section 
			}
				
		}
		catch (Exception e)
		{
			exception_dumpToLog(e);

			exception_dumpToOwner(e);
		}
	} // }}}
}
