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
package projectviewer.views.custom;

import java.io.File;
import projectviewer.*;
import projectviewer.views.ViewAction;


/**
 * An action to add file in the current buffer to the project.
 */
class AddCurrentBufferAction extends ViewAction {

   /**
    * Create a new <code>AddCurrentBufferAction</code>.
    */
   public AddCurrentBufferAction() {
      super("Add Buffer To Project");
   }

   /**
    * Perform the action.  Any {@link ProjectException}s thrown will be handled
    * appropriately.
    */
   protected void performAction() throws ProjectException {
      ProjectDirectory dir = (ProjectDirectory) artifact;
      ProjectFile file = dir.addFile(new File(getView().getBuffer().getPath()));
      if (file != null)
         projectViewer.getTreeModel().nodeStructureChanged(file.getParent());
   }

}


