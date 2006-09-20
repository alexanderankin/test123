/*
 *
 * CommandQueue.java
 * Copyright (C) 2001 Dominic Stolerman
 * dstolerman@jedit.org
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

import java.util.Vector;
import java.util.Enumeration;


public class CommandQueue
{
  public CommandQueue()
  {
    qu = new Vector();
  }
  
  /**
   * Adds a command to th beginning of the queue;
   */
  public synchronized void addFirst(Command c)
  {
    qu.insertElementAt(c, 0);
    insertPos++;
  }
  
  /**
   * Adds commands to the end of the queue;
   */
  public synchronized void add(Command c)
  {
    qu.insertElementAt(c, insertPos);
    insertPos++;
  }
  
  /**
   * Adds commands to the queue to be run after those added using {@link #add(Command)} and {@link #addFirst(Command)}.
   */
  public synchronized void addLast(Command c)
  {
    qu.addElement(c);
  }
  
  public int size()
  {
    return qu.size();
  }
  
  public synchronized void executeNext(ScriptContext sc)
  {
    ((Command)qu.remove(0)).run(sc);
  }
  
  public void executeAll(ScriptContext sc)
  {
    while(qu.size() != 0)
    {
      executeNext(sc);
    }
  }
  
  private int insertPos = 0;
  private Vector qu;
}

