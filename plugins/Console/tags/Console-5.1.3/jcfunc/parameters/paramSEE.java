/*
 * paramSEE.java - returned values for CF.SEE
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
   Enum <code>paramSEE</code> contains returned values for control function CF.SEE.
 */
public enum paramSEE
{
	//{{{ enum's values
	/** the shifted part is limited to the active page in the presentation component */
	Limit_ActivePage(0),
	
	/** the shifted part is limited to the active line in the presentation component */
	Limit_ActiveLine(1),
	
	/** the shifted part is limited to the active field in the presentation component */
	Limit_ActiveField(2),
	
	/** the shifted part is limited to the active qualified area */
	Limit_ActiveQualArea(3),
	
	/** the shifted part consists of the relevant part of the entire presentation component */
	PartOfEntireComponent(4),
	
	/** Don't define in Standart */
	Nonstandard(-1);
	//}}}
	
	private int value; 

	paramSEE(int value)
	{
		this.value = value;
	}
	
	//{{{ getIntValue() method
	/**
	   Returns int-value, which is corresponded to enum-value.
	   @param val enum-value
	   @return int-value
	 */
	public static int getIntValue(paramSEE val)
	{
		return val.value;
	} //}}}
	
	//{{{ getEnumValue() method
	/**
	   Returns enum-value by int-value.
	   @param val int-value
	   @return enum-value
	 */
	public static paramSEE getEnumValue(int val)
	{
		for ( paramSEE element: values() )
			if (element.value == val)
				return element;
		
		return Nonstandard;
	} //}}}
}	
