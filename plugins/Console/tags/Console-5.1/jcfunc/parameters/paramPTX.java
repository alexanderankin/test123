/*
 * paramPTX.java - returned values for CF.PTX
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
   Enum <code>paramPTX</code> contains returned values for control function CF.PTX.
 */
public enum paramPTX
{
	//{{{ enum's values
	/** end of parallel texts */
	End_PTX(0),
	
	/** beginning of a string of principal parallel text */
	BeginningPrincipal_PTX(1),
	
	/** beginning of a string of supplementary parallel text */
	BeginningSupplementary_PTX(2),
	
	/** beginning of a string of supplementary Japanese phonetic annotation */
	BeginningSupplementary_JPA(3),
	
	/** beginning of a string of supplementary Chinese phonetic annotation */
	BeginningSupplementary_CPA(4),
	
	/** end of a string of supplementary phonetic annotations */
	EndSupplementary_PA(5),
	
	/** Don't define in Standart */
	Nonstandard(-1);
	//}}}
	
	private int value; 

	paramPTX(int value)
	{
		this.value = value;
	}
	
	//{{{ getIntValue() method
	/**
	   Returns int-value, which is corresponded to enum-value.
	   @param val enum-value
	   @return int-value
	 */
	public static int getIntValue(paramPTX val)
	{
		return val.value;
	} //}}}
	
	//{{{ getEnumValue() method
	/**
	   Returns enum-value by int-value.
	   @param val int-value
	   @return enum-value
	 */
	public static paramPTX getEnumValue(int val)
	{
		for ( paramPTX element: values() )
			if (element.value == val)
				return element;
		
		return Nonstandard;
	} //}}}
}	
