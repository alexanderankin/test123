/*
 * paramED.java - returned values for CF.ED
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
   Enum <code>paramED</code> contains returned values for control function CF.ED.
 */
public enum paramED
{
	//{{{ enum's values
	/** 
	    the active position and the character positions up to the end
	    of the page are put into the erased state
         */ 
	FromHereUpToEnd(0),
	
	/**
	    the character positions from the beginning of the page
	    up to and including the active position are put into the erased state

	 */ 
	FromBeginningUpToHere(1),
	
	/**
	    all character positions of the page are put into the erased state
	 */ 
	AllLine(2),
	
	/** Don't define in Standart */
	Nonstandard(-1);
	//}}}
	
	private int value; 

	paramED(int value)
	{
		this.value = value;
	}
	
	//{{{ getIntValue() method
	/**
	   Returns int-value, which is corresponded to enum-value.
	   @param val enum-value
	   @return int-value
	 */
	public static int getIntValue(paramED val)
	{
		return val.value;
	} //}}}
	
	//{{{ getEnumValue() method
	/**
	   Returns enum-value by int-value.
	   @param val int-value
	   @return enum-value
	 */
	public static paramED getEnumValue(int val)
	{
		for ( paramED element: values() )
			if (element.value == val)
				return element;
		
		return Nonstandard;
	} //}}}
}	
