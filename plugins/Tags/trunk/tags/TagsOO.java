/*
 * TagsOO.java
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
import java.util.Vector;
import java.awt.*;
import javax.swing.*;

import gnu.regexp.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.textarea.*;

public class TagsOO {

  /***************************************************************************/
  protected static final String RE_STRING_CLASS = 
                                "(^[\\s]*[a-z]*[ ]*class[ ]*[A-Za-z]*)"; // class Foo
  protected static final String RE_STRING_IMPLEMENTS = ""; // implements Bar
  protected static final String RE_STRING_EXTENDS = 
                                "(extends[ ]*[A-Za-z]*)"; // extends Duck
  protected static final String PARTIAL_RE_STRING_VAR =
                                "([A-Za-z][A-Za-z]*[ ]*";
    // Needs + "varName" + ")"...

  protected static RE classRE_;
  protected static RE implementsRE_;
  protected static RE extendsRE_;

  static {
    try {
      classRE_ = new RE(RE_STRING_CLASS, 0, RESyntax.RE_SYNTAX_EGREP);
      implementsRE_ = new RE(RE_STRING_IMPLEMENTS, 0, RESyntax.RE_SYNTAX_EGREP);
      extendsRE_ = new RE(RE_STRING_EXTENDS, 0, RESyntax.RE_SYNTAX_EGREP);
    } catch (NullPointerException npe) {
      System.out.println("Pattern was null");
    } catch (REException ree) {
      System.out.println("Input pattern couldn't be parsed.");
    }
  }
  
  static protected String varName_;
  static protected String methodOrFieldName_;
  static protected String className_;

  static protected View view_;
  static protected JEditTextArea textArea_;  
  
  /***************************************************************************/
  public static void followTag(View v, JEditTextArea t, Buffer b, 
                               boolean newView) {
    view_ = v;
    textArea_ = t;

    if (!analizeTextAroundCursor()) {
      Toolkit.getDefaultToolkit().beep();
      return;
    }
    
    Vector matchStrings = getREStringMatches(varName_);
  
    Vector tagNames = buildTagNames(matchStrings, methodOrFieldName_);

    int caretLine = textArea_.getCaretLine();
    int lineIdx = textArea_.getCaretPosition() - // offsets from beg of file
                  textArea_.getLineStartOffset(caretLine);
    
    Point location = new Point(textArea_.offsetToX(caretLine, lineIdx),
                           textArea_.getPainter().getFontMetrics().getHeight() *
                           (textArea_.getBuffer().physicalToVirtual(caretLine) - 
                           textArea_.getFirstLine() + 1));
    SwingUtilities.convertPointToScreen(location, textArea_.getPainter());
    //Macros.message(view_, location.toString());
    
    if (true) {
      new ChooseTagList(view_, textArea_, b, newView, tagNames, location);
    }
    else {
      StringBuffer message = new StringBuffer();
      int numMatches = tagNames.size();
      for (int i = 0; i < numMatches; i++) {
        if (tagNames.elementAt(i) != null)
          message.append(((String) tagNames.elementAt(i)) + "\n"); 
      }
      Macros.message(view_, message.toString());
    }
  }

  /***************************************************************************/
  private static boolean analizeTextAroundCursor() {

    int caretLine = textArea_.getCaretLine();  // get line num of caret
    String lineText = textArea_.getLineText(caretLine); // get text line where caret is
    int lineIdx = textArea_.getCaretPosition() - // offsets from beg of file
                  textArea_.getLineStartOffset(caretLine);
                  
    // reset                  
    methodOrFieldName_ = null;
    varName_ = null;
    className_ = null;
    
    int lineLength = lineText.length();
    if (lineLength == 0 || lineIdx == lineLength)
      return false;
    
    char ch = lineText.charAt(lineIdx);
    if (ch == '.') {
      
      // get variable name
      int start = TextUtilities.findWordStart(lineText, lineIdx - 1, "_");
      varName_ = lineText.substring(start, lineIdx);
      
      // get method or field name
      start = TextUtilities.findWordStart(lineText, lineIdx + 2, "_");
      int end   = TextUtilities.findWordEnd(lineText, lineIdx + 2, "_");
      //Macros.message(view_, "start end: " + start + " " + end);
      methodOrFieldName_ = lineText.substring(start, end);
      
      //Macros.message(view_, "Dot:  " + varName_ + "." + methodOrFieldName_);
    }
    else if (Character.isLetter(ch)) {

      // Search forward from cursor for '.'
      boolean found = false;
      int i;
      for (i = lineIdx; i < lineLength && !found; i++) {
        ch = lineText.charAt(i);  
        if (Character.isLetter(ch))
          continue;
        else if (ch == '.')
          found = true;
        else
          break;
      }
      
      if (found) {   // of form ClassName.method()OrFieldName
        
        // Get class name
        int start = TextUtilities.findWordStart(lineText, lineIdx, "_");
        int end   = TextUtilities.findWordEnd(lineText, lineIdx + 1, "_");
        className_ = lineText.substring(start, end);
        
        // Get method or field name
        if (i != lineLength) {
          start = TextUtilities.findWordStart(lineText, i + 2, "_");
          end   = TextUtilities.findWordEnd(lineText, i + 2, "_");
          methodOrFieldName_ = lineText.substring(start, end);
        }
        
        //Macros.message(view_, "Letter:  \"" + className_ + "." + 
        //               methodOrFieldName_ + "\"");
      }
      else {        // of form method()OrFieldName or ClassName
        // method or field name
        int start = TextUtilities.findWordStart(lineText, lineIdx, "_");
        int end   = TextUtilities.findWordEnd(lineText, lineIdx + 1, "_");
        methodOrFieldName_ = lineText.substring(start, end);
        
        //Macros.message(view_, "Letter:  " + methodOrFieldName_);
      }
      
    }
    else {
      return false;
    }
    
    return true;
  }

  /***************************************************************************/
  private static Vector getREStringMatches(String varName) {
    Vector matchStrings = new Vector();

    // Create regular expression strings
    String reString = null;
    if (false) {  
      // this way is a little slow on large files when using the the string var
      // addition
      reString = RE_STRING_CLASS + "|" + RE_STRING_EXTENDS;
      if (varName != null)  // add varName if needed
        reString = reString + "|" + PARTIAL_RE_STRING_VAR + varName + ")";
    }
    else { // experimental
      if (varName == null)
        reString = RE_STRING_CLASS + "|" + RE_STRING_EXTENDS;
      else
        reString = PARTIAL_RE_STRING_VAR + varName + ")";
    }

    //Macros.message(view_, reString);
    RE myExpression = null;    
    try {
      // Create regular expression
      myExpression = new RE(reString, 0, RESyntax.RE_SYNTAX_EGREP);
          
      // Read through file line by line and test for RE
      int numLines = textArea_.getLineCount();
      String line = null;
      REMatch[] matches = null;
      String matchString;
      for (int i = 0; i < numLines; i++) {
        line = textArea_.getLineText(i);
        
        matches = myExpression.getAllMatches(line);
        for (int j = 0; j < matches.length; j++) {
          matchString = matches[j].toString();
          matchString = matchString.trim(); // remove trailing white space
          matchString = removeExtraWhiteSpace(matchString);
          
          matchStrings.addElement(matchString); // + "(line " + (i+1) + ")");
        }
      }
    
    } catch (NullPointerException npe) {
      System.out.println("Patter was null");
    } catch (REException ree) {
      System.out.println("Input pattern couldn't be parsed.");
    }
    
    return matchStrings;
  }
  
  /***************************************************************************/
  private static Vector buildTagNames(Vector matches, 
                                      String methodOrFieldName) {
                                       
    Vector tagNames = new Vector();
    int    numMatches = matches.size();
    
    // First sort matches
    MiscUtilities.quicksort(matches, new MiscUtilities.StringICaseCompare());
    
    // Remove duplicates
    String matchString;
    String previous = (String) matches.elementAt(0);
    for (int i = 1; i < numMatches; i++) {
      matchString = (String) matches.elementAt(i);
      
      if (previous.equals(matchString)) {
        matches.setElementAt(null,i);
      }
      else
        previous = matchString;
    }
    
    // build tag names
    int start, end;
    String className;
    for (int i = 0; i < numMatches; i++) {
      matchString = (String) matches.elementAt(i);
      if (matchString == null)
        continue;
       
      className = null;
      if (classRE_.getMatch(matchString) != null) {
        start = matchString.lastIndexOf(' ') + 1; // + 1 to get to letter
        if (start != -1)
          className = matchString.substring(start, matchString.length());
        
        //Macros.message(view_, "Class:  " + className + "." + 
        //               methodOrFieldName);
      }
      else if (extendsRE_.getMatch(matchString) != null) {
        start = matchString.lastIndexOf(' ') + 1; // + 1 to get to letter
        if (start != -1)
          className = matchString.substring(start, matchString.length());
        
        //Macros.message(view_, "Extends:  " + className + "." + 
        //               methodOrFieldName);
      }
      else {
        end = matchString.indexOf(' ');
        if (end != -1)
          className = matchString.substring(0, end);
        //Macros.message(view_, className + "." + methodOrFieldName);
      }
      
      if (className != null && methodOrFieldName != null)
        tagNames.addElement(className + "." + methodOrFieldName);
      
    }
    
    if (className_ != null) {
      if (methodOrFieldName != null)
        tagNames.addElement(className_ + "." + methodOrFieldName);
      else
        tagNames.addElement(className_);
    }
    /* methodOrFieldName may actually be a class name or stand alone function.
     * we should add it to the list as itself...
     */
    if (methodOrFieldName != null)
      tagNames.addElement(methodOrFieldName);

    // Sort tag names
    MiscUtilities.quicksort(tagNames, new MiscUtilities.StringICaseCompare());
    
    return tagNames;
  }

  /***************************************************************************/
  private static String removeExtraWhiteSpace(String string) {
    if (string == null)
      return null;
    
    StringBuffer buf = new StringBuffer();
    
    int length = string.length();
    char c;
    boolean sawSpace = false;
    for (int i = 0; i < length; i++) {
      c = string.charAt(i);
      if (c == ' ' && sawSpace)
        continue;  // don't append
        
      sawSpace = c == ' ';
      buf.append(c);
    }
    
    return buf.toString();
  }
  

  
}


