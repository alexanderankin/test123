/*
 * TagsPlugin.java
 * Copyright (c) 2001, 2002 Kenrick Drew (kdrew@earthlink.net)
 * Copyright (c) 2003 Ollie Rutherfurd (oliver@rutherfurd.net)
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
import java.awt.Toolkit;
import java.awt.Container;
import java.awt.Component;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.search.SearchAndReplace;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.util.Log;
//}}}

public class TagsPlugin extends EBPlugin
{

  //{{{ declarations
  private static TagFiles tagFiles;
  private static HashMap tagStacks;
  MouseHandler mouseHandler_;
  //}}}

  //{{{ start() method
  public void start()
  {
    mouseHandler_ = new MouseHandler();
    installMouseListener();
  } //}}}

  //{{{ stop()
  public void stop()
  {
    uninstallMouseListener();
    mouseHandler_ = null;
  } //}}}

  //{{{ installMouseListener() method
  protected void installMouseListener()
  {
    View view = jEdit.getFirstView();
    while(view != null)
    {
      setMouseMotionListener(view);
      EditPane[] editPanes = view.getEditPanes();
      for(int i=0; i < editPanes.length; i++)
      {
        setMouseMotionListener(editPanes[i].getTextArea());
      }
      view = view.getNext();
    }
  } //}}}

  //{{{ uninstallMouseListener() method
  public void uninstallMouseListener()
  {
    View view = jEdit.getFirstView();
    while(view != null)
    {
      removeMouseMotionListener(view);
      EditPane[] editPanes = view.getEditPanes();
      for(int i=0; i < editPanes.length; i++)
      {
        removeMouseMotionListener(editPanes[i].getTextArea());
      }
      view = view.getNext();
    }
  } //}}}

  //{{{ handleMessage() method
  public void handleMessage(EBMessage ebmsg)
  {
    if (ebmsg instanceof EditPaneUpdate)
    {
      EditPaneUpdate epu = (EditPaneUpdate) ebmsg;
      EditPane editPane = ((EditPaneUpdate)ebmsg).getEditPane();

      if (epu.getWhat() == EditPaneUpdate.CREATED)
      {
        setMouseMotionListener(editPane.getView());
        setMouseMotionListener(editPane.getTextArea());
      }
      epu = null;
      editPane = null;
    }
    if(ebmsg instanceof BufferUpdate)
    {
      // when a buffer is closed, we want
      // to release all reference to it
      // in the TagStackModels which contain
      // references to it
      BufferUpdate bu = (BufferUpdate)ebmsg;
      if(bu.getWhat() == BufferUpdate.CLOSED)
      {
        HashMap models = getTagStackModels();
        View[] views = jEdit.getViews();
        for(int i=0; i < views.length; i++)
        {
          TagStackModel model = (TagStackModel)models.get(views[i]);
          if(model != null)
          {
            model.releaseBuffer(bu.getBuffer());
          }
        }
      }
    }
    else if(ebmsg instanceof PropertiesChanged)
    {
      tagFiles = null;
    }
  } //}}}

  //{{{ setMouseMotionListener() method
  protected void setMouseMotionListener(Container container)
  {
    Component c;
    for (int i = 0; i < container.getComponentCount(); i++)
    {
      c = container.getComponent(i);
      c.addMouseMotionListener(mouseHandler_);
      if (c instanceof Container)
        setMouseMotionListener((Container) c);
    }
    c = null;
  } //}}}

  //{{{ removeMouseMotionListener() method
  protected void removeMouseMotionListener(Container container)
  {
    Component c;
    for(int i=0; i < container.getComponentCount(); i++)
    {
      c = container.getComponent(i);
      c.removeMouseMotionListener(mouseHandler_);
      if(c instanceof Container)
        removeMouseMotionListener((Container)c);
    }
  } //}}}

  //{{{ pushPosition() method
  public static void pushPosition(View view)
  {
    TagStackModel model = getTagStack(view);
    try
    {
      StackPosition pos = model.peek();
      String path = view.getBuffer().getPath();
      // ignore case in case closed and reopened 
      // w/different case on windows.
      if(path.equalsIgnoreCase(pos.getPath()))
      {
        if(view.getTextArea().getCaretLine()+1 == pos.getLineNumber())
        {
          Log.log(Log.DEBUG, TagsPlugin.class,
            "Not pushing duplicate position onto the stack.");
          return;
        }
      }
    }
    catch(EmptyStackException e)
    {
    }
    model.push(new StackPosition(view));
  } //}}}

  //{{{ popPosition() method
  public static void popPosition(View view)
  {
    TagStackModel model = getTagStack(view);
    try
    {
      StackPosition pos = null;
      pos = model.pop();
      /*
      * Below is a bit of hackery to make the Tag Stack
      * behave as if the current position is on the top
      * of the stack.  So, unless the top of the stack
      * is open at the current position, we go to that
      * position instead of popping it from the stack.
      */
      String path = view.getBuffer().getPath();
      if(path.equalsIgnoreCase(pos.getPath()))
      {
        if(view.getTextArea().getCaretLine()+1 == pos.getLineNumber())
        {
          try
          {
            pos = model.peek();
          }
          catch(EmptyStackException ese)
          {
            // stack is empty
            Log.log(Log.DEBUG, TagsPlugin.class,
              "Couldn't do stack popping trickery");  // ##
          }
        }
        else
        {
          // cursor is not at current position
          // so put position back on the stack
          model.push(pos);
        }
      }
      else
      {
        // current buffer is not position's buffer, so
        // put the position back on the stack.
        model.push(pos);
      }
      // move to position
      pos.goTo(view);
    }
    catch(EmptyStackException e)
    {
      Toolkit.getDefaultToolkit().beep();
    }
  } //}}}

  //{{{ clearStack() method
  public static void clearStack(View view)
  {
    TagStackModel model = getTagStack(view);
    model.removeAllElements();
  } //}}}

  //{{{ getTagFiles() method
  public static TagFiles getTagFiles()
  {
    if(tagFiles == null)
      tagFiles = new TagFiles();
    return tagFiles;
  } //}}}

  //{{{ getTagStack() method
  public static TagStackModel getTagStack(View view)
  {
    HashMap models = getTagStackModels();
    TagStackModel model = (TagStackModel)models.get(view);
    if(model == null)
    {
      model = new TagStackModel();
      models.put(view,model);
    }
    return model;
  } //}}}

  //{{{ getTagStackModels() method
  private static HashMap getTagStackModels()
  {
    if(tagStacks == null)
      tagStacks = new HashMap();
    return tagStacks;
  } //}}}

}

// :collapseFolds=1:noTabs=true:lineSeparator=\r\n:tabSize=2:indentSize=2:deepIndent=false:folding=explicit:
