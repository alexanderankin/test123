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
package projectviewer.ui.actions;

import org.gjt.sp.util.Log;
import projectviewer.*;
import projectviewer.ui.ProjectViewer;


/**
 * An action to save project data.
 */
public class SaveProjectAction extends ActionBase {

   private Project project;

   /**
    * Create a new <code>SaveProjectAction</code>.
    */
   public SaveProjectAction(ProjectViewer aViewer, Project aProject) {
      super("save-project", aViewer);
      project = aProject;
   }

   /**
    * Save a project.
    */
   protected void performAction() throws ProjectException {
      getProjectManager().save(project);
   }

}
