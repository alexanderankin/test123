/*
 * GenericTagsParser.java
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

public abstract class GenericTagsParser implements TagsParser {
  
  /***************************************************************************/
  protected String tag_;
  protected Vector tagLines_;
  
  /***************************************************************************/
  public GenericTagsParser() {
    reinitialize();
  }

  /***************************************************************************/
  public void reinitialize() {
    tag_ = null;
    if (tagLines_ != null)
      tagLines_.removeAllElements();
  }
  
  /***************************************************************************/
  public boolean findTagLines(String tagFileName, String tagToLookFor) {
    return findTagLines(tagFileName, tagToLookFor, null); 
  }
  
  /***************************************************************************/
  public boolean findTagLines(String tagFileName, String tagToLookFor, 
                            View view) {
    
    tag_ = tagToLookFor;
    
    String line = null;
    
    File file = new File(tagFileName);
    if (!file.exists()) {
      return false;
    }
    if (!file.canRead()) {
      Tags.displayMessage(view, "Can't read " + tagFileName);
      return false;
    }
    
    //displayMessage(view, tagFileName);
    RandomAccessFile raf = null;
    try {
      raf = new RandomAccessFile(file, "r");
    } catch (Exception e) {
      Tags.displayMessage(view, 
                    "File says file exists, but RandomAccessFile says otherwise");
    }

    if (view != null)
     view.getStatus().setMessage("Searching " + tagFileName);
    
    // Binary search for tag
    boolean found = false;
    long start = 0;
    long end = file.length();
    long mid = end / 2;
    long lastPos = 0;
    long forwardPos = 0;
    int compare = 0;
    while (!found && mid != start && mid != end) {
      try {
        raf.seek(mid);
      } catch (IOException ioe) {
        Tags.displayMessage(view, "Can't seek!");
      }
      lastPos = skipBackwardToBeginningOfLine(raf, view);
      try {
        line = raf.readLine();
        forwardPos = raf.getFilePointer();
      } catch (IOException ioe) {
        Tags.displayMessage(view, "IOException caught");
      }
      if (line != null) {
        //displayMessage(view, line);
        found = foundTagMatch(line, tagToLookFor);
        if (found) {
          if (tagLines_ == null)
            tagLines_ = new Vector(5);
          tagLines_.addElement(line);
        }
        if (!found) {
          compare = tagToLookFor.compareTo(line);
          if (compare < 0)
            end = mid;
            else if (compare > 0)
              start = mid;
              mid = ((end - start) / 2) + start;
        }
      }
      else
         break;
    }
    
    // Linear search backward since it is possible we could have landed in 
    // middle of a group of matching tags.
    if (found) {
      long backupPos = lastPos - 2;
      long currentPos = 0;
      boolean differentTag = false;
      while (backupPos >= 0 && !differentTag && currentPos != forwardPos) {
        try { 
          raf.seek(backupPos);
          
          lastPos = skipBackwardToBeginningOfLine(raf, view);
          line = raf.readLine(); 
          differentTag = !foundTagMatch(line, tagToLookFor); 
          if (!differentTag) {
            backupPos = lastPos - 2;
            tagLines_.insertElementAt(line, 0);
          }
          currentPos = raf.getFilePointer();
        }
        catch (IOException ioe) { 
          Tags.displayMessage(view, "Problem backing up"); 
        }
      }
    }
    
    // Linear search forward
    boolean foundForward = found;
    try { raf.seek(forwardPos); } // skip forward to the end of binary search
    catch  (IOException ioe) {    // position.
      Tags.displayMessage(view, "Couldn't return foward"); 
    }
    while (foundForward) {
      try {
        line = raf.readLine();
      } catch (IOException ioe) {
        Tags.displayMessage(view, "IOException caught");
      }
      foundForward = foundTagMatch(line, tagToLookFor);
      if (foundForward)
        tagLines_.addElement(line);
    }
    
    try {
     raf.close();
    } catch (IOException ioe) {
     Tags.displayMessage(view, "Can't close tag file!");
    }
    raf = null;
    
    if (!found)
      line = null;
    
    if (view != null)
     view.getStatus().setMessage("");
    
    line = null;
    file = null;

    return (tagLines_ != null && tagLines_.size() > 0);
  }

  /***************************************************************************/
  public Vector getTagLines() {
    return tagLines_;
  }

  /***************************************************************************/
  public int getNumberOfFoundTags() {
    if (tagLines_ == null)
      return 0;
      
    return tagLines_.size();
  }
  
  /***************************************************************************/
  public String getDefinitionFileName(int index) { return null; }
  public String getDefinitionSearchString(int index){ return null; }
  public int getDefinitionLineNumber(int index) { return -1; }
  
  /***************************************************************************/
  public String getCollisionChooseString(int index) {
    if (!checkIndex(index))
      return null;

    StringBuffer b = new StringBuffer();
    if ((index + 1) < 10)
      b.append(" " + (index + 1));
    else
      b.append((index + 1));
      
    b.append(": " + tag_ + " (" + getDefinitionFileName(index) + 
             ")");
    
    return b.toString();
  }
  
  /***************************************************************************/
  public String getTagLine(int index) {
    if (!checkIndex(index))
      return null;
      
    return (String) tagLines_.elementAt(index);
  }

  /***************************************************************************/
  public String getTag() { return tag_; }

  
  /*+*************************************************************************/
  protected long skipBackwardToBeginningOfLine(RandomAccessFile raf,
                                               View view) {
    int c = 'a';
    long offset = 0;

    while (c != '\n' && offset != 1) {
      try {
        c = raf.read();

        offset = raf.getFilePointer();
        if (offset != 1 && c != '\n')
          raf.seek(offset - 2);
        else if (offset == 1) {
          offset = 0;
          raf.seek(0);
        }

      } catch (IOException ioe) {
        Tags.displayMessage(view, "IOException caught");
      }
    }
    return offset;
  }
  
  /***************************************************************************/
  protected boolean foundTagMatch(String lineFromFile, String tagToLookFor) {
    boolean matches = lineFromFile.startsWith(tagToLookFor);;
    if (matches) { // only "starts with", need to compare tokens
      StringTokenizer st = new StringTokenizer(lineFromFile);
      if (st.hasMoreTokens()) {
        String tagFromLine = st.nextToken();
        matches = tagToLookFor.equals(tagFromLine);
        tagFromLine = null;
      }
      
      st = null;
    }
    
    return matches;
  }
  
  /*+*************************************************************************/
  protected String massageSearchString(String string) {
   
    StringBuffer buf = new StringBuffer(string.length() * 2);
    buf.append(string.substring(2,string.length() - 2)); // get rid of / and / 

    int length = buf.length();
    char c;
    for (int i = 0; i < length; i++) {
      c = buf.charAt(i);
      if (c == '*' || c == '(' || c == ')' || c == '[' || c == ']') {
        buf.insert(i, '\\');
        i++;
        length++;
      }
    }
   
    return buf.toString();
  }
  
  /***************************************************************************/
  public String toString() { return "Generic Tag Parser"; }
  
  /***************************************************************************/
  protected boolean checkIndex(int index) {
    if (tagLines_ == null)
      return false;
      
    return (index < tagLines_.size() && index >= 0); 
  }
    
  /***************************************************************************/
  static public void main(String args[]) {

  }
}
