/*
 * Tags.java
 * Copyright (c) 2001, 2002 Kenrick Drew
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
 *
 * $Id$
 */

package tags;

//{{{ imports
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
import org.gjt.sp.jedit.gui.HistoryModel;
//}}}

final public class Tags {

  //{{{ declarations
  protected static boolean ui_ = true;

  protected static ExuberantCTagsParser parser_ = new ExuberantCTagsParser();

  protected static String tagFileName_;
  protected static String searchString_;
  protected static int    lineNumber_;

  protected static Point currentMousePoint_;
  /**
   * Ken has scripts which use the plugin code
   * outside jEdit, so this flag is needed
   * for when the code is not running as a jEdit
   * plugin
   */
  protected static boolean jEditAvailable = true;
  //}}}

  //{{{ methods to allow this class to be used outside jEdit

  //{{{ setJEditAvailable(boolean) method
  public static boolean setJEditAvailable(boolean status)
  {
    boolean old = jEditAvailable;
    jEditAvailable = status;
    return old;
  } //}}}

  //{{{ isJEditAvailable() method
  public static boolean isJEditAvailable()
  {
    return jEditAvailable;
  } //}}}

  //}}}

  //{{{ setMousePosition() method
  public static void setMousePosition(Point p)
  {
    currentMousePoint_ = p;
    //Don't fill log with alot of stuff...
    //Log.log(Log.DEBUG, null, p);
  } //}}}

  //{{{ setDialogPosition() method
  public static void setDialogPosition(Component parentComponent,
                                       JDialog dialog)
  {
    if (parentComponent == null)
      return;

    if (currentMousePoint_ != null &&
        jEdit.getBooleanProperty("options.tags.open-dialogs-under-cursor"))
    {
      Point p = new Point(currentMousePoint_);
      Dimension dlgSize = dialog.getSize();
      p.x = p.x - (dlgSize.width / 2);
      p.y = p.y - (dlgSize.height / 2);

      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

      if (p.x + dlgSize.width > screenSize.width)  // off the right
      {
        p.x = screenSize.width - dlgSize.width;
      }
      if (p.x < 0)   // off the left
      {
        p.x = 0;  // prefer left to right screen aligned...
      }

      if (p.y + dlgSize.height > screenSize.height)  // off the bottom
      {
        p.y = screenSize.height - dlgSize.height;
      }
      if (p.y < 0)  // off the top
      {
        p.y = 0;
      }

      dialog.setLocation(p);
      p = null;
    }
    else
    {
      dialog.setLocationRelativeTo(parentComponent);
      if (currentMousePoint_ == null)
        Log.log(Log.DEBUG, null,
                "Placed dialog relative to parent b/c no mouse point.");
    }
  } //}}}

  public static String getCurrentBufferTagFilename()
  {
    return jEdit.getProperty("options.tags.current-buffer-file-name","tags");
  }

  //{{{ setUseCurrentBufTagFile() method
  public static void setUseCurrentBufTagFile(boolean val) {
    jEdit.setBooleanProperty("options.tags.tag-search-current-buff-tag-file",
                             val);
  } //}}}

  //{{{ getUseCurrentBufTagFile() method
  public static boolean getUseCurrentBufTagFile() {
    return jEdit.getBooleanProperty(
             "options.tags.tag-search-current-buff-tag-file", true);
  } //}}}

  //{{{ setSearchAllTagFiles() method
  public static void setSearchAllTagFiles(boolean val) {
    jEdit.setBooleanProperty("options.tags.tag-search-all-files", val);
  } //}}}

  //{{{ getSearchAllTagFiles() method
  public static boolean getSearchAllTagFiles() {
    return jEdit.getBooleanProperty("options.tags.tag-search-all-files", false);
  } //}}}

  //{{{ getSearchInParentDirs() method
  public static boolean getSearchInParentDirs()
  {
    return jEdit.getBooleanProperty("options.tags.tags-search-parent-dir");
  } //}}}

  //{{{ enterAndFollowTag() method
  public static void enterAndFollowTag(final View view,
                                       final JEditTextArea textArea,
                                       final Buffer buffer)
  {
    TagsEnterTagDialog dialog = new TagsEnterTagDialog(view, parser_, null);
    if (dialog.showDialog())
    {
      followTag(view, textArea, buffer, dialog.getOtherWindow(), false,
                dialog.getFuncName());
    }
    else
      textArea.requestFocus();
    dialog = null;
  } //}}}

