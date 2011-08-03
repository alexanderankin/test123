/*
 * InputThread.java - A running process
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

//{{{ Imports
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;
//}}}

/**
 * Thread for feeding input to a running subprocess.
 */
class InputThread extends Thread
{
	InputStream inputPipe;
	OutputStream out;
	Output error;
	ConsoleProcess cProcess;
	boolean aborted;

	//{{{ InputThread constructor
	InputThread(ConsoleProcess process, OutputStream out)
	{
		this.cProcess = process;
		this.error = process.getErrorOutput();
		this.inputPipe = process.getPipeInput();
		this.out = out;
	} //}}}

	//{{{ run() method
	public void run()
	{
		BufferedReader inr = new BufferedReader(new InputStreamReader(inputPipe));
		String _line;
		try {
			while ( (_line = inr.readLine()) != null)
		
			{
				if (aborted)
					break;
				_line += '\n'; // fixes "Press any key to continue . . ." (cjp)
				_line = new String(_line.getBytes(), jEdit.getProperty("console.encoding"));
				out.write(_line.getBytes());
				try {
					out.flush() ;
				}
				catch (IOException flushe) {}
			}
		}
		catch(IOException e)
		{
			if(!aborted)
			{
				Log.log(Log.ERROR,this,e);

				Console console = cProcess.getConsole();
				if(console != null)
				{
					String[] args = { e.toString() };
					error.print(console.getErrorColor(),	
						jEdit.getProperty("console.shell.error",args));
				}
			}
		}
		finally
		{
			try
			{
				out.close();
			}
			catch(Exception e2)
			{
			}

			cProcess.threadDone();
		}
	} //}}}

	//{{{ abort() method
	void abort()
	{
		aborted = true;
		interrupt();
	} //}}}
}
