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
import gnu.regexp.*;
import java.awt.Color;
import java.io.*;
import java.util.Stack;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.search.RESearchMatcher;
import org.gjt.sp.util.Log;
import errorlist.*;
//}}}

class InputThread extends Thread
{
	boolean aborted;
	String input;
	BufferedWriter out;
	String lineSep;
	Output error;
	ConsoleProcess process;

	//{{{ InputThread constructor
	InputThread(ConsoleProcess process, String input,
		OutputStream outputStream)
	{
		this.process = process;
		this.input = input;
		this.error = process.getErrorOutput();
		out = new BufferedWriter(new OutputStreamWriter(
			outputStream));
		lineSep = System.getProperty("line.separator");
	} //}}}

	//{{{ run() method
	public void run()
	{
		try
		{
			if(input != null)
			{
				for(int i = 0; i < input.length(); i++)
				{
					char ch = input.charAt(i);
					if(ch == '\n')
						out.write(lineSep);
					else
						out.write(ch);
				}
			}
			out.close();
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

			try
			{
				out.close();
			}
			catch(Exception e2)
			{
			}
		}
		finally
		{
			process.threadDone();
		}
	} //}}}

	//{{{ abort() method
	public void abort()
	{
		aborted = true;
		/* try
		{
			out.close();
		}
		catch(IOException io)
		{
		} */
	} //}}}
}
