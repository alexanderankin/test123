/*
 * OpenIt jEdit Plugin (SourceFileListModel.java) 
 *  
 * Copyright (C) 2003 Matt Etheridge (matt@etheridge.org)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
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

package org.etheridge.openit.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

/**
 * Lightweight ListModel - Java's DefaultListModel is too bloody slow!
 */
public class SourceFileListModel extends AbstractListModel
{
  // the list of source files
  private List mContents = new ArrayList();
  
  public void refreshModel(List newContents)
  {
    // if there are any elements, then remove them and fire appropriate event
    if (!mContents.isEmpty()) {
      int currentContentsSize = mContents.size();
      mContents.clear();
      fireIntervalRemoved(this, 0, currentContentsSize - 1);
    }
    
    // add any new elements to the list
    if (!newContents.isEmpty()) {
      // add new contents
      mContents.addAll(newContents);
      fireIntervalAdded(this, 0, mContents.size()-1);
    }
  }
     
  public Object getElementAt(int index)
  {
    return mContents.get(index);
  }
  
  public int getSize()
  {
    return mContents.size();
  }
           
  public boolean isEmpty()
  {
    return mContents.isEmpty();
  }
}
