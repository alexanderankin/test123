/*
 * TagFile.java
 * Copyright (c) 2001 Kenrick Drew
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

public class TagFile {
  
  public final static String DEFAULT_CATAGORY = "Default";
  
  protected String path_;
  protected String catagory_;
  
  public TagFile(String path, String catagory) {
    path_ = path;
    catagory_ = catagory;
  }
  
  public String toString() { return getPath(); }
  
  public String getPath() { return path_; }
  public String getCatagory() { return catagory_; }
}
