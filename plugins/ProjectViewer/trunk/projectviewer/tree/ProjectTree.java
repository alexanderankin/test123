/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more detaProjectTreeSelectionListenerils.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package projectviewer.tree;

import java.awt.Cursor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.InputEvent;
import javax.swing.JTree;

import org.gjt.sp.util.Log;

import projectviewer.ProjectFile;


/**
 * A tree that can handle drag and drop.
 */
public class ProjectTree
  extends JTree
  implements DragGestureListener, DragSourceListener
{
  
  private DragSource dragSrc;
  
  /**
   * Create a new <code>ProjectTree</code>.
   */
  public ProjectTree() {
    super();
    putClientProperty("JTree.lineStyle", "Angled");
    dragSrc = new DragSource();
    
    DragGestureRecognizer dragRecognizer = dragSrc.createDefaultDragGestureRecognizer(
      this, DnDConstants.ACTION_COPY_OR_MOVE, this );
    dragRecognizer.setSourceActions(
      dragRecognizer.getSourceActions() & ~InputEvent.BUTTON3_MASK);
  }
  
  /**
   * Recognize a drag gesture.
   */
  public void dragGestureRecognized( DragGestureEvent evt ) {
    //Log.log( Log.DEBUG, this, "Received drag gesture" );
    Object node = getLastSelectedPathComponent();
    if ( !( node instanceof ProjectFile ) ) return;
    
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
  public void dragEnter(DragSourceDragEvent dsde) {
    //setCursor(dsde);
  }

  /**
   * Called as the hotspot moves over a platform dependent drop site.
   */
  public void dragOver(DragSourceDragEvent dsde) {
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
