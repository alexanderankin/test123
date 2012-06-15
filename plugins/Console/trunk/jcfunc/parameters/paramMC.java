/*
 * paramMC.java - returned values for CF.MC
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
   Enum <code>paramMC</code> contains returned values for control function CF.MC.
 */
public enum paramMC
{
	//{{{ enum's values

	/** initiate transfer to a primary auxiliary device */
	InitToPimary(0),
	
	/** initiate transfer from a primary auxiliary device */
	InitFromPrimary(1),
	
	/** initiate transfer to a secondary auxiliary device */
	InitToSecondary(2),
	
	/** initiate transfer from a secondary auxiliary device */
	InitFromSecondary(3),
	
	/** stop relay to a primary auxiliary device */
	StopRelayPrimary(4),
	
	/** start relay to a primary auxiliary device */
	StartRelayPrimary(5),
	
	/** stop relay to a secondary auxiliary device */
	StopRelaySecondary(6),
	
	/** start relay to a secondary auxiliary device */
	StartRelaySecondary(7),
	
	/** Don't define in Standart */                 
	Nonstandard(-1);
	//}}}
	
	private int value; 

	paramMC(int value)
	{
		this.value = value;
	}
	
	//{{{ getIntValue() method
	/**
	   Returns int-value, which is corresponded to enum-value.
	   @param val enum-value
	   @return int-value
	 */
	public static int getIntValue(paramMC val)
	{
		return val.value;
	} //}}}
	
	//{{{ getEnumValue() method
	/**
	   Returns enum-value by int-value.
	   @param val int-value
	   @return enum-value
	 */
	public static paramMC getEnumValue(int val)
	{
		for ( paramMC element: values() )
			if (element.value == val)
				return element;
		
		return Nonstandard;
	} //}}}
}	
