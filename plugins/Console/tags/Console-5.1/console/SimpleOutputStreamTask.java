/*
 * SimpleOutputStreamTask.java - Thread for outputting data from InputStream to
 *                               Output using Color
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

// {{{ imports
import java.awt.Color;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;
// }}}

// {{{ class description
/**
<p>SimpleOutputStreamTask is a base outputting class. It gets a data from
   an external <i>InputStream</i> and dumps one to an external <i>Output</i>
   using a <i>defaultColor</i>.
   
   Main working method <b>run()</b> of this class is marked as <b>final</b>.
   On the other hand SimpleOutputStreamTask provides its descendants with
   a few methods, which descendants can override. 
   Below these methods are shown inside the <b>run()</b> method's body 
   
   run() {
       try {
      	   <b>beforeWorking()</b>
      	   
      	   try {
      	       MAIN-LOOP {
      	           try {
      	               WAITING-LOOP {
      	                  // reading from InputStream isr
      	                  // sleep
      	                  
      	                  <b>actionInsideWaitingLoop(isr)</b>
      	               }
      	           } catch (InterruptedException ie) {
      	               break // MAIN-LOOP
      	           }
      	           // lineBuffer's filling
      	           
      	           <b>outputData()</b>
      	       }
      	   } finally {
      	       <b>finalOutputting()</b>
      	       <b>afterWorking()</b>
      	   }
       } catch (Exception e) {
       	   <b>exception_dumpToLog(e)</b>
       	   <b>exception_dumpToOwner(e)</b>
       }
   }
</p>
 */
 // }}}
public class SimpleOutputStreamTask extends StreamTask
{
	private WaitingLoop waitingLoop;
	
	protected int BufferSize     = 1024;
	protected int SleepDelayMSec = 20;
	
	protected InputStream in;
	protected Color defaultColor;
	protected Output output;
	protected StringBuilder lineBuffer;
	
	// {{{ constructor
	public SimpleOutputStreamTask(InputStream in, Output output, Color defaultColor)
	{
		super("Output thread");
		
		this.in = in;
		this.output = output;
		this.defaultColor = defaultColor;
	} // }}}
	
	// {{{ actionInsideWaitingLoop() method
	/**
	 * Run waiting loop inside.
	 */
	protected void actionInsideWaitingLoop(InputStreamReader isr) throws Exception
	{
	} // }}}
	
	// {{{ afterWorking() method
	/**
	 * Run AFTER:
	 * - main working loop ends
	 * - "finalOutputing()" method
	 *
	 * (under "try" section) 
	 */
	protected void afterWorking() throws Exception
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
	
	// {{{ finalOutputting() method
	/**
	 * Dump remained data from internal buffer "lineBuffer"
	 * to output "output" with color "defaultColor".
	 * Run AFTER main working loop ends, but BEFORE "afterWorking()" method.
	 *
	 * (under "finally" section)
	 */
	protected void finalOutputting()
	{
		if (lineBuffer.length() > 0)
		{
			output.print( defaultColor, lineBuffer.toString() );
		}
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
	@Override
	public final void run()
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
		
		if (waitingLoop == null)
		{
			setWaitingLoop(WLTypes.nonsyncWL);
		}
		
		try
		{
			char[] input = new char[BufferSize];
			int read = 0;
			
			lineBuffer = new StringBuilder(BufferSize); 
			
			beforeWorking();
			
			try
			{
				while ( !abortFlag )
				{
					try
					{
						read = waitingLoop.readIfReady(isr, input, 0, input.length);
					}
					catch (InterruptedIOException iioe)
					{
						break;
					}
					catch (InterruptedException ie)
					{
						break;
					}
					
					lineBuffer.append(input, 0, read);
					
					outputData();
				}
			}
			finally
			{
				finalOutputting();
			}
			
			afterWorking();
			
		}
		catch (Exception e)
		{
			exception_dumpToLog(e);
			
			exception_dumpToOwner(e);
		}
	} // }}}

	// {{{ outputData() method
	/**
	 * - Dump collected data from internal buffer "lineBuffer"
	 *   to output "output" with color "defaultColor"
	 * - Clean internal buffer "lineBuffer"
	 *
	 * (under "try" section) 
	 */
	protected void outputData() throws Exception
	{
		output.print( defaultColor, lineBuffer.toString() );
		lineBuffer.setLength(0);
	} // }}}
	
