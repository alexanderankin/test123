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
package projectviewer.event;

import java.util.EventObject;
import projectviewer.Project;
import projectviewer.ui.ProjectViewer;


/**
 * A project viewer event.
 */
public class ProjectViewerEvent
         extends EventObject
{

   private Project project;

   /**
    * Create a new <code>ProjectViewerEvent</code>.
    */
   public ProjectViewerEvent( ProjectViewer src, Project prj )
   {
      super( src );
      project = prj;
   }

   /**
    * Returns the {@link ProjectViewer}.
    */
   public ProjectViewer getProjectViewer()
   {
      return (ProjectViewer) getSource();
   }

   /**
    * Returns the {@link Project}.
    */
   public Project getProject()
   {
      return project;
   }

}
