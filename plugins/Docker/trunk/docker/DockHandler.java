/*
 * DockerPlugin.java
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

import java.awt.Component;

import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.gui.PanelWindowContainer;

/**
 * A handler for handling the collapsing of a particular dock.
 */
public class DockHandler {
   private String dockName;
   private boolean visible;
   private DockableWindowManager wm;
   private PanelWindowContainer dock;
   private DockerConfig config;

   /**
    * Create a new <code>DockHandler</code>.
    */
   public DockHandler(String aDockName,
                      DockableWindowManager aWm,
                      PanelWindowContainer aDock,
                      DockerConfig aConfig) {
      dockName = aDockName;
      wm = aWm;
      dock = aDock;
      visible = false;
      config = aConfig;
   }

   /**
    * Detach this handler from the dock.
    */
   public void detach() {
   }

   public void autoHide() {
      if (config.isAutoHideEnabled(dockName)) {
         String dockable = getVisibleDockable();
         if (dockable != null && !config.isAutoHideOverride(dockable)) {
            collapse();
         }
      }
   }

   /**
    * Collapse this dock.
    */
   public void collapse() {
      dock.show(null);
   }

   public void saveDockState() {
      visible = dock.getCurrent() != null;
   }

   public void restoreDockState() {
      if (visible) {
         dock.showMostRecent();
      } else {
         dock.show(null);
      }
   }

   public boolean isDockVisible() {
      return dock.getCurrent() != null;
   }

   private String getVisibleDockable() {
      String[] dockables = dock.getDockables();
      for (int i=0; i<dockables.length; i++) {
         if (wm.isDockableWindowVisible(dockables[i])) {
            return dockables[i];
         }
      }
      return null;
   }
}
