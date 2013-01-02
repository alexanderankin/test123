/*
 * ParsingOutputStreamTask.java - Outputting thread using an error parrser
 *                                and an ANSI escaped sequencies parser 
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
import java.util.regex.*;
import java.util.ArrayList;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;

import errorlist.ErrorSource;
import errorlist.DefaultErrorSource;

import jcfunc.Description;
// }}}

/**
   ParsingOutputStreamTask is an outputting thread using an error parrser and
   an ANSI escaped sequencies parser.
   User can set parsers either at the moment of class's creating or after class'
   creation. If some parser is not defined - the class does not use that parser.
   
   A char's sequence from an InputStream (size == SimpleOutputStreamTask.BufferSize)
   is divided by line's breaks (if any), because the error parser might work
   with individual lines only, not the whole sequence - in common case
   InputStream can produce really many individual lines. On the other hand, each
   individual line is output in EDT (AWT-EventQueue). So, if InputStream produces
   a lot of lines - most of the time EDT processes them, and not other events
   of the jEdit's interface: it will hang until all the generated lines will
   be processed. However, this can be avoided if the output are not the individual
   lines, but an array of them. In this case, the number of events that must
   be processed EDT, sharply reduced.
   For this reason after processing of each individual line ParsingOutputStreamTask
   tries flush one to the cache. If the cache is full (CACHE_SIZE_LIMIT) or current
   style attributes are changed then the whole cache is flushed to output.
 */
public class ParsingOutputStreamTask extends SimpleOutputStreamTask
{
	// {{{ private members
	private CommandOutputParser errorParser = null;
	private AnsiEscapeParser ansiParser = null;
	
	private final int DEFAULT = CommandOutputParser.DEFAULT;
	private final int WARNING = ErrorSource.WARNING;
	private final int ERROR   = ErrorSource.ERROR;
	
	private int shiftUCW;
	private Pattern eolPattern;
	private SimpleAttributeSet commonAttrs;
	private SimpleAttributeSet defaultAttrs, warningAttrs, errorAttrs;
	
	// {{{ cache's members
	private final int CACHE_SIZE_LIMIT = 100;  // line's count
	private SimpleAttributeSet cache_lastAttrs;
	private int cache_strCount = 0;
	private StringBuilder cache = new StringBuilder();
	// }}}
	
	// {{{ init() method
	private void init(Color defaultColor, Color backgroundColor)
	{
		shiftUCW = 0;
		
		eolPattern = Pattern.compile("\n");
		
		commonAttrs = new SimpleAttributeSet();
		StyleConstants.setBackground(commonAttrs, backgroundColor);
		StyleConstants.setForeground(commonAttrs, defaultColor);
		
		defaultAttrs = setDefaultAttrs(null);
	} // }}}
	
	// {{{ resetCache() method
	private boolean resetCache(boolean flag)
	{
		if (flag || cache_strCount >= CACHE_SIZE_LIMIT)
		{
			output.writeAttrs(cache_lastAttrs, cache.toString());
			
			cache_strCount  = 0;
			cache.setLength(0);
			
			return true;
		}
		
		return false;
	} // }}}
	
	// {{{ outputting() method
	private void outputting(SimpleAttributeSet currentAttrs, String str)
	{
		if ( resetCache(currentAttrs != cache_lastAttrs) )
		{
			cache_lastAttrs = currentAttrs;
		}
		
		cache.append(str);
		cache_strCount++;
	} // }}}
	
	// {{{ printString() method
	private void printString(String str, boolean isUnterminated)
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
		if ( ansiParser != null && ansiParser.touch(AnsiEscapeParser.Behaviour.PARSE, str) )
		{
			seqs = ansiParser.parse(str, true);
			str  = ansiParser.removeAll(str); // => str's length is changed
		}
		
		// error/warning parser
		int errorStatus = errorParser != null ?
								errorParser.processLine(str) :
								DEFAULT;
		
