/*
 * paramSSU.java - returned values for CF.SSU
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright(C) 2012, Artem Bryantsev
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
   Enum <code>paramSSU</code> contains returned values for control function CF.SSU.
 */
public enum paramSSU
{
	//{{{ enum's values
	/** The dimensions of this unit are device-dependent */
	Character(0),
	
	/** Millimetre */
	Millimetre(1),
	
	/** 0,03528 mm(1/720 of 25,4 mm) */
	ComputerDecipoint(2),
	
	/** 0,03759 mm(10/266 mm) */
	Decididot(3),
	
	/** 0,0254 mm(1/1000 of 25,4 mm) */
	Mil(4),
	
	/** 0,02117 mm(1/1200 of 25,4 mm) */
	BasicMeasuringUnit(5),
	
	/** 0,001 mm */
	Micrometre(6),
	
	/** The smallest increment that can be specified in a device */
	Pixel(7),
	
	/** 0,03514 mm(35/996 mm) */
	Decipoint(8),
	
	/** Don't define in Standart */
	Nonstandard(-1);
	//}}}
	
	private int value; 

	paramSSU(int value)
	{
		this.value = value;
	}
	
	//{{{ getIntValue() method
	/**
	   Returns int-value, which is corresponded to enum-value.
	   @param val enum-value
	   @return int-value
	 */
	public static int getIntValue(paramSSU val)
	{
		return val.value;
	} //}}}
	
	//{{{ getEnumValue() method
	/**
	   Returns enum-value by int-value.
	   @param val int-value
	   @return enum-value
	 */
	public static paramSSU getEnumValue(int val)
	{
		for( paramSSU element: values() )
			if(element.value == val)
				return element;
		
		return Nonstandard;
	} //}}}
}	
