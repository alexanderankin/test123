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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.swing.text.AttributeSet;

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

	private StringBuilder lineBuffer;
	private boolean pendingCr;
	private int uncoloredWritten;
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
		copt = new CommandOutputParser(console.getView(), es, defaultColor);
		copt.setDirectory(currentDirectory);
		lineBuffer = new StringBuilder(100);
		pendingCr = false;
		uncoloredWritten = 0;
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
					if (pendingCr)
					{
						flushLine(output, "\r");
					}
					else if (lineBuffer.length() > 0)
					{
						flushLine(output, "");
					}
					break;
				}

				for (int i = 0; i < read; i++)
				{
					char c = input[i];
					if (c == '\n')
					{
						if (pendingCr)
						{
							flushLine(output, "\r\n");
						}
						else
						{
							flushLine(output, "\n");
						}
					}
					else
					{
						if (pendingCr)
						{
							flushLine(output, "\r");
						}
		
						if (c == '\r')
						{
							pendingCr = true;
						}
						else
						{
							lineBuffer.append(c);
						}
					}
				}

				// Following output shows unterminated lines
				// such as prompt of interactive programs.
				if (lineBuffer.length() > uncoloredWritten)
				{
					String tail = lineBuffer.substring(uncoloredWritten);
					output.writeAttrs(null, tail);
					uncoloredWritten += tail.length();
				}
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
			copt.finishErrorParsing();
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

	// {{{ flushLine() method
	private void flushLine(Output output, String eol)
	{
		// we need to write the line break to the output, but we
		// can't pass it to the "processLine()" method or the
		// regexps won't recognize anything.
		String line = lineBuffer.toString();
		copt.processLine(line);
		AttributeSet color = ConsolePane.colorAttributes(copt.getColor());
		if (uncoloredWritten > 0)
		{
			output.setAttrs(uncoloredWritten, color);
			output.writeAttrs(color,
				lineBuffer.substring(uncoloredWritten) + eol);
		}
		else try 
		{
			output.writeAttrs(color, line + eol);
		} catch (Exception err) {
			Log.log (Log.ERROR, this, "Can't Flush:", err);
		}

		lineBuffer.setLength(0);
		pendingCr = false;
		uncoloredWritten = 0;
	} //}}}

} // }}}

