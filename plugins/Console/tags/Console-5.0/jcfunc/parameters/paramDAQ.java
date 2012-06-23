/*
 * paramDAQ.java - returned values for CF.DAQ
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
   Enum <code>paramDAQ</code> contains returned values for control function CF.DAQ.
 */
public enum paramDAQ
{
	//{{{ enum's values
	/** unprotected and unguarded */ 
	UnPRT_and_UnGRD(0),
	
	/** protected and guarded */ 
	PRT_and_GRD(1),
	
	/** graphic character input */ 
	GraphInput(2),
	
	/** numeric input */ 
	NumInput(3),
	
	/** alphabetic input */ 
	AlphaInput(4),
	
	/** input aligned on the last character position of the qualified area */ 
	AlignOnLast(5),
	
	/** fill with ZEROs */ 
	FillZero(6),
	
	/** 
	    set a character tabulation stop at the active presentation position    
	    (the first character position of the qualified area) to indicate           
	    the beginning of a field
	 */ 
	FieldBeginning(7),
	
	/** protected and unguarded */ 
	PRT_and_UnGRD(8),
	
	/** fill with SPACEs */ 
	FillSpace(9),
	
	/** input aligned on the first character position of the qualified area */ 
	AlignFirst(10),
	
	/**
	    the order of the character positions in the input field is reversed,   
	    i.e. the last position in each line becomes the first and vice versa;  
	    input begins at the new first position.
	  */ 
	ReversedInput(11),

	/** Don't define in Standart */
	Nonstandard(-1);
	//}}}
	
	private int value; 

	paramDAQ(int value)
	{
		this.value = value;
	}
	
	//{{{ getIntValue() method
	/**
	   Returns int-value, which is corresponded to enum-value.
	   @param val enum-value
	   @return int-value
	 */
	public static int getIntValue(paramDAQ val)
	{
		return val.value;
	} //}}}
	
	//{{{ getEnumValue() method
	/**
	   Returns enum-value by int-value.
	   @param val int-value
	   @return enum-value
	 */
	public static paramDAQ getEnumValue(int val)
	{
		for ( paramDAQ element: values() )
			if (element.value == val)
				return element;
		
		return Nonstandard;
	} //}}}
}	
