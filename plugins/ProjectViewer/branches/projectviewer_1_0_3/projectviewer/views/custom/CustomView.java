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
import javax.swing.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
import org.mobix.xml.*;
import org.xml.sax.SAXException;
import projectviewer.*;
import projectviewer.ui.UI;
import projectviewer.ui.ActionMap;
import projectviewer.views.BaseView;


/**
 * A file view that displays files is a user customizable fashion.  The user
 * creates their own directories and add files, irrespective of their physical
 * filesystem location.
 */
public class CustomView extends BaseView
{

   /**
    * Create a new <code>CustomView</code>.
    */
   public CustomView()
   {
      super("Custom");
   }

   /**
    * Configure this view.
    *
    * <p>SPECIFIED ID: projectviewer.FileView</p>
    */
   public void config(View view, Project project)
   {
      String value = JOptionPane.showInputDialog(view, "Specify view name");
      if (value == null) return;
      setName(value);
   }

   /**
    * Add a file to this view.
    *
    * <p>SPECIFIED ID: projectviewer.FileView</p>
    */
   public ProjectFile addProjectFile(View view, ProjectArtifact prjArtifact)
   {
      String[] files = UI.getFiles(view, null);
      // TODO: Use Filter.  chooser.setFileFilter( new NonViewFileFilter(this) );
      //chooser.setAcceptAllFileFilterUsed(false); #JDK1.3
      if (files == null || files.length < 1)
         return null;
      ProjectDirectory parent = (prjArtifact instanceof ProjectFile) ?
         (ProjectDirectory) prjArtifact.getParent() :
         (ProjectDirectory) prjArtifact;
      ProjectFile firstFile = null;
      for (int i=0; i<files.length; i++) {
         ProjectFile eachFile = addFile(new File(files[i]));
         if (firstFile == null && eachFile != null)
            firstFile = eachFile;
      }
      return firstFile;
   }

   /**
    * Returns a map of actions that are available for artifacts of this view.
    * If <code>null</code> is returned, there are no additional actions.
    *
    * <p>SPECIFIED ID: projectviewer.FileView</p>
    */
   public ActionMap getActions()
   {
      ActionMap actions = new ActionMap();
      actions.addProjectDirectoryAction(new AddCurrentBufferAction());
      actions.addProjectDirectoryAction(new AddDirectoryAction());
      return actions;
   }

   /**
    * Save this view.
    *
    * <p>SPECIFIED ID: projectviewer.FileView</p>
    */
   public void save(XmlWriteContext xmlWrite) throws SAXException
   {
      xmlWrite.startElement("view", createViewXmlAttributes());
      saveChildren(this, xmlWrite);
      xmlWrite.endElement("view");
   }

}
