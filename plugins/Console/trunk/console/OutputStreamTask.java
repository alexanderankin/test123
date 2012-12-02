/*
 * OutputStreamTask.java - Thread for the process output handling.
 *
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999-2012 Slava Pestov, Alan Ezust, Marcelo Vanzin, Artem Bryantsev 
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.regex.*;
import java.util.ArrayList;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.StringList;

import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;

import jcfunc.Description;
// }}}

// {{{ class OutputStreamTask
class OutputStreamTask extends StreamTask
{
	// {{{ Private members
	private ConsoleProcessTask process;

	private InputStream in;
	CommandOutputParser copt = null;
	AnsiEscapeParser ansiescp = null;
	
	private final int DEFAULT = CommandOutputParser.DEFAULT;
	private final int WARNING = ErrorSource.WARNING;
	private final int ERROR   = ErrorSource.ERROR;
	
	private StringBuilder lineBuffer;
	private int shiftUCW;
	private Pattern eolPattern;
	private SimpleAttributeSet commonAttrs;
	private SimpleAttributeSet defaultAttrs, warningAttrs, errorAttrs;
	// }}}

	// {{{ constructor
	OutputStreamTask(ConsoleProcessTask process, InputStream in, Color defaultColor)
	{
		this.process = process;
		this.in = in;
		
		String currentDirectory = process.getCurrentDirectory();
		Console console = process.getConsole();
		DefaultErrorSource es = console.getErrorSource();
		
		copt = new CommandOutputParser(console.getView(), es, defaultColor);
		copt.setDirectory(currentDirectory);
		
		ansiescp = new AnsiEscapeParser(defaultColor, console.getConsolePane().getBackground());
		
		lineBuffer = new StringBuilder(100);
		shiftUCW = 0;
		
		eolPattern = Pattern.compile("\n");
		
		commonAttrs = new SimpleAttributeSet();
		StyleConstants.setBackground(commonAttrs, console.getConsolePane().getBackground());
		StyleConstants.setForeground(commonAttrs, defaultColor);
		
		defaultAttrs = setDefaultAttrs(null);
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
			int read = 0;
			
			process.streamStart(this);
			
			try
			{
				while ( !abortFlag ) // working loop
				{
					try
					{
						/* wait until:                                           *
						 * - either the end of the input stream has been reached *
						 * - or user interrupts this thread.                     */
						while (true) // waiting loop
						{
							if (abortFlag)
							{
								throw new InterruptedException("Break the main loop: aborting");
							}
							else if ( isr.ready() )
							{
								if ((read = isr.read(input, 0, input.length)) == -1)
								{
									throw new InterruptedException("Break the main loop: input stream is empty"); 
								}
								else
								{
									break; // break waiting loop only
								}
							}
							else if (finishFlag)
							{
								throw new InterruptedException("End the main loop.");
							}
							
							Thread.sleep(100);
						}
					}
					catch (InterruptedException ie)
					{
						break; // break working loop
					}
					
					String line = lineBuffer.append(input, 0, read).toString();		// "111\n222\n333\n444"
					
					// convert all line breaks to internal "standard": "\n"
					if ( ConsolePane.eolExchangeRequired() )
					{
						lineBuffer.setLength(0);
						line = lineBuffer.append( ConsolePane.eolExchanging(line) ).toString();
					}
				
					// remove all ANSI escape sequences
					if ( ansiescp.touch(AnsiEscapeParser.Behaviour.REMOVE_ALL, line) )
					{
						lineBuffer.setLength(0);
						line = lineBuffer.append( ansiescp.removeAll(line) ).toString();
					}
					
					Matcher matcher = eolPattern.matcher(line);
					
					int bPosition = 0;
					while ( !abortFlag && matcher.find() )
					{									// -> "111\n" -> "222\n" -> "333\n"
						printString(output, line.substring( bPosition, matcher.end() ), false);
						bPosition = matcher.end();
					}
					// remove already printed substrings
					if (bPosition > 0)
					{
						lineBuffer.delete(0, bPosition);
					}
					
					// there is some unterminated "tail":  -> "444"
					if (abortFlag)
					{
						break; // break working loop
					}
					else if ( lineBuffer.length() > 0 )
					{
						if ( !isr.ready() )
						{
							Thread.sleep(50);
							
							if ( !isr.ready() )
							{
								printString(output, lineBuffer.toString(), true);
								lineBuffer.setLength(0);
							}
						}
					}
				}
			}
			finally
			{
				if (lineBuffer.length() > 0)
				{
					printString(output, lineBuffer.toString(), false);
				}
				
				copt.finishErrorParsing();
				
				process.streamFinish(this);
			}
			
		}
		catch (Exception e)
		{
			Log.log(Log.ERROR, e, e);
			
			process.errorNotification(e);
		}
		
	} // }}}
	
	//{{{ printString() method
	/*
	 * output - Console/Buffer output
	 * str    - printed string
	 * isUnterminated - flag: is 'str' unterminated string?
	 */
	private void printString(Output output, String str, boolean isUnterminated)
	{
		ArrayList<Description> seqs = null;
		SimpleAttributeSet currentAttrs = null;
		
		//{{{ style's relations
/*      escape-Attrs      commonAttrs
 *          |               |    |
 *          +-------+-------+    |
 *                  |            +-----+------------------+
 *                  |                  |                  |
 *            defaultAttrs        warningAttrs       errorAttrs
 *                  |                  |                  |
 *                  +------------------+------------------+
 *                                     |
 *                                currentAttrs
 */
    	//}}}
		
		// look for ANSI escape sequences 
		if ( ansiescp.touch(AnsiEscapeParser.Behaviour.PARSE, str) )
		{
			seqs = ansiescp.parse(str, true);
			str  = ansiescp.removeAll(str); // => str's length is changed
		}
		
		// error/warning parser
		int errorStatus = copt.processLine(str);
		
		// choose color of full line 
		if (isUnterminated)
		{
			shiftUCW += str.length();
		}
		else
		{
			currentAttrs = updateCurrentAttrs( errorStatus, copt.getColor() );
			
			if ( shiftUCW > 0 )
			{
				output.setAttrs(shiftUCW, currentAttrs);
				shiftUCW = 0;
			}
		}
		
		// write line to output with/without color
		try 
		{
			if (seqs == null) // no any ANSI escape sequences
			{
				output.writeAttrs(currentAttrs, str);
			}
			else 
			{
				boolean firstFlushed = false;
				
				// go over functions
				for ( Description descr: seqs )
				{
					// if first sequence does not situated at line's begining
					// should output preceding substring
					if ( !firstFlushed )
					{
						if (descr.bPosition > 0) {
							flushSubstring(output, str, currentAttrs, 0, descr.bPosition);
						}
						firstFlushed = true;
					}
					
					switch (descr.function)
					{
						case SGR:
							// new default style's creation:
							//   1. always cancel previous escape-style
							//   2. if no any escape-styles (resetting) - setup commonAttrs
							defaultAttrs = setDefaultAttrs( ansiescp.processSGRparameters(descr.parameters, defaultAttrs) );
							currentAttrs = updateCurrentAttrs( errorStatus, copt.getColor() );
							
							flushSubstring(output, str, currentAttrs, descr.bPosition, descr.ePosition);
							break;
						case NUL:
						default :
							flushSubstring(output, str, currentAttrs, descr.bPosition, descr.ePosition);	
							break;
					}
				}
			}
		}
		catch (Exception err)
		{
			Log.log (Log.ERROR, this, "Can't Flush:", err);
		}		
	} //}}}
	
	//{{{ flushSubstring() method
	private void flushSubstring(Output output, String str, SimpleAttributeSet localAttrs, int bpos, int epos)
	{
		if (epos - bpos > 0)
			output.writeAttrs( localAttrs, str.substring(bpos, epos) );
	} //}}}
	
	//{{{ setDefaultAttrs() method
	private SimpleAttributeSet setDefaultAttrs(SimpleAttributeSet newAttrs)
	{
		return newAttrs == null ? commonAttrs : newAttrs;
	} //}}}
	
	//{{{ setNondefaultAttrs() method
	private SimpleAttributeSet setNondefaultAttrs(SimpleAttributeSet nondefAttrs, Color color)
	{
		if (nondefAttrs == null)
		{
			nondefAttrs = new SimpleAttributeSet(commonAttrs);
			StyleConstants.setForeground(nondefAttrs, color);
		}
		
		return nondefAttrs;
	} //}}}
	
	//{{{ updateCurrentAttrs() method
	private SimpleAttributeSet updateCurrentAttrs(int errorStatus, Color errorColor)
	{
		switch (errorStatus)
		{
			case DEFAULT:
				return defaultAttrs;
			case WARNING:
				warningAttrs = setNondefaultAttrs(warningAttrs, errorColor);
				return warningAttrs;
			case ERROR:
				errorAttrs   = setNondefaultAttrs(errorAttrs  , errorColor);
				return errorAttrs;
		}
		
		return defaultAttrs;
	} //}}}
} // }}}

