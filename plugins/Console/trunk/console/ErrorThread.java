/*
 * ErrorThread.java - A running process
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2012 contributors 
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;
// }}}

/**
 * @deprecated
 * Thread for handing error output of running subprocesses.
 * Write data to a Console's ErrorOutput. 
 */

//{{{ ErrorThread class
public class ErrorThread extends OutputThread
{
	//{{{ Private members
	private ConsoleProcess process;
	private InputStream in;
	private boolean aborted;
	private Color errorColor;
	//}}}
	
	//{{{ ErrorThread constructor
	ErrorThread(ConsoleProcess process, InputStream in, Color errorColor)
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
		StringBuilder lineBuffer = new StringBuilder(100); 
		try
		{
			char[] input = new char[1024];
			while (!aborted)
			{
				int read = isr.read(input, 0, input.length);
				if (aborted)
				{
					break;
				}
				else if (read == -1)
				{
					if (lineBuffer.length() > 0)
					{
						output.print( errorColor, lineBuffer.toString() );
					}
					break;
				}
				
				output.print( errorColor, lineBuffer.append(input, 0, read).toString() );
				lineBuffer.setLength(0);
				
				Thread.sleep(10);
			}
		}
		catch (Exception e)
		{
			if (!aborted)
			{
				if ( process.isForeground() )
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
			try
			{
				in.close();
			}
			catch (IOException e2)
			{
			}

			process.threadDone();
		}
	} //}}}
	
	//{{{ abort() method
	void abort()
	{
		aborted = true;
		interrupt();
	} //}}}
} //}}}
