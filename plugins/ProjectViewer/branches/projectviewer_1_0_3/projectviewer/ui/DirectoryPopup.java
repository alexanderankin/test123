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

import projectviewer.ProjectDirectory;
import projectviewer.ui.actions.*;


/**
 * A popup for a {@link ProjectDirectory}s.
 */
public class DirectoryPopup extends Popup {

   private ProjectDirectory dir;

   /**
    * Create a new <code>DirectoryPopup</code>.
    */
   public DirectoryPopup(ProjectDirectory aDirectory, ProjectViewer viewer) {
      super(viewer);
      dir = aDirectory;
      initComponents();
   }

   /**
    * Initialize menu components.
    */
   private void initComponents() {
      add(new AddProjectFileAction(projectViewer, dir.getView()));
      add(new RemoveArtifactAction(projectViewer, dir));
   }

}
