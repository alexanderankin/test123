/*
 * ErrorStreamTask.java - Thread for the process error handling.
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
import java.io.UnsupportedEncodingException;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;
// }}}


class ErrorStreamTask extends StreamTask
{
	//{{{ Private members
	private ConsoleProcessTask process;
	private InputStream in;
	private Color errorColor;
	//}}}
	
	//{{{ ErrorThread constructor
	ErrorStreamTask(ConsoleProcessTask process, InputStream in, Color errorColor)
	{
		this.process = process;
		this.in = in;
		this.errorColor = errorColor;
	} //}}}
	
	//{{{ run() method
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

		Output output = process.getErrorOutput();
		try
		{
			StringBuilder lineBuffer = new StringBuilder(100); 
			char[] input = new char[1024];
			int read = 0;
			
			process.streamStart(this);
			
			try
			{
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
							else if ( isr.ready() )
							{
								if ((read = isr.read(input, 0, input.length)) == -1)
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
							
							Thread.sleep(100);
						}
					}
					catch (InterruptedException ie)
					{
						break;
					}
					
					output.print( errorColor, lineBuffer.append(input, 0, read).toString() );
					lineBuffer.setLength(0);
				}
			}
			finally
			{
				if (lineBuffer.length() > 0)
				{
					output.print( errorColor, lineBuffer.toString() );
				}
				
				process.streamFinish(this);
			}
			
		}
		catch (Exception e)
		{
			if ( process.isForeground() )
			{
				Log.log(Log.ERROR, e, e);
			}
			
			process.errorNotification(e);
		}
	} //}}}
}
