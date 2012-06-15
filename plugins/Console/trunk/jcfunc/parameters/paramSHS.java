/*
 * paramSHS.java - returned values for CF.SHS
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
   Enum <code>paramSHS</code> contains returned values for control function CF.SHS.
 */
public enum paramSHS
{
	//{{{ enum's values
	/** 10 characters per 25,4 mm */
	Ten(0),
	
	/** 12 characters per 25,4 mm */
	Twelve(1),
	
	/** 15 characters per 25,4 mm */
	Fifteen(2),
	
	/** 6 characters per 25,4 mm  */
	Six(3),
	
	/** 3 characters per 25,4 mm  */
	Three(4),
	
	/** 9 characters per 50,8 mm  */
	Nine(5),
	
	/** 4 characters per 25,4 mm  */
	Four(6),
	
	/** Don't define in Standart */
	Nonstandard(-1);
	//}}}
	
	private int value; 

	paramSHS(int value)
	{
		this.value = value;
	}
	
	//{{{ getIntValue() method
	/**
	   Returns int-value, which is corresponded to enum-value.
	   @param val enum-value
	   @return int-value
	 */
	public static int getIntValue(paramSHS val)
	{
		return val.value;
	} //}}}
	
	//{{{ getEnumValue() method
	/**
	   Returns enum-value by int-value.
	   @param val int-value
	   @return enum-value
	 */
	public static paramSHS getEnumValue(int val)
	{
		for ( paramSHS element: values() )
			if (element.value == val)
				return element;
		
		return Nonstandard;
	} //}}}
}	
