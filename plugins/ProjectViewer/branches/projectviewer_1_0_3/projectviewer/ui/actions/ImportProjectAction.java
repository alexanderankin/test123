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

import java.io.File;
import javax.swing.JOptionPane;
import org.gjt.sp.util.Log;
import projectviewer.*;
import projectviewer.ui.*;


/**
 * An action to import a project.
 */
public class ImportProjectAction extends ActionBase {

   /**
    * Create a new <code>ImportProjectAction</code>.
    */
   public ImportProjectAction(ProjectViewer aProjectViewer) {
      super("import-project", aProjectViewer);
   }

   /**
    * Open up a project.
    */
   protected void performAction() throws ProjectException {
      File file = UI.getFile(projectViewer.getView(),
                             ProjectPlugin.getPluginHome() + "/");
      if (file == null) return;
      Project prj = getProjectManager().loadProject(file);
      if (getProjectManager().hasProject(prj.getName())) {
         boolean result = UI.confirmYesNo(projectViewer, "overwrite-project",
                                          new Object[] { prj.getName() });
         if (!result) return;
         getProjectManager().remove(prj.getName());
      }
      getProjectManager().addProject(prj);
      projectViewer.getTreeModel().load();
      projectViewer.setProject(prj);
   }

}
