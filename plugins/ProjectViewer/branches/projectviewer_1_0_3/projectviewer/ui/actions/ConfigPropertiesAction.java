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
package projectviewer.ui.actions;

import projectviewer.*;
import projectviewer.ui.*;


/**
 * An action to configure project properties.
 */
public class ConfigPropertiesAction extends ActionBase {

   private Project project;

   /**
    * Create a new <code>ConfigPropertiesAction</code>.
    */
   public ConfigPropertiesAction(ProjectViewer aProjectViewer, Project aProject) {
      super("config-properties", aProjectViewer);
      project = aProject;
   }

   /**
    * Show a dialog allowing the use to configure project properties.
    */
   protected void performAction() throws ProjectException {
      PropertiesPanel.showDialog(projectViewer.getView(), project);
   }

}