	// {{{ setWaitingLoop() method
	/**
	 * Setup waiting loop.
	 * Output handler reads data from external stream inside this loop.
	 */
	public void setWaitingLoop(WLTypes wl)
	{
		switch (wl)
		{
			case syncWL :
				waitingLoop = new SynchronizedWL();
				break;
				
			case nonsyncWL :
				waitingLoop = new NonsynchronizedWL();
				break;
				
			case blockWL :
				waitingLoop = new BlockingWL();
				break;
				
			default:
				waitingLoop = new NonsynchronizedWL();
		}
	} // }}}
	
	// {{{ inner classes
	
	// {{{ enum WLTypes
	/** Types of waiting loop. */
	public enum WLTypes
	{
		/** nonblocking synchronized waiting loop */
		syncWL,
		
		/** nonblocking asynchronized waiting loop (default) */
		nonsyncWL,
		
		/** blocking asynchronized waiting loop */
		blockWL
	} // }}}
	
	// {{{ class WaitingLoop
	private class WaitingLoop
	{
		public int readIfReady(InputStreamReader isr, char[] input, int offset, int length)
			throws Exception
		{
			throw new InterruptedException("End the main loop.");
		}
	} // }}}
	
	// {{{ class NonsynchronizedWL
	private class NonsynchronizedWL extends WaitingLoop
	{
		@Override
		public int readIfReady(InputStreamReader isr, char[] input, int offset, int length)
			throws Exception
		{
			int result = 0;
			
			while (true) // waiting loop
			{
				if (SimpleOutputStreamTask.this.abortFlag)
				{
					throw new InterruptedException("Break the main loop: aborting");
				}
				else if ( isr.ready() )
				{
					if ( (result = isr.read(input, offset, length)) == -1 )
					{
						throw new InterruptedException("Break the main loop: input stream is empty"); 
					}
					else
					{
						break; // break waiting loop only
					}
				}
				else if (SimpleOutputStreamTask.this.finishFlag)
				{
					throw new InterruptedException("End the main loop.");
				}
				
				// sleep()
				Thread.sleep(SimpleOutputStreamTask.this.SleepDelayMSec);
				
				SimpleOutputStreamTask.this.actionInsideWaitingLoop(isr);
			}
			
			return result;
		}
	} // }}}
	
	// {{{ class SynchronizedWL
	private class SynchronizedWL extends WaitingLoop
	{
		@Override
		public int readIfReady(InputStreamReader isr, char[] input, int offset, int length)
			throws Exception
		{
			int result = 0;
			
			synchronized (SimpleOutputStreamTask.this.in)
			{
				while (true) // waiting loop
				{
					if (SimpleOutputStreamTask.this.abortFlag)
					{
						throw new InterruptedException("Break the main loop: aborting");
					}
					else if ( isr.ready() )
					{
						if ( (result = isr.read(input, offset, length)) == -1 )
						{
							throw new InterruptedException("Break the main loop: input stream is empty"); 
						}
						else
						{
							break; // break waiting loop only
						}
					}
					else if (SimpleOutputStreamTask.this.finishFlag)
					{
						throw new InterruptedException("End the main loop.");
					}
					
					// wait()
					SimpleOutputStreamTask.this.in.wait(SimpleOutputStreamTask.this.SleepDelayMSec);
					
					SimpleOutputStreamTask.this.actionInsideWaitingLoop(isr);
				}
			}
			
			return result;
		}
	} // }}}
	
	// {{{ class BlockingWL
	private class BlockingWL extends WaitingLoop
	{
		@Override
		public int readIfReady(InputStreamReader isr, char[] input, int offset, int length)
			throws Exception
		{
			SimpleOutputStreamTask.this.actionInsideWaitingLoop(isr);
			
			int result = isr.read(input, offset, length);
			
			if (result == -1)
			{
				throw new InterruptedException("Break the main loop: input stream is empty");
			}
			
			return result;
		}
	} // }}}
	
	// }}}
	
	// }}}
}
