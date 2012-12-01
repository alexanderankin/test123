/*
 * AnsiEscapeParser.java - 
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

public class AnsiEscapeParser
{
	// {{{ private members
	private Behaviour ansi_Behaviour;
	private escMatcher ansi_Matcher;
	private Color defaultColor;
	// }}}
	
	// {{{ constructors
	public AnsiEscapeParser(int ansi_mode, Behaviour behaviour, CF[] func_arr, Color defColor)
	{
		ansi_Matcher = new escMatcher(ansi_mode, Pattern.DOTALL);
		
		setBehaviour(behaviour);
		setFunctions(func_arr);
		setDefaultColor(defColor); 
	}
	
	public AnsiEscapeParser(Color defColor)
	{
		// choose matcher's mode
		int ansi_mode = Sequences.MODE_7BIT;
		if ( jEdit.getProperty("options.ansi-escape.mode").contentEquals("8bit") )
		{
			ansi_mode = Sequences.MODE_8BIT;
		}
		
		// define matcher's behaviour
		Behaviour behaviour = Behaviour.IGNORE_ALL; 
		String str = jEdit.getProperty("options.ansi-escape.behaviour");
		if ( str.contentEquals("remove") )
		{
			behaviour = Behaviour.REMOVE_ALL;
		}
		else if ( str.contentEquals("parse") )
		{
			behaviour = Behaviour.PARSE;
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
		
		ansi_Matcher = new escMatcher(ansi_mode, Pattern.DOTALL);
		
		setBehaviour(behaviour);
		setFunctions(func_arr);
		setDefaultColor(defColor); 
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
	
	// {{{ getDefaultColor() method
	public Color getDefaultColor()
	{
		return defaultColor;
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
	public boolean touch(Behaviour behaviour, String line)
	{
		return ansi_Behaviour == behaviour && matches(line);
	} // }}}
	
	// {{{ setBehaviour() method
	public void setBehaviour(Behaviour newBehaviour)
	{
		ansi_Behaviour = newBehaviour;
	} // }}}
	
	// {{{ setDefaultColor() method
	public void setDefaultColor(Color newColor)
	{
		defaultColor = newColor;
	} // }}}
	
	// {{{ setFunctions() method
	public void setFunctions(CF... cmds)
	{
		ansi_Matcher.setPatterns(cmds);
	} // }}}
	
	// {{{ processSGRparameters() method
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
					clr = defaultColor;
				
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
					clr = defaultColor;

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
	public enum Behaviour
	{
		IGNORE_ALL,
		REMOVE_ALL,
		PARSE
	} // }}}
	
}
