/*
 * ViewHandler.java
 * :tabSize=3:indentSize=3:noTabs=true:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2001, 2002 Calvin Yu
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
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

package docker;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.*;

import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.gui.PanelWindowContainer;

/**
 * Handles docking for a given view.
 */
public class ViewHandler implements FocusListener
{

   private View view;
   private Map docks;
   private Set editPanes;

   /**
    * Create a new <code>ViewHandler</code>
    */
   public ViewHandler(View aView)
   {
      view = aView;
      docks = new HashMap(4);
      editPanes = new HashSet(2);
      init();
   }

   /**
    * Initialize this handler.
    */
   public void init()
   {
      docks.clear();
      attachDock(DockableWindowManager.TOP);
      attachDock(DockableWindowManager.LEFT);
      attachDock(DockableWindowManager.BOTTOM);
      attachDock(DockableWindowManager.RIGHT);
      EditPane[] editPanes = view.getEditPanes();
      for (int i=0; i<editPanes.length; i++) {
         editPaneCreated(editPanes[i]);
      }
   }

   /**
    * Detach this handler from the view.
    */
   public void detach()
   {
      for (Iterator i = docks.values().iterator(); i.hasNext();) {
         ((DockHandler) i.next()).detach();
      }
      EditPane[] editPanes = view.getEditPanes();
      for (int i=0; i<editPanes.length; i++) {
         editPaneDestroyed(editPanes[i]);
      }
   }

   /**
    * Handle an edit pane creation message.
    */
   public void editPaneCreated(EditPane editPane)
   {
      editPane.getTextArea().removeFocusListener(this);
      editPane.getTextArea().addFocusListener(this);
      editPanes.add(editPane);
   }

   /**
    * Handle an edit pane destruction message.
    */
    public void editPaneDestroyed(EditPane editPane)
    {
       editPane.getTextArea().removeFocusListener(this);
       editPanes.remove(editPane);
    }

   // {{{ FocusListener Methods
   /**
    * Handle a focus gained event.
    */
   public final void focusGained(FocusEvent evt)
   {
      for (Iterator i = docks.values().iterator(); i.hasNext();) {
         ((DockHandler) i.next()).collapse();
      }
   }

   /**
    * Handle a focus lost event.
    */
   public final void focusLost(FocusEvent evt) {}
   // }}}

   /**
    * Attach a handler to a dock.
    */
   private void attachDock(String dockName)
   {
      if (DockerPlugin.getPlugin().isEnabled(dockName)) {
         int compIdx = 0;
         PanelWindowContainer container = null;
         if (DockableWindowManager.TOP.equals(dockName)) {
            container = view.getDockableWindowManager().getTopDockingArea();
            compIdx = 4;
         } else if (DockableWindowManager.LEFT.equals(dockName)) {
            container = view.getDockableWindowManager().getLeftDockingArea();
            compIdx = 5;
         } else if (DockableWindowManager.BOTTOM.equals(dockName)) {
            container = view.getDockableWindowManager().getBottomDockingArea();
            compIdx = 6;
         } else if (DockableWindowManager.RIGHT.equals(dockName)) {
            container = view.getDockableWindowManager().getRightDockingArea();
            compIdx = 7;
         }
         docks.put(dockName, new DockHandler(dockName, container,
            view.getDockableWindowManager().getComponent(compIdx)));
      }
   }

}

