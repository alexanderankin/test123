/*
 * TagFile.java
 * Copyright (c) 2001, 2002 Kenrick Drew
 * kdrew@earthlink.net
 *
 * This file is part of the TagsPlugin
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
 *
 * $Id$
 */

package tags;

//{{{ imports
import java.io.*;
import java.lang.System.*;
import java.util.*;
//}}}

public class TagFile
{

  //{{{ private declarations
  /** tag index file path */
  protected String path;

  /** does the tag index file represent the tag file in the current buffer's directory */
  protected boolean currentDirIndexFile = false;

  /** whether to search tag index file */
  protected boolean enabled;
  //}}}

  //{{{ TagFile(path,enabled) constructor
  public TagFile(String path, boolean enabled)
  {
    if(path.equalsIgnoreCase(Tags.getCurrentBufferTagFilename()))
    {
      this.currentDirIndexFile = true;
    }
    this.path = path;
    this.enabled = enabled;
  } //}}}

  //{{{ TagFile(propvalue) constructor
  /**
  * Property string format:  path, true/false
  *   or
  * path
  */
  public TagFile(String propertyStringOrPath)
  {
    StringTokenizer st = new StringTokenizer(propertyStringOrPath);

    String path = null;
    boolean enabled = true;

    if (st.hasMoreElements())
      path = st.nextToken();

    if (st.hasMoreElements())
      enabled = st.nextToken().equals("true");

    // XXX consolidate to one constructor
    this.path = path;
    this.enabled = enabled;

    st = null;
  } //}}}

  //{{{ toDebugString() method
  public String toDebugString()
  {
    String check = null;

    if (enabled)
      check = "x";
    else
      check = " ";

    return "[" + check + "] " + getPath();
  }//}}}

  //{{{ toString() method
  public String toString()
  {
    return "TagFile path=" + getPath() + ", enabled=" + isEnabled()
      + ", currentDirIndexFile=" + currentDirIndexFile;
  } //}}}

  //{{{ getPath() method
  public String getPath()
  {
    return path;
  } //}}}

  //{{{ setPath() method
  public void setPath(String path)
  {
    this.path = new String(path);
  } //}}}

  //{{{ isEnabled() method
  public boolean isEnabled()
  {
    return enabled;
  } //}}}

  //{{{ setEnabled() method
  public void setEnabled(boolean enabled)
  {
    this.enabled = enabled;
  } //}}}

  //{{{ isCurrentDirIndexFile()
  public boolean isCurrentDirIndexFile()
  {
    return currentDirIndexFile;
  } //}}}

}

// :collapseFolds=0:noTabs=true:lineSeparator=\r\n:tabSize=2:indentSize=2:deepIndent=false:folding=explicit:
