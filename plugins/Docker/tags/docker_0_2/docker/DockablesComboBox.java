/*
 * DockablesComboBox.java
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

import java.util.Iterator;
import java.util.List;

import javax.swing.JComboBox;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.DockableWindowManager;

/**
 * A combo box of registered dockables.
 */
class DockablesComboBox extends JComboBox {
   /**
    * Create a new <code>DockablesComboBox</code>.
    */
   public DockablesComboBox() {
   }

   public void setDockables(List dockables) {
      removeAllItems();
      for (Iterator i = dockables.iterator(); i.hasNext();) {
         addItem(new DockableModel((String) i.next()));
      }
   }

   public String getSelectedDockableName() {
      int idx = getSelectedIndex();
      if (idx < 0) {
         return null;
      }
      return ((DockableModel) getSelectedItem()).name;
   }

   private class DockableModel {
      public String name;
      public DockableModel(String aName) {
         name = aName;
      }

      public String toString() {
         return jEdit.getProperty(name + ".title");
      }
   }
}

