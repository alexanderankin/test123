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
package projectviewer.ui.tree;

import java.awt.event.*;
import javax.swing.JPopupMenu;
import javax.swing.tree.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
import projectviewer.*;
import projectviewer.ui.*;


/**
 * A <code>java.awt.event.MouseListener</code> for listening to the tree.
 */
public class ProjectTreeMouseListener implements MouseListener {

   private ProjectViewer projectViewer;

   /**
    * Create a new <code>ProjecTreeMouseListener</code>.
    */
   public ProjectTreeMouseListener(ProjectViewer aProjectViewer) {
      projectViewer = aProjectViewer;
   }

   /**
    * Receive notification that the mouse was clicked.
    */
   public void mouseClicked(MouseEvent evt) {
      if (!isFileClicked(evt)) return ;
      if (evt.getClickCount() != 2) return ;
      ProjectFile file = (ProjectFile) getTarget(evt);

      if ( !file.isOpened() ) {
         openFile( file );
         return ;
      }
      if ( isInCurrentBuffer(file) ) {
         closeFile(file);
      } else {
         showFile(file);
      }
   }

   /**
    * Receive notification that the mouse was pressed.
    */
   public void mousePressed(MouseEvent evt) {
      if (!GUIUtilities.isPopupTrigger(evt)) return;

      TreeNode target = getTarget(evt);
      projectViewer.setTreeSelection(target);
      if (target instanceof Project) {
         showPopup(new ProjectPopup((Project) target, projectViewer), evt);
      } else if (target instanceof ProjectProxyNode) {
         showPopup(new ProjectPopup(((ProjectProxyNode) target).getProxy(), projectViewer), evt);
      } else if (target instanceof FileView) {
         showPopup(new ViewPopup((FileView) target, projectViewer), evt);
      } else if (target instanceof ProjectDirectory) {
         showPopup(new DirectoryPopup((ProjectDirectory) target, projectViewer), evt);
      } else if (target instanceof ProjectFile) {
         showPopup(new FilePopup((ProjectFile) target, projectViewer), evt);
      } else if (target == null) {
         showPopup(new DefaultPopup(projectViewer), evt);
      }
   }

   /**
    * Receive notification that the mouse was released.
    */
   public void mouseReleased(MouseEvent evt) {}

   /**
    * Receive notification that the mouse entered a component.
    */
   public void mouseEntered(MouseEvent evt) {}

   /**
    * Receive notification that the mouse exited a component.
    */
   public void mouseExited(MouseEvent evt) {}

   /**
    * Takes a given file and highlights it in the current view.
    */
   private void showFile(ProjectFile file) {
      projectViewer.getView().setBuffer( file.getBuffer() );
   }

   /**
    * Takes a file and opens it up in jEdit.
    */
   public void openFile( ProjectFile file ) {
      View view = projectViewer.getView();
      view.setBuffer(jEdit.openFile(view, null, file.getPath(), false, false));
   }

   /**
    * Close the specified project file.
    */
   private void closeFile( ProjectFile file ) {
      jEdit.closeBuffer( projectViewer.getView(), file.getBuffer() );
   }

   /**
    * Returns <code>true</code> if the given file is in the current buffer.
    */
   private boolean isInCurrentBuffer( ProjectFile file ) {
      return file.pathEquals( projectViewer.getView().getBuffer().getPath() );
   }

   /**
    * Returns <code>true</code>.
    */
   private boolean isFileClicked(MouseEvent evt) {
      return (getTarget(evt) instanceof ProjectFile);
   }

   /**
    * Returns the object that this target of the given mouse event.
    */
   private TreeNode getTarget(MouseEvent evt) {
      TreePath path = projectViewer.getProjectTree().getPathForLocation(evt.getX(), evt.getY());
      return (path == null) ? null : (TreeNode) path.getLastPathComponent();
   }

   /**
    * Show a popup for the given mouse event.
    */
   private void showPopup(JPopupMenu popup, MouseEvent evt) {
      popup.show(projectViewer.getProjectTree(), evt.getX(), evt.getY());
   }

}
