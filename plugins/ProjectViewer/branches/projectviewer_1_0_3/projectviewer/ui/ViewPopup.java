/*
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
package projectviewer.ui;

import java.util.Iterator;
import javax.swing.*;
import projectviewer.*;
import projectviewer.ui.ActionMap;
import projectviewer.ui.actions.*;


/**
 * A popup for a {@link FileView}s.
 */
public class ViewPopup extends Popup {

   private FileView view;

   /**
    * Create a new <code>ViewPopup</code>.
    */
   public ViewPopup(FileView aView, ProjectViewer viewer) {
      super(viewer);
      view = aView;
      initComponents();
   }

   /**
    * Initialize menu components.
    */
   private void initComponents() {
      add(new AddProjectFileAction(projectViewer, view));
      add(new RemoveArtifactAction(projectViewer, view));

      Iterator i = view.getActions().findActions(view).iterator();
      if (i.hasNext()) addSeparator();
      while (i.hasNext()) {
         Action action = (Action) i.next();
         if (action instanceof ProjectAction) {
            ((ProjectAction) action).setProjectViewer(projectViewer);
         }
         add(action);
      }
   }

}
