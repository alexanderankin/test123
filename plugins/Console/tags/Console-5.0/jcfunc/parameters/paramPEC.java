/*
 * paramPEC.java - returned values for CF.PEC
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
   Enum <code>paramPEC</code> contains returned values for control function CF.PEC.
 */
public enum paramPEC
{
	//{{{ enum's values
	/** normal (as specified by SCS, SHS or SPI) */
	Normal(0),

	/** expanded (multiplied by a factor not greater than 2) */
	Expanded(1),

	/** condensed (multiplied by a factor not less than 0,5) */
	Condensed(2),

	/** Don't define in Standart */                
	Nonstandard(-1);
	//}}}
	
	private int value; 

	paramPEC(int value)
	{
		this.value = value;
	}
	
	//{{{ getIntValue() method
	/**
	   Returns int-value, which is corresponded to enum-value.
	   @param val enum-value
	   @return int-value
	 */
	public static int getIntValue(paramPEC val)
	{
		return val.value;
	} //}}}
	
	//{{{ getEnumValue() method
	/**
	   Returns enum-value by int-value.
	   @param val int-value
	   @return enum-value
	 */
	public static paramPEC getEnumValue(int val)
	{
		for ( paramPEC element: values() )
			if (element.value == val)
				return element;
		
		return Nonstandard;
	} //}}}
}	
