/*
 * DirectoryStack.java - A running process
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
package console;

// {{{ imports 
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;
// }}} 


// {{{ DirectoryStack 
/**
 * Processes "make entering" and "make leaving" messages, to update the
 * current directory. Uses a stack so it can push and pop previous values.
 *  
 */
public class DirectoryStack
{

	// {{{ processLine() 	
	/**
	 * @return true if the directory has changed
	 *    (i.e. an entering/leaving message has been encountered
	 */
	public boolean processLine(CharSequence line)
	{
		Matcher match = makeEntering.matcher(line);
		if (match.find())
		{
			String enteringDir = match.group(1);
			push(enteringDir);
			return true;
		}

		match = makeLeaving.matcher(line);
		if (match.find() && !isEmpty())
		{
			pop();
			return true;
		}
		return false;
	}
	// }}}
	
	// {{{ static initialization block 
	static
	{
		try
		{
			makeEntering = Pattern.compile(jEdit.getProperty("console.error.make.entering"));
			makeLeaving = Pattern.compile(jEdit.getProperty("console.error.make.leaving"));
		} catch (PatternSyntaxException re)
		{
			Log.log(Log.ERROR, ConsoleProcess.class, re);
		}
	}
	// }}}

	// {{{ Constructor
	public DirectoryStack()
	{
		mList = new LinkedList<String>();
	}
	// }}}

	// {{{ current() 
	/** @return the current directory on the top of the stack
	*/
	public String current()
	{
		return mList.getLast();
	}
	// }}}
	
	// {{{ push()
	public void push(String v)
	{
		if (v != null)
		{
//			Log.log(Log.WARNING, DirectoryStack.class, "Push: " + v);
			mList.add(v);
		}
	}
	// }}}
	
	// {{{ isEmpty()

	public boolean isEmpty()
	{
		return mList.isEmpty();
	}

	// }}}
	
	// {{{ pop()
	public String pop()
	{
		if (mList.size() < 1)
			return null;
		String retval =  mList.removeLast(); 
//		Log.log(Log.WARNING, DirectoryStack.class, "Pop: " + retval);
		return retval;
	}
	// }}}

	// {{{ main()
	public static void main(String args[])
	{
		DirectoryStack ds = new DirectoryStack();
		ds.push("Hello");
		ds.push("there");
		System.out.println(ds.pop());
		System.out.println(ds.pop());
		System.out.println(ds.pop());
	}
	// }}}
	
	// {{{ Data Members
	LinkedList<String> mList;
	private static Pattern makeEntering, makeLeaving;
	// }}}
} 
// }}}
