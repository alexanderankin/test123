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
import org.gjt.sp.util.Log;

abstract class GenericTagsParser implements TagsParser {
  
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
  public boolean findTagLines(String tagFileName, String tagToLookFor, 
                              View view) {
    
    tag_ = tagToLookFor;
    
    String line = null;
    
    File file = new File(tagFileName);
    if (!file.exists()) {
      Log.log(Log.WARNING, this, "Tag file " + tagFileName + 
              " does not exist.");
      return false;
    }
    if (!file.canRead()) {
      Log.log(Log.ERROR, this, "Can't read " + tagFileName);
      return false;
    }
    
    RandomAccessFile raf = null;
    try {
      raf = new RandomAccessFile(file, "r");
    } catch (Exception e) {
      Log.log(Log.ERROR, this, 
          e + ":  File says file exists, but RandomAccessFile says otherwise");
      return false;
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
        Log.log(Log.ERROR, this, ioe + ":  Can't seek in " + tagFileName);
        return false;
      }
      lastPos = skipBackwardToBeginningOfLine(raf, view);
      try {
        line = raf.readLine();
        forwardPos = raf.getFilePointer();
      } catch (IOException ioe) {
        Log.log(Log.ERROR, this, ioe + ":  Can't read line from " + 
                tagFileName);
        return false;
      }
      if (line != null) {
        found = foundTagMatch(line, tagToLookFor);
        if (found) {
          if (tagLines_ == null)
            tagLines_ = new Vector(5);

          tagLines_.addElement(createTagLine(line, tagFileName));
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
          if (line != null)
          {
            differentTag = !foundTagMatch(line, tagToLookFor); 
            if (!differentTag) {
              backupPos = lastPos - 2;
              tagLines_.insertElementAt(createTagLine(line, tagFileName), 0);
            }
            currentPos = raf.getFilePointer();
          }
          else
            break;
        }
        catch (IOException ioe) { 
          Log.log(Log.ERROR, this, "Problem backing up"); 
        }
      }
    }
    
    // Linear search forward
    boolean foundForward = found;
    try { raf.seek(forwardPos); } // skip forward to the end of binary search
    catch  (IOException ioe) {    // position.
      Log.log(Log.ERROR, this, "Couldn't return forward");
    }
    while (foundForward) {
      try {
        line = raf.readLine();
      } catch (IOException ioe) {
        Log.log(Log.ERROR, this, ioe + ":  Can't read line from " + 
                tagFileName);
      }
      if (line != null)
      {
        foundForward = foundTagMatch(line, tagToLookFor);
        if (foundForward)
          tagLines_.addElement(createTagLine(line, tagFileName));
      }
      else 
        break;
    }
    
    try { raf.close(); } 
    catch (IOException ioe) { 
      Log.log(Log.ERROR, this, ioe + ":  Can't close " + tagFileName);
    }
    raf = null;
    
    if (!found)
      line = null;
    
    if (view != null)
     view.getStatus().setMessage("");
    
    line = null;
    file = null;

    updateTagLines();
    
    return (tagLines_ != null && tagLines_.size() > 0);
  }

  /***************************************************************************/
  protected void updateTagLines() 
  {
    if (tagLines_ == null)
      return;
      
    int size = tagLines_.size();
    TagLine tagLine = null;
    for (int i = 0; i < size; i++)
    {
      tagLine = (TagLine) tagLines_.elementAt(i);
      tagLine.index_ = i + 1;
    }
  }
  
  /***************************************************************************/
  public int getNumberOfFoundTags() {
    if (tagLines_ == null)
      return 0;
      
    return tagLines_.size();
  }
  
  /***************************************************************************/
  public Vector getTagLines() { return tagLines_; }

  /***************************************************************************/
  public ChooseTagList getCollisionListComponent(View view)
  {
    ChooseTagList ctl = new ChooseTagList(view, this);
    return ctl;
  }
  
  /***************************************************************************/
  public TagLine getTagLine(int index) {
    if (!checkIndex(index))
      return null;
      
    return (TagLine) tagLines_.elementAt(index);
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
          break;
        }

      } catch (IOException ioe) {
        Log.log(Log.ERROR, this, 
                "Problem skipping backward to beginning of tag line");
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
    Log.log(Log.DEBUG, this, "Search string:  " + string);   
    
    StringBuffer buf = new StringBuffer(string.length() * 2);
    buf.append(string.substring(1,string.length() - 1)); // get rid of / and / 
    
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
    
    Log.log(Log.DEBUG, this, "Massaged search string:  " + buf.toString());   
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
}
