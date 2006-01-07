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
import java.io.InputStream;
import java.io.InputStreamReader;
import org.gjt.sp.jedit.View;
import errorlist.DefaultErrorSource;

// }}}

/**
 * Parses the output of a running Process.
 * 
 * Refactored from ConsolePlugin.parseLine().
 * 
 * @author ezust
 * @since Java 1.5, Jedit 4.3
 * @version $Id$
 */

public class CommandOutputParser
{

	// {{{ Constructors
	/**
	 * Creates an instance of an output parser.
	 * An output parser will send coloured output to the Shell of the
	 * given View.
	 * 
	 * @param v - the current View
	 * @param output
	 */
	
	
	public CommandOutputParser(View v, Output out, DefaultErrorSource es)
	{
		console = ConsolePlugin.getConsole(v);
		output = out;
		color = console.getInfoColor();
		lastMatcher = null;
		done = false;
		view = v;
		errorSource = es;
		errorMatchers = ConsolePlugin.getErrorMatchers();

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
		color = console.getInfoColor();
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
				color = console.getErrorColor();
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
		output.writeAttrs(ConsolePane.colorAttributes(c), text + "\n" );
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

	private Output output;
	
	private DefaultErrorSource.DefaultError lastError = null;

	private View view;

	private DefaultErrorSource errorSource;

	private ErrorMatcher[] errorMatchers;

	private ErrorMatcher lastMatcher;
	
	private Console console;
	
	private Color color;

	private boolean done;

	// static final Pattern newLine = Pattern.compile("\r?\n");
	// }}}
}
