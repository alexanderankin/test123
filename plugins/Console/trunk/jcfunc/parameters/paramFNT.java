/*
 * paramFNT.java - returned values for CF.FNT
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
   Enum <code>paramFNT</code> contains returned values for control function CF.FNT.
 */
public enum paramFNT
{
	//{{{ enum's values
	/** primary (default) font */
	Font_Pimary(0),
	
	/** first alternative font */
	Font_Alt1(1),
	
	/** second alternative font */
	Font_Alt2(2),
	
	/** third alternative font */
	Font_Alt3(3),
	
	/** fourth alternative font */
	Font_Alt4(4),
	
	/** fifth alternative font */
	Font_Alt5(5),
	
	/** sixth alternative font */
	Font_Alt6(6),
	
	/** seventh alternative font */
	Font_Alt7(7),
	
	/** eighth alternative font */
	Font_Alt8(8),
	
	/** ninth alternative font */
	Font_Alt9(9),
	
	/** Don't define in Standart */
	Nonstandard(-1);
	//}}}
	
	private int value; 

	paramFNT(int value)
	{
		this.value = value;
	}
	
	//{{{ getIntValue() method
	/**
	   Returns int-value, which is corresponded to enum-value.
	   @param val enum-value
	   @return int-value
	 */
	public static int getIntValue(paramFNT val)
	{
		return val.value;
	} //}}}
	
	//{{{ getEnumValue() method
	/**
	   Returns enum-value by int-value.
	   @param val int-value
	   @return enum-value
	 */
	public static paramFNT getEnumValue(int val)
	{
		for ( paramFNT element: values() )
			if (element.value == val)
				return element;
		
		return Nonstandard;
	} //}}}
}	
