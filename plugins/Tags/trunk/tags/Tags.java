/*
 * Tags.java
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
import javax.swing.JOptionPane;
import java.awt.Toolkit;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.search.SearchAndReplace;
import org.gjt.sp.jedit.io.VFSManager;

import gnu.regexp.*;

public class Tags {

  /*+*************************************************************************/
  protected static final Vector tagFiles_ = new Vector(15);
  protected static final Stack tagFileStack_ = new Stack();
  protected static final Stack tagCaretPosStack_ = new Stack();

  protected static boolean debug_ = false;
  protected static boolean ui_ = true;

  /* NOTE 1:
   * Yes there is only one parser.  At one time there used to be different 
   * versions based on the tag prorgam that created it (C, GNU, Exuberant).
   * However, Exuberant C Tags is just a super set of C and GNU and can 
   * handel all.  However there is Emacs' etags, which I plan to implement
   * at some point, so I'm going to leave this stuff here.  There is no 
   * performance issue since parser_ always refers to one of the items in the
   * array.
   */
  protected static final int EXUBERANT_C_TAGS  = 0;
  protected static final int NUM_PARSERS = 1;
  
  // The current parser and parser registration (or lack thereof) should be
  // restructured.  How about static {} blocks in the parser class regestring
  // it...?
  protected static final TagsParser parsers_[] = new TagsParser[NUM_PARSERS];
  
  static {
    parsers_[EXUBERANT_C_TAGS] = new ExuberantCTagsParser();
  }
  
  protected static int currentParserType_ = EXUBERANT_C_TAGS;
  protected static TagsParser parser_ = parsers_[currentParserType_];
  
  protected static String tagFileName_;
  protected static String searchString_;
  protected static int    lineNumber_;
  
  /***************************************************************************/
  public static void loadTagFiles() {
    // NOTE:  We are not remembering tag file catagories at this time...
    
    // Clear out all tag files (just to make sure...)
    Tags.clearTagFiles();
    
    // Get property string
    String tagFiles = jEdit.getProperty("tags-tag-files");
    
    // Break into tokens and append tag filename
    StringTokenizer st = new StringTokenizer(tagFiles, ",");
    String fileName = null;
    while (st.hasMoreElements()) {
      fileName = (String) st.nextElement();
      if (fileName != null)
        Tags.appendTagFile(fileName);
    }
  }
  
  /***************************************************************************/
  public static void writeTagFiles() {
    
    StringBuffer b = new StringBuffer();
    
    String tagFileName = null;
    int numTagFiles = tagFiles_.size();
    for (int i = 0; i < numTagFiles; i++) {
      tagFileName = (String) ((TagFile)tagFiles_.elementAt(i)).getPath();
      if (tagFileName != null) {
        b.append(tagFileName);
        if (i < (numTagFiles - 1))
          b.append(",");
      }
    }
    
    jEdit.setProperty("tags-tag-files", b.toString());
  }
  
  /***************************************************************************/
  // See Note 1
  public static void setParserType(int parser) {
    if (parser >= 0 && parser < NUM_PARSERS) {
      currentParserType_ = parser;
    }
    
    parser_ = parsers_[currentParserType_];
  }
  
  /***************************************************************************/
  // See Note 1
  public static int getParserType() { return currentParserType_; }
  
  /*+*************************************************************************/
  public static void appendTagFile(String file) {
    tagFiles_.addElement(new TagFile(file, TagFile.DEFAULT_CATAGORY));
  }
  
  /*+*************************************************************************/
  public static void appendTagFile(String file, String catagory) {
    tagFiles_.addElement(new TagFile(file, catagory));
  }

  /*+*************************************************************************/
  public static void addTagFile(String file, String catagory, int index) {
    tagFiles_.insertElementAt(new TagFile(file, catagory),index); 
  }
  
  /*+*************************************************************************/
  public static void prependTagFile(String file) {
    tagFiles_.insertElementAt(new TagFile(file, TagFile.DEFAULT_CATAGORY),0);
  }
  
  /*+*************************************************************************/
  public static void prependTagFile(String file, String catagory) {
    tagFiles_.insertElementAt(new TagFile(file, catagory),0);
  }

  /*+*************************************************************************/
  public static Object removeTagFile(int index) {
    Object obj = tagFiles_.elementAt(index);
    tagFiles_.removeElementAt(index);
    return obj;
  }
  
  /*+*************************************************************************/
  public static void clearTagFiles() { tagFiles_.removeAllElements(); }

  /*+*************************************************************************/
  public static void clearTagFiles(String catagory) { 
    int numTagFiles = tagFiles_.size();
    for (int i = numTagFiles - 1; i >= 0; i--) {
      if (((TagFile)tagFiles_.elementAt(i)).getCatagory().equals(catagory))
        tagFiles_.removeElementAt(i);
    }
  }

  /***************************************************************************/
  public static void setUseCurrentBufTagFile(boolean val) {
    jEdit.setBooleanProperty("options.tags.tag-search-current-buff-tag-file",
                             val);
  }
  
  /***************************************************************************/
  public static boolean getUseCurrentBufTagFile() {
    return jEdit.getBooleanProperty(
                       "options.tags.tag-search-current-buff-tag-file", false);
  }

  /***************************************************************************/
  public static void setSearchAllTagFiles(boolean val) {
    jEdit.setBooleanProperty("options.tags.tag-search-all-files", val);
  }
  
  /***************************************************************************/
  public static boolean getSearchAllTagFiles() { 
    return jEdit.getBooleanProperty("options.tags.tag-search-all-files", false);
  }

  /***************************************************************************/
  public static void displayTagFiles(View view) {
    StringBuffer allFiles = new StringBuffer();
    int numTagFiles = tagFiles_.size();
    for (int i = 0; i < numTagFiles; i++) {
      allFiles.append("[" + 
                      (String) ((TagFile) tagFiles_.elementAt(i)).getCatagory() +
                      "]  ");
      allFiles.append((String) ((TagFile) tagFiles_.elementAt(i)).getPath());
      allFiles.append("\n");
    }
    displayMessage(view, "Tag files:  \n" + allFiles.toString());
    allFiles = null;
  }
  
  /***************************************************************************/
  public static void enterAndFollowTag(View view, JEditTextArea textArea,
                                       Buffer buffer) {
    
		// The panel is a bit overkill but it was done for a dialog before this
		// was done using JOptionPane...
    TagsEnterTagPanel enterTagPanel = new TagsEnterTagPanel(null, false);
	
		String[] buttonNames = { jEdit.getProperty("options.tags.tag-ok.label"), 
                             jEdit.getProperty("options.tags.tag-cancel.label") 
                           };
	
		int ret = JOptionPane.showOptionDialog(view, enterTagPanel,
                          jEdit.getProperty("tags.enter-tag-dlg.title"),
                          JOptionPane.DEFAULT_OPTION,
                          JOptionPane.QUESTION_MESSAGE, null, 
                          buttonNames, buttonNames[0]);
																
		if (ret == 0) {
			followTag(view, textArea, buffer, enterTagPanel.getOtherWindow(),
								enterTagPanel.getFuncName());
		}
		
		enterTagPanel = null;  // we probably should reuse this...
  }  
  
  /*+*************************************************************************/
  public static void followTag(View view, JEditTextArea textArea, 
                               Buffer buffer, boolean newView) {
    String funcName;
    
    
    funcName = textArea.getSelectedText();
    if (funcName == null) {
      String modeName = buffer.getMode().getName();
      
      funcName = getFuncNameUnderCursor(textArea);
      //Macros.message(view, "\"" + test + "\"");
      //textArea.selectWord();
      //funcName = textArea.getSelectedText();
    }
    
    followTag(view, textArea, buffer, newView, funcName);
  }

  /*+*************************************************************************/
  public static void followTag(View currentView, JEditTextArea currentTextArea, 
                               Buffer buf, boolean openNewView, 
                               String funcName ) {
    
    Buffer buffer = null;
    
    if (funcName == null) {
      Toolkit.getDefaultToolkit().beep();
      return;
    }

    ui_ = currentView != null;
      
    if (!getUseCurrentBufTagFile() && tagFiles_.size() == 0) {
      Toolkit.getDefaultToolkit().beep();
      if (ui_) 
        Macros.error(currentView, 
                jEdit.getProperty("tags.message.no-tag-index-files"));

      return;
    }
      
      
    if (ui_) {
      buffer = currentView.getBuffer();
    }
      
    parser_.reinitialize();
    
    // Search default tag file if needed
    boolean found = false;
    String tagFileName = null;
    File defaultTagFile = null;
    if (getUseCurrentBufTagFile()) {
      File currentBufferFile = new File(buffer.getPath());
      defaultTagFile = new File(currentBufferFile.getParent() + 
                   System.getProperty("file.separator") + 
                   jEdit.getProperty("options.tags.current-buffer-file-name"));
      if (defaultTagFile.exists()) {
        found = parser_.findTagLines(defaultTagFile.getPath(),
                                     funcName, currentView) || found;
      }
    }
    
    // Search tag files if needed
    if (!found || getSearchAllTagFiles()) {
      int numTagFiles = tagFiles_.size();
      for (int i = 0; i < numTagFiles; i++) {        
        tagFileName = (String) ((TagFile)tagFiles_.elementAt(i)).getPath();
        if (defaultTagFile != null && 
            tagFileName.equals(defaultTagFile.getPath()))
          continue;
        
        found = parser_.findTagLines(tagFileName, funcName, 
                                     currentView) || found;
        if (!getSearchAllTagFiles()  && found)
          break;
      }
    }

    // Handle what was found (or not found)
    if (parser_.getNumberOfFoundTags() > 1) {
      if (ui_)
        new ChooseTagListPopup(parser_, currentView, openNewView);
      else
        processTagLine(0, currentView, openNewView, funcName);
    }
    else if (parser_.getNumberOfFoundTags() > 0) {
      processTagLine(0, currentView, openNewView, funcName);
    }
    else if (ui_) {
      displayMessage(currentView, "\"" + funcName + "\" not found!");
    }

  }
  
  /***************************************************************************/
  public static void processTagLine(int tagLineIndex, View currentView, 
                                    boolean openNewView, String funcName) {
    
    View           tagToView = null;
    JEditTextArea  currentTextArea = null;
    JEditTextArea  tagToTextArea = null;
    Buffer         buffer = null;
    
    tagFileName_ = null;
    searchString_ = null;
    lineNumber_ = -1;
    
    if (ui_) {
      if (openNewView)
        tagToView = jEdit.newView(currentView, currentView.getBuffer());
      else
        tagToView = currentView;
        
      currentTextArea = currentView.getTextArea();
      buffer = currentView.getBuffer();
    }
    
    if (ui_)
      currentView.showWaitCursor();      

    String tagLine = parser_.getTagLine(tagLineIndex);
    
    if (debug_)
      displayMessage(currentView, tagLine);
   
    tagFileName_ = parser_.getDefinitionFileName(tagLineIndex);
    if (tagFileName_ != null) {
  
      // Remember current position on tag stack
      if (ui_) {
        tagFileStack_.push(buffer.getPath());
        tagCaretPosStack_.push(
                    new Integer(currentTextArea.getCaretPosition()));
      }
                           
      // Open the file
      //displayMessage(currentView, tagFileName);
      if (ui_) {
				/* Slava if you look at this, I'm not sure I understand how this
				 * fixes your problem.  It doesn't work.
				 */
				File tagFile = new File(tagFileName_);
				if (!tagFile.exists()) {
					Macros.error(currentView, 
											 "The tag file name path \"" + tagFileName_ + "\"\n" +
											 "that is listed in the tag index file, does not\n" +
											 "exist.  You may need to update your tag index\n" +
											 "files, or perhaps you passed a relative path to\n" +
											 "the ctags program.");
          currentView.hideWaitCursor();
          return;
				}
				else {
					//jEdit.openFile(tagToView, tagFile.getParent(), tagFileName_, 
					//               false, null);
					jEdit.openFile(tagToView, tagFileName_);
          tagToTextArea = tagToView.getTextArea();          
				}
      }
      else {
        if (debug_)
          displayMessage(currentView, "Open file:  " + tagFileName_);
      }
    
       
      searchString_ = parser_.getDefinitionSearchString(tagLineIndex);
      final View v = tagToView;  // for VFSManager inner class
      if (searchString_ != null) {
  
        if (!ui_) {
          if (debug_)
            displayMessage(currentView, "Search string:  " + searchString_);
        }
        else {
          // This is how jEdit.gotoMarker() and its use by jEdit.openFile()
          // does it.  However b/c this code is under a GUI callback I 
          // thought we are already in the swing GUI thread.  Guess not.
          // When in Rome...
          VFSManager.runInAWTThread(new Runnable() {
            public void run() {
              // set the caret pos to the beginning for searching...
              v.getTextArea().setCaretPosition(0);
              
              boolean prevRegexpState = SearchAndReplace.getRegexp();
              boolean prevReverseSearch = SearchAndReplace.getReverseSearch();
              SearchAndReplace.setRegexp(true);
              SearchAndReplace.setReverseSearch(false);
              SearchAndReplace.setSearchString(searchString_);
              SearchAndReplace.find(v);
              SearchAndReplace.setRegexp(prevRegexpState);
              SearchAndReplace.setReverseSearch(prevReverseSearch);
              
              v.getTextArea().removeFromSelection(
                                         v.getTextArea().getCaretPosition());
  
            }
          });
        }
      }
      else {
        lineNumber_ = parser_.getDefinitionLineNumber(tagLineIndex);
        if (ui_) {
          VFSManager.runInAWTThread(new Runnable() {            
            public void run() {
              v.getTextArea().setCaretPosition(
                            v.getTextArea().getLineStartOffset(lineNumber_));
            }
          });
          
        }
        else {
          if (debug_)
            displayMessage(currentView, "Goto line:  " + lineNumber_);
        }
      }
  
      if (ui_)
        tagToView.getStatus().setMessage("Found: " + funcName);
  
    }
    else
      displayMessage(currentView, "What?:  " + tagLine);
  
    tagLine = null;
    
    if (currentView != null) {
      currentView.hideWaitCursor();
    }

  }
  
  /***************************************************************************/
  public static String getTagFileName() { return tagFileName_; }
  
  /***************************************************************************/
  public static String getSearchString() { return searchString_; }
  
  /***************************************************************************/
  public static int getLineNumber() { return lineNumber_; }
  
  /*+*************************************************************************/
  public static void popTag(View view, JEditTextArea textArea) {
    view.hideWaitCursor();

    if (!tagFileStack_.empty()) {
      jEdit.openFile(view, (String) tagFileStack_.pop());
      textArea.setCaretPosition(((Integer)tagCaretPosStack_.pop()).intValue());
    }
    else {
      //displayMessage(view, "Tag stack is empty!");
      Toolkit.getDefaultToolkit().beep();
    }
  }

  /***************************************************************************/
  static protected String getFuncNameUnderCursor(JEditTextArea textArea) {
    int caretLine = textArea.getCaretLine();  // get line num of caret
    String lineText = textArea.getLineText(caretLine); // get text line where caret is
    int lineIdx = textArea.getCaretPosition() - // offsets from beg of file
                  textArea.getLineStartOffset(caretLine);
                  
    int lineLength = lineText.length();
    if (lineLength == 0 || lineIdx == lineLength)
      return null;
    
    String tagName = null;
    char ch = lineText.charAt(lineIdx);
    if (Character.isLetter(ch) || ch == '_') {
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
        String className = lineText.substring(start, end);
        
        // Get method or field name
        String methodOrFieldName = null;
        if (i != lineLength) {
          start = TextUtilities.findWordStart(lineText, i + 2, "_");
          end   = TextUtilities.findWordEnd(lineText, i + 2, "_");
          methodOrFieldName = lineText.substring(start, end);
        }
        
        tagName = className + 
                  ((methodOrFieldName != null) ? "." : "") + 
                  ((methodOrFieldName != null) ? methodOrFieldName : "");
        
        //Macros.message(view_, "Letter:  \"" + tagName);
      }
      else {        // of form method()OrFieldName or ClassName
        // method or field name
        int start = TextUtilities.findWordStart(lineText, lineIdx, "_");
        int end   = TextUtilities.findWordEnd(lineText, lineIdx + 1, "_");
        tagName = lineText.substring(start, end);
        
        //Macros.message(view_, "Letter:  " + tagName);
      }
      
    }
    else {
      return null;
    }
    
    return tagName;
  }
  
  /***************************************************************************/
  static protected String getFuncNameUnderCursorOld(JEditTextArea textArea) {
    // Old way of getting tag under cursor, see new way above which seems
    // much better
    
    int caretLine = textArea.getCaretLine();  // get line num of caret
    String lineText = textArea.getLineText(caretLine); // get text line where caret is
    int lineIdx = textArea.getCaretPosition() - // offsets from beg of file
                  textArea.getLineStartOffset(caretLine);

    String funcName = null;
    int lineLength = lineText.length();
    if (lineLength == 0 || lineIdx == lineLength)
      return null;
    
    int start = TextUtilities.findWordStart(lineText, lineIdx, "_");
    int end   = TextUtilities.findWordEnd(lineText, lineIdx + 1, "_");
    if (start != -1 && end != -1) {
      funcName = lineText.substring(start,end);
      funcName = funcName.trim();
      if (funcName.length() == 0)
        funcName = null;
    }
    return funcName;
  }
  
 /***************************************************************************/
 static public void displayMessage(View view, String message) {
   if (view == null)
     System.out.println(message);
   else
     Macros.message(view, message);
 }
 
 /*****************************************************************************/
 static public void main(String args[]) {
   debug_ = true;
 }

}