		// choose color of full line 
		if (isUnterminated)
		{
			shiftUCW += str.length();
		}
		else
		{
			currentAttrs = updateCurrentAttrs(errorStatus);
			
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
				outputting(currentAttrs, str);
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
							flushSubstring(str, currentAttrs, 0, descr.bPosition);
						}
						firstFlushed = true;
					}
					
					switch (descr.function)
					{
						case SGR:
							// new default style's creation:
							//   1. always cancel previous escape-style
							//   2. if no any escape-styles (resetting) - setup commonAttrs
							defaultAttrs = setDefaultAttrs( ansiParser.processSGRparameters(descr.parameters, defaultAttrs) );
							currentAttrs = updateCurrentAttrs(errorStatus);
							
							flushSubstring(str, currentAttrs, descr.bPosition, descr.ePosition);
							break;
						case NUL:
						default :
							flushSubstring(str, currentAttrs, descr.bPosition, descr.ePosition);	
							break;
					}
				}
			}
		}
		catch (Exception err)
		{
			Log.log (Log.ERROR, this, "Can't print data:", err);
		}		
	} // }}}
	
	// {{{ flushSubstring() method
	private void flushSubstring(String str, SimpleAttributeSet localAttrs, int bpos, int epos)
	{
		if (epos - bpos > 0)
		{
			outputting( localAttrs, str.substring(bpos, epos) );
		}
	} // }}}
	
	// {{{ setDefaultAttrs() method
	private SimpleAttributeSet setDefaultAttrs(SimpleAttributeSet newAttrs)
	{
		return newAttrs == null ? commonAttrs : newAttrs;
	} // }}}
	
	// {{{ setNondefaultAttrs() method
	private SimpleAttributeSet setNondefaultAttrs(SimpleAttributeSet nondefAttrs, Color color)
	{
		if (nondefAttrs == null)
		{
			nondefAttrs = new SimpleAttributeSet(commonAttrs);
			StyleConstants.setForeground(nondefAttrs, color);
		}
		
		return nondefAttrs;
	} // }}}
	
	// {{{ updateCurrentAttrs() method
	private SimpleAttributeSet updateCurrentAttrs(int errorStatus)
	{
		switch (errorStatus)
		{
			case DEFAULT:
				return defaultAttrs;
			case WARNING:
				warningAttrs = setNondefaultAttrs(warningAttrs, errorParser.getColor() );
				return warningAttrs;
			case ERROR:
				errorAttrs   = setNondefaultAttrs(errorAttrs  , errorParser.getColor() );
				return errorAttrs;
		}
		
		return defaultAttrs;
	} // }}}
	// }}}
	
	// {{{ constructor
	/**
	   Create instance using given View and DefaultErrorSource.
	   
	   @param in input stream, from which we receive data
	   @param output instance implements Output
	   @param defaultColor default foreground color
	   @param backgroundColor default backgroundColor color
	   @param view working jEdit's view
	   @param des error source for error parser
	   @param currentDirectory console's current (working) directory 
	 */
	public ParsingOutputStreamTask(InputStream in,
								   Output output,
								   Color defaultColor,
								   Color backgroundColor,
								   View view,
								   DefaultErrorSource des,
								   String currentDirectory)
	{
		super(in, output, defaultColor);
		
		setErrorParser(view, des, defaultColor, currentDirectory);
		setAnsiParser(defaultColor, backgroundColor);
		
		init(defaultColor, backgroundColor);
	}
	
	/**
	   Create instance using default Console's parameters.
	   
	   @param in input stream, from which we receive data
	   @param output instance implements Output
	   @param defaultColor default foreground color
	   @param console console, which manipulates the input stream
	   @param currentDirectory console's current (working) directory 
	 */
	public ParsingOutputStreamTask(InputStream in,
								   Output output,
								   Color defaultColor,
								   Console console,
								   String currentDirectory)
	{
		this(in,
			 output,
			 defaultColor,
			 console.getConsolePane().getBackground(),
			 console.getView(),
			 console.getErrorSource(),
			 currentDirectory);
	}
	
	/**
	   Create instance using external parsers.
	   
	   @param in input stream, from which we receive data
	   @param output instance implements Output
	   @param defaultColor default foreground color
	   @param backgroundColor default backgroundColor color
	   @param extErrorParser given error parser instance
	   @param extAnsiParser given ansi parser instance
	 */
	public ParsingOutputStreamTask(InputStream in,
								   Output output,
								   Color defaultColor,
								   Color backgroundColor,
								   CommandOutputParser extErrorParser,
								   AnsiEscapeParser extAnsiParser)
	{
		super(in, output, defaultColor);
		
		setErrorParser(extErrorParser);
		setAnsiParser(extAnsiParser);
		
		init(defaultColor, backgroundColor);
	} // }}}
	
	// {{{ actionInsideWaitingLoop() method
	/**
	 * Extended outputting: working process outputs nothing a long time.
	 * In that case flush unterminated string.
	 */
	@Override
	protected void actionInsideWaitingLoop(InputStreamReader isr) throws Exception
	{
		// there is some unterminated "tail":  -> "444" (see outputData() method)
		if ( !isr.ready() )
		{
			printString(lineBuffer.toString(), true);
			lineBuffer.setLength(0);
		}
		
		resetCache( cache.length() > 0 );
	} // }}}
	
	// {{{ afterWorking() method
	@Override
	protected void afterWorking() throws Exception
	{
		errorParser.finishErrorParsing();
	} // }}}
	
	// {{{ finalOutputting() method
	@Override
	protected void finalOutputting()
	{
		if (lineBuffer.length() > 0)
		{
			printString(lineBuffer.toString(), false);
		}
		
		resetCache( cache.length() > 0 );
	} // }}}
	
	// {{{ outputData() method
	/**
	 * Do followed:
	 * - exchanging and removing symbols in whole input line
	 * - splitting input line by line breaks
	 */
	@Override
	protected void outputData() throws Exception
	{
		String line = lineBuffer.toString();		// "111\n222\n333\n444"
		
		// convert all line breaks to internal "standard": "\n"
		if ( ConsolePane.eolExchangeRequired() )
		{
			lineBuffer.setLength(0);
			line = lineBuffer.append( ConsolePane.eolExchanging(line) ).toString();
		}
	
		// remove all ANSI escape sequences
		if ( ansiParser != null && ansiParser.touch(AnsiEscapeParser.Behaviour.REMOVE_ALL, line) )
		{
			lineBuffer.setLength(0);
			line = lineBuffer.append( ansiParser.removeAll(line) ).toString();
		}
		
		Matcher matcher = eolPattern.matcher(line);
		
		int bPosition = 0;
		while ( !abortFlag && matcher.find() )
		{									// -> "111\n" -> "222\n" -> "333\n"
			printString(line.substring( bPosition, matcher.end() ), false);
			bPosition = matcher.end();
		}
		// remove already printed substrings
		if (bPosition > 0)
		{
			lineBuffer.delete(0, bPosition);
		}
		/*
		 * If lineBuffer is not empty then either it's content will be flushed
		 * in actionInsideWaitingLoop() method or new data will be added to one.
		 */
	} // }}}
	
	// {{{ setAnsiParser() method
	/**
	   Set given ansi parser.
	   Setting parser to "null" turns off this parser.
	   
	   @param extAnsiParser given ansi parser instance
	 */
	public void setAnsiParser(AnsiEscapeParser extAnsiParser)
	{
		ansiParser = extAnsiParser;
	}
	
	/**
	   Create new ansi parser and set one.
	   @param defaultColor default foreground color
	   @param backgroundColor default backgroundColor color
	 */
	public void setAnsiParser(Color defaultColor, Color backgroundColor)
	{
		ansiParser = new AnsiEscapeParser(defaultColor, backgroundColor);
	} // }}}
	
	// {{{ setErrorParser() method
	/**
	   Set given error parser..
	   Setting parser to "null" turns off this parser.
	   
	   @param extErrorParser given error parser instance
	 */
	public void setErrorParser(CommandOutputParser extErrorParser)
	{
		errorParser = extErrorParser;
	}
	
	/**
	   Create new error parser and set one.
	   @param view working jEdit's view
	   @param des error source for error parser
	   @param defaultColor default foreground color
	   @param currentDirectory console's current (working) directory 
	 */
	public void setErrorParser(View view,
							   DefaultErrorSource des,
							   Color defaultColor,
							   String currentDirectory)
	{
		errorParser = new CommandOutputParser(view, des, defaultColor);
		errorParser.setDirectory(currentDirectory);
	} // }}}
}

