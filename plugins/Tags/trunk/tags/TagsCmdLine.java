/*
 * TagsCmdLine.java
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

import gnu.regexp.*;

public class TagsCmdLine {
 
 /****************************************************************************/
 static protected int getLineNumber(String fileName, String searchString) {
   
   BufferedReader bufferedReader = null;
   try { 
     bufferedReader = new BufferedReader(new FileReader(fileName));
   }
   catch (FileNotFoundException fnf) {
     bufferedReader = null;
   }
   
   if (bufferedReader == null)
     return -1;
   
   int lineNumber = 1;

   RE searchRE = null;
   try {
     searchRE = new RE(searchString, 0, RESyntax.RE_SYNTAX_EGREP);
   } catch (NullPointerException npe) {
     System.out.println("Pattern was null");
   } catch (REException ree) {
     System.out.println("Input pattern couldn't be parsed.");
   }
   
   try {
     String line;
     while ((line = bufferedReader.readLine()) != null) {
       if (searchRE.getMatch(line) != null)
         return lineNumber;
       lineNumber++;
     }
     line = null;
   } 
   catch (IOException ioe) {
     lineNumber = -1;
   }           

   searchRE = null;
   bufferedReader = null;
   
   return -1;                
 }
 
 /****************************************************************************/
 static public void main(String args[]) {
   if (args.length == 0 || args.length != 2) {
     System.out.println("Usage:  PTCSRC func_name");
     System.out.println("      Returns 1 if found");
     System.out.println("      Returns 0 if not found");
     System.out.println("      Returns 2 if error");
     System.exit(2);
   }
   
   // Command line arg checking
   File dir = new File(args[0]);
   if (!dir.exists()) {
     System.out.println(args[0] + " does not exist.");
     System.exit(2);
   }
   
   if (!dir.isDirectory()) {
     System.out.println(args[0] + " is not a directory.");
     System.exit(2);
   }
   
   if (!dir.canRead()) {
     System.out.println("You do not have permission to read " + args[0]);
     System.exit(2);
   }
   dir = null;
   
   // Setup PTC tags files
   String PTCSRC = args[0];
   Tags.appendTagFile(PTCSRC + "/softdb/tags.1", "PTC");
   Tags.appendTagFile(PTCSRC + "/softdb/tags.2", "PTC");
   Tags.appendTagFile(PTCSRC + "/softdb/tags.3", "PTC");
   Tags.appendTagFile(PTCSRC + "/softdb/tags.4", "PTC");
   Tags.appendTagFile(PTCSRC + "/softdb/tags.5", "PTC");
   Tags.appendTagFile(PTCSRC + "/softdb/tags.o.1", "PTC");
   Tags.appendTagFile(PTCSRC + "/softdb/tags.o.2", "PTC");
   Tags.appendTagFile(PTCSRC + "/softdb/tags.o.3", "PTC");
   Tags.appendTagFile(PTCSRC + "/softdb/tags.o.4", "PTC");
   Tags.appendTagFile(PTCSRC + "/softdb/tags.o.5", "PTC");
   Tags.appendTagFile(PTCSRC + "/softdb/tags.prodev", "PTC");

   PTCSRC = null;
   
   int returnVal = 0;
   
   // Get function name
   String funcName = args[1];
   
   Tags.followTag(null, null, null, false, funcName);
   String tagFileName = Tags.getTagFileName();
   String searchString = Tags.getSearchString();
   int lineNumber = -1;
   if (searchString == null)
     lineNumber = Tags.getLineNumber();
     
   if (tagFileName != null) {
     //System.out.print("File:  " + tagFileName);
     if (searchString != null) {
       //System.out.println("Search string:  " + searchString);
       lineNumber = getLineNumber(tagFileName, searchString);
       //System.out.println("Line number:  " + lineNumber);
     }
     else {
       //System.out.println("Line number:  " + lineNumber);
     }
     
     System.out.println(tagFileName + " +line:" + lineNumber);
     returnVal = 1;
   }
   else {
     //System.out.println("\"" + funcName + "\" not found");
     returnVal = 0;
   }
   
   funcName = null;
   tagFileName = null;
   searchString = null;
   
   System.exit(returnVal);
 }
}

