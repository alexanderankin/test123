package console.ssh;

// {{{ Imports

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.swing.text.AttributeSet;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;


import console.CommandOutputParser;
import console.Console;
import console.ConsolePane;
import console.ErrorOutput;
import console.Output;

import errorlist.DefaultErrorSource;

// }}}

/**
 * Thread for handing output of running remote ssh commands 
 *
 * @version $Id: StreamThread.java 10982 2007-11-06 05:02:42Z ezust $
 */

// {{{ class StreamThread
class StreamThread extends Thread
{

	private boolean aborted;
	private Console console;
	private InputStream in;
	CommandOutputParser copt = null;
	// Console output
	Output output;
	private StringBuilder lineBuffer;
	private boolean pendingCr;
	private int uncoloredWritten;
	// }}}

	// {{{ StreamThread constructor
	
	
	/**
	 * @param in - a stream to read things from, that we want to display.
	 */
	public StreamThread(Console console, InputStream in, Output output, Color defaultColor)
	{
		this.in = in;
		this.output = output;
		this.console = console;
		ConsoleState cs = ConnectionManager.getConsoleState(console);
		String currentDirectory = cs.getPath();
		DefaultErrorSource es = new SecureErrorSource(cs);
		copt = new CommandOutputParser(console.getView(), es, defaultColor);
		copt.setDirectory(currentDirectory);
		cs.setDirectoryChangeListener( copt);
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

		
		try
		{
			char[] input = new char[1024];
			while (!aborted)
			{
				int read = isr.read(input, 0, input.length);
//				Log.log(Log.MESSAGE, this, input);
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
				Output error = new ErrorOutput(console);
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
		else
		{
			output.writeAttrs(color, line + eol);
		}

		lineBuffer.setLength(0);
		pendingCr = false;
		uncoloredWritten = 0;
	} //}}}

} // }}}

