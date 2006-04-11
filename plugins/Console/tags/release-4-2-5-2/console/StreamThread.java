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
		DefaultErrorSource es = console.getErrorSource();
		copt = new CommandOutputParser(console.getView(),	es, defaultColor);
		copt.setDirectory(currentDirectory);

		_lineBuffer = new StringBuffer();
	} // }}}

	// {{{ run() method
	public void run()
	{
		BufferedReader inr = new BufferedReader(new InputStreamReader(in));
		Output output = process.getOutput();

		try
		{
			String _line;
			while ( (_line = inr.readLine()) != null)
			{
				if (aborted)
					break;

				_line = new String(_line.getBytes(),
							jEdit.getProperty("console.encoding"));
				copt.processLine(_line);
				output.writeAttrs(ConsolePane.colorAttributes(copt.getColor()),
						  _line);
				output.writeAttrs(null, "\n");
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
}

