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
package projectviewer.views.defaultview;

import java.io.File;
import projectviewer.*;


/**
 * An action for and project files.
 */
public class AddProjectFileAction
extends projectviewer.views.AddProjectFileAction
{

   /**
    * Add a project file.
    */
   protected ProjectFile addFileToView(File f, FileView fileView) {
      return ((DefaultView) fileView).addProjectFile(f);
   }

   /**
    * Returns the starting path based on the context project artifact.
    */
   protected String getStartingPath() {
      DefaultView defView = (DefaultView) artifact.getView();
      File startDir = (defView.equals(artifact)) ?
         defView.getProjectRoot() : ProjectArtifacts.getDirectory(artifact);
      return startDir.getAbsolutePath() + "/";
   }

}
