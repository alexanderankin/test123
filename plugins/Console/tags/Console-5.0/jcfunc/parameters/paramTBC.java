/*
 * paramTBC.java - returned values for CF.TBC
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
   Enum <code>paramTBC</code> contains returned values for control function CF.TBC.
 */
public enum paramTBC
{
	//{{{ enum's values

	/** the character tabulation stop at the active presentation position is cleared */
	Clear_CharTabStop(0),
	
	/** the line tabulation stop at the active line is cleared */
	Clear_LineTabStop(1),
	
	/** all character tabulation stops in the active line are cleared */
	Clear_AllCharTabStop_ActiveLine(2),
	
	/** all character tabulation stops are cleared */
	Clear_AllCharTabStop(3),
	
	/** all line tabulation stops are cleared */
	Clear_AllLineTabStop(4),
	
	/** all tabulation stops are cleared */
	Clear_AllTabStop(5),
	
	/** Don't define in Standart */
	Nonstandard(-1);
	//}}}
	
	private int value; 

	paramTBC(int value)
	{
		this.value = value;
	}
	
	//{{{ getIntValue() method
	/**
	   Returns int-value, which is corresponded to enum-value.
	   @param val enum-value
	   @return int-value
	 */
	public static int getIntValue(paramTBC val)
	{
		return val.value;
	} //}}}
	
	//{{{ getEnumValue() method
	/**
	   Returns enum-value by int-value.
	   @param val int-value
	   @return enum-value
	 */
	public static paramTBC getEnumValue(int val)
	{
		for ( paramTBC element: values() )
			if (element.value == val)
				return element;
		
		return Nonstandard;
	} //}}}
}	
