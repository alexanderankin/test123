/*
 * ExuberantInfoItem.java
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

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;

class ExuberantInfoItem 
{
  /***************************************************************************/
  String origToken_;
  String formattedToken_;
  
  /***************************************************************************/
  public ExuberantInfoItem(String token) 
  { 
    origToken_ = new String(token);
    
    int colonIndex = token.indexOf(':');
    if (colonIndex != -1)
      formattedToken_ = token.substring(0,colonIndex) + ": " + 
                       token.substring(colonIndex + 1);
    else
      formattedToken_ = new String(token);
  }

  /***************************************************************************/
  public String toString() { return origToken_; }
  
  /***************************************************************************/
  public String toHTMLString() { return formattedToken_; }
}
