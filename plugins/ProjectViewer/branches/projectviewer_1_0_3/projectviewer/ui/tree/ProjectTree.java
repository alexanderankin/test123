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

import java.awt.Cursor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.MouseEvent;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.util.Log;
import projectviewer.ProjectFile;


/**
 * A tree that can handle drag and drop.
 */
public class ProjectTree extends JTree
implements DragGestureListener, DragSourceListener
{

   private DragSource dragSrc;

   /**
    * Create a new <code>ProjectTree</code>.
    */
   public ProjectTree(TreeModel model)
   {
      super(model);
      putClientProperty("JTree.lineStyle", "Angled");
      setCellRenderer(new ProjectTreeRenderer());
      setRootVisible(false);
      DragSource.getDefaultDragSource()
         .createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, this);
   }

   /**
    * Recognize a drag gesture.
    */
   public void dragGestureRecognized( DragGestureEvent evt )
   {
      Object node = getLastSelectedPathComponent();
      if ( !( node instanceof ProjectFile ) )
         return;
      if (evt.getTriggerEvent() instanceof MouseEvent &&
         GUIUtilities.isPopupTrigger((MouseEvent) evt.getTriggerEvent())) {
         return;
      }
      evt.getDragSource().startDrag( evt,
                                     DragSource.DefaultMoveNoDrop,
                                     (Transferable) node,
                                     this );
   }

   /**
    * This method is invoked to signify that the Drag and Drop
    * operation is complete.
    */
   public void dragDropEnd(DragSourceDropEvent dsde) {}

   /**
    * Called as the hotspot enters a platform dependent drop site.
    */
   public void dragEnter(DragSourceDragEvent dsde)
   {
      //setCursor(dsde);
   }

   /**
    * Called as the hotspot moves over a platform dependent drop site.
    */
   public void dragOver(DragSourceDragEvent dsde)
   {
      //setCursor(dsde);
   }

   /**
    * Called when the user has modified the drop gesture.
    */
   public void dropActionChanged(DragSourceDragEvent dsde) {}

   /**
    * Called as the hotspot exits a platform dependent drop site.
    */
   public void dragExit(DragSourceEvent dsde) {}

}
