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
package projectviewer.ui;

import java.util.Iterator;
import javax.swing.tree.TreeNode;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.BufferUpdate;
import projectviewer.*;


/**
 * An <code>EBComponent</code> that listens for jEdit events and updates the
 * the node in the tree accordingly.
 */
public class EditBusListener implements EBComponent {

   private ProjectViewer projectViewer;


   /**
    * An listener for <code>EditBus</code> events.
    */
   public EditBusListener(ProjectViewer aProjectViewer) {
      projectViewer = aProjectViewer;
   }

   /**
    * Start the listener.
    */
   public void start() {
      EditBus.addToBus( this );
   }
   
   /**
    * Stop the listener.
    */
   public void stop() {
      EditBus.removeFromBus( this );
   }

  /**
   * Handle any buffer updates or closes.
   */
  public void handleMessage( EBMessage message ) {
    if ( !(message instanceof BufferUpdate) ) return;
    BufferUpdate update = (BufferUpdate) message;
    
    if ( update.getWhat().equals(BufferUpdate.LOADED) )
       bufferLoaded(update.getBuffer());
    else if ( update.getWhat().equals(BufferUpdate.CLOSED) )
       bufferClosed(update.getBuffer());
  }
  
   /**
    * Handle a buffer loaded event.
    */
   protected void bufferLoaded(Buffer buffer) {
      updateFileNodes(buffer);
   }

   /**
    * Handle a buffer closed event.
    */
   protected void bufferClosed(Buffer buffer) {
      updateFileNodes(buffer);
   }

   /**
    * Update any project file nodes represented by this buffer.
    */
   private void updateFileNodes(Buffer buffer) {
      Project prj = projectViewer.getCurrentProject();
      for (Iterator i = prj.views(); i.hasNext();) {
         FileView each = (FileView) i.next();
         ProjectFile file = each.findProjectFile(buffer.getPath());
         if (file != null) {
            updateNode(file);
         }
      }
   }

   /**
    * Update the given node.
    */
   private void updateNode(TreeNode node) {
      projectViewer.getTreeModel().nodeChanged(node);
   }

}
