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
import java.util.Iterator;
import projectviewer.*;
import projectviewer.digester.*;
import org.apache.commons.digester.Digester;
import org.gjt.sp.jedit.View;
import org.mobix.xml.*;
import org.xml.sax.SAXException;


/**
 * A {@link FileView} base class.
 */
public abstract class BaseView extends ProjectDirectory
implements FileView
{

   /**
    * Create a new <code>BaseView</code>.
    */
   public BaseView(String aName) {
      super(aName, null, null);
   }

   /**
    * Set the {@link Project} which owns this view.
    *
    * <p>SPECIFIED ID: projectviewer.FileView</p>
    */
   public void setProject(Project aProject)
   {
      parent = aProject;
   }

   /**
    * Returns the project.
    */
   public Project getProject() {
      return (Project) getParent();
   }

   /**
    * Remove an artifact.
    *
    * <p>SPECIFIED ID: projectviewer.FileView</p>
    */
   public void removeProjectArtifact(View view, ProjectArtifact artifact)
   {
      if (artifact instanceof ProjectFile)
         removeProjectFile((ProjectFile) artifact);
      else if (artifact == this)
         getProject().remove(this);
      else if (artifact instanceof ProjectDirectory)
         removeProjectDirectory((ProjectDirectory) artifact);
   }

   /**
    * Returns <code>true</code> if the given file is a project file under this
    * view.
    *
    * <p>SPECIFIED ID: projectviewer.FileView</p>
    */
   public boolean isFileInView(File file)
   {
      FileInViewEvaluator evaluator = new FileInViewEvaluator(file);
      new ArtifactTreeWalker(
         this, evaluator, ArtifactTreeWalker.FILES_ONLY).walk();
      return evaluator.isFileInView();
   }

   /**
    * Find a project file for the given path, returning <code>null</code> if it
    * doesn't exist.
    */
   public ProjectFile findProjectFile(String path)
   {
      ProjectFileFinder finder = new ProjectFileFinder(path);
      new ArtifactTreeWalker(
         this, finder, ArtifactTreeWalker.FILES_ONLY).walk();
      return finder.getProjectFile();
   }

   /**
    * Load a initialization parameter for this view.
    *
    * <p>SPECIFIED ID: projectviewer.FileView</p>
    */
   public void setInitParameter(String name, String value) {}

   /**
    * Initialize this digester to load data into this project.
    *
    * <p>SPECIFIED ID: projectviewer.FileView</p>
    */
   public void initDigester(Digester digester)
   {
      digester.addRule("*/view/param", new LoadParamRule(digester)); 
      digester.addCallMethod("*/file", "addFile", 2, new Class[] {String.class, String.class});
      digester.addCallParam("*/file", 0, "name");
      digester.addCallParam("*/file", 1, "path");
      digester.addRule("*/dir", new CreateDirectoryRule(digester));
   }

   /**
    * Convert this object to an object.
    */
   public String toString()
   {
      return getName();
   }

   /**
    * Remove a project file.
    */
   protected void removeProjectFile(ProjectFile file)
   {
      ((ProjectDirectory) file.getParent()).removeFile(file);
   }

   /**
    * Remove a project directory.
    */
   protected void removeProjectDirectory(ProjectDirectory dir)
   {
      ((ProjectDirectory) dir.getParent()).removeDirectory(dir);
   }

   /**
    * Create the mandatory attributes for a {@link FileView} element.
    *
    * @see FileView#save(XmlWriteContext).
    */
   protected SimpleAttributes createViewXmlAttributes() {
      SimpleAttributes atts = new SimpleAttributes("name", getName());
      atts.addAttribute("type", getClass().getName());
      return atts;
   }

   /**
    * Save the children of the given directory.
    */
   protected static void saveChildren(ProjectDirectory dir, XmlWriteContext xmlWrite)
   throws SAXException
   {
      for (Iterator i = dir.directories(); i.hasNext();)
         save((ProjectDirectory) i.next(), xmlWrite);
      for (Iterator i = dir.files(); i.hasNext();)
         save((ProjectFile) i.next(), xmlWrite);
   }

   /**
    * Write a param element to the given xml.
    */
   protected static void writeParamElement(XmlWriteContext xmlWrite, String name,
                                           String value)
   throws SAXException
   {
      SimpleAttributes atts = new SimpleAttributes("name", name);
      atts.addAttribute("value", value);
      xmlWrite.writeElement("param", atts);
   }

   /**
    * Save the given project file.
    */
   private static void save(ProjectFile file, XmlWriteContext xmlWrite)
   throws SAXException
   {
      SimpleAttributes atts = new SimpleAttributes("name", file.getName());
      atts.addAttribute("path", file.getPath());
      xmlWrite.writeElement("file", atts);
   }

   /**
    * Save the given project directory.
    */
   private static void save(ProjectDirectory dir, XmlWriteContext xmlWrite)
   throws SAXException
   {
      SimpleAttributes atts = new SimpleAttributes("name", dir.getName());
      atts.addAttribute("path", dir.getPath());
      xmlWrite.startElement("dir", atts);
      saveChildren(dir, xmlWrite);
      xmlWrite.endElement("dir");
   }

   /**
    * Evaluates if a file is in this view.
    */
   private class FileInViewEvaluator
   implements ArtifactTreeWalker.Evaluator
   {

      private File file;
      private boolean fileInView;

      /**
       * Create a new <code>FileInViewEvaluator</code>.
       */
      public FileInViewEvaluator(File aFile) {
         file = aFile;
         fileInView = false;
      }

      /**
       * Returns <code>true</code> if the file is in the view.
       */
      public boolean isFileInView() {
         return fileInView;
      }

      /**
       * Evaluate a file.
       */
      public int evaluate(ProjectArtifact node) {
         if (((ProjectFile) node).pathEquals(file.getAbsolutePath())) {
            fileInView = true;
            return ArtifactTreeWalker.ABORT;
         }
         return ArtifactTreeWalker.EVAL_CHILDREN;
      }

   }

   /**
    * Locates a file in the given view.
    */
   private class ProjectFileFinder
   implements ArtifactTreeWalker.Evaluator
   {

      private String path;
      private ProjectFile file;

      /**
       * Create a new <code>ProjectFileFinder</code>.
       */
      public ProjectFileFinder(String aPath) {
         path = aPath;
      }

      /**
       * Returns the project file if found.
       */
      public ProjectFile getProjectFile() {
         return file;
      }

      /**
       * Evaluate a file.
       */
      public int evaluate(ProjectArtifact node) {
         if (((ProjectFile) node).pathEquals(path)) {
            file = (ProjectFile) node;
            return ArtifactTreeWalker.ABORT;
         }
         return ArtifactTreeWalker.EVAL_CHILDREN;
      }

   }
}
