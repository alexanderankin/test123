/*
 * paramSPD.java - returned values for CF.SPD
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
   Enum <code>paramSPD</code> contains returned values for control function CF.SPD.
 */
public enum paramSPD
{
	//{{{ enum's values
	/* first parameter */
	
	/**
	    line orientation: horizontal
	    line progression: top-to-bottom
	    character path:   left-to-right
	 */
	Horizontal_TB_LR(0),
	
	/**
	    line orientation: vertical
	    line progression: right-to-left
	    character path:   top-to-bottom
	 */
	Vertical_RL_TB(1),
	
	/**
	    line orientation: vertical
	    line progression: left-to-right
	    character path:   top-to-bottom
	 */
	Vertical_LR_TB(2),
	
	/**
	    line orientation: horizontal
	    line progression: top-to-bottom
	    character path:   right-to-left
	 */
	Horizontal_TB_RL(3),
	
	/**
	    line orientation: vertical
	    line progression: left-to-right
	    character path:   bottom-to-top
	 */
	Vertical_LR_BT(4),
	
	/**
	    line orientation: horizontal
	    line progression: bottom-to-top
	    character path:   right-to-left
	 */
	Horizontal_BT_RL(5),
	
	/**
	    line orientation: horizontal    
	    line progression: bottom-to-top
	    character path:   left-to-right
	 */
	Horizontal_BT_LR(6),
	
	/**
	    line orientation: vertical
	    line progression: right-to-left
	    character path:   bottom-to-top
	 */
	Vertical_RL_BT(7),
	
	/* second parameter */
	
	/** undefined (implementation-dependent) */
	Undefined(0),
	
	/**
	    the content of the presentation component is updated to correspond to the content of the data
	    component according to the newly established characteristics of the presentation component; the
	    active data position is moved to the first character position in the first line in the data component, the
	    active presentation position in the presentation component is updated accordingly
	 */
	Update_Presentation(1),
	
	/**
	    the content of the data component is updated to correspond to the content of the presentation
	    component according to the newly established characteristics of the presentation component; the
	    active presentation position is moved to the first character position in the first line in the presentation
	    component, the active data position in the data component is updated accordingly.
	 */
	Update_Data(2),
	
	/** Don't define in Standart */
	Nonstandard(-1);
	//}}}
	
	private int value; 

	paramSPD(int value)
	{
		this.value = value;
	}
	
	//{{{ getIntValue() method
	/**
	   Returns int-value, which is corresponded to enum-value.
	   @param val enum-value
	   @return int-value
	 */
	public static int getIntValue(paramSPD val)
	{
		return val.value;
	} //}}}
	
	//{{{ getEnumValue() method
	/**
	   Returns enum-value by int-value.
	   @param val int-value
	   @return enum-value
	 */
	public static paramSPD getEnumValue(int val)
	{
		return getEnumValue(val, true);
	}
	
	/**
	   Returns enum-value by int-value.
	   @param val int-value
	   @param first flag: return value of the first parameter
	   @return enum-value
	 */
	public static paramSPD getEnumValue(int val, boolean first)
	{
		if (first) {
			for ( paramSPD element: values() )
				if (element.value == val)
					return element;
			
			return Nonstandard;
			
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
