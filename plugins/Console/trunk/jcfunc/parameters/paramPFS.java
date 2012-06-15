/*
 * paramPFS.java - returned values for CF.PFS
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
   Enum <code>paramPFS</code> contains returned values for control function CF.PFS.
 */
public enum paramPFS
{
	//{{{ enum's values
	/** tall basic text communication format */
	TextComTail(0),
                
	/** wide basic text communication format */
	TextComWide(1),

	/** tall basic A4 format */
	A4BasicTail(2),

	/** wide basic A4 format */
	A4BasicWide(3),

	/** tall North American letter format */
	NALetterTail(4),

	/** wide North American letter format */
	NALetterWide(5),

	/** tall extended A4 format */
	A4ExtendedTail(6),

	/** wide extended A4 format */
	A4ExtendedWide(7),

	/** tall North American legal format */
	NALegalTail(8),

	/** wide North American legal format */
	NALegalWide(9),

	/** A4 short lines format */
	A4ShortLines(10),

	/** A4 long lines format */
	A4LongLines(11),

	/** B5 short lines format */
	B5ShortLines(12),

	/** B5 long lines format */
	B5LongLines(13),

	/** B4 short lines format */
	B4ShortLines(14),

	/** B4 long lines format */
	B4LongLines(15),

	/** Don't define in Standart */
	Nonstandard(-1);
	//}}}
	
	private int value; 

	paramPFS(int value)
	{
		this.value = value;
	}
	
	//{{{ getIntValue() method
	/**
	   Returns int-value, which is corresponded to enum-value.
	   @param val enum-value
	   @return int-value
	 */
	public static int getIntValue(paramPFS val)
	{
		return val.value;
	} //}}}
	
	//{{{ getEnumValue() method
	/**
	   Returns enum-value by int-value.
	   @param val int-value
	   @return enum-value
	 */
	public static paramPFS getEnumValue(int val)
	{
		for ( paramPFS element: values() )
			if (element.value == val)
				return element;
		
		return Nonstandard;
	} //}}}
}	
