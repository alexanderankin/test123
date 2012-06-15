/*
 * paramIDCS.java - returned values for CF.IDCS
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
   Enum <code>paramIDCS</code> contains returned values for control function CF.IDCS.
 */
public enum paramIDCS
{
	//{{{ enum's values
	/** used with the DIAGNOSTIC state of the STATUS REPORT TRANSFER MODE (SRTM) */ 
	SRTM(1),
	
	/** reserved for Dynamically Redefinable Character Sets (DRCS) */ 
	DRCS(2),
	
	/** Don't define in Standart */
	Nonstandard(-1);
	//}}}
	
	private int value; 

	paramIDCS(int value)
	{
		this.value = value;
	}
	
	//{{{ getIntValue() method
	/**
	   Returns int-value, which is corresponded to enum-value.
	   @param val enum-value
	   @return int-value
	 */
	public static int getIntValue(paramIDCS val)
	{
		return val.value;
	} //}}}
	
	//{{{ getEnumValue() method
	/**
	   Returns enum-value by int-value.
	   @param val int-value
	   @return enum-value
	 */
	public static paramIDCS getEnumValue(int val)
	{
		for ( paramIDCS element: values() )
			if (element.value == val)
				return element;
		
		return Nonstandard;
	} //}}}
}	
