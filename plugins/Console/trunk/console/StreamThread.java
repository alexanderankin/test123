/*
 * StreamThread.java - A running process
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

class StreamThread extends Thread
{
	//{{{ StreamThread constructor
	StreamThread(ConsoleProcess process, InputStream in)
	{
		this.process = process;
		this.currentDirectory = process.getCurrentDirectory();
		this.in = in;

		// for parsing error messages from 'make'
		currentDirectoryStack = new Stack();
		currentDirectoryStack.push(currentDirectory);
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

				handleInput(buf,offset,len);
			}
		}
		catch(Exception e)
		{
			if(!aborted)
			{
				Log.log(Log.ERROR,this,e);

				Console console = process.getConsole();
				Output error = process.getErrorOutput();

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
				in.close();
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

	//{{{ Private members
	private ConsoleProcess process;
	private boolean aborted;
	private InputStream in;
	private String currentDirectory;
	private Stack currentDirectoryStack; // for make

	private static RE makeEntering, makeLeaving;

	//{{{ handleInput() method
	private void handleInput(byte[] buf, int offset, int len)
		throws UnsupportedEncodingException
	{
		Console console = process.getConsole();
		Output output = process.getOutput();
                
		if(console == null || output == null)
			return;
                
		Color color = null;
                
		/* REMatch match = makeEntering.getMatch(line);
		if(match == null)
		{
			match = makeLeaving.getMatch(line);
			if(match == null)
			{
				String _currentDirectory;
				if(currentDirectoryStack.isEmpty())
				{
					// should not happen...
					_currentDirectory = currentDirectory;
				}
				else
					_currentDirectory = (String)currentDirectoryStack.peek();
                
				int type = ConsolePlugin.parseLine(
					console.getView(),line,
					_currentDirectory,
					console.getErrorSource());
				switch(type)
				{
				case ErrorSource.ERROR:
					color = console.getErrorColor();
					break;
				case ErrorSource.WARNING:
					color = console.getWarningColor();
					break;
				}
			}
			else if(!currentDirectoryStack.isEmpty())
				currentDirectoryStack.pop();
		}
		else
			currentDirectoryStack.push(match.toString(1)); */
                
		output.write(color,new String(buf,offset,len,"ASCII"));
	} //}}}

	//}}}

	//{{{ Class initializer
	static
	{
		try
		{
			makeEntering = new RE(jEdit.getProperty("console.error.make.entering"),
				0,RESearchMatcher.RE_SYNTAX_JEDIT);
			makeLeaving = new RE(jEdit.getProperty("console.error.make.leaving"),
				0,RESearchMatcher.RE_SYNTAX_JEDIT);
		}
		catch(REException re)
		{
			Log.log(Log.ERROR,ConsoleProcess.class,re);
		}
	} //}}}
}
