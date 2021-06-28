/*
 * StreamThread.java - A running process
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Parts Copyright (C) 1999-2012 Slava Pestov, Alan Ezust, Marcelo Vanzin, Artem Bryantsev 
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
import java.io.IOException;
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

import jcfunc.*;
import jcfunc.parameters.*;
// }}}

/**
 * @deprecated
 * Thread for handing output of running sub-processes.
 *
 * @version $Id$
 */

// {{{ class StreamThread
@Deprecated
class StreamThread extends OutputThread
{
	// {{{ Private members
	private ConsoleProcess process;

	private boolean aborted;

	private InputStream in;
	CommandOutputParser copt = null;
	
	private final int DEFAULT = CommandOutputParser.DEFAULT;
	private final int WARNING = ErrorSource.WARNING;
	private final int ERROR   = ErrorSource.ERROR;
	
	private StringBuilder lineBuffer;
	private int shiftUCW;
	private int ANSI_BEHAVIOUR;
	private Pattern eolPattern;
	private Pattern eolReplacingPattern;
	private escMatcher ansi_Matcher;
	private SimpleAttributeSet commonAttrs;
	private Color defaultColor;
	private SimpleAttributeSet defaultAttrs, warningAttrs, errorAttrs;
	// }}}

	// {{{ StreamThread constructor
	/**
	 * @param showStatus - prints the error status when the thread is finished.
	 */
	StreamThread(ConsoleProcess process, InputStream in, Color defaultColor)
	{
		this.process = process;
		this.in = in;
		
		String currentDirectory = process.getCurrentDirectory();
		Console console = process.getConsole();
		DefaultErrorSource es = console.getErrorSource();
		
		copt = new CommandOutputParser(console.getView(), es, defaultColor);
		copt.setDirectory(currentDirectory);
		
		lineBuffer = new StringBuilder(100);
		shiftUCW = 0;
		
		eolPattern = Pattern.compile("\n");
		if (!System.getProperty("line.separator").equals("\n"))
		{
			eolReplacingPattern = Pattern.compile( System.getProperty("line.separator") );
		}
		
		this.defaultColor = defaultColor; 
		
		commonAttrs = new SimpleAttributeSet();
		StyleConstants.setBackground(commonAttrs, console.getConsolePane().getBackground());
		StyleConstants.setForeground(commonAttrs, defaultColor);
		
		defaultAttrs = setDefaultAttrs(null);
		
		// choose matcher's mode
		int ansi_mode = Sequences.MODE_7BIT;
		if ( jEdit.getProperty("options.ansi-escape.mode").contentEquals("8bit") )
		{
			ansi_mode = Sequences.MODE_8BIT;
		}
		
		ansi_Matcher = new escMatcher(ansi_mode, Pattern.DOTALL);
		
		// define matcher's behaviour
		ANSI_BEHAVIOUR = -1;		// ignor all 
		String str = jEdit.getProperty("options.ansi-escape.behaviour");
		if ( str.contentEquals("remove") )
		{
			ANSI_BEHAVIOUR = 0;	// remove all
		}
		else if ( str.contentEquals("parse") )
		{
			ANSI_BEHAVIOUR = 1;	// parse
		}
			
		// fill parsing control function's list
		StringList funcs     = StringList.split( jEdit.getProperty("options.ansi-escape.func-list").toLowerCase(), "\\s+");
		String avaible_funcs = jEdit.getProperty("options.ansi-escape.func-list-values");
		
		int i = 0;
		while ( i < funcs.size() )
		{
			if ( !avaible_funcs.contains( funcs.get(i) ) )
			{
				str = funcs.remove(i);
				str = null;
				i--;
			}
			i++;
		}
		
		CF[] func_arr = new CF[funcs.size()];
		for (i = 0; i < funcs.size(); i++)
		{
			func_arr[i] = CF.valueOf(CF.class, funcs.get(i).toUpperCase() );
		}
		
		ansi_Matcher.setPatterns(func_arr);
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
			while (!aborted)
			{
				int read = isr.read(input, 0, input.length);
				if (aborted)
				{
					break;
				}
				else if (read == -1)
				{
					if (lineBuffer.length() > 0)
					{
						printString(output, lineBuffer.toString(), false);
					}
					break;
				}
				
				String line = lineBuffer.append(input, 0, read).toString();		// "111\n222\n333\n444"
				
				// convert all line breaks to internal "standard": "\n"
				if (eolReplacingPattern != null)
				{
					lineBuffer.setLength(0);
					line = lineBuffer.append( eolReplacingPattern.matcher(line).replaceAll("\n") ).toString();
				}
			
				// remove all ANSI escape sequences
				if ( ANSI_BEHAVIOUR == 0 && ansi_Matcher.matches(line) )
				{
					lineBuffer.setLength(0);
					line = lineBuffer.append( ansi_Matcher.removeAll(line) ).toString();
				}
				
				Matcher matcher = eolPattern.matcher(line);
				
				int bPosition = 0;
				while ( matcher.find() )
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
				if ( lineBuffer.length() > 0 )
				{
					if ( !isr.ready() )
					{
						sleep(50);
						
						if ( !isr.ready() )
						{
							printString(output, lineBuffer.toString(), true);
							lineBuffer.setLength(0);
						}
					}
				}
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
			copt.finishErrorParsing();
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

	//{{{ abort() method
	void abort()
	{
		aborted = true;
		interrupt();
	} //}}}
	
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
		if ( ANSI_BEHAVIOUR == 1 && ansi_Matcher.matches(str) )
		{                         
			seqs = ansi_Matcher.parse(str, true);
			str  = ansi_Matcher.removeAll(str); // => str's length is changed
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
							defaultAttrs = setDefaultAttrs( processSGRparameters(descr.parameters, defaultAttrs) );
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
	
	//{{{ processSGRparameters() method
	private SimpleAttributeSet processSGRparameters(int[] parameters, SimpleAttributeSet baseAttrs)
	{
		SimpleAttributeSet funcAttrs = new SimpleAttributeSet(baseAttrs);
		int intensity = 0; 
		Color clr = null;
		
		// go over SGR's parameters
		for (int value: parameters)
		{
			paramSGR valSGR = paramSGR.getEnumValue(value);
			
			switch (valSGR)
			{
			case Reset:
				return null;
			case Bright:
				intensity = 1;
				break;
			case Faint :
				intensity = -1;
				break;
			case Italic:
				StyleConstants.setItalic(funcAttrs, true);
				break;
			case Underline_Single:
			case Underline_Doubly:
				StyleConstants.setUnderline(funcAttrs, true);
				break;
			case CrossedOut:
				StyleConstants.setStrikeThrough(funcAttrs, true);
				break;
			case Normal_Int:
				intensity = 0;
				break;
			case Normal_Style:
				StyleConstants.setItalic(funcAttrs, false);
				break;
			case Underline_NGT:
				StyleConstants.setUnderline(funcAttrs, false);
				break;
			case CrossedOut_NGT:
				StyleConstants.setStrikeThrough(funcAttrs, false);
				break;
			case Color_Text_Black   :
			case Color_Text_Red     :
			case Color_Text_Green   :
			case Color_Text_Yellow  :
			case Color_Text_Blue    :
			case Color_Text_Magenta :
			case Color_Text_Cyan    :
			case Color_Text_White   :
			case Color_Text_Reserved:
				clr = paramSGR.getColor(valSGR);
				
				switch (intensity)
				{
				case  1:
					clr = clr.darker();
					break;
				case -1:
					clr = clr.brighter();
					break;
				}
				
				StyleConstants.setForeground(funcAttrs, clr == null ? defaultColor : clr);
				break;
			case Color_Bkgr_Black   :
			case Color_Bkgr_Red     :
			case Color_Bkgr_Green   :
			case Color_Bkgr_Yellow  :
			case Color_Bkgr_Blue    :
			case Color_Bkgr_Magenta :
			case Color_Bkgr_Cyan    :
			case Color_Bkgr_White   :
			case Color_Bkgr_Reserved:
				clr = paramSGR.getColor(valSGR);

				switch (intensity)
				{
				case  1:
					clr = clr.darker();
					break;
				case -1:
					clr = clr.brighter();
					break;
				}
				
				StyleConstants.setBackground(funcAttrs, clr == null ? defaultColor : clr);
				break;
			default:
				break;
			}
		}
		
		return funcAttrs;
	}
	//}}}
	
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

