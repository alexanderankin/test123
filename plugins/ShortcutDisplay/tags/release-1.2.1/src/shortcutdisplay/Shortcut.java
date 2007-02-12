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


import org.gjt.sp.jedit.*;
import shortcutdisplay.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.util.*; 
import org.gjt.sp.jedit.gui.*;


/**
 *  Convienient way to pass around the three Strings associated with a shortcut
 *  - the name, and the two shortcuts
 *
 *@author	 jchoyt
 *@created	April 29, 2004
 */
class Shortcut implements Comparable
{

	
	String action;
	String shortcut1; 

	
	/**
	 *  Description of the Method
	 *
	 *@param  o  Description of the Parameter
	 *@return	Description of the Return Value
	 */
	public int compareTo(Object o)
	{
		Shortcut passedSc = (Shortcut) o;
		return shortcut1.compareTo(passedSc.getShortcut1());
	} 

	
	/**
	 *  Gets the action attribute of the Shortcut object
	 *
	 *@return	The action value
	 */
	public String getAction()
	{
		return action;
	} 

	
	/**
	 *  Gets the shortcut1 attribute of the Shortcut object
	 *
	 *@return	The shortcut1 value
	 */
	public String getShortcut1()
	{
		return shortcut1;
	} 

	
	/**
	 *  Constructor for the Shortcut object
	 *
	 *@param  s1   Description of the Parameter
	 *@param  act  Description of the Parameter
	 */
	public Shortcut(String act, String s1)
	{
		action = act;
		shortcut1 = s1;
	} 

	
	/**
	 *  Description of the Method
	 *
	 *@return	Description of the Return Value
	 */
	public String toString()
	{
		return "ShortcutDisplay.Shortcut[shortcut=" + shortcut1 + ", action=" + action + "]";
	} 
} 


