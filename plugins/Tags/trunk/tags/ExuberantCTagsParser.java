/*
 * ExuberantCTagsParser.java
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
 Exuberant C Tags are of the form:
 
   --format=2 (default)
     tag_name<TAB>file_name<TAB>ex_cmd;"<TAB>extension_fields
   --format=1 
     tag_name<TAB>file_name<TAB>ex_cmd
     
*/

public class ExuberantCTagsParser extends GNUCTagsParser {
  
  /***************************************************************************/
  public ExuberantCTagsParser() { super(); }
  
  /***************************************************************************/
  public String getDefinitionFileName() {
    String tagDefinitionFileName = super.getDefinitionFileName();
    
    /* Exuberant C Tags comes with Cygwin.  However the path names are in a 
     * form that Cygwin emulates Unix with.  The path /cygdrive/c/* isn't 
     * actually a path but a mapped path to c:/*.  Since this will screw up
     * JEdit, we will test for this type of path and convert to one that 
     * JEdit knows...
     */
    if (tagDefinitionFileName.startsWith("/cygdrive/")) {
      char driveLetter = tagDefinitionFileName.charAt(10);
      tagDefinitionFileName = driveLetter + ":" +
                               tagDefinitionFileName.substring(11);
    }
    
    return tagDefinitionFileName; 
  }

  /***************************************************************************/
  public String getDefinitionSearchString(int index) {
    /* This func should probably be broken up into two parts in GNUCTagsParser
     * so that we can call the first, string token with "" or ";\"", then
     * call the second.  But, I'm currently being lazy...
     */    
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
    if (tagLine.lastIndexOf(";\"") == -1)  // --format=2 (default)
      tagDefinitionSearchString = st.nextToken("");
    else                                    // --format=1
      tagDefinitionSearchString = st.nextToken(";\"");


    // Check to see if the search string is a number.  Number search
    // strings are actually line numbers of #define tags
    boolean isNumber = true;
    int lineNumber = 0;
    tagLineNumber_ = -1;
    tagDefinitionSearchString = tagDefinitionSearchString.trim();
    try {
      lineNumber = Integer.parseInt(tagDefinitionSearchString);
      tagLineNumber_ = lineNumber;  // parse OK, remember number
      tagDefinitionSearchString = null; // forget that it is a search string
    } catch (NumberFormatException nfe) {
      isNumber = false;
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
  public String toString() { return "Exuberant C Tags"; }
}
