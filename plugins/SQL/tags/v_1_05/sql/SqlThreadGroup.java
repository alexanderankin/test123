/**
 * SqlThreadGroup.java - Sql Plugin
 * :tabSize=8:indentSize=8:noTabs=false:
 *
 * Copyright (C) 2001 Sergey V. Udaltsov
 * svu@users.sourceforge.net
 *
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

package sql;

import java.util.*;

/**
 *  Description of the Class
 *
 * @author     svu
 */
public class SqlThreadGroup
{
	protected ThreadGroup threadGroup = null;

	protected ArrayList listeners = new ArrayList();


	/**
	 *  Constructor for the SqlThreadGroup object
	 *
	 * @param  name  Description of Parameter
	 * @since
	 */
	public SqlThreadGroup(String name)
	{
		threadGroup = new ThreadGroup(name);
	}


	/**
	 *  Gets the NumberOfRequest attribute of the SqlThreadGroup object
	 *
	 * @return    The NumberOfRequest value
	 * @since
	 */
	public int getNumberOfRequest()
	{
		// not always exact but ..
		return threadGroup.activeCount();
	}


	/**
	 *  Adds a feature to the Listener attribute of the SqlThreadGroup object
	 *
	 * @param  l  The feature to be added to the Listener attribute
	 * @since
	 */
	public void addListener(Listener l)
	{
		synchronized (listeners)
		{
			listeners.add(l);
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  l  Description of Parameter
	 * @since
	 */
	public void removeListener(Listener l)
	{
		synchronized (listeners)
		{
			listeners.remove(l);
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  r  Description of Parameter
	 * @since
	 */
	public void runInGroup(final Runnable r)
	{
		final Thread th = new Thread(
		                          threadGroup,
		                          new Runnable()
		                          {
			                          public void run()
			                          {
				                          fireChange(threadGroup.activeCount());
				                          r.run();
				                          fireChange(threadGroup.activeCount() - 1);
			                          }
		                          });
		th.start();
	}


	/**
	 *  Description of the Method
	 *
	 * @param  numberOfActiveThreads  Description of Parameter
	 * @since
	 */
	protected void fireChange(int numberOfActiveThreads)
	{
		List v = null;
		synchronized (listeners)
		{
			v = (List) listeners.clone();
		}

		for (Iterator e = v.iterator();
		                e.hasNext();)
		{
			final Listener l = (Listener) e.next();
			l.groupChanged(numberOfActiveThreads);
		}
	}


	public static interface Listener
	{
		void groupChanged(int numberOfActiveThreads);
	}

}

