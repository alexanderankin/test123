/*
 * AnsiEscapeParser.java - parser ANSI escaped sequencies.
 *
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2012 Artem Bryantsev 
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

import java.util.ArrayList;
import java.util.regex.*;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.StringList;

import jcfunc.*;
import jcfunc.parameters.*;
// }}}

/**<p>
 * AnsiEscapeParser class wraps calls of JCFunc-library's functions in itself.
 *
 * Workflow:
 * 1) create new parser with some parameters (choose parsing MODE)
 * 2) use:
 *    - "touch()" or "matches()" for escaped sequencies' searching in input line
 *    - "parse()" for parsing escaped sequencies in input line
 *    - "processSGRparameters()" if found escaped sequence is SGR (color) function
 * 3) user may change "on-the-fly":
 *    - parser's behaviour (ignore, remove, parse)
 *    - set of parsing sequencies
 * 4) user cann't change "on-the-fly" parsing MODE.</p>
 */
public class AnsiEscapeParser
{
	// {{{ private members
	private Behaviour ansi_Behaviour;
	private escMatcher ansi_Matcher;
	private Color defaultFColor, defaultBColor;
	// }}}
	
	// {{{ constructors
	/**<p>
	 * Parser's state is decribed by following properties:
	 * @param ansi_mode - type of escaped sequencies (what kind of escaped sequencies parser processes?)
	 *                    @see jcfunc.Sequences.MODE_7BIT
	 *                    @see jcfunc.Sequences.MODE_8BIT
	 *                    Usually use 7-bit mode.
	 * @param behaviour - parser's behaviour proper (what parser does with found sequencies?)
	 *                    @see Behaviour
	 * @param func_arr  - array of processing functions (escaped sequence == ESC + function)
	 *                    @see jcfunc.CF
	 * @param defFColor - default foreground color (for SGR-function) 
	 * @param defBColor - default background color (for SGR-function)</p> 
	 */
	public AnsiEscapeParser(int ansi_mode, Behaviour behaviour, CF[] func_arr, Color defFColor, Color defBColor)
	{
		ansi_Matcher = new escMatcher(ansi_mode, Pattern.DOTALL);
		
		setBehaviour(behaviour);
		setFunctions(func_arr);
		setDefaultFColor(defFColor);
		setDefaultBColor(defBColor);
	}
	
	/**<p>
	 * This constructor gets parser's properties from Console's file of properties.  
	 * @param defFColor - default foreground color (for SGR-function) 
	 * @param defBColor - default background color (for SGR-function)</p> 
	 */
	public AnsiEscapeParser(Color defFColor, Color defBColor)
	{
		// choose matcher's mode
		int ansi_mode = jEdit.getIntegerProperty("ansi-escape.mode", Sequences.MODE_7BIT);
		switch (ansi_mode)
		{
			case Sequences.MODE_7BIT:
				break;
				
			case Sequences.MODE_8BIT:
				break;
				
			default:
				ansi_mode = Sequences.MODE_7BIT;
		}
		
		// define matcher's behaviour
		Behaviour behaviour = Behaviour.PARSE; 
		int bhvr = jEdit.getIntegerProperty("ansi-escape.behaviour", 2);
		switch (bhvr)
		{
			case 0:
				behaviour = Behaviour.IGNORE_ALL;
				break;
				
			case 1:
				behaviour = Behaviour.REMOVE_ALL;
				break;
				
			case 2:
				behaviour = Behaviour.PARSE;
				break;
				
			default:
				behaviour = Behaviour.PARSE;
		}
			
		// fill parsing control function's list
		StringList funcs     = StringList.split( jEdit.getProperty("ansi-escape.func-list").toLowerCase(), "\\s+");
		String avaible_funcs = jEdit.getProperty("ansi-escape.func-list-values");
		String str = "";
		
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
		
		ansi_Matcher = new escMatcher(ansi_mode, Pattern.DOTALL);
		
		setBehaviour(behaviour);
		setFunctions(func_arr);
		setDefaultFColor(defFColor);
		setDefaultBColor(defBColor);
	} // }}}
	
