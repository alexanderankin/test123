/*
 * TagLine.java
 * Copyright (c) 2001, 2002 Kenrick Drew
 * kdrew@earthlink.net
 *
 * This file is part of TagsPlugin
 *
 * TagsPlugin is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * TagsPlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package tags;

import java.io.*;
import java.lang.System.*;
import java.util.*;


class TagLine 
{
  
  /***************************************************************************/
  protected int index_ = -1;
  protected String tag_;
  protected String definitionFile_;
  protected String origSearchString_;  // unadultered search string
  protected String searchString_; // fit for searching routine consumption
  protected int    definitionLineNumber_ = -1;
  protected File   tagIndexFile_;
  
  protected Vector exuberantInfoItems_;
  
  /***************************************************************************/
  public TagLine(String tag, String definitionFile, String origSearchString,
                 String searchString, int definitionLineNumber,
                 String tagIndexFile) 
  {
    index_ = -1;
    tag_ = tag;
    definitionFile_ = definitionFile;
    origSearchString_ = origSearchString;
    searchString_ = searchString;
    definitionLineNumber_ = definitionLineNumber;
    tagIndexFile_ = new File(tagIndexFile);
  }
  
  /***************************************************************************/
  public String getDefinitionFileName() { return definitionFile_; }
  
  /***************************************************************************/
  public String getDefinitionSearchString() { return searchString_; }
  
  /***************************************************************************/
  public int getDefinitionLineNumber() { return definitionLineNumber_; }
  
  /***************************************************************************/
  public File getTagIndexFile() { return tagIndexFile_; }
  
  /***************************************************************************/
  public String toString() 
  {
    StringBuffer b = new StringBuffer();
    if ((index_) < 10)
      b.append(" " + (index_));
    else
      b.append((index_));

    b.append(": " + tag_ + " (" + definitionFile_ + ")");
    
    return b.toString();
  }

}
