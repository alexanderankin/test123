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

import java.util.*;
import javax.swing.Action;
import projectviewer.*;


/**
 * An action map for handling dynamic actions.
 */
public class ActionMap {

   private static ArtifactFilter fileViewFilter;

   private List filters;
   private List actions;


   /**
    * Create a new <code>ActionMap</code>.
    */
   public ActionMap() {
      filters = new ArrayList();
      actions = new ArrayList();
   }

   /**
    * Add an action.
    */
   public void add(ArtifactFilter filter, ProjectAction action) {
      filters.add(filter);
      actions.add(action);
   }

   /**
    * Add an action that works only for {@link FileView}s.
    */
   public void addFileViewAction(ProjectAction action) {
      add(getFileViewFilter(), action);
   }

   /**
    * Find the actions for the given artifact.
    */
   public List findActions(ProjectArtifact artifact) {
      List artifactActions = new ArrayList();
      for (int i=0; i<filters.size(); i++) {
         if (((ArtifactFilter) filters.get(i)).accept(artifact)) {
            artifactActions.add(actions.get(i));
         }
      }
      return artifactActions;
   }

   /**
    * Returns a filter that accepts on {@link FileView}s.
    */
   private static ArtifactFilter getFileViewFilter() {
      if (fileViewFilter == null) fileViewFilter = new FileViewFilter();
      return fileViewFilter;
   }

   /**
    * An filter to accept only instances of {@link FileView}.
    */
   private static class FileViewFilter implements ArtifactFilter {

      /**
       * Accepts this file if <code>true</code> is returned.
       */
      public boolean accept(ProjectArtifact artifact) {
         return (artifact instanceof FileView);
      }

   }

}
