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
package projectviewer.views;

import java.io.File;
import java.util.*;
import javax.swing.tree.TreeNode;
import projectviewer.*;
import projectviewer.ui.UI;

/**
 * An action for add project files into a view.
 */
public abstract class AddProjectFileAction extends ViewAction {

   /**
    * Create a new <code>AddProjectFileAction</code>.
    */
   public AddProjectFileAction() {
      super("add-files");
   }

   /**
    * Perform the action.
    */
   public void performAction() {
      String[] files = getFiles();
      if (files == null) return;

      ProjectFile file = null;
      FileView fileView = artifact.getView();
      Set changedParents = new HashSet(files.length);
      for (int i=0; i<files.length; i++) {
         file = addFileToView(new File(files[i]), fileView);
         if (file != null)
            changedParents.add(file.getParent());
      }

      for (Iterator i = changedParents.iterator(); i.hasNext();) {
         projectViewer.getTreeModel().nodeStructureChanged((TreeNode) i.next());
      }
      projectViewer.setTreeSelection(file);
   }

   /**
    * Add the given file object.
    */
   protected abstract ProjectFile addFileToView(File f, FileView fileView);

   /**
    * Returns the path to the directory where the file dialog should start at.
    */
   protected String getStartingPath() {
      return null;
   }

   /**
    * Returns a list of files for addition.
    */
   private String[] getFiles() {
      // TODO: Use Filter.  chooser.setFileFilter( new NonViewFileFilter(this) );
      //chooser.setAcceptAllFileFilterUsed(false); #JDK1.3
      String[] files = UI.getFiles(getView(), getStartingPath());
      return (files != null && files.length > 0) ? files : null;
   }

}
