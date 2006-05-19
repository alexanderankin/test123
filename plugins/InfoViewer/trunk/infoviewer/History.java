/*
 * History.java - Model for an URL History
 * Copyright (C) 1999-2001 Dirk Moebius
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

package infoviewer;

import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;
import org.gjt.sp.jedit.jEdit;

/**
 * this class maintains a list of visitid URLs and remembers the current entry,
 * that is being viewed.
 */
public class History
{

	private Stack backStack = new Stack();
	private Stack forwardStack = new Stack();
	// private Vector entries = new Vector();

//	private int currentPos = -1;

	public History()
	{
	}

	/**
	 * add a new entry to the history. The new entry is made the current
	 * entry of the history.
	 */
	public synchronized void add(TitledURLEntry e)
	{
		if (e == null)
			return;
		backStack.push(e);
		forwardStack.clear();
	}

	/** returns the current URL of the history, as String. */
	public String getCurrent()
	{
		if (backStack.isEmpty()) return null;
		TitledURLEntry entry = (TitledURLEntry)backStack.lastElement();
		return entry.getURL();
	}

	
	public int getHistoryPos() {
		return backStack.size();
	}
	/**
	 * sets the internal state of the history to the next entry and returns
	 * its URL.
	 * 
	 * @return the next URL as String, or null if the end of the history is
	 *         reached.
	 */
	public synchronized TitledURLEntry getNext(TitledURLEntry current)
	{
		if (forwardStack.isEmpty()) return null;
		TitledURLEntry element = (TitledURLEntry)forwardStack.pop();
		backStack.push(current);
		return element;
	}

	/** return true, if there is a next entry in the history. */
	public boolean hasNext()
	{
		return !forwardStack.isEmpty();
	}

	/**
	 * sets the internal state of the history to the previous entry and
	 * returns its URL.
	 * 
	 * @return the previous URL as String, or null if the beginning of the
	 *         history is reached.
	 */
	public synchronized TitledURLEntry getPrevious(TitledURLEntry current)
	{
		TitledURLEntry element = (TitledURLEntry)backStack.pop();
		forwardStack.push(current);
		return element;
	}

	/** return true, if there is a previous entry in the history. */
	public boolean hasPrevious()
	{
		return !backStack.isEmpty();
	}


	/**
	 * get the last entries from the history, but now more than specified in
	 * the property 'infoviewer.max_go_menu'. The entries are such that the
	 * current entry is among them.
	 */
	public TitledURLEntry[] getGoMenuEntries()
	{
		int max = getMaxVisibleMenuEntries();
		int count = backStack.size();
		if (count > max) count = max;
		TitledURLEntry[] entries = new TitledURLEntry[count];
		Iterator itr = backStack.iterator();
		int i=0;
		while (itr.hasNext() && (i < count)) {
			TitledURLEntry ent = (TitledURLEntry) itr.next();
			entries[i++]=ent;
		}
		return entries;
	}
/*
	private TitledURLEntry[] getEntries(int from, int to)
	{
		Vector v = new Vector();
		for (int i = from; i <= to; i++)
		{
			TitledURLEntry e = getEntry(i);
			if (e != null)
			{
				e.setHistoryPos(i);
				v.addElement(e);
			}
		}
		TitledURLEntry[] entr = new TitledURLEntry[v.size()];
		v.copyInto(entr);
		return entr;
	}
*/
	private int getMaxVisibleMenuEntries()
	{
		String history = jEdit.getProperty("history");
		int max;
		try
		{
			max = Integer.parseInt(history);
			if (max < 1)
				throw new NumberFormatException();
		}
		catch (NumberFormatException e)
		{
			max = 20;
		}
		return max;
	}

}