  //{{{ followTag() method
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
  } //}}}

  //{{{ followTag() method
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
    boolean searchAllTagFiles = ui_ ? getSearchAllTagFiles() : false;
    boolean searchInParentDirs = ui_ ? getSearchInParentDirs() : false;

    if (!useCurrentBufTagFile && TagsPlugin.getTagFiles().size() == 0) {
      Toolkit.getDefaultToolkit().beep();
      if (ui_)
        GUIUtilities.error(currentView, "no-tag-index-files", null);

      return;
    }

    if (ui_) {
      buffer = currentView.getBuffer();
    }

    parser_.reinitialize();

    // Search tag files if needed
    TagFile tf = null;
    int numTagFiles = TagsPlugin.getTagFiles().size();
    boolean found = false;
    String tagFileName = null;
    long start = System.currentTimeMillis();
    for (int i = 0; i < numTagFiles; i++)
    {
      tf = TagsPlugin.getTagFiles().get(i);
      if (!tf.isEnabled())
      {
        Log.log(Log.DEBUG, Tags.class, "Skipping " + tf.getPath() +
                                 " b/c not enabled");
        continue;
      }

      if (tf.currentDirIndexFile && useCurrentBufTagFile)
      {
        String fileSep = System.getProperty("file.separator");
        String parent = buffer.getDirectory();
        String filename = null;
        String defaultTags = Tags.getCurrentBufferTagFilename();
        int checks = 0;

        while(filename == null
              && (searchInParentDirs == true || checks == 0))
        {
          checks++;

          Log.log(Log.DEBUG, Tags.class,
            "Looking for tags in: " + parent);  // ##

          File f = new File(parent + fileSep + defaultTags);
          if(!f.exists())
          {
            if(f.getParentFile().getParentFile() == null)
              break;
            else
              parent = f.getParentFile().getParent();
          }
          else
          {
            if(f.isDirectory())
            {
              if(f.getParentFile().getParentFile() == null)
                break;
              else
                parent = f.getParentFile().getParent();
            }
            else
            {
              filename = f.getPath();
              Log.log(Log.DEBUG, Tags.class,
                "Found tags file: " + filename);  // ##
            }
          }
        }

        tagFileName = filename;
        if(tagFileName == null)
        {
          Log.log(Log.DEBUG, Tags.class,
            "No tags file found for current buffer -- skipping to next in list."); // ##
          continue;
        }

      } // end if tag index file for current buffer
      else
        tagFileName = tf.getPath();

      if(tagFileName != null)
      {
        found = parser_.findTagLines(tagFileName, funcName,
                                   currentView) || found;
      }

      if (!searchAllTagFiles && found)
        break;
    } // end searching tag files loop

    long end = System.currentTimeMillis();

    Log.log(Log.DEBUG, Tags.class,
            "Found tag(s) in " + (end - start) * .001 + " seconds.");

    // Handle what was found (or not found)
    if (parser_.getNumberOfFoundTags() > 1)
    {
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

  } //}}}

  //{{{ processTagLine() method
  protected static void processTagLine(int tagLineIndex, View currentView,
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
        TagsPlugin.pushPosition(currentView);

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
            SearchFileSet oldFileset = SearchAndReplace.getSearchFileSet();
            String oldSearchString = SearchAndReplace.getSearchString();

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
            SearchAndReplace.setSearchFileSet(oldFileset);
            SearchAndReplace.setSearchString(oldSearchString);

						v.getTextArea().removeFromSelection(
                 v.getTextArea().getCaretPosition());
            TagsPlugin.pushPosition(v);
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
              TagsPlugin.pushPosition(v);
            }
          });
        }
      }

      if (ui_)
      {
        tagToView.getStatus().setMessage("Found: " + funcName);

        HistoryModel taggingHistoryModel =
                               HistoryModel.getModel("tags.enter-tag.history");
        taggingHistoryModel.addItem(funcName);
      }
    }
    else
      Log.log(Log.ERROR, null, "What?:  " + tagLine);

    tagLine = null;
  } //}}}

  protected static String getTagFileName() { return tagFileName_; }

  protected static String getSearchString() { return searchString_; }

  protected static int getLineNumber() { return lineNumber_; }

  //{{{ getFuncNameUnderCursor() method
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
    if (Character.isLetter(ch) || ch == '_')
    {
      // Search forward from cursor for '.'
      boolean found = false;
      int i;
      for (i = lineIdx; i < lineLength && !found; i++)
      {
        ch = lineText.charAt(i);
        if (Character.isLetter(ch))
          continue;
        else if (ch == '.')
          found = true;
        else
          break;
      }

      if (found &&      // of form ClassName.method()OrFieldName
          jEdit.getBooleanProperty("options.tags.tag-extends-through-dot"))
      {
        // Get class name
        int start = TextUtilities.findWordStart(lineText, lineIdx, "_");
        int end   = TextUtilities.findWordEnd(lineText, lineIdx + 1, "_");
        String className = lineText.substring(start, end);

        // Get method or field name
        String methodOrFieldName = null;
        if (i != lineLength)
        {
          start = TextUtilities.findWordStart(lineText, i + 2, "_");
          end   = TextUtilities.findWordEnd(lineText, i + 2, "_");
          methodOrFieldName = lineText.substring(start, end);
        }

        tagName = className +
                  ((methodOrFieldName != null) ? "." : "") +
                  ((methodOrFieldName != null) ? methodOrFieldName : "");

        //Macros.message(view_, "Letter:  \"" + tagName);
      }
      else         // of form method()OrFieldName or ClassName
      {
        // method or field name
        int start = TextUtilities.findWordStart(lineText, lineIdx, "_");
        int end   = TextUtilities.findWordEnd(lineText, lineIdx + 1, "_");
        tagName = lineText.substring(start, end);

        //Macros.message(view_, "Letter:  " + tagName);
      }

    }
    else
    {
      return null;
    }

    return tagName;
  } //}}}

  //{{{ openDefinitionFile() method
  static protected boolean openDefinitionFile(View view, TagLine tagLine)
  {
    boolean opened = false;
    File tagFile = new File(tagLine.getDefinitionFileName());
    if (!tagFile.exists())
    {
      Object args[] = { tagFileName_ };
      GUIUtilities.error(view, "tag-def-file-not-found", args);
      opened = false;
    }
    else
    {
      jEdit.openFile(view, tagFile.getAbsolutePath());
      opened = true;
    }

    tagFile = null;
    return opened;
  } //}}}

}

// :collapseFolds=1:noTabs=true:lineSeparator=\r\n:tabSize=2:indentSize=2:deepIndent=false:folding=explicit:
