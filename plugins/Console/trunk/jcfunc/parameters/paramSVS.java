/*
 * paramSVS.java - returned values for CF.SVS
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
   Enum <code>paramSVS</code> contains returned values for control function CF.SVS.
 */
public enum paramSVS
{
	//{{{ enum's values
	/** 6 lines per 25,4 mm  */
	Six_TI(0),
	
	/** 4 lines per 25,4 mm  */
	Four_TI(1),
	
	/** 3 lines per 25,4 mm  */
	Three_TI(2),
	
	/** 12 lines per 25,4 mm */
	Twelve_TI(3),
	
	/** 8 lines per 25,4 mm  */
	Eight_TI(4),
	
	/** 6 lines per 30,0 mm  */
	Six_TH(5),
	
	/** 4 lines per 30,0 mm  */
	Four_TH(6),
	
	/** 3 lines per 30,0 mm  */
	Three_TH(7),
	
	/** 12 lines per 30,0 mm */
	Twelve_TH(8),
	
	/** 2 lines per 25,4 mm  */
	Two_TI(9),
			       
	/** Don't define in Standart */
	Nonstandard(-1);
	//}}}
	
	private int value; 

	paramSVS(int value)
	{
		this.value = value;
	}
	
	//{{{ getIntValue() method
	/**
	   Returns int-value, which is corresponded to enum-value.
	   @param val enum-value
	   @return int-value
	 */
	public static int getIntValue(paramSVS val)
	{
		return val.value;
	} //}}}
	
	//{{{ getEnumValue() method
	/**
	   Returns enum-value by int-value.
	   @param val int-value
	   @return enum-value
	 */
	public static paramSVS getEnumValue(int val)
	{
		for ( paramSVS element: values() )
			if (element.value == val)
				return element;
		
		return Nonstandard;
	} //}}}
}	
