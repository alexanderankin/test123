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

import javax.swing.JMenu;
import projectviewer.Project;
import projectviewer.ui.actions.*;


/**
 * A popup for a {@link Project}.
 */
public class ProjectPopup extends Popup
{

   private Project project;

   /**
    * Create a new <code>ProjectPopup</code>.
    */
   public ProjectPopup(Project aProject, ProjectViewer viewer)
   {
      super(viewer);
      project = aProject;
      initComponents();
   }

   /**
    * Initialize menu components.
    */
   private void initComponents()
   {
      JMenu addViewMenu = new JMenu("Add View");
      String[] viewNames = projectViewer.getPlugin().getViewManager().getViewNames();
      for (int i = 0; i < viewNames.length; i++)
         addViewMenu.add(new CreateViewAction(viewNames[i], projectViewer, project));
      add(addViewMenu);
      add(new SaveProjectAction(projectViewer, project));
      add(new RemoveProjectAction(projectViewer, project));
      addSeparator();
      add(new ImportProjectAction(projectViewer));
      add(new CreateProjectAction(projectViewer));
   }

}
