/*
 * DockablesList.java
 * :tabSize=3:indentSize=3:noTabs=true:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003 Calvin Yu
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

import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JList;

import org.gjt.sp.jedit.jEdit;

/**
 * A list of dockables.
 */
class DockablesList extends JList {
   /**
    * Create a new <code>DockablesList</code>.
    */
   public DockablesList() {
      setModel(new DockablesModel());
   }

   public void addDockable(String name) {
      ((DockablesModel) getModel()).addDockable(name);
   }

   public List getDockables() {
      return ((DockablesModel)getModel()).dockables;
   }

   private class DockablesModel extends AbstractListModel {
      private List dockables;

      public DockablesModel() {
         dockables = new LinkedList();
      }

      public int getSize() {
         return dockables.size();
      }

      public Object getElementAt(int idx) {
         return jEdit.getProperty(dockables.get(idx) + ".title");
      }

      public void addDockable(String name) {
         int idx = getSize();
         dockables.add(name);
         fireIntervalAdded(this, idx, idx);
      }
   }
}