	// {{{ isBehaviour() method
	public boolean isBehaviour(Behaviour behaviour)
	{
		return ansi_Behaviour == behaviour;
	} // }}}
	
	// {{{ getBehaviour() method
	public Behaviour getBehaviour()
	{
		return ansi_Behaviour;
	} // }}}
	
	// {{{ getDefaultFColor() method
	public Color getDefaultFColor()
	{
		return defaultFColor;
	} // }}}
	
	// {{{ getDefaultBColor() method
	public Color getDefaultBColor()
	{
		return defaultBColor;
	} // }}}
	
	// {{{ matches() method
	public boolean matches(String line)
	{
		return ansi_Matcher.matches(line);
	} // }}}
	
	// {{{ parse() methods
	public ArrayList<Description> parse(String line)
	{
		return ansi_Matcher.parse(line);
	}
	
	public ArrayList<Description> parse(String line, boolean ignoreSequences)
	{
		return ansi_Matcher.parse(line, ignoreSequences);
	} // }}}
	
	// {{{ remove() methods
	public String remove(String line, CF func)
	{
		return ansi_Matcher.remove(line, func);
	} // }}}
	
	// {{{ removeAll() method
	public String removeAll(String line)
	{
		return ansi_Matcher.removeAll(line);
	} // }}}
	
	// {{{ touch() method
	/**<p>
	 * Check two things:
	 * @param behaviour - is it the current parser's behaviour?
	 * @param line      - are there any matches?</p> 
	 */
	public boolean touch(Behaviour behaviour, String line)
	{
		return ansi_Behaviour == behaviour && matches(line);
	} // }}}
	
	// {{{ setBehaviour() method
	public void setBehaviour(Behaviour newBehaviour)
	{
		ansi_Behaviour = newBehaviour;
	} // }}}
	
	// {{{ setDefaultFColor() method
	public void setDefaultFColor(Color newColor)
	{
		defaultFColor = newColor;
	} // }}}
	
	// {{{ setDefaultBColor() method
	public void setDefaultBColor(Color newColor)
	{
		defaultBColor = newColor;
	} // }}}
	
	// {{{ setFunctions() method
	public void setFunctions(CF... cmds)
	{
		ansi_Matcher.setPatterns(cmds);
	} // }}}
	
	// {{{ processSGRparameters() method
	/**<p>
	 * This method is handler of only one (but most popular) function: SGR function.
	 * SGR function controls substring's color and style.
	 * 
	 * Usually text with SGR function looks like that:
	 * <em>This is #SGR#only example#/SGR#: you'll never see tag "#SGR#" in your texts.</em>
	 * There is used some "base" text style for whole line but under tag "#SGR#" base style is changed.
	 *
	 * Parameters:
	 * @param parameters - array of parameters; usually it is parser's working result.
	 *                     @see jcfunc.Description
	 * @param baseAttrs  - base text's style
	 *
	 * @return SimpleAttributeSet - combination base text's style with SGR-parameters</p>
	 */
	public SimpleAttributeSet processSGRparameters(int[] parameters, SimpleAttributeSet baseAttrs)
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
				if (clr == null)
					clr = defaultFColor;
				
				switch (intensity)
				{
				case  1:
					clr = clr.darker();
					break;
				case -1:
					clr = clr.brighter();
					break;
				}
				
				StyleConstants.setForeground(funcAttrs, clr);
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
				if (clr == null)
					clr = defaultBColor;

				switch (intensity)
				{
				case  1:
					clr = clr.darker();
					break;
				case -1:
					clr = clr.brighter();
					break;
				}
				
				StyleConstants.setBackground(funcAttrs, clr);
				break;
			default:
				break;
			}
		}
		
		return funcAttrs;
	}
	//}}}
	
	// {{{ internal enum Behaviour
	/** Behaviour class describes parser's behaviour */
	public enum Behaviour
	{
		/** parser ignores any sequencies */
		IGNORE_ALL,
		/** parser removes any sequencies from input string */
		REMOVE_ALL,
		/** parser processes sequencies */
		PARSE
	} // }}}
	
}
