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

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.tree.TreeNode;
import org.apache.commons.digester.Digester;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
import org.mobix.xml.*;
import org.xml.sax.SAXException;
import projectviewer.ui.*;
import projectviewer.ui.ActionMap;
import projectviewer.views.BaseView;


/**
 * A file view that displays files from a root directory.
 */
public class DefaultFileView extends BaseView
{
   private File root;

   /**
    * Create a new <code>DefaultFileView</code>.
    */
   public DefaultFileView()
   {
      super("Default");
      root = new File(System.getProperty("user.dir"));
   }

   /**
    * Returns <code>true</code> if the given file is a project file under this
    * view.
    *
    * <p>SPECIFIED ID: projectviewer.FileView</p>
    */
   public boolean isFileInView(File file)
   {
      ProjectDirectory parentDir = findDirectory(file.getParentFile());
      if (parentDir == null)
         return false;
      return parentDir.containsFile(file.getName());
   }

   /**
    * Find a project file for the given path, returning <code>null</code> if it
    * doesn't exist.
    */
   public ProjectFile findProjectFile(String path)
   {
      File file = new File(path);
      ProjectDirectory parent = findDirectory(file.getParentFile());
      if (parent == null)
         return null;
      return parent.getFile(file.getName());
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

      JFileChooser chooser = new JFileChooser();
      chooser.setDialogTitle("Select a root directory for this view");
      chooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );

