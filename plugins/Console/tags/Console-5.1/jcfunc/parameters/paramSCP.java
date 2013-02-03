/*
 * paramSCP.java - returned values for CF.SCP
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
   Enum <code>paramSCP</code> contains returned values for control function CF.SCP.
 */
public enum paramSCP
{
	//{{{ enum's values
	/* first parameter */
	
	/**
	    left-to-right (in the case of horizontal line orientation),
	    or top-to-bottom (in the case of vertical line orientation)
	 */
	LeftToRight(1),
	
	/**
	    right-to-left (in the case of horizontal line orientation),
	    or bottom-to-top (in the case of vertical line orientation)
	 */
	RightToLeft(2),

	/* second parameter */
	
	/** undefined (implementation-dependent) */
	Undefined(0),
	
	/**
	    the content of the active line in the presentation component (the line that contains the active
	    presentation position) is updated to correspond to the content of the active line in the data component
	    (the line that contains the active data position) according to the newly established character path
	    characteristics in the presentation component; the active data position is moved to the first character
	    position in the active line in the data component, the active presentation position in the presentation
	    component is updated accordingly
	 */
	Update_Presentation(1),

	/**
	    the content of the active line in the data component (the line that contains the active data position) is
	    updated to correspond to the content of the active line in the presentation component (the line that
	    contains the active presentation position) according to the newly established character path
	    characteristics of the presentation component; the active presentation position is moved to the first
	    character position in the active line in the presentation component, the active data position in the data
	    component is updated accordingly
	 */
	Update_Data(2),
	
	/** Don't define in Standart */
	Nonstandard(-1);
	//}}}
	
	private int value; 

	paramSCP(int value)
	{
		this.value = value;
	}
	
	//{{{ getIntValue() method
	/**
	   Returns int-value, which is corresponded to enum-value.
	   @param val enum-value
	   @return int-value
	 */
	public static int getIntValue(paramSCP val)
	{
		return val.value;
	} //}}}
	
	//{{{ getEnumValue() method
	/**
	   Returns enum-value by int-value.
	   @param val int-value
	   @return enum-value
	 */
	public static paramSCP getEnumValue(int val)
	{
		return getEnumValue(val, true);
	}
	
	/**
	   Returns enum-value by int-value.
	   @param val int-value
	   @param first flag: return value of the first parameter
	   @return enum-value
	 */
	public static paramSCP getEnumValue(int val, boolean first)
	{
		if (first) {
			switch (val) {
			   case 1: return RightToLeft;
			   case 2: return LeftToRight;
			  default: return Nonstandard; 
			}
			
		} else {
			switch (val) {
			   case 0: return Undefined;
			   case 1: return Update_Presentation;
			   case 2: return Update_Data;
			  default: return Nonstandard; 
			}
			
		}
	} //}}}
}	
