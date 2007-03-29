/*
 * DockerFocusTraversalPolicy.java
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

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Window;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.text.JTextComponent;

import org.gjt.sp.util.Log;


class DockerFocusTraversalPolicy extends FocusTraversalPolicy {
   private FocusTraversalPolicy delegate;

   public DockerFocusTraversalPolicy(FocusTraversalPolicy aDelegate) {
      delegate = aDelegate;
   }

   public Component getComponentAfter(Container root, Component c) {
      return delegate.getComponentAfter(root, c);
   }

   public Component getComponentBefore(Container root, Component c) {
      return delegate.getComponentBefore(root, c);
   }

   public Component getDefaultComponent(Container root) {
      Log.log(Log.DEBUG, this, "Finding default component: " + root);
      if (isInDock(root)) {
         Component c = findDefaultComponent(root);
         if (c != null)
            return c;
      }
      return delegate.getDefaultComponent(root);
   }

   public Component getFirstComponent(Container root) {
      return delegate.getFirstComponent(root);
   }

   public Component getInitialComponent(Window win) {
      return delegate.getInitialComponent(win);
   }

   public Component getLastComponent(Container root) {
      return delegate.getLastComponent(root);
   }

   private boolean isInDock(Container root) {
      if (root == null)
         return false;
      if ("org.gjt.sp.jedit.gui.PanelWindowContainer$DockablePanel".equals(root.getClass().getName()))
         return true;
      return isInDock(root.getParent());
   }

   private Component findDefaultComponent(Container root) {
      for (int i=0; i<root.getComponentCount(); i++) {
         Component c = root.getComponent(i);
         if (isFocusableComponent(c)) {
            Log.log(Log.DEBUG, this, "found focusable component: " + c);
            return c;
         }
         if (c instanceof Container) {
            c = findDefaultComponent((Container) c);
            if (c != null) {
               return c;
            }
         }
      }
      return null;
   }

   private boolean isFocusableComponent(Component c) {
      return c instanceof JTextComponent
         || c instanceof JComboBox
         || c instanceof JList
         || c instanceof JTable
         || c instanceof JTree;
   }
}

