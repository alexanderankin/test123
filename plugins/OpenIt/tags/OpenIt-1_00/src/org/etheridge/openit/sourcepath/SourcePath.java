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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Represents a source path, which is made up of SourcePathFiles (ie. java source
 * files).
 */
public class SourcePath
{
  // list of source path elements in this source path
  private List mSourcePathElements;
  
  public SourcePath(String sourcePath)
  {
    // tokenize on the path separator
    String pathSeparator = System.getProperty("path.separator");
    StringTokenizer tokenizer = new StringTokenizer(sourcePath, pathSeparator);
    mSourcePathElements = new ArrayList();
    while (tokenizer.hasMoreTokens()) {
      mSourcePathElements.add(new DirectorySourcePathElement(tokenizer.nextToken()));
    }
  }
  
  public List getSourcePathElements()
  {
    return Collections.unmodifiableList(mSourcePathElements);
  }
  
    
}
