/*
 * ParsingOutputStreamTask.java - Outputting thread using an error parrser
 *                                and an ANSI escaped sequencies parser 
 *
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999-2012 Slava Pestov, Marcelo Vanzin, Artem Bryantsev 
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
	
	private final int CACHE_SIZE_LIMIT = 100;  // line's count
	private SimpleAttributeSet cache_lastAttrs;
	private int cache_strCount;
	private int buffer_start;
	
	// {{{ init() method
	private void init(Color defaultColor, Color backgroundColor)
	{
		shiftUCW = 0;
		
		eolPattern = Pattern.compile("\n");
		
		commonAttrs = new SimpleAttributeSet();
		StyleConstants.setBackground(commonAttrs, backgroundColor);
		StyleConstants.setForeground(commonAttrs, defaultColor);
		
		defaultAttrs = setDefaultAttrs(null);
		
		buffer_start   = 0;
		cache_strCount = 0;
	} // }}}
	
	// {{{ handleString() method
	private void handleString(String str, boolean isUnterminated)
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
				push(currentAttrs, str);
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
			push( localAttrs, str.substring(bpos, epos) );
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
	
	// {{{ push() method
	/**
	   Push a string to the outputting cache with a some AttributeSet.
	   If the current value of AttributeSet is not a same as one previously
	   saved then at first this method flushes the cached data and saves
	   the current value of AttributeSet.
	   @param currentAttrs current value of the AttributeSet
	   @param str outputed string
	 */
	public void push(SimpleAttributeSet currentAttrs, String str)
	{
		if ( pop(currentAttrs != cache_lastAttrs) )
		{
			cache_lastAttrs = currentAttrs;
		}
		
		buffer_start = lineBuffer.append(str).length();
	} // }}}
	
	// {{{ pop() method
	/**
	   Pop the cached data from the outputting cache to Output. When "forced" is
	   "true" the outputting cache is flushed by force.
	   @param forced if "true" the cache is flushed by force
	   @return if the cache is flushed successfully then return "true"
	 */
	public boolean pop(boolean forced)
	{
		if (forced || cache_strCount >= CACHE_SIZE_LIMIT)
		{
			output.writeAttrs(cache_lastAttrs, lineBuffer.substring(0, buffer_start));
			
			if ( buffer_start < lineBuffer.length() )  // data over cache
			{
				lineBuffer.delete(0, buffer_start);
			}
			else  // only cache
			{
				lineBuffer.setLength(0);
			}
			
			buffer_start   = 0;
			cache_strCount = 0;
			
			return true;
		}
		
		return false;
	} // }}}
	
	// {{{ trim_cache()
	/**
	   Remove a noncached data (placed AFTER the cache) from the cache.
	   @return return a noncached data 
	 */
	public String trim_cache()
	{
		String result = lineBuffer.substring(buffer_start);
		
		lineBuffer.setLength(buffer_start);
		
		return result;
	} // }}}
	
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
	 * In that case flush and a cached data and a noncached (unterminated string).
	 */
	@Override
	protected void actionInsideWaitingLoop(InputStreamReader isr) throws Exception
	{
		if (lineBuffer.length() > 0)
		{
			if ( !isr.ready() )
			{
				handleString(trim_cache(), true);
			}
			
			pop(true);
		}
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
			handleString(trim_cache(), false);
			
			pop(true);
		}
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
		/* the idea:
		 *	1. save "external" buffer (lineBuffer) to "internal" (line)
		 *	2. use "external" buffer as an outputting cache
		 *	3. if we have an unbreaked input string - append it to "external"
		 *	   buffer AFTER the cached data; use "buffer_start" for navigation
		 *	   purposes
		 */
		
		String line = lineBuffer.substring(buffer_start);
		
		// convert all line breaks to internal "standard": "\n"
		if ( ConsolePane.eolExchangeRequired() )
		{
			lineBuffer.setLength(buffer_start);
			line = lineBuffer.append( ConsolePane.eolExchanging(line) ).toString();
		}
	
		// remove all ANSI escape sequences
		if ( ansiParser != null && ansiParser.touch(AnsiEscapeParser.Behaviour.REMOVE_ALL, line) )
		{
			lineBuffer.setLength(buffer_start);
			line = lineBuffer.append( ansiParser.removeAll(line) ).toString();
		}
		
		// now there is no necessity to use external buffer -> clean
		lineBuffer.ensureCapacity(buffer_start + line.length());
		trim_cache();
		
		Matcher matcher = eolPattern.matcher(line);
		
		int bPosition = 0;
		while ( !abortFlag && matcher.find() )
		{
			handleString(line.substring( bPosition, matcher.end() ), false);
			bPosition = matcher.end();
			cache_strCount++;
		}
		// unterminated string -> save it to external buffer over the cache
		if ( bPosition < line.length() )
		{
			lineBuffer.append(line.substring(bPosition));
		}
		
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

