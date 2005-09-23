/*
 * StringList.java - Helper functions for 
    perl-like string lists.
 *
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2005 Alan Ezust
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

package console.utils;

// {{{ imports
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
// }}}

public class StringList extends LinkedList<String>
{

    // {{{ StringList()
    	public StringList()
	{
	}
    
	public StringList(Object[] array)
	{
		addAll(array);
    } // }}}

    // {{{ addAll() 
	public void addAll(Object[] array)
	{
		for (int i = 0; i < array.length; ++i)
		{
			add(array[i].toString());
		}
	}   // }}}
    
    // {{{ split()     
	public static StringList split(String orig, Object delim)
	{
		if ((orig == null) || (orig.length() == 0))
			return new StringList();
		return new StringList(orig.split(delim.toString()));
	} // }}} 

    // {{{ toString() 
	public String toString()
	{
		return join("\n");
	}
    // }}}
    
    // {{{ join() 
	public static String join(Collection c, String delim)
	{
		StringList sl = new StringList();
		Iterator itr = c.iterator();
		while (itr.hasNext())
		{
			Object o = itr.next();
			String s = o.toString();
			sl.add(s);
		}
		return sl.join(delim);
	}

	public String join(String delim)
	{
		int s = size();
		if (s < 1)
			return "";
		if (s == 1)
			return get(0).toString();
		else
		{
			StringBuffer retval = new StringBuffer();
			retval.append(get(0));
			for (int i = 1; i < s; ++i)
				retval.append(delim + get(i));
			return retval.toString();
		}

	}
    // }}}
    
    // {{{ main() 
	public static void main(String args[])
	{
		String teststr = "a,b,c,d,e,f";
		StringList sl = StringList.split(teststr, ",");
		String joinstr = sl.join(",");
		// assert(teststr.equals(joinstr));
		System.out.println("Test Passed");

    }// }}} 

}

