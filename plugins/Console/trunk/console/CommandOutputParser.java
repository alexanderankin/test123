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
import errorlist.ErrorSource;

// }}}

/**
 * Parses the output of a running Process.
 * 
 * Refactored from ConsolePlugin.parseLine().
 * This class contains all code related to parsing the output of console commands.
 * 
 * @author ezust
 * @since Console 4.2
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
	
	public CommandOutputParser(View v, DefaultErrorSource es)
	{
		console = ConsolePlugin.getConsole(v);
		output = console.getOutput();
		color = console.getInfoColor();
		lastMatcher = null;
		view = v;
		errorSource = es;
	}

	// }}}

	
	public int processLine(String text) {
		return processLine(text, true);
	}
	
	// {{{ processLine();
	/**
	 * Process a line of input. Checks all the enabled ErrorMatchers'
	 *  regular expressions, sets the  proper current color, 
	 *  changes directories if there are chdir patterns found. 
	 * Adds errors to the ErrorList plugin if necessary.
	 * 
	 * @param text a line of text
	 * @param disp if true, will also send to the Output. 
	 */
	public int processLine(String text, boolean disp)
	{
		int retval = -1;
		if (text == null)
			return -1;
		
		if (directoryStack.processLine(text))
		{
			display(console.getWarningColor(), text);
			return ErrorSource.WARNING;
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
				return lastError.getErrorType();
			}
			else
			{
				errorSource.addError(lastError);
				retval = lastError.getErrorType();
				lastMatcher = null;
				lastError = null;
				return retval;
			}
		}
		color = console.getInfoColor();
		for (ErrorMatcher m: errorMatchers.m_matchers.values()) {
			DefaultErrorSource.DefaultError error = m.match(view, text, directory,
				errorSource);
			
			/* We found a match, but we do not want to print anything
			    until we have finished continuing lines. */
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
		if (disp) display(text);
		return retval;

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
			errorSource.addError(lastError);
			lastError = null;
			lastMatcher = null;
		}

	}

	// }}}

	private DirectoryStack directoryStack = new DirectoryStack();

	private Output output;

	private DefaultErrorSource.DefaultError lastError = null;

	private View view;

	private DefaultErrorSource errorSource;

	private ErrorListModel errorMatchers;

	private ErrorMatcher lastMatcher;
	
	private Console console;
	
	private Color color;

	// static final Pattern newLine = Pattern.compile("\r?\n");
	// }}}
}
