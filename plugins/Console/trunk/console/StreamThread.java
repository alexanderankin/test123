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

import java.awt.Color;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

/**
 * NOT USED in JEDIT 4.3
 * @deprecated use CommandOutputParserThread instead.
 */

class StreamThread extends Thread
{
	// {{{ Private members
	private ConsoleProcess process;

	private boolean aborted;

	private InputStream in;

	private StringBuffer lineBuffer;

	private Color defaultColor;

	// {{{ StreamThread constructor
	StreamThread(ConsoleProcess process, InputStream in, Color defaultColor)
	{
		this.process = process;
		// process.getOutput()

		this.in = in;
		this.defaultColor = defaultColor;
		lineBuffer = new StringBuffer();
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
		} catch (Exception e)
		{
			if (!aborted)
			{
				Log.log(Log.ERROR, this, e);

				Console console = process.getConsole();
				Output error = process.getErrorOutput();

				if (console != null)
				{
					String[] args = { e.toString() };
					error.print(console.getErrorColor(), jEdit.getProperty(
							"console.shell.error", args));
				}
			}
		} finally
		{
			try
			{
				in.close();
			} catch (Exception e2)
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
	private void handleInput(byte[] buf, int len)
			throws UnsupportedEncodingException
	{
		// Console console = process.getConsole();
		Output output = process.getOutput();

		Color color = defaultColor;

		/*
		 * We consider \r\n to be one line, not two, for error parsing purposes.
		 */
		boolean lastCR = false;
		int lastOffset = 0;

		for (int i = 0; i < len; i++)
		{
			char ch = (char) buf[i];
			if (ch == '\n')
			{
				if (lastCR)
				{
					lastCR = false;
					lastOffset = i + 1;
				} else
				{
					output
							.writeAttrs(ConsolePane.colorAttributes(color),
									new String(buf, lastOffset, i - lastOffset,
											"ASCII"));
					output.writeAttrs(null, "\n");
					lastOffset = i + 1;
					handleLine(lineBuffer);
				}
			} else if (ch == '\r')
			{
				handleLine(lineBuffer);
				output.writeAttrs(ConsolePane.colorAttributes(color),
						new String(buf, lastOffset, i - lastOffset, "ASCII"));
				output.writeAttrs(null, "\n");
				lastOffset = i + 1;
				lastCR = true;
			} else
			{
				lineBuffer.append(ch);
				lastCR = false;
			}
		}

		if (lastOffset != len)
		{
			output.writeAttrs(ConsolePane.colorAttributes(color), new String(
					buf, lastOffset, len - lastOffset, "ASCII"));
		}
	} // }}}

	// {{{ handleLine() method
	private void handleLine(StringBuffer buf)
	{
		String line = buf.toString();

		buf.setLength(0);
		Console console = process.getConsole();
/*		ConsolePlugin.parseLine(console.getView(), line, console
				.getErrorSource()); */
	}

}
