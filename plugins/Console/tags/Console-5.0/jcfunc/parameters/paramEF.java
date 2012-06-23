/*
 * paramEF.java - returned values for CF.EF
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
   Enum <code>paramEF</code> contains returned values for control function CF.EF.
 */
public enum paramEF
{
	//{{{ enum's values
	/** 
	    the active position and the character positions up to the end
	    of the field are put into the erased state
         */ 
	FromHereUpToEnd(0),
	
	/**
	    the character positions from the beginning of the field
	    up to and including the active position are put into the erased state

	 */ 
	FromBeginningUpToHere(1),
	
	/**
	    all character positions of the field are put into the erased state
	 */ 
	AllLine(2),
	
	/** Don't define in Standart */
	Nonstandard(-1);
	//}}}
	
	private int value; 

	paramEF(int value)
	{
		this.value = value;
	}
	
	//{{{ getIntValue() method
	/**
	   Returns int-value, which is corresponded to enum-value.
	   @param val enum-value
	   @return int-value
	 */
	public static int getIntValue(paramEF val)
	{
		return val.value;
	} //}}}
	
	//{{{ getEnumValue() method
	/**
	   Returns enum-value by int-value.
	   @param val int-value
	   @return enum-value
	 */
	public static paramEF getEnumValue(int val)
	{
		for ( paramEF element: values() )
			if (element.value == val)
				return element;
		
		return Nonstandard;
	} //}}}
}	
