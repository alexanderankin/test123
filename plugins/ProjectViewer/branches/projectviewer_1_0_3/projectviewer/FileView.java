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
package projectviewer;

import java.io.File;
import java.util.*;
import org.gjt.sp.jedit.View;
import org.apache.commons.digester.Digester;
import org.mobix.xml.XmlWriteContext;
import org.xml.sax.SAXException;
import projectviewer.ui.ActionMap;


/**
 * A view to project files.
 */
public interface FileView extends ProjectArtifact
{

   /**
    * Sets the name of the view.
    */
   public void setName(String aName);

   /**
    * Set the {@link Project} which owns this view.
    */
   public void setProject(Project aProject);

   /**
    * Returns <code>true</code> if the given file is a project file under this
    * view.
    */
   public boolean isFileInView(File file);

   /**
    * Find a project file, returning <code>null</code> if it doesn't exist.
    */
   public ProjectFile findProjectFile(String path);

   /**
    * Returns a iteration of project files.
    */
   public Iterator files();

   /**
    * Returns a iteration of project directories.
    */
   public Iterator directories();

   /**
    * Returns a map of actions that are available for artifacts of this view.
    * If <code>null</code> is returned, there are no additional actions.
    */
   public ActionMap getActions();

   /**
    * Add a project file.  This method should render any GUI that it needs to
    * allow the user to select an eligible project file.
    *
    * @param view The jEdit view that is requesting the file addition.
    * @param prjArtifact The {@link ProjectArtifact} that the new {@link ProjectFile}
    * should be created.  How is value is used is view dependent.  For example,
    * {@link DefaultFileView} uses this value to display the starting directory of
    * the file chooser.
    */
   public ProjectFile addProjectFile(View view, ProjectArtifact artifact);

   /**
    * Remove a project artifact.  This method should render any GUI that it needs
    * to allow the user to remove the given artifact.
    */
   public void removeProjectArtifact(View view, ProjectArtifact artifact);

   /**
    * Configure this view.  This method should render any GUI that it needs
    * inorder to configure this view.
    */
   public void config(View view, Project project);

   /**
    * Save the view configuration.
    */
   public void save(XmlWriteContext xmlWrite) throws SAXException;

   /**
    * Set a view property.  This is used during the project loading phase so
    * that projects can be loaded in a generic way.
    */
   public void setInitParameter(String name, String value);

   /**
    * Initialize this digester to load data into this project.
    */
   public void initDigester(Digester digester);

}
