/*
 * OpenIt jEdit Plugin (SourcePathElement.java) 
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

package org.etheridge.openit.sourcepath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an element in the source path (ie. D:\source or D:\source.jar)
 */
public abstract class SourcePathElement
{
  // the "name" of this source path element - eg. D:\source
  private String mName;
  
  // list of source files in this source path element
  protected List mSourceFiles;
  
  public SourcePathElement(String name)
  {
    mName = name;
    
    mSourceFiles = new ArrayList();
    
    loadFiles();
  }

  public String getName()
  {
    return mName;
  }
  
  public List getSourcePathFiles()
  {
    return Collections.unmodifiableList(mSourceFiles);
  }
  
  /**
   * Loads the files in this source path element.
   */
  protected abstract void loadFiles();
}
