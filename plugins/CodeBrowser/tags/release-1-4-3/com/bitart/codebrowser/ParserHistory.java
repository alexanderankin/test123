/*
 * ParserHistory.java - adaption for codeBrowser from BufferHistory
 * Copyright (C) 2003 Rudolf Widmann
 *
 *	Description:
 *	This class collects objects of the type CBRoot
 *  It is used to save parsing results
 * 
 *  A CBRoot object is identified by its path and its edit mode
 *  As it is usually a small list, sequential search is sufficient
 *
 *	$Id$
 */
package com.bitart.codebrowser;

import org.gjt.sp.jedit.*;
import java.util.*;
import org.gjt.sp.util.Log;

/**
 * ParserHistory
 * @author Rudolf Widmann
 */
public class ParserHistory
{
	static final CBRoot getEntry(String path, String mode)
	{
		//Log.log(Log.DEBUG, BeanShell.class,"+++ ParserHistory.getEntry.31: path = "+path+", mode = "+mode);
		Enumeration itr = history.elements();
		while(itr.hasMoreElements())
		{
			Entry entry = (Entry)itr.nextElement();
			if(entry.path.equals(path) && entry.mode.equals(mode))
				return entry.root;
		}
		return null;
	}

	static final void removeEntry(String path, String mode)
	{
		//Log.log(Log.DEBUG, BeanShell.class,"+++ ParserHistory.removeEntry.45: path = "+path+", mode = "+mode);
		for(int i = 0; i < history.size(); i++)
		{
			Entry entry = (Entry)history.elementAt(i);
			if(entry.mode.equals(mode) && entry.path.equals(path))
			{
				history.removeElementAt(i);
				return;
			}
		}
	}

 	static final void setEntry(String path, String mode, CBRoot root)
	{
		//Log.log(Log.DEBUG, BeanShell.class,"+++ ParserHistory.setEntry.70: path = "+path+", mode = "+mode);
		removeEntry(path, mode);
		addEntry(new Entry(path, mode, root));
	}

	public final static Vector getParserHistory()
	{
		return history;
	}

	// private members
	private static Vector history;
	private static boolean pathsCaseInsensitive;

	static
	{
		history = new Vector();
	}

	private static void addEntry(Entry entry)
	{
		int max = jEdit.getIntegerProperty("options.codebrowser.parser_history.value",5);
		history.addElement(entry);
		while(history.size() > max)
			history.removeElementAt(0);
	}

	/**
	 * Code browser entry.
	 */
	private static class Entry
	{
		String path;
		String mode;
		CBRoot root;

		Entry(String path, String mode, CBRoot root)
		{
			this.path = path;
			this.mode = mode;
			this.root = root;
		}
	}
}
