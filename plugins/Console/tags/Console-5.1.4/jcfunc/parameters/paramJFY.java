/*
 * paramJFY.java - returned values for CF.JFY
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
   Enum <code>paramJFY</code> contains returned values for control function CF.JFY.
 */
public enum paramJFY
{
	//{{{ enum's values
	/** no justification, end of justification of preceding text */
	Justification_NGT(0),
	
	/** word fill */
	WordFill(1),
	
	/** word space */
	WordSpace(2),
	
	/** letter space */
	LetterSpace(3),
	
	/** hyphenation */
	Hyphenation(4),
	
	/** flush to line home position margin */
	HomeAligment(5),
	
	/** centre between line home position and line limit position margins */
	Centre(6),
	
	/** flush to line limit position margin */
	LimitAligment(7),
	
	/** Italian hyphenation */
	ItalianHyphenation(8),

	/** Don't define in Standart */
	Nonstandard(-1);
	//}}}
	
	private int value; 

	paramJFY(int value)
	{
		this.value = value;
	}
	
	//{{{ getIntValue() method
	/**
	   Returns int-value, which is corresponded to enum-value.
	   @param val enum-value
	   @return int-value
	 */
	public static int getIntValue(paramJFY val)
	{
		return val.value;
	} //}}}
	
	//{{{ getEnumValue() method
	/**
	   Returns enum-value by int-value.
	   @param val int-value
	   @return enum-value
	 */
	public static paramJFY getEnumValue(int val)
	{
		for ( paramJFY element: values() )
			if (element.value == val)
				return element;
		
		return Nonstandard;
	} //}}}
}	
