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
import javax.swing.*;
import projectviewer.*;
import projectviewer.views.ViewAction;


/**
 * A project viewer popup menu.
 */
public class Popup extends JPopupMenu {

   protected ProjectViewer projectViewer;
   
   /**
    * Create a new <code>Popup</code>.
    */
   public Popup(ProjectViewer aProjectViewer) {
      projectViewer = aProjectViewer;
   }

   /**
    * A view specific actions.
    */
   protected void addViewActions(ProjectArtifact targetArtifact) {
      Iterator i = targetArtifact.getView().getActions()
         .findActions(targetArtifact.getView()).iterator();
      if (i.hasNext()) addSeparator();
      while (i.hasNext()) {
         Action action = (Action) i.next();
         if (action instanceof ViewAction)
            ((ViewAction) action).setArtifact(targetArtifact);
         if (action instanceof ProjectAction)
            ((ProjectAction) action).setProjectViewer(projectViewer);
         add(action);
      }
   }

}
