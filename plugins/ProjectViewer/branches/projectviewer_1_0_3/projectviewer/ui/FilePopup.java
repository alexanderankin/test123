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

import projectviewer.ProjectFile;
import projectviewer.ui.actions.RemoveArtifactAction;


/**
 * A popup for a {@link ProjectFile}s.
 */
public class FilePopup extends Popup {

   private ProjectFile file;

   /**
    * Create a new <code>FilePopup</code>.
    */
   public FilePopup(ProjectFile aFile, ProjectViewer viewer) {
      super(viewer);
      file = aFile;
      initComponents();
   }

   /**
    * Initialize menu components.
    */
   private void initComponents() {
      add(new RemoveArtifactAction(projectViewer, file));
      addViewActions(file);
   }

}
