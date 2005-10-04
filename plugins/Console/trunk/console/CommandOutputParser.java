/*
 * CommandOutputParser.java - For processing output of shell commands 
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2005 Alan Ezust
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

// {{{ imports
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.gjt.sp.jedit.View;
import errorlist.DefaultErrorSource;

// }}}

/**
 * Parses the output of a running Process.
 * 
 * @author ezust
 * @since Java 1.5, Jedit 4.3
 */

public class CommandOutputParser
{

	// {{{ Constructors
	/**
	 * Creates an instance of an output parser.
	 * 
	 */
	public CommandOutputParser(View v, ConsoleProcess consoleP, DefaultErrorSource es)
	{
		consoleProcess = consoleP;
		console = consoleProcess.getConsole();
		color = console.getInfoColor();
		lastMatcher = null;
		done = false;
		view = v;
		errorSource = es;
		errorMatchers = ConsolePlugin.getErrorMatchers();

		stdout = consoleP.getMergedOutputs();
		reader = new InputStreamReader(stdout);
		breader = new BufferedReader(reader, 80);
	}

	// }}}

	// {{{ processLine();
	/**
	 * Process a line of input Checks all the regular expressions, sets the
	 * proper current color, changes directories if make patterns match.
	 * Adds errors to the ErrorList plugin if necessary.
	 * 
	 * @param text
	 *                a line of text
	 */
	public void processLine(String text)
	{
		if (text == null)
			return;
		if (directoryStack.processLine(text))
		{
			display(console.getWarningColor(), text);
			return;
		}
		String directory = directoryStack.current();
		if (lastError != null)
		{
			String message = null;
			if (lastMatcher != null
				&& lastMatcher.match(view, text, directory, errorSource) == null)
				message = lastMatcher.matchExtra(text);
			if (message != null)
			{
				display(text);
				lastError.addExtraMessage(message);
				return;
			}
			else
			{
				errorSource.addError(lastError);
				lastMatcher = null;
				lastError = null;
			}
		}
		color = consoleProcess.getConsole().getInfoColor();
		for (int i = 0; i < errorMatchers.length; i++)
		{
			ErrorMatcher m = errorMatchers[i];

			DefaultErrorSource.DefaultError error = m.match(view, text, directory,
				errorSource);
			if (error != null)
			{
				// Log.log(Log.WARNING,
				// CommandOutputParserThread.class, "New Error
				// in dir:" + directory);
				lastError = error;
				color = consoleProcess.getConsole().getErrorColor();
				lastMatcher = m;
				break;
			}
		}
		display(text);

	}

	// }}}

	// {{{ getColor()
	public Color getColor()
	{
		return color;
	}

	// }}}

	// {{{ setDirectory()

	public void setDirectory(String currentDirectory)
	{
		directoryStack.push(currentDirectory);
	}

	// }}}

	// {{{ unused code
	protected void display(Color c, String text)
	{
		if (text == null)
			return;
		// consoleProcess.getOutput().writeAttrs(ConsolePane.colorAttributes(c),
		// text + "\n" );

	}

	protected void display(String text)
	{
		if (text == null)
			return;
		/*
		 * consoleProcess.getOutput().writeAttrs(ConsolePane.colorAttributes(color),
		 * text + "\n");
		 */
	}

	/**
	 * Not used currently - we are using StreamThread.run() instead This
	 * version has some bugs in it.
	 */
	protected void run()
	{
		console = consoleProcess.getConsole();

		done = false;
		try
		{
			while (!done)
			{
				if (!consoleProcess.isRunning())
				{
					done = true;
					break;
				}
				console.updateAnimation();
				String text = breader.readLine();
				processLine(text);
			}
			breader.close();
		}
		catch (IOException ioe)
		{
			done = true;
		}

		consoleProcess.threadDone();
		Shell s = console.getShell();
		s.printPrompt(console, console.getOutput());
		console.setShell(s);

	}

	// }}}

	// {{{ finishErrorParsing()
	public void finishErrorParsing()
	{
		if (lastError != null)
		{
			done = true;
			errorSource.addError(lastError);
			lastError = null;
			lastMatcher = null;
		}

	}

	// }}}

	// {{{ Private Data Members
	private InputStreamReader reader;

	private BufferedReader breader;

	private DirectoryStack directoryStack = new DirectoryStack();

	private InputStream stdout;

	private DefaultErrorSource.DefaultError lastError = null;

	private View view;

	private DefaultErrorSource errorSource;

	private ErrorMatcher[] errorMatchers;

	private ErrorMatcher lastMatcher;

	private ConsoleProcess consoleProcess;

	private Console console;

	private Color color;

	private boolean done;

	// static final Pattern newLine = Pattern.compile("\r?\n");
	// }}}
}
