/*
 * OpenIt jEdit Plugin (QuickAccessSourcePath.java) 
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

package org.etheridge.openit.sourcepath;

import gnu.regexp.RE;
import gnu.regexp.REException;
import gnu.regexp.RESyntax;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.etheridge.openit.OpenItProperties;
import org.etheridge.openit.sourcepath.SourcePath;
import org.etheridge.openit.sourcepath.SourcePathElement;
import org.etheridge.openit.sourcepath.SourcePathFile;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

/**
 * This class is a wrapper around a SourcePath that provides quicker access to 
 * the elements in a source path.
 *
 * On construction it will take all of the elements in the source path and store
 * them in data structures that provide quick access to the underlying source
 * path files.
 *
 * NOTE: this wrapper is provided to provide for better GUI response time.
 */
public class QuickAccessSourcePath
{
  // the wrapped source path
  private SourcePath mWrappedSourcePath;
  
  // quick access data map - this is where the "quick" access is implemented.
  // maps between each letter of the alphabet (a-z) and a list of source path
  // files that begin with that letter.
  //
  // Theoretically this should increase data access 26 times faster, however 
  // letter distribution is not quite so simple ;)
  //
  // NOTE: if there are NO classes starting with a particular letter, there will
  // be NO entry in the map.  
  //
  // For example:
  //
  // a -> [Alpha.java, Animal.java, Allo.java]
  // b -> [Bob.java, Builder.java]
  // ...
  // z -> [Zebra.java]
  
  private Map mQuickAccessMap;
  
  /**
   * Substring index size - this integer determines how many characters should
   * be used as a substring index into the quick access map.  
   * 
   * For example, if the index size is 2, and the file to be indexed is 
   * "this.java", the following indexes will be added to the quick access map
   * if they do not already exist (and "this.java" will be mapped):
   *
   * th - this.java
   * hi - this.java
   * is - this.java
   * s. - this.java
   * .j - this.java
   * ja - this.java
   * av - this.java
   * va - this.java
   *
   * The higher the substring index size the more potential indexes there will
   * be, meaning it will take longer to index, but will be quicker doing lookups.
   *
   * This value *could* be user-configurable, to allow the user to control this
   * ratio.
   */
  private static final int msIndexSize = 2;
  
  /**
   * Constructs a QuickAccessSourcePath
   *
   * NOTE: this is a *BUSY* constructor - it will potentially take a while!
   */
  public QuickAccessSourcePath(SourcePath sourcePath)
  {
    mWrappedSourcePath = sourcePath;
    
    initialize();
  }
  
  /**
   * Gets a list of all SourceFiles starting with the specified character.
   */
  public List getSourceFilesStartingWith(char ch)
  {
    List sourceFileList = (List) mQuickAccessMap.get(String.valueOf(ch).toLowerCase());
    
    if(sourceFileList == null) {
      return new ArrayList();
    }
    
    return Collections.unmodifiableList(sourceFileList);
  }
  
  /**
   * Gets a list of all SourceFiles containing the specified substring.
   */
  public List getSourceFilesContaining(String string)
  {
    if (string.length() > msIndexSize) {
      string = string.substring(0,msIndexSize);
    }
 
    List sourceFileList = (List) mQuickAccessMap.get(string.toLowerCase());
    
    if(sourceFileList == null) {
      return new ArrayList();
    }
    
    return Collections.unmodifiableList(sourceFileList);
  }
  
  //
  // private helper methods
  //
  
  private void initialize()
  {
    RE regularExpression = null;
    try {
      String regularExpressionProperty = 
        jEdit.getProperty(OpenItProperties.EXCLUDES_REGULAR_EXPRESSION);
      if (regularExpressionProperty != null) {
        regularExpression = new RE(org.gjt.sp.jedit.MiscUtilities.globToRE(
          regularExpressionProperty), RE.REG_MULTILINE, RESyntax.RE_SYNTAX_POSIX_EXTENDED);
      }
    } catch (REException e) {
      Log.log(Log.MESSAGE, QuickAccessSourcePath.class, 
        "[OpenIt Plugin]: Invalid excludes regular expression: " + 
          jEdit.getProperty(OpenItProperties.EXCLUDES_REGULAR_EXPRESSION));
    }
    
    // initialize the quick access map
    mQuickAccessMap = new HashMap();
    
    // for each element in the source path, go through its list of classes
    // and 
    for (Iterator i = mWrappedSourcePath.getSourcePathElements().iterator(); i.hasNext();) {
      SourcePathElement sourcePathElement = (SourcePathElement) i.next();
      
      // iterate through files and store in quick access map
      for (Iterator j = sourcePathElement.getSourcePathFiles().iterator(); j.hasNext();) {
        SourcePathFile sourcePathFile = (SourcePathFile) j.next();
        
        // if the filename does not match the excludes regular expression then
        // add it to the quick access map, otherwise ignore it.
        if (regularExpression == null || !regularExpression.isMatch(sourcePathFile.getFullName())) {
          // get first letter
          String firstLetter = sourcePathFile.getFullName().toLowerCase().substring(0,1);
          List currentLetterList = (List) mQuickAccessMap.get(firstLetter);
          if (currentLetterList == null) {
            currentLetterList = new ArrayList();
            mQuickAccessMap.put(firstLetter, currentLetterList);
          }
          if (!currentLetterList.contains(sourcePathFile)) {
            currentLetterList.add(sourcePathFile);
          }
          
          // index substrings if user wants them indexed
          if (jEdit.getBooleanProperty(OpenItProperties.ALLOW_SUBSTRING_MATCHING, true)) {
            String fileName = sourcePathFile.getFullName().toLowerCase();
            for (int x = 0; x < fileName.length(); x++) {
              if (x <= fileName.length() - msIndexSize) {
                String currentSubstring = fileName.substring(x, x+msIndexSize);
                
                List substringList = (List) mQuickAccessMap.get(currentSubstring);
                if (substringList == null) {
                  substringList = new ArrayList();
                  mQuickAccessMap.put(currentSubstring, substringList);
                }
                if (!substringList.contains(sourcePathFile)) {
                  substringList.add(sourcePathFile);
                }
              }
            }
          }
        }
      }
      
    }
    
    // sort each list in the quick access map
    for (Iterator i = mQuickAccessMap.values().iterator(); i.hasNext();) {
      List currentList = (List) i.next();
      Collections.sort(currentList);
    }
  }
  
}
