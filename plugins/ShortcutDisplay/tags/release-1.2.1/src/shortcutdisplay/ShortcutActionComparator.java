/*
 * :tabSize=4:indentSize=4:noTabs=true:
 * 
 *
 *  $Source$
 *  Copyright (C) 2004 Jeffrey Hoyt
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package shortcutdisplay;


import java.util.Comparator;
import org.gjt.sp.jedit.*;
import shortcutdisplay.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.util.*; 
import org.gjt.sp.jedit.gui.*;


/**
 *  Comparator to sort Shortcut objects by shortcut instead of action
 *
 *@author	 jchoyt
 *@created	January 3, 2005
 */
class ShortcutActionComparator implements Comparator
{

	
	private static ShortcutActionComparator comparator; 

	
	/**
	 *  Sorts the shortcuts by shortcut
	 *
	 *@param  o1	Description of the Parameter
	 *@param  o2	Description of the Parameter
	 *@return	   a negative integer, zero, or a positive integer as the first
	 *	  argument is less than, equal to, or greater than the second.
	 */
	public int compare(Object o1, Object o2)
	{
		Shortcut sc1 = (Shortcut) o1;
		Shortcut sc2 = (Shortcut) o2;
		return sc1.getAction().compareTo(sc2.getAction());
	} 

	
	/**
	 *  Constructor for the ShortcutActionComparator object
	 */
	private ShortcutActionComparator() { } 

	
	/**
	 *  Gets the comparator attribute of the ShortcutActionComparator class
	 *
	 *@return	The comparator value
	 */
	public static ShortcutActionComparator getComparator()
	{
		if (comparator == null)
		{
			comparator = new ShortcutActionComparator();
		}
		return comparator;
	} 
}
