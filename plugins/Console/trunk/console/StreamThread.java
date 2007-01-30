/*
 * StreamThread.java - A running process
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999, 2005 Slava Pestov, Alan Ezust, Marcelo Vanzin
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
import java.io.BufferedReader;
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
	// }}}

	// {{{ StreamThread constructor
	/**
	 * @param showStatus - prints the error status when the thread is finished.
	 */
	StreamThread(ConsoleProcess process, InputStream in, Color defaultColor)
	{
		this.process = process;
		this.in = in;
		String currentDirectory = process.getCurrentDirectory();
		Console console = process.getConsole();
		DefaultErrorSource es = console.getErrorSource();
		copt = new CommandOutputParser(console.getView(),	es, defaultColor);
		copt.setDirectory(currentDirectory);

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
		char oldchar = ' ';
		try
		{
			StringBuilder lb = new StringBuilder();
			do
			{
				int dat = isr.read();
				if(dat == -1 || aborted) break;

				char c = (char)dat;
				lb.append(c);

				if((c == '\n' && oldchar != '\r') || c == '\r')
				{
					process(lb, output);
				}
				oldchar = c;
			} while (!aborted);
			if (lb.length() > 0) {
				process(lb, output);
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
			try
			{
				in.close();
			}
			catch (IOException e2)
			{
			}

			process.threadDone();


		}
	} // }}}

	// {{{ abort() method
	void abort()
	{
		aborted = true;
		interrupt();
	} // }}}

	private void process(StringBuilder buf, Output output)
	{
		assert (buf != null && buf.length() > 0) : "buffer is empty";
		String _line = buf.toString();
		int length = _line.length();
		int end = length;
		if (length > 0 && _line.charAt(length - 1) == '\n') end--;
		if (length > 1 && _line.charAt(length - 2) == '\r') end--;

		if (end == length) {
			copt.processLine(_line);
		} else {
			copt.processLine(_line.substring(0, end));
		}
		output.writeAttrs(ConsolePane.colorAttributes(copt.getColor()), _line);
		buf.setLength(0);
	}

} // }}}

