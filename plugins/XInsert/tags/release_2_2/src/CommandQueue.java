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
import org.gjt.sp.util.Log;


public class CommandQueue {
  public CommandQueue() {
    queue = new Vector();
  }
  
  /**
   * Adds a command to th beginning of the queue;
   */
  public synchronized void addFirst(Command cmd) {
    queue.insertElementAt(cmd, 0);
    insertPos++;
  }
  
  /**
   * Adds commands to the end of the queue;
   */
  public synchronized void add(Command cmd) {
    queue.insertElementAt(cmd, insertPos);
    insertPos++;
  }
  
  /**
   * Adds commands to the queue to be run after those added using {@link #add(Command)} and {@link #addFirst(Command)}.
   */
  public synchronized void addLast(Command cmd) {
    queue.addElement(cmd);
  }
  
  public int size() {
    return queue.size();
  }
  
  public synchronized void executeNext(ScriptContext context) {
    ((Command) queue.remove(0)).run(context);
  }
  
  public void executeAll(ScriptContext context) {
    while(queue.size() != 0) {
      executeNext(context);
    }
  }
  
  private int insertPos = 0;
  private Vector queue;
}

