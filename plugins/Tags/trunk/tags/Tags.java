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
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.search.*;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.gui.KeyEventWorkaround;

import gnu.regexp.*;

final public class Tags {

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
    
    /* Previously we use to store tag index filenames in one property, 
     * seperated by commas.  This wouldn't allow for tag index file paths to
     * have commas, so we use a numbered property scheme.  But we need to 
     * support and convert from the old to the new scheme, so check for the
     * old, nix the old property, and convert to the new.
     */
    
    // Get old property string
    String tagFiles = jEdit.getProperty("tags-tag-files");
    
    if (tagFiles != null)
    {
      // Break into tokens and append tag filename
      StringTokenizer st = new StringTokenizer(tagFiles, ",");
      String fileName = null;
      while (st.hasMoreElements()) 
      {
        fileName = (String) st.nextElement();
        if (fileName != null)
        {
          Log.log(Log.DEBUG, null, "Loading tag index file: " + fileName);
          Tags.appendTagFile(fileName);
        }
      }
    }
    else
    {
      int index = 0;
      String tagIndexFileName = null;
      while ((tagIndexFileName = 
                jEdit.getProperty("tags-tag-index-file" + index)) != null)
      {
        Log.log(Log.DEBUG, null, 
                "Loading tag index file: " + tagIndexFileName);
        Tags.appendTagFile(tagIndexFileName);
        index++;
      }
    }
    jEdit.setProperty("tags-tag-files", null);  // remove old property
  }
  
  /***************************************************************************/
  public static void writeTagFiles() {
    
    jEdit.setProperty("tags-tag-files", null); // remove old property

    String tagFileName = null;
    int numTagFiles = tagFiles_.size();
    int i = 0;
    while (i < numTagFiles ||
           jEdit.getProperty("tags-tag-index-file" + i) != null)
    {
      if (i < numTagFiles)
      {
        tagFileName = (String) ((TagFile)tagFiles_.elementAt(i)).getPath();
        if (tagFileName != null) 
        {
          /* yes I know "propertizing" isn't a word! */
          Log.log(Log.DEBUG, null, 
                  "Propertizing tag index file: " + tagFileName);
          jEdit.setProperty("tags-tag-index-file" + i, tagFileName);
        }
      }
      else // remove any previous entries >= numTagFiles
        jEdit.setProperty("tags-tag-index-file" + i, null);
        
      i++;
    }    
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
  public static void appendTagFile(String file) 
  {
    TagFile tf = new TagFile(file, TagFile.DEFAULT_CATAGORY);
    tf.currentDirIndexFile_ = 
       file.equals(jEdit.getProperty("options.tags.current-buffer-file-name"));
    
    tagFiles_.addElement(tf);
  }
  
  /*+*************************************************************************/
  public static void addTagFile(String file, int index) 
  {
    TagFile tf = new TagFile(file, TagFile.DEFAULT_CATAGORY);
    tf.currentDirIndexFile_ = 
       file.equals(jEdit.getProperty("options.tags.current-buffer-file-name"));
    
    tagFiles_.insertElementAt(tf, index); 
  }
  
  /*+*************************************************************************/
  public static void prependTagFile(String file) 
  {
    TagFile tf = new TagFile(file, TagFile.DEFAULT_CATAGORY);
    tf.currentDirIndexFile_ = 
       file.equals(jEdit.getProperty("options.tags.current-buffer-file-name"));
    
    tagFiles_.insertElementAt(tf, 0);
  }
  
  /***************************************************************************/
  public static int getTagFileCount()
  {
    if (tagFiles_ == null)
      return 0;
      
    return tagFiles_.size();
  }
  
  /***************************************************************************/
  public static String getTagFileName(int index) 
  {
   if (tagFiles_ == null)
      return null;
   
   int size = tagFiles_.size();
   if (index >= 0 && index < size)
     return ((TagFile) tagFiles_.elementAt(index)).getPath();

   return null;
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
  public static void enterAndFollowTag(final View view, 
                                       final JEditTextArea textArea,
                                       final Buffer buffer) 
  {
    TagsEnterTagDialog dialog = new TagsEnterTagDialog(view, null, false);
    if (dialog.showDialog())
    {
      followTag(view, textArea, buffer, dialog.getOtherWindow(), false,
      dialog.getFuncName());
    }
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
    
    followTag(view, textArea, buffer, newView, true, funcName);
  }

  /*+*************************************************************************/
  public static void followTag(View currentView, JEditTextArea currentTextArea, 
                               Buffer buf, boolean openNewView, 
                               boolean collisionPopup, String funcName ) 
  {
    Buffer buffer = null;
    
    if (funcName == null) {
      Toolkit.getDefaultToolkit().beep();
      return;
    }

    ui_ = currentView != null;

    boolean useCurrentBufTagFile = ui_ ? getUseCurrentBufTagFile() : false;
    boolean serachAllTagFiles = ui_ ? getSearchAllTagFiles() : false;
      
    if (!useCurrentBufTagFile && tagFiles_.size() == 0) {
      Toolkit.getDefaultToolkit().beep();
      if (ui_) 
        GUIUtilities.error(currentView, "no-tag-index-files", null);

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
    if (useCurrentBufTagFile) {
      File currentBufferFile = new File(buffer.getPath());
      defaultTagFile = new File(currentBufferFile.getParent() + 
                   System.getProperty("file.separator") + 
                   jEdit.getProperty("options.tags.current-buffer-file-name"));
      if (defaultTagFile.exists()) 
      {
        found = parser_.findTagLines(defaultTagFile.getPath(),
                                     funcName, currentView) || found;
      }
      else
        Log.log(Log.WARNING, null, "Default tag file " + 
                defaultTagFile.getPath() + " does not exist");
    }
    
    // Search tag files if needed
    TagFile tf = null;
    if (!found || serachAllTagFiles) 
    {
      int numTagFiles = tagFiles_.size();
      for (int i = 0; i < numTagFiles; i++) 
      {
        tf = (TagFile) tagFiles_.elementAt(i);
        if (TagsOptionsPanel.newCurBufStuff_ && tf.currentDirIndexFile_ &&
            useCurrentBufTagFile)
        {
          File currentBufferFile = new File(buffer.getPath());
          tagFileName = currentBufferFile.getParent() + 
                   System.getProperty("file.separator") + 
                   jEdit.getProperty("options.tags.current-buffer-file-name");
        }
        else
          tagFileName = tf.getPath();
        if (defaultTagFile != null && 
            tagFileName.equals(defaultTagFile.getPath()))
          continue;
        
        found = parser_.findTagLines(tagFileName, funcName, 
                                     currentView) || found;
        if (!serachAllTagFiles && found)
          break;
      }
    }
    
    // Handle what was found (or not found)
    if (parser_.getNumberOfFoundTags() > 1) {
      if (ui_) 
      {
        if (collisionPopup)
          new ChooseTagListPopup(parser_, currentView, openNewView);
        else
          new ChooseTagListDialog(parser_, currentView, openNewView);
      }
      else
        processTagLine(0, currentView, openNewView, funcName);
    }
    else if (parser_.getNumberOfFoundTags() > 0) {
      processTagLine(0, currentView, openNewView, funcName);
    }
    else if (ui_) 
    {
      Object args[] = { funcName };
      GUIUtilities.error(currentView, "func-not-found", args);
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
    
    TagLine tagLine = parser_.getTagLine(tagLineIndex);
    
    tagFileName_ = tagLine.getDefinitionFileName();
    if (tagFileName_ != null) {
  
      // Remember current position on tag stack
      if (ui_) 
        pushPosition(currentView);
                           
      // Open the file
      if (ui_) 
      {
        if (!openDefinitionFile(tagToView, tagLine))
          return;
      }
       
      searchString_ = tagLine.getDefinitionSearchString();
      final View v = tagToView;  // for VFSManager inner class
      if (searchString_ != null && ui_) 
      {
				// This is how jEdit.gotoMarker() and its use by jEdit.openFile()
				// does it.  However b/c this code is under a GUI callback I 
				// thought we are already in the swing GUI thread.  Guess not.
				// When in Rome...
				VFSManager.runInAWTThread(new Runnable() {
					public void run() {
						// set the caret pos to the beginning for searching...
						v.getTextArea().setCaretPosition(0);
						
            // get current search values/parameters
            SearchAndReplace.save();
            SearchFileSet fileset = SearchAndReplace.getSearchFileSet();
            
            // set current search values/parameters
            SearchAndReplace.setSearchFileSet(new CurrentBufferSet());
            SearchAndReplace.setRegexp(true);
						SearchAndReplace.setReverseSearch(false);
            SearchAndReplace.setIgnoreCase(false);
            SearchAndReplace.setBeanShellReplace(false);
            SearchAndReplace.setAutoWrapAround(true);
            
						SearchAndReplace.setSearchString(searchString_);  // search
            SearchAndReplace.find(v);
            
            // Be nice and restore search values/parameters
            SearchAndReplace.load();
            SearchAndReplace.setSearchFileSet(fileset);
            
						v.getTextArea().removeFromSelection(
																			 v.getTextArea().getCaretPosition());
					}
				});

      }
      else 
      {
        lineNumber_ = tagLine.getDefinitionLineNumber();
        if (ui_) 
        {
          VFSManager.runInAWTThread(new Runnable() {            
            public void run() {
              // minus 1 b/c line numbers start at 0
              v.getTextArea().setCaretPosition(
                          v.getTextArea().getLineStartOffset(lineNumber_ - 1));
            }
          });
        }
      }
  
      if (ui_)
        tagToView.getStatus().setMessage("Found: " + funcName);
  
    }
    else
      Log.log(Log.ERROR, null, "What?:  " + tagLine);
  
    tagLine = null;
  }
  
  /***************************************************************************/
  public static String getTagFileName() { return tagFileName_; }
  
  /***************************************************************************/
  public static String getSearchString() { return searchString_; }
  
  /***************************************************************************/
  public static int getLineNumber() { return lineNumber_; }
  
  /***************************************************************************/
  public static void pushPosition(View view) 
  {
    tagFileStack_.push(view.getBuffer().getPath());
    tagCaretPosStack_.push(new Integer(view.getTextArea().getCaretPosition()));
  }
  
  /*+*************************************************************************/
  public static void popTag(View view, JEditTextArea textArea) {
    if (!tagFileStack_.empty()) {
      jEdit.openFile(view, (String) tagFileStack_.pop());
      textArea.setCaretPosition(((Integer)tagCaretPosStack_.pop()).intValue());
    }
    else {
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
  static protected boolean openDefinitionFile(View view, TagLine tagLine) 
  {
    File tagFile = new File(tagLine.getDefinitionFileName());
    if (!tagFile.exists()) 
    {
      Object args[] = { tagFileName_ };
      GUIUtilities.error(view, "tag-def-file-not-found", args);
      return false;
    }
    
    jEdit.openFile(view, tagFile.getAbsolutePath());
    return true;
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
  
}

