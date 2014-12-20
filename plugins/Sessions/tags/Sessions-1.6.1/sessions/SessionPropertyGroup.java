/*
 * SessionPropertyGroup.java - session property pane group
 * Copyright (C) 2001 Dirk Moebius
 *
 * Based on OptionGroup.java Copyright (C) 2000 mike dillon
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


package sessions;


import java.util.Enumeration;
import java.util.Vector;
import java.util.Collections;
import java.util.Comparator;
import org.gjt.sp.util.Log;


public class SessionPropertyGroup
{

	/**
	 * @param label  the label to be shown in the dialog.
	 */
	public SessionPropertyGroup(String label)
	{
		this.label = label;
		children = new Vector();
	}


	public String getLabel()
	{
		return label;
	}


	public void addGroup(SessionPropertyGroup group)
	{
		if(children.indexOf(group) == -1)
			children.addElement(group);
	}


	public void addPane(SessionPropertyPane pane)
	{
		if(children.indexOf(pane) == -1)
			children.addElement(pane);
	}


	public Enumeration getChildren()
	{
		return children.elements();
	}


	public void save()
	{
		Enumeration myEnum = getChildren();
		while(myEnum.hasMoreElements())
		{
			Object elem = myEnum.nextElement();
			try
			{
				if(elem instanceof SessionPropertyPane)
					((SessionPropertyPane)elem).save();
				else if(elem instanceof SessionPropertyGroup)
					((SessionPropertyGroup)elem).save();
			}
			catch(Throwable t)
			{
				Log.log(Log.ERROR, elem, "Error saving session properties pane");
				Log.log(Log.ERROR, elem, t);
			}
		}
	}


	public final String toString()
	{
		return getLabel();
	}


	void sort()
	{
		Collections.sort(children, new PaneCompare());
	}


	private String label;
	private Vector children;


	/**
	 * A comparator for property pane sorting that always sorts
	 * the DefaultSessionPropertyPane first.
	 */
	private class PaneCompare implements Comparator
	{
		public int compare(Object obj1, Object obj2)
		{
			if(obj1 == obj2)
				return 0;
			else if(obj1 instanceof DefaultSessionPropertyPane)
				return Integer.MIN_VALUE;
			else if(obj2 instanceof DefaultSessionPropertyPane)
				return Integer.MAX_VALUE;
			else
				return obj1.toString().compareTo(obj2.toString());
		}
	}
	
}
