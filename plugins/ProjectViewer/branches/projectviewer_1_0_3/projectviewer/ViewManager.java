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

import java.util.*;
import projectviewer.ProjectException;


/**
 * Manages the available {@link FileView}s.
 */
public class ViewManager
{

   private List viewNames;
   private List viewTypes;

   /**
    * Create a new <code>ViewManager</code>.
    */
   public ViewManager()
   {
      viewNames = new ArrayList();
      viewTypes = new ArrayList();
      addView("Default", "projectviewer.views.defaultview.DefaultView");
      addView("Custom" , "projectviewer.views.custom.CustomView");
   }

   /**
    * Returns a list of view names.
    */
   public String[] getViewNames()
   {
      String[] names = new String[viewNames.size()];
      return (String[]) viewNames.toArray(names);
   }

   /**
    * Create a view of the given name.
    */
   public FileView createView(String viewName) throws ProjectException
   {
      int index = viewNames.indexOf(viewName);
      try {
         return (FileView) Class.forName((String) viewTypes.get(index)).newInstance();
      } catch (Exception e) {
         throw new ProjectException("Error creating view '" + viewName + "'");
      }
   }

   /**
    * Add a view.
    */
   public void addView(String name, String type)
   {
      viewNames.add(name);
      viewTypes.add(type);
   }

}
