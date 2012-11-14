/*
 * InputStreamTask.java - Thread for the process input handling.
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
class InputStreamTask extends StreamTask
{
	private PipedOutputStream userInput;
	OutputStream processOutput;
	ConsoleProcessTask process;

	//{{{ constructor
	InputStreamTask(ConsoleProcessTask process, OutputStream processOutput, PipedOutputStream userInput)
	{
		this.process = process;
		this.processOutput = processOutput;
		this.userInput = userInput;
	} //}}}

	//{{{ run() method
	public void run()
	{
		String _line = "";
		
		try
		{
			process.streamStart(this);
			
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
							
							Thread.sleep(100);
						}
					}
					catch (InterruptedException ie)
					{
						break;
					}
					
					_line += '\n'; // fixes "Press any key to continue . . ." (cjp)
					_line = new String(_line.getBytes(), jEdit.getProperty("console.encoding"));
					processOutput.write(_line.getBytes());
					processOutput.flush();
				}
			}
			finally
			{
				process.streamFinish(this);
				
				pipeIn.close(); // <-- throws IOExeption - is called last in this section 
			}
				
		}
		catch (Exception e)
		{
			Log.log(Log.ERROR, this, e);

			process.errorNotification(e);
		}
	} //}}}
}
