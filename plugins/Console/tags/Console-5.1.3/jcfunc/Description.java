/*
 * Description.java - storage for some control sequence 
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
 
package jcfunc;

/**
   Class <code>Description</code> stores data about some control sequence.
 */
public class Description
{	
	/**
	   Index of sequence's first character.
	 */
	public int bPosition;
	
	/**
	   Index of character followed after sequence's last character.
	 */
	public int ePosition;
	
	/**
	   Control function of sequence.
	 */
	public CF function;
	
	/**
	   Parameters of control function, if any.
	 */
	public int[] parameters = null;
	
	/**
	   Constructor.
	   @param start index of sequence's first character
	   @param end index of character followed after sequence's last character
	   @param func control function of sequence
	   @param params parameters of control function
	 */
	public Description(int start, int end, CF func, int[] params)
	{
		bPosition  = start;
		ePosition  = end;
		function   = func;
		parameters = params; 
	}
	
	/**
	   Returns string representation of this object.
	 */
	public String toString()
	{
		StringBuilder str = new StringBuilder();
		
		str.append(" (")
		   .append(bPosition)
		   .append(", ")
		   .append(ePosition)
		   .append(") [");
		   
		for (int i = 0; i < parameters.length; i++) {
			str.append( parameters[i] );
			if (i < parameters.length - 1) str.append(",");
		}
		
		return function.toString() + str.append("]").toString();
	}
}
