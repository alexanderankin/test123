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
import org.gjt.sp.jedit.View;
import errorlist.DefaultErrorSource;
import errorlist.DefaultErrorSource.DefaultError;
import errorlist.ErrorSource;

// }}}

/**
 * Parses the output of a running Process.
 *
 * Refactored from ConsolePlugin.parseLine().
 * This class contains all code related to parsing the output of console commands.
 * Derived shells can return extended versions of this for handling their own
 * Output processing by overriding @ref Shell.createOutputParser()
 *
 * @author ezust
 * @since Console 4.2
 * @version $Id$
 */
// {{{ class CommandOutputParser
public class CommandOutputParser
{
	// {{{ data members
	DirectoryStack directoryStack = new DirectoryStack();
	Output output;
	protected DefaultError lastError = null;
	View view;
	DefaultErrorSource errorSource;
	ErrorListModel errorMatchers = ErrorListModel.load();
	ErrorMatcher lastMatcher;
	protected Console console;
	Color defaultColor;
	Color color;
	// }}}

	// {{{ Constructors
	/**
	 * Creates an instance of an output parser.
	 * An output parser will send coloured output to the Shell of the
	 * given View.
	 *
	 * @param v - the current View
	 * @param es - An ErrorSource which corresponds to the plugin which is generating the errors.
	 * @param defaultColor - the default color to use when errors are not found
	 * 
	 * TODO: Use the es to determine which errormatchers to look at?
	 */

	public CommandOutputParser(View v, DefaultErrorSource es, Color defaultColor)
	{
		console = ConsolePlugin.getConsole(v);
		output = console.getOutput();
		this.defaultColor = defaultColor;
		this.color = defaultColor;
		lastMatcher = null;
		view = v;
		errorSource = es;
	}

	// }}}


	// {{{ processLine methods
	/** 
	 * Processes a line without displaying it to the Output
	 */
	final public int processLine(String text) {
		return processLine(text, false);
	}

	/**
	 * Process a line of input. Checks all the enabled ErrorMatchers'
	 *  regular expressions, sets the  proper current color,
	 *  changes directories if there are chdir patterns found.
	 * Adds errors to the ErrorList plugin if necessary.
	 *
	 * @param text a line of text
	 * @param disp if true, will also send to the Output.
	 * @return -1 if there is no error, or ErrorSource.WARNING,
	 *       or ErrorSource.ERROR if there is a warning or an error found in text.
	 */
	public int processLine(String text, boolean disp)
	{
		int retval = -1;
		if (text == null)
			return -1;

		if (directoryStack.processLine(text))
		{
			if (disp) display(color, text);
			return ErrorSource.WARNING;
		}

		String directory = directoryStack.current();

		// Check if there was a previous error/warning to continue
		if (lastError != null)
		{
			String message = null;
			if (lastMatcher != null
				&& lastMatcher.match(view, text, directory, errorSource) == null)
				message = lastMatcher.matchExtra(text);
			if (message != null)
			{
				lastError.addExtraMessage(message);
				return lastError.getErrorType();
			}
			else
			{
				errorSource.addError(lastError);
				lastMatcher = null;
				lastError = null;
			}
		}
		color = defaultColor;
		for (ErrorMatcher m: errorMatchers.m_matchers) 
		{
			DefaultError error = m.match(view, text, directory,
				errorSource);

			/* We found a match, but we do not want to print anything
			    until we have finished continuing lines. */
			if (error != null)
			{
				// Log.log(Log.WARNING,
				// CommandOutputParserThread.class, "New Error
				// in dir:" + directory);
				lastError = error;
				lastMatcher = m;
				int type = lastError.getErrorType();
				if (type == ErrorSource.ERROR)
				{
					color = console.getErrorColor();
				}
				else if (type == ErrorSource.WARNING)
				{
					color = console.getWarningColor();
				}
				break;
			}
		}
		if (disp) display(text);
		return retval;

	}
	// }}}

	// {{{ getColor() method
	public Color getColor()
	{
		return color;
	} // }}}

	// {{{ setDirectory()
	public void setDirectory(String currentDirectory)
	{
		directoryStack.push(currentDirectory);
	} // }}}

	// {{{ display (unused)
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
		output.writeAttrs(ConsolePane.colorAttributes(color), text + "\n" );
	} // }}}


	// {{{ finishErrorParsing()
	public void finishErrorParsing()
	{
		if (lastError != null)
		{
			errorSource.addError(lastError);
			lastError = null;
			lastMatcher = null;
		}
	} // }}}


	// static final Pattern newLine = Pattern.compile("\r?\n");
} // }}}
