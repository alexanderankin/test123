/*
 * OpenIt jEdit Plugin (RegularExpressionSourcePathFilter.java) 
 *  
 * Copyright (C) 2004 Matt Etheridge (matt@etheridge.org)
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

package org.etheridge.openit.sourcepath.filter;

import org.etheridge.openit.utility.OpenItRE;

public class RegularExpressionSourcePathFilter implements SourcePathFilter
{
  private OpenItRE mRegularExpression; 
  
  /**
   * Constructor.
   *
   * @param reString the regular expression string used to determine the 
   * source path elements that should be filtered out.
   */
  public RegularExpressionSourcePathFilter(String reString, boolean ignoreCase)
  {
    mRegularExpression = new OpenItRE(reString, !ignoreCase);
  }
    
  //
  // SourcePathFilter implementation
  //
  
  public boolean isSourcePathElementAllowed(String elementString)
  {
    // if the element string matches the regular expression, then return false
    return !mRegularExpression.isMatch(elementString);
  }
}
