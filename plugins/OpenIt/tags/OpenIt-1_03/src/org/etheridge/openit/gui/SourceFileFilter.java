/*
 * OpenIt jEdit Plugin (SourceFileFilter.java) 
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

import gnu.regexp.RE;
import gnu.regexp.REException;
import gnu.regexp.RESyntax;

import java.util.Iterator;
import java.util.List;

import org.etheridge.openit.sourcepath.SourcePathFile;

/**
 * Filters source files according to a regular expression
 */
public class SourceFileFilter
{
  private RE mRegularExpression; 

  public void setRegularExpressionString(String regularExpression) throws REException
  {
    if (regularExpression == null) {
      mRegularExpression = null;
      return;
    }
    
    mRegularExpression = new RE(org.gjt.sp.jedit.MiscUtilities.globToRE(regularExpression), 
      RE.REG_MULTILINE, RESyntax.RE_SYNTAX_POSIX_EXTENDED);
  }
  
  public void clearRegularExpression()
  {
    mRegularExpression = null;
  }
  
  /** 
   * Removes any files in the list that do not match the current regular
   * expression
   */
  public void filter(List sourcePathFiles)
  {
    // if the regular expression has not been set, then do not filter
    if (mRegularExpression == null) {
      return;
    }
    
    // if the regular expression is valid, then remove any files that do not
    // match the regular expression.
    for (Iterator i = sourcePathFiles.iterator(); i.hasNext();) {
      SourcePathFile currentSourcePathFile = (SourcePathFile) i.next();
      String fileName = currentSourcePathFile.getFullName();
      if (!mRegularExpression.isMatch(fileName)) {
        i.remove();
      }
    }
  }
  
}
