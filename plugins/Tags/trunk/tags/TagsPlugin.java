/*
 * TagsPlugin.java
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
 */

package tags;

import java.io.*;
import java.lang.System.*;
import java.util.*;
import java.util.Vector;
import java.awt.Toolkit;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.search.SearchAndReplace;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.util.Log;

import gnu.regexp.*;

public class TagsPlugin extends EBPlugin 
{

  /***************************************************************************/
  public static final String NAME = "tags";
  public static final String MENU = "tags.menu";
  public static final String PROPERTY_PREFIX = "plugin.TagsPlugin.";
  
  protected static boolean debug_ = false;

  /***************************************************************************/
  MouseHandler mouseHandler_;
  
  /*+*************************************************************************/
  public void start() 
  {
    super.start();
    
    Tags.init();
  }
  
  /*+*************************************************************************/
  public void stop() 
  {
    super.stop();
    
    Tags.writeTagFiles();
  }
  
  /*+*************************************************************************/
  public void createMenuItems(Vector menuItems) {
    menuItems.addElement(GUIUtilities.loadMenu(MENU));
  }
  
  /***************************************************************************/
  public void createOptionPanes(OptionsDialog od) {
    od.addOptionPane(new TagsOptionsPanel());
  }

  /***************************************************************************/
  // Initially ripped from BufferTabsPlugin.handleMessage()...
  public void handleMessage(EBMessage ebmsg) 
  {
    if (ebmsg instanceof EditPaneUpdate) 
    {
      EditPaneUpdate epu = (EditPaneUpdate) ebmsg;
      EditPane editPane = ((EditPaneUpdate)ebmsg).getEditPane();
      JEditTextArea textArea = editPane.getTextArea();
      
      if (epu.getWhat() == EditPaneUpdate.CREATED) 
      {
        if (mouseHandler_ == null)
          mouseHandler_ = new MouseHandler();
        
        textArea.getPainter().addMouseListener(mouseHandler_);
        textArea.getPainter().addMouseMotionListener(mouseHandler_);
        
        Log.log(Log.DEBUG, this, "Edit pane created.  Added listener.");
      }
      else if (epu.getWhat() == EditPaneUpdate.DESTROYED)
      {
        Log.log(Log.DEBUG, this, "Edit pane destroyed");
        
        // We should play nice and remove the mouse handler from the text area
      }
      epu = null;
      editPane = null;
      textArea = null;
    }
  }
}