      if (chooser.showOpenDialog(view) == JFileChooser.CANCEL_OPTION)
         return ;
      root = chooser.getSelectedFile();
      importFiles(view);
   }

   /**
    * Add a file to this view.
    *
    * <p>SPECIFIED ID: projectviewer.FileView</p>
    */
   public ProjectFile addProjectFile(View view, ProjectArtifact prjArtifact)
   {
      File startDir = (equals(prjArtifact)) ? root : ProjectArtifacts.getDirectory(prjArtifact);
      String[] files = UI.getFiles(view, startDir.getAbsolutePath() + "/");
      // TODO: Use Filter.  chooser.setFileFilter( new NonViewFileFilter(this) );
      //chooser.setAcceptAllFileFilterUsed(false); #JDK1.3
      if (files == null || files.length < 1) return null;
      ProjectFile firstFile = null;
      for (int i=0; i<files.length; i++) {
         ProjectFile eachFile = addProjectFile(new File(files[i]));
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
      actions.addFileViewAction(new AddCurrentBufferAction());
      actions.addFileViewAction(new SynchronizeAction());
      actions.addFileViewAction(new ImportAction());
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
      writeParamElement(xmlWrite, "root", root.getAbsolutePath());
      saveChildren(this, xmlWrite);
      xmlWrite.endElement("view");
   }

   /**
    * Load a parameter for this view.
    */
   public void setInitParameter(String name, String value)
   {
      if ("root".equals(name)) {
         root = new File(value);
      }
   }

   /**
    * Import files to this view.
    */
   protected void importFiles(Component c) {
      ImportPanel importPanel = new ImportPanel();
      Window win = SwingUtilities.getWindowAncestor(c);
      ViewConfigDialog dialog =
         new ViewConfigDialog((Frame) win, "Import File(s)", importPanel);
      UI.center(dialog);
      dialog.setVisible(true);
      if (dialog.isOk() && importPanel.isImportEnabled()) {
         List files = importPanel.getFiles(root);
         int total = 0;
         for (Iterator i = files.iterator(); i.hasNext();) {
            if (addProjectFile((File) i.next()) != null) total++;
         }
         JOptionPane.showMessageDialog( win,
                                        "Imported " + total + " file(s) into your project",
                                        "Import Successful",
                                        JOptionPane.INFORMATION_MESSAGE );
      }
   }

   /**
    * Returns the project root.
    */
   protected File getProjectRoot()
   {
      return root;
   }

   /**
    * Set the project root directory.
    */
   protected void setProjectRoot(File aRoot)
   {
      root = aRoot;
   }

   /**
    * Add a project file to this view.  This differs from {@link addFile(File)} in that
    * this method will build a directory structure that mirrors the file's physical
    * structure, starting from the view's root.
    */
   protected ProjectFile addProjectFile(File fileObj)
   {
      ProjectDirectory dir = ensureDirectory(fileObj.getParentFile());
      if (dir != null && !dir.containsFile(fileObj.getName()))
         return dir.addFile(fileObj);
      return null;
   }

   /**
    * Finds a {@link ProjectDirectory} that represents the given directory, returning
    * <code>null</code> if one doesn't exist.
    */
   protected ProjectDirectory findDirectory(File dir)
   {
      if (dir == null)
         return null;
      if (root.equals(dir))
         return this;

      ProjectDirectory parentDir = findDirectory(dir.getParentFile());
      if (parentDir == null)
         return null;
      ProjectDirectory targetDir = parentDir.getDirectory(dir.getName());
      return (targetDir == null) ? null : targetDir;
   }

   /**
    * Finds a the directory for the given path, building the directory structure
    * if necessary.
    */
   protected ProjectDirectory ensureDirectory(File dir)
   {
      if (dir == null)
         return null;
      if (root.equals(dir))
         return this;
      ProjectDirectory parentDir = ensureDirectory(dir.getParentFile());
      if (parentDir == null)
         return null;
      ProjectDirectory targetDir = parentDir.getDirectory(dir.getName());
      if (targetDir == null)
         targetDir = parentDir.addDirectory(dir);
      return targetDir;
   }

   /**
    * An action to add file in the current buffer to the project.
    */
   private class AddCurrentBufferAction extends ActionBase {

      /**
       * Create a new <code>ImportAction</code>.
       */
      public AddCurrentBufferAction() {
         super("Add Buffer To Project");
      }

      /**
       * Perform the action.  Any {@link ProjectException}s thrown will be handled
       * appropriately.
       */
      protected void performAction() throws ProjectException {
         ProjectFile file = addProjectFile(
            new File(projectViewer.getView().getBuffer().getPath()));
         if (file != null)
            projectViewer.getTreeModel().nodeStructureChanged(file.getParent());
      }

   }

   /**
    * An action to import files into this view.
    */
   private class ImportAction extends ActionBase {

      /**
       * Create a new <code>ImportAction</code>.
       */
      public ImportAction() {
         super("Import...");
      }

      /**
       * Perform the action.  Any {@link ProjectException}s thrown will be handled
       * appropriately.
       */
      protected void performAction() throws ProjectException {
         importFiles(projectViewer);
         projectViewer.getTreeModel().nodeStructureChanged(DefaultFileView.this);
      }

   }

   /**
    * An action to synchronize the files in this view with the actual file in
    * the file system.
    */
   private class SynchronizeAction extends ActionBase
   implements ArtifactTreeWalker.Evaluator
   {

      /**
       * Create a new <code>ActionBase</code>.
       */
      public SynchronizeAction() {
         super("Synchronize");
      }

      /**
       * Evaluate the visited artifact node.
       */
      public int evaluate(ProjectArtifact artifact) {
         if (artifact == DefaultFileView.this)
            return ArtifactTreeWalker.EVAL_CHILDREN;
         if (artifact instanceof ProjectFile) {
            ProjectFile file = (ProjectFile) artifact;
            if (!file.toFile().exists()) {
               removeProjectFile(file);
            }
         } else if (artifact instanceof ProjectDirectory) {
            ProjectDirectory dir = (ProjectDirectory) artifact;
            if (!new File(dir.getPath()).exists()) {
               removeProjectDirectory(dir);
               return ArtifactTreeWalker.SKIP_CHILDREN;
            }
         }
         return ArtifactTreeWalker.EVAL_CHILDREN;
      }

      /**
       * Perform the action.  Any {@link ProjectException}s thrown will be handled
       * appropriately.
       */
      protected void performAction() throws ProjectException {
         ArtifactTreeWalker walker = new ArtifactTreeWalker(DefaultFileView.this,
                                                            this);
         walker.walk();
         projectViewer.getTreeModel().nodeStructureChanged(DefaultFileView.this);
      }

   }

}
