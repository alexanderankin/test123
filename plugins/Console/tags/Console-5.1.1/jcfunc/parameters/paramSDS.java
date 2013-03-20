/*
 * paramSDS.java - returned values for CF.SDS
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
   Enum <code>paramSDS</code> contains returned values for control function CF.SDS.
 */
public enum paramSDS
{
	//{{{ enum's values
	/** end of a directed string; re-establish the previous direction */
	End(0),
	
	/** start of a directed string; establish the direction left-to-right */
	Start_LeftToRight(1),
	
	/** start of a directed string; establish the direction right-to-left */
	Start_RightToLeft(2),
 
	/** Don't define in Standart */
	Nonstandard(-1);
	//}}}
	
	private int value; 

	paramSDS(int value)
	{
		this.value = value;
	}
	
	//{{{ getIntValue() method
	/**
	   Returns int-value, which is corresponded to enum-value.
	   @param val enum-value
	   @return int-value
	 */
	public static int getIntValue(paramSDS val)
	{
		return val.value;
	} //}}}
	
	//{{{ getEnumValue() method
	/**
	   Returns enum-value by int-value.
	   @param val int-value
	   @return enum-value
	 */
	public static paramSDS getEnumValue(int val)
	{
		for ( paramSDS element: values() )
			if (element.value == val)
				return element;
		
		return Nonstandard;
	} //}}}
}	
