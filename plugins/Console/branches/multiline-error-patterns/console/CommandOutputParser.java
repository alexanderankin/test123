/*
 * CommandOutputParser.java - For processing output of shell commands
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2005 Alan Ezust
 * Copyright (C) 2010 Eric Le Lay
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
	boolean hitEnd;
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
	public int processLine(StringBuilder text, String eol, boolean disp)
	{
		boolean justHitEnd = false;
		int retval = -1;
		if (text == null)
			return -1;

		if (directoryStack.processLine(text))
		{
			if (disp){
				text.append(eol);
				display(color, text);
			}
			text.setLength(0);
			return ErrorSource.WARNING;
		}

		String directory = directoryStack.current();

		// Check if there was a previous error/warning to continue
		// extra line pattern is not allowed to need multi-line input
		if (lastError != null)
		{
			String message = null;
			if (lastMatcher != null
				&& lastMatcher.match(view, text, directory, errorSource) == null)
				message = lastMatcher.matchExtra(text);
			if (message != null)
			{
				lastError.addExtraMessage(message);
				if (disp){
					text.append(eol);
					display(getColor(),text);
				}
				text.setLength(0);
				hitEnd = false;
				return lastError.getErrorType();
			}
			else
			{
				if (errorSource.getErrorCount() == 0)
					ErrorSource.registerErrorSource(errorSource);
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
			else if(m.hitEnd())
			{
				justHitEnd = true;
			}
		}
		
		if(justHitEnd && lastError == null)
		{
			hitEnd = true;
			text.append(eol);
		}
		// was retaining some lines, didn't find an error
		// and no ErrorMatcher just hit end
		// => must display the first line and retry with every line after the first one
		else if(hitEnd && lastError==null)
		{
			hitEnd = false;
			retval = skipFirstLineAndReplay(text,eol,disp);
		}else{
			hitEnd = false;
			if(disp){
				text.append(eol);
				display(getColor(),text);
			}
			text.setLength(0);
		}
		
		return retval;
	}
	
	/**
	 * display the first line with defaultColor
	 * and reprocess text starting from the second line.
	 * */
	private int skipFirstLineAndReplay(StringBuilder text,String eol, boolean disp){
		int retval = -1;
		int beginSecondLine = firstEndOfLine(text);
		if(beginSecondLine >= 0){
			CharSequence oldLine; 
			if(beginSecondLine < text.length()-1 && text.charAt(beginSecondLine)=='\r'
					&& text.charAt(beginSecondLine)+1=='\n')
			{
				oldLine = text.subSequence(0,beginSecondLine+2);
				text.delete(0,beginSecondLine+2);
			}
			else
			{
				oldLine = text.subSequence(0,beginSecondLine+1);
				text.delete(0,beginSecondLine+1);
			}
			
			if(disp)display(defaultColor,oldLine);
			
			retval = replay(text,eol,disp);
		}else{
			// only one line without eol
			if(disp)
			{
				text.append(eol);
				display(defaultColor,text);
			}
			text.setLength(0);
		}
		return retval;
	}
	
	/**
	 * process the first line of text, then append each following line
	 * */
	private int replay(StringBuilder text, String eol,boolean disp){
		// only 1 empty line
		if(text.length() == 0 && !"".equals(eol))
		{
			return processLine(text,eol,disp);
		}
		// src will contain the text remaining to replay
		// text will contain the new lineBuffer
		// text can be consumed from the begining by processLine
		// and gets appended to from here.
		StringBuilder src = new StringBuilder(text);
		text.setLength(0);
		int retval = -1;
		//replay each line...
		while(src.length()>0){
			int beginSecondLine = firstEndOfLine(src);
			if(beginSecondLine >= 0){ // ==0 for empty lines
				String eofirstl;
				text.append(src.subSequence(0,beginSecondLine)); 
				if(beginSecondLine < text.length()-1 && text.charAt(beginSecondLine)=='\r'
						&& text.charAt(beginSecondLine)+1=='\n')
				{
					eofirstl = "\r\n";
					src.delete(0,beginSecondLine+2);
				}
				else
				{
					eofirstl = String.valueOf(src.charAt(beginSecondLine)); 
					src.delete(0,beginSecondLine+1);
				}
				retval = processLine(text,eofirstl,disp);
			}else{
				// last line
				text.append(src);
				return processLine(text,eol,disp);
			}
		}
		return retval;
	}
	
	/**
	 * @return	first index of CR or NL
	 * */
	private int firstEndOfLine(CharSequence text) {
		for(int i=0;i<text.length();i++){
			switch(text.charAt(i)){
			case '\n':
			case '\r':
				return i;
			default:
			}
		}
		return -1;
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

	// {{{ display
	protected void display(Color c, CharSequence text)
	{
		if (text == null)
			return;
		output.writeAttrs(ConsolePane.colorAttributes(c), text);
	} // }}}

	// {{{ finishErrorParsing()
	public void finishErrorParsing(StringBuilder text, boolean disp)
	{
		// one error could be still pending
		if (lastError != null)
		{
			errorSource.addError(lastError);
			lastError = null;
			lastMatcher = null;
		}
		// there may be some buffered lines in the pipeline.
		// force them out (one at a time at worst, if each of them triggers hitEnd() and none matches) 
		while(text.length()>0){
			skipFirstLineAndReplay(text,"",disp);
		}
		hitEnd = false;
	} // }}}

} // }}}
