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
 */

package tags;
 
import java.io.*;
import java.lang.System.*;
import java.util.*;

public class TagFile 
{
	/****************************************************************************/
  public final static String DEFAULT_CATAGORY = "Default";

	/****************************************************************************/
  protected String path_;
  protected String catagory_;
  
  protected boolean currentDirIndexFile_ = false;

  protected boolean enabled_; // wether you should use this tag file or not in 
                              // searches

	/****************************************************************************/
  public TagFile(String path, String catagory) 
  {
    this(path, catagory, true);
  }
  
  /***************************************************************************/
  public TagFile(String path, String catagory, boolean enabled)
  {
    init(path, catagory, true);
  }

  /***************************************************************************/
  /* Property string format:  path, catagory, true/false 
   *    or
   * just the path to tag index file
   */
  public TagFile(String propertyStringOrPath)
  {
    StringTokenizer st = new StringTokenizer(propertyStringOrPath);
    
    String path = null;
    String catagory = DEFAULT_CATAGORY;
    boolean enabled = true;
    
    if (st.hasMoreElements())
      path = st.nextToken();
    
    if (st.hasMoreElements())
      catagory = st.nextToken();
      
    if (st.hasMoreElements())
      enabled = st.nextToken().equals("true");
      
    init(path, catagory, enabled);
    
    st = null;
  }
  
  /***************************************************************************/
  protected void init(String path, String catagory, boolean enabled)
  {
    path_ = path;
    if (catagory == null)
      catagory_ = DEFAULT_CATAGORY;
    else
      catagory_ = catagory;
    enabled_ = enabled;
  }

	/****************************************************************************/
  public String toString() { return getPath(); }

	/****************************************************************************/
  public String toDebugString() 
  {
    String check = null;
    
    if (enabled_) 
      check = "x";
    else
      check = " ";
    
    return "[" + catagory_ + "] [" + check + "] " + getPath();
  }
	
  /***************************************************************************/
  public String getPropertyString()
  {
    return path_ + " " + catagory_ + " " + enabled_;
  }
  
  /***************************************************************************/
  public String getPath() { return path_; }
 
  /***************************************************************************/
  public void setPath(String path) { path_ = new String(path); }
  
  /***************************************************************************/
  public String getCatagory() { return catagory_; }
  
  /***************************************************************************/
  public boolean isEnabled() { return enabled_; }
  
  /***************************************************************************/
  public void setEnabled(boolean enabled) { enabled_ = enabled; }
}
