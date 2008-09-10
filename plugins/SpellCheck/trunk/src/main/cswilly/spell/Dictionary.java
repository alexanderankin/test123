/*
* $Revision$
* $Date$
* $Author$
*
* Copyright (C) 2008 Eric Le Lay
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

package cswilly.spell;



/**
 * Bean to keep separate the internal name of a dictionary from 
 * it's description.
 * this class is immutable
 */
public class Dictionary{
	private final String name;
	private final String description;
	
	public Dictionary(String name, String description){
		this.name=name;
		this.description=description;
	}
	
	/**
	 * @return a user-friendly description of this dictionary
	 */
	 public String getDescription(){
		 return description;
	 }
	
	/**
	 * @return an identifier for the EngineManager to find it
	 */
	public String getName(){
		return name;
	}

	public String toString(){
		return getDescription();
	}
}
