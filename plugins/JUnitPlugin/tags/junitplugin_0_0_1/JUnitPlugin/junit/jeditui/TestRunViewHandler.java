/*
 * TestRunViewHandler.java
 * Copyright (c) 2002 Calvin Yu
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

package junit.jeditui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;

/**
 * Handles popup for {@link TestRunView}s.
 */
public class TestRunViewHandler implements MouseListener
{

   private TestRunContext testRunContext;
   private TestRunView testRunView;

   /**
    * Create a new <code>TestRunViewHandler</code>.
    */
   public TestRunViewHandler(TestRunView aTestRunView,
                             TestRunContext aTestRunContext)
   {
      testRunView = aTestRunView;
      testRunContext = aTestRunContext;
   }

   /**
    * Mouse clicked.
    */
   public void mouseClicked(MouseEvent evt) {}

   /**
    * Mouse pressed.
    */
   public void mousePressed(MouseEvent evt) {}

   /**
    * Mouse released.
    */
   public void mouseReleased(MouseEvent evt)
   {
      System.out.println(">>> isPopupTrigger(): " + GUIUtilities.isPopupTrigger(evt));
      if (GUIUtilities.isPopupTrigger(evt)
          && testRunView.getSelectedTest() != null) {
         JPopupMenu popup = new JPopupMenu();
         JMenuItem menuItem = new JMenuItem(jEdit.getProperty("junit.test-run-view.popup.run.label"));
         menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
               testRunContext.runSelectedTest(testRunView.getSelectedTest());
            }
         });
         popup.add(menuItem);
         popup.show((Component) evt.getSource(), evt.getX(), evt.getY());
      }
   }

   /**
    * Mouse entered.
    */
   public void mouseEntered(MouseEvent evt) {}

   /**
    * Mouse exited.
    */
   public void mouseExited(MouseEvent evt) {}

}

