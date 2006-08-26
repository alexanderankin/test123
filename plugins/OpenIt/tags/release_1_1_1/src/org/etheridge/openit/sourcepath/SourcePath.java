/*
 * OpenIt jEdit Plugin (SourcePath.java) 
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

import java.io.File;
import java.lang.String;
import java.lang.StringBuffer;
import java.lang.System;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.etheridge.openit.sourcepath.filter.SourcePathFilter;

/**
 * Represents a source path, which is made up of SourcePathFiles (ie. java source
 * files).
 */
public class SourcePath
{
  // list of source path elements in this source path
  private List mSourcePathElements;
  private SourcePathFilter mFilter;
  
  public SourcePath(String sourcePath, SourcePathFilter filter)
  {
    mFilter = filter;
    
    // tokenize on the path separator
    String pathSeparator = System.getProperty("path.separator");
    StringTokenizer tokenizer = new StringTokenizer(sourcePath, pathSeparator);
    mSourcePathElements = new ArrayList();
    while (tokenizer.hasMoreTokens()) {
      String currentToken = tokenizer.nextToken();
      if (filter.isSourcePathElementAllowed(currentToken)) {
        SourcePathElement element = new DirectorySourcePathElement(currentToken, filter);
        if (!element.isLink()) {
          mSourcePathElements.add(element);
        }
      }
    }
  }
  
  /**
   * Adds a single element to the sourcepath
   *
   * @param sourcePathElement the string representation of a single source path
   * element. (ie. D:\source)
   */
  public void addSourcePathElement(String sourcePathElement)
  {
   if (mSourcePathElements == null) {
      mSourcePathElements = new ArrayList();
    }
    mSourcePathElements.add(new DirectorySourcePathElement(sourcePathElement, mFilter));
  }
  
  public List getSourcePathElements()
  {
    return Collections.unmodifiableList(mSourcePathElements);
  }
  
  /**
   * @return a string representation of this source path.
   */
  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    for (Iterator i = mSourcePathElements.iterator(); i.hasNext();) {
      buffer.append(((SourcePathElement)i.next()).getName());
      if (i.hasNext()) {
        buffer.append(File.pathSeparatorChar);
      }
    }
    return buffer.toString();
  }
}
