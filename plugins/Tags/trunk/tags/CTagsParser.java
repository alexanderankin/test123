/*
 * CTagsParser.java
 * Copyright (c) 2001 Kenrick Drew
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

/*
 C Tags are of the form:
 
   tagName /path/to/defintion/of/tagName/file.extension /^  search string $/
   
 or

   tagName /path/to/defintion/of/tagName/file.extension line_number
 
*/

public class CTagsParser extends GenericTagsParser {
  
  /***************************************************************************/
  protected int tagLineNumber_ = -1;
  
  /***************************************************************************/
  public CTagsParser() { super(); }
  
  /***************************************************************************/
  public void reinitialize() {
    super.reinitialize();
    
    tagLineNumber_ = -1;
  }
  
  /***************************************************************************/
  public String getDefinitionFileName(int index) {
    if (!checkIndex(index))
      return null;

    String tagLine = (String) tagLines_.elementAt(index);      
    if (tagLine == null)
      return null;

    String tagDefinitionFileName = null;

    StringTokenizer st = new StringTokenizer(tagLine);
    if (st.hasMoreTokens())  // skip tag from tag line
      st.nextToken();
    if (st.hasMoreTokens()) {
      tagDefinitionFileName = st.nextToken();  // get file name
    }
    st = null;
    
    return tagDefinitionFileName; 
  }

  /***************************************************************************/
  public String getDefinitionSearchString(int index) {
    
    tagLineNumber_ = -1;

    if (!checkIndex(index))
      return null;

    String tagLine = (String) tagLines_.elementAt(index);
    if (tagLine == null)
      return null;
    
    String tagDefinitionSearchString = null;

    StringTokenizer st = new StringTokenizer(tagLine);
    st.nextToken(); // skip tag from tag line
    st.nextToken(); // skip tag definition file name

    // get search string
    tagDefinitionSearchString = st.nextToken("");


    // Check to see if the search string is a number.  Number search
    // strings are actually line numbers of #define tags
    boolean isNumber = true;
    int lineNumber = 0;
    tagDefinitionSearchString = tagDefinitionSearchString.trim();
    try {
      lineNumber = Integer.parseInt(tagDefinitionSearchString);
      tagLineNumber_ = lineNumber;  // parse OK, remember number
      tagDefinitionSearchString = null; // forget that it is a search string
    } catch (NumberFormatException nfe) {
      isNumber = false;
      tagLineNumber_ = -1;
    }
    /* Tags.displayMessage(view, searchString + "  " + isNumber + " " + 
                           lineNumber);*/

    if (!isNumber) {
      tagDefinitionSearchString = 
                              massageSearchString(tagDefinitionSearchString);
    }

    st = null;

    
    return tagDefinitionSearchString; 
  }

  /***************************************************************************/
  public int getDefinitionLineNumber(int index) {
    getDefinitionSearchString(index); // This will parse and get number if there
                                      // is one.
      
    return tagLineNumber_; 
  }
  
  /***************************************************************************/
  public String toString() { return "C Tags"; }
}
