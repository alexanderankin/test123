/*
 * StreamThread.java - A running process
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999, 2005 Slava Pestov, Alan Ezust
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
import java.io.*;

import javax.swing.text.AttributeSet;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;

import errorlist.DefaultErrorSource;

// }}}

/**
 * Thread for handing output of running sub-processes.
 * 
 * @version $Id$
 */

class StreamThread extends Thread
{
	// {{{ Private members
	private ConsoleProcess process;

	private boolean aborted;

	private InputStream in;

	private StringBuffer _lineBuffer;

	CommandOutputParser copt = null;

	// private static RE makeEntering, makeLeaving;
	// }}}

	// {{{ StreamThread constructor
	StreamThread(ConsoleProcess process, InputStream in, Color defaultColor)
	{
		this.process = process;
		this.in = in;

		// for parsing error messages from 'make'
		String currentDirectory = process.getCurrentDirectory();
		Console console = process.getConsole();
		InputStream stdout = process.getMergedOutputs();
		DefaultErrorSource es = console.getErrorSource();
		copt = new CommandOutputParser(console.getView(),	es, defaultColor);
		copt.setDirectory(currentDirectory);

		_lineBuffer = new StringBuffer();
	} // }}}

	// {{{ run() method
	public void run()
	{
		byte[] buf = new byte[4096];

		try
		{
			for (;;)
			{
				if (aborted)
					break;

				int len = in.read(buf, 0, buf.length);

				if (len <= 0 || process.getConsole() == null
					|| process.getOutput() == null)
					break;

				handleInput(buf, len);
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

	// {{{ handleInput() method
	/** This method is actually not properly named - it is handling the 
	 *   OUTPUT of a running process.
	 *   
	 *   TODO: rename to handleOutput.
	 */
	private void handleInput(byte[] buf, int len) throws UnsupportedEncodingException
	{
//		Console console = process.getConsole();
		Output output = process.getOutput();

		/*
		 * We consider \r\n to be one line, not two, for error parsing
		 * purposes. lastCR is true if the previous character was a \r
		 */
		boolean lastCR = false;
		int lastOffset = 0;
		for (int i = 0; i < len; i++)
		{
			char ch = (char) (buf[i] & 0xFF);
			if (ch == '\n')
			{
				if (lastCR)
				{
					lastCR = false;
					lastOffset = i + 1;
				}
				else
				{
					handleLine(_lineBuffer);
					output.writeAttrs(ConsolePane.colorAttributes(copt.getColor()), 
						new String(buf, lastOffset, i - lastOffset, jEdit
							.getProperty("console.encoding")));
					output.writeAttrs(null, "\n");
					lastOffset = i + 1;

				}
			}
			else if (ch == '\r')
			{
				handleLine(_lineBuffer);
				output.writeAttrs(ConsolePane.colorAttributes(copt.getColor()), 
					new String(buf, lastOffset, i - lastOffset, jEdit
						.getProperty("console.encoding")));
				output.writeAttrs(null, "\n");
				lastOffset = i + 1;
				lastCR = true;
			}
			else
			{
				_lineBuffer.append(ch);
				lastCR = false;
			}
		}

		if (lastOffset != len)
		{
			output.writeAttrs(ConsolePane.colorAttributes(copt.getColor()), new String(
				buf, lastOffset, len - lastOffset, jEdit
					.getProperty("console.encoding")));
		}
	} // }}}

	// {{{ handleLine() method
	/**
	 * @buf a buffer which will be read, and clobbered.
	 */
	private int handleLine(StringBuffer buf) throws UnsupportedEncodingException
	{
		String line = new String(buf.toString().getBytes("ISO8859_1"), jEdit
			.getProperty("console.encoding"));

		buf.setLength(0);
		return copt.processLine(line);
	} 
	// }}}
}
