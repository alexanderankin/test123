/*
 * paramSAPV.java - returned values for CF.SAPV
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2012, Artem Bryantsev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
 
package jcfunc.parameters;

/**
   Enum <code>paramSAPV</code> contains returned values for control function CF.SAPV.
 */
public enum paramSAPV
{
	//{{{ enum's values
	/** 
	    default presentation (implementation-defined); cancels the effect of any
	    preceding occurrence of SAPV in the data stream
	 */
	Reset(0),
	
	/**
	    the decimal digits are presented by means of the graphic symbols used
	    in the Latin script
	 */
	DecDigits_Latin(1),
	
	/**
	    the decimal digits are presented by means of the graphic symbols used
	    in the Arabic script, i.e. the Hindi symbols
	 */
	DecDigits_Arabic(2),
	
	/**
	    when the direction of the character path is right-to-left, each of
	    the graphic characters in the graphic character set(s) in use which is
	    one of a left/right-handed pair (parentheses, square brackets,
	    curly brackets, greater-than/less-than signs, etc.) is presented as
	    "mirrored", i.e. as the other member of the pair. For example, the coded
	    graphic character given the name LEFT PARENTHESIS is presented as
	    RIGHT PARENTHESIS, and vice versa
	 */
	ExchangePairChars(3),
	
	/**
	    when the direction of the character path is right-to-left, all graphic
	    characters which represent operators and delimiters in mathematical formulae
	    and which are not symmetrical about a vertical axis are presented
	    as mirrored about that vertical axis
	 */
	FlipOperators(4),
	
	/** the following graphic character is presented in its isolated form */
	Form_Isolated(5),
	
	/** the following graphic character is presented in its initial form */
	Form_Initial(6),
	
	/** the following graphic character is presented in its medial form */
	Form_Medial(7),
	
	/** the following graphic character is presented in its final form */
	Form_Final(8),
	
	/**
	    where the bit combination 02/14 is intended to represent a decimal mark
	    in a decimal number it shall be presented by means of the graphic symbol FULL STOP
	 */
	Dot_FullStop(9),
	
	/**
	    where the bit combination 02/14 is intended to represent a decimal mark
	    in a decimal number it shall be presented by means of the graphic symbol COMMA
	 */
	Dot_Comma(10),
	
	/** vowels are presented above or below the preceding character */
	Vowels_AboveOrBelow(11),
	
	/** vowels are presented after the preceding character */
	Vowels_After(12),
	
	/**
	    contextual shape determination of Arabic scripts, including the LAM-ALEPH
	    ligature but excluding all other Arabic ligatures
	 */
	ArabicScripts_WithLamAlephOnly(13),
	
	/** contextual shape determination of Arabic scripts, excluding all Arabic ligatures */
	ArabicScripts_WithoutAnyLigatures(14),
	
	/** cancels the effect of parameter values 3 and 4 */
	Cansel_ExchangingAndFlipping(15),
	
	/** vowels are not presented */
	Vowels_NotPresented(16),
	
	/**
	    when the string direction is right-to-left, the italicized characters
	    are slanted to the left; when the string direction is left-to-right,
	    the italicized characters are slanted to the right
	 */
	Italic_SlantToMainDirection(17),
	
	/**
	    contextual shape determination of Arabic scripts is not used,
	    the graphic characters - including the digits - are presented in the form
	    they are stored (Pass-through)
	 */
	ArabicScripts_NotMutable_WithDigits(18),
	
	/**
	    contextual shape determination of Arabic scripts is not used,
	    the graphic characters - excluding the digits - are presented in the form
	    they are stored (Pass-through)
	 */
	ArabicScripts_NotMutable_WithoutDigits(19),
	
	/** the graphic symbols used to present the decimal digits are device dependent */
	DecDigits_Device(20),
	
	/**
	    establishes the effect of parameter values 5, 6, 7, and 8 for the following
	    graphic characters until cancelled
	 */
	Form_EstablishForFollowing(21),
	
	/**
	    cancels the effect of parameter value 21, i.e. re-establishes the effect
	    of parameter values 5, 6, 7, and 8 for the next single graphic character only.
	 */
	Form_CancelForFollowing(22),

	/** Don't define in Standart */
	Nonstandard(-1);
	//}}}
	
	private int value; 

	paramSAPV(int value)
	{
		this.value = value;
	}
	
	//{{{ getIntValue() method
	/**
	   Returns int-value, which is corresponded to enum-value.
	   @param val enum-value
	   @return int-value
	 */
	public static int getIntValue(paramSAPV val)
	{
		return val.value;
	} //}}}
	
	//{{{ getEnumValue() method
	/**
	   Returns enum-value by int-value.
	   @param val int-value
	   @return enum-value
	 */
	public static paramSAPV getEnumValue(int val)
	{
		for ( paramSAPV element: values() )
			if (element.value == val)
				return element;
		
		return Nonstandard;
	} //}}}
}	
