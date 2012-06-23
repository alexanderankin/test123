/*
 * paramSCO.java - returned values for CF.SCO
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
   Enum <code>paramSCO</code> contains returned values for control function CF.SCO.
 */
public enum paramSCO
{
	//{{{ enum's values
	/** 0 degree    */
	North(0),
	
	/** 45 degrees  */
	NorthWest(1),
	
	/** 90 degrees  */
	West(2),
	
	/** 135 degrees */
	SouthWest(3),
	
	/** 180 degrees */
	South(4),
	
	/** 225 degrees */
	SouthEast(5),
	
	/** 270 degrees */
	East(6),
	
	/** 315 degrees */
	NorthEast(7),
	
	/** Don't define in Standart */
	Nonstandard(-1);
	//}}}
	
	private int value; 

	paramSCO(int value)
	{
		this.value = value;
	}
	
	//{{{ getIntValue() method
	/**
	   Returns int-value, which is corresponded to enum-value.
	   @param val enum-value
	   @return int-value
	 */
	public static int getIntValue(paramSCO val)
	{
		return val.value;
	} //}}}
	
	//{{{ getEnumValue() method
	/**
	   Returns enum-value by int-value.
	   @param val int-value
	   @return enum-value
	 */
	public static paramSCO getEnumValue(int val)
	{
		for ( paramSCO element: values() )
			if (element.value == val)
				return element;
		
		return Nonstandard;
	} //}}}
}	
