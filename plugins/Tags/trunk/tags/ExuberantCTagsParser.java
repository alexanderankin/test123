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
import org.gjt.sp.util.Log;

/*
 Exuberant C Tags are of the form:
 
   --format=2 (default)
     tag_name<TAB>file_path_name<TAB>ex_cmd;"<TAB>extension_fields
   --format=1 
     tag_name<TAB>file_path_name<TAB>ex_cmd
     
     ex_cmd can be of the form:
       /^  search string $/
     or
       line_number
     
*/

class ExuberantCTagsParser extends GenericTagsParser {

  /***************************************************************************/
  public ExuberantCTagsParser() { super(); }
  
  /***************************************************************************/
  public TagLine createTagLine(final String tagLine, final String tagIndexFile)
  {
    if (tagLine == null)
      return null;

    /*** Get the definition file name ***/
    String tagDefinitionFileName = null;      
      
    StringTokenizer st = new StringTokenizer(tagLine);
    if (st.hasMoreTokens())  // skip tag from tag line
      st.nextToken();
    if (st.hasMoreTokens()) {
      tagDefinitionFileName = st.nextToken();  // get file name
    }

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

    /* resolve relative path names here */
    File tagFile = new File(tagDefinitionFileName);
    if (!tagFile.isAbsolute())
    {
      File tagIndexFilePath = new File(tagIndexFile);
      tagDefinitionFileName = tagIndexFilePath.getParent() + 
                              System.getProperty("file.separator") +
                              tagDefinitionFileName;
      tagIndexFilePath = null;
    }
    tagFile = null;
    
    /*** Get the search string ***/
    String tagDefinitionSearchString = null;
    
    // get search string
    if (tagLine.lastIndexOf(";\"") == -1)    // --format=1
      tagDefinitionSearchString = st.nextToken("");
    else                                     // --format=2 (default)
    {
      // We use '"' b/c we can't use the multi character delimiter that
      // Exuberant C Tags uses (which is ;").  We will add on the "$/" for 
      // the string massager.
      tagDefinitionSearchString = st.nextToken("\"");
      
      // Loop off ; of the format 2 delim of ;"
      tagDefinitionSearchString = tagDefinitionSearchString.substring(0,
                           tagDefinitionSearchString.length() - 1);
      Log.log(Log.DEBUG, this, "Search string from tokenizer:  " + 
              tagDefinitionSearchString);
    }
    // Check to see if the search string is a number.  Number search
    // strings are actually line numbers of #define tags
    boolean isNumber = true;
    int lineNumber = 0;
    tagDefinitionSearchString = tagDefinitionSearchString.trim();
    try {
      lineNumber = Integer.parseInt(tagDefinitionSearchString);
      tagDefinitionSearchString = null; // forget that it is a search string
    } catch (NumberFormatException nfe) {
      isNumber = false;
    }

    String origTagDefinitionSearchString = null;
    if (!isNumber && tagDefinitionSearchString != null) 
    {
      origTagDefinitionSearchString = 
                       tagDefinitionSearchString.substring(2,
                                       tagDefinitionSearchString.length() - 2);
      tagDefinitionSearchString = 
                              massageSearchString(tagDefinitionSearchString);
    }

    /*** Parse exuberant item info ***/
    if (st.hasMoreTokens())
      st.nextToken(" \t\n\r\f");  // get rid of ;"
    while (false && st.hasMoreTokens())
    {
      Macros.message(null, st.nextToken());
    }
    
    TagLine tl = new TagLine(tag_, tagDefinitionFileName,
                             origTagDefinitionSearchString, 
                             tagDefinitionSearchString, lineNumber,
                             tagIndexFile);

    return tl;
  }
  
  /***************************************************************************/
  public String toString() { return "Exuberant C Tags"; }
}
