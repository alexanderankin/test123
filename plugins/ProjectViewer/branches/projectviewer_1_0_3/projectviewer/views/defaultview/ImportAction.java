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
import java.util.*;
import org.gjt.sp.jedit.View;
import projectviewer.*;
import projectviewer.ui.*;
import projectviewer.views.ViewAction;


/**
 * An action to import files into this view.
 */
public class ImportAction extends ViewAction
{

   /**
    * Create a new <code>ImportAction</code>.
    */
   public ImportAction()
   {
      super("import-files");
   }

   /**
    * Import files to this view.
    */
   public static void importFiles(View view, DefaultView fileView) {
      List files = ImportPanel.showDialog(view, fileView.getProjectRoot());
      if (files == null) return;
      int total = 0;
      for (Iterator i = files.iterator(); i.hasNext();) {
         if (fileView.addProjectFile((File) i.next()) != null)
            total++;
      }
      UI.message(view, "import-completed",
         new String[] {Integer.toString(total), fileView.getProject().getName()});
   }

   /**
    * Perform the action.  Any {@link ProjectException}s thrown will be handled
    * appropriately.
    */
   protected void performAction() throws ProjectException
   {
      importFiles(getView(), (DefaultView) artifact.getView());
      projectViewer.getTreeModel().nodeStructureChanged(artifact);
   }

}
