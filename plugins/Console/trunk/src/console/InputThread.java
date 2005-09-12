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
import java.io.InputStream;
import java.io.OutputStream;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;
//}}}

class InputThread extends Thread
{
	InputStream in;
	OutputStream out;
	Output error;
	ConsoleProcess process;
	boolean aborted;

	//{{{ InputThread constructor
	InputThread(ConsoleProcess process, OutputStream out)
	{
		this.process = process;
		this.error = process.getErrorOutput();
		this.in = process.getPipeInput();
		this.out = out;
	} //}}}

	//{{{ run() method
	public void run()
	{
		byte[] buf = new byte[4096];
		int offset = 0;

		try
		{
			for(;;)
			{
				if(aborted)
					break;

				int len = in.read(buf,offset,
					buf.length - offset);

				if(len <= 0)
					break;

				out.write(buf,offset,len);
				out.flush();
			}
		}
		catch(Exception e)
		{
			if(!aborted)
			{
				Log.log(Log.ERROR,this,e);

				Console console = process.getConsole();
				if(console != null)
				{
					String[] args = { e.toString() };
					error.print(console.getErrorColor(),
						jEdit.getProperty(
						"console.shell.error",args));
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

			process.threadDone();
		}
	} //}}}

	//{{{ abort() method
	void abort()
	{
		aborted = true;
		interrupt();
	} //}}}
}
