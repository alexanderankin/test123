/*
 * paramQUAD.java - returned values for CF.QUAD
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
   Enum <code>paramQUAD</code> contains returned values for control function CF.QUAD.
 */
public enum paramQUAD
{
	//{{{ enum's values
	/** flush to line home position margin */
	HomeAligment(0),
	
	/** flush to line home position margin and fill with leader */
	HomeAligmentAndFilling(1),
	
	/** centre between line home position and line limit position margins */
	Centre(2),
	
	/** centre between line home position and line limit position margins and fill with leader */
	CentreAndFilling(3),
	
	/** flush to line limit position margin */
	LimitAligment(4),
	
	/** flush to line limit position margin and fill with leader */
	LimitAligmentAndFillinig(5),
	
	/** flush to both margins */
	JustifiedAlignment(6),
	
	/** Don't define in Standart */
	Nonstandard(-1);
	//}}}
	
	private int value; 

	paramQUAD(int value)
	{
		this.value = value;
	}
	
	//{{{ getIntValue() method
	/**
	   Returns int-value, which is corresponded to enum-value.
	   @param val enum-value
	   @return int-value
	 */
	public static int getIntValue(paramQUAD val)
	{
		return val.value;
	} //}}}
	
	//{{{ getEnumValue() method
	/**
	   Returns enum-value by int-value.
	   @param val int-value
	   @return enum-value
	 */
	public static paramQUAD getEnumValue(int val)
	{
		for ( paramQUAD element: values() )
			if (element.value == val)
				return element;
		
		return Nonstandard;
	} //}}}
}	
