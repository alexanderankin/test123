/*
 * paramCTC.java - returned values for CF.CTC
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
   Enum <code>paramCTC</code> contains returned values for control function CF.CTC.
 */
public enum paramCTC
{
	//{{{ enum's values
	/** a character tabulation stop is set at the active presentation position */
	CharTabStopSet(0),

	/** a line tabulation stop is set at the active line (the line that contains    
	    the active presentation position) */
	LineTabStopSet(1),

	/** the character tabulation stop at the active presentation position is cleared */
	CharTabStopClear(2),

	/** the line tabulation stop at the active line is cleared */
	LineTabStopClear(3),

	/** all character tabulation stops in the active line are cleared */
	CharTabStopClearLine(4),

	/** all character tabulation stops are cleared */
	CharTabStopClearAll(5),

	/** all line tabulation stops are cleared */
	LineTabStopClearAll(6),
	
	/** Don't define in Standart */
	Nonstandard(-1);
	//}}}
	
	private int value; 

	paramCTC(int value)
	{
		this.value = value;
	}
	
	//{{{ getIntValue() method
	/**
	   Returns int-value, which is corresponded to enum-value.
	   @param val enum-value
	   @return int-value
	 */
	public static int getIntValue(paramCTC val)
	{
		return val.value;
	} //}}}
	
	//{{{ getEnumValue() method
	/**
	   Returns enum-value by int-value.
	   @param val int-value
	   @return enum-value
	 */
	public static paramCTC getEnumValue(int val)
	{
		for ( paramCTC element: values() )
			if (element.value == val)
				return element;
		
		return Nonstandard;
	} //}}}
}	
