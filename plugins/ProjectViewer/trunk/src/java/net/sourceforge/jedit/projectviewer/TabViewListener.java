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

package net.sourceforge.jedit.projectviewer;




import javax.swing.*;
import javax.swing.event.*;

/**
@author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
@version $Revision$
*/
public class TabViewListener implements ChangeListener {

    private ProjectViewer viewer = null;

    public TabViewListener(ProjectViewer viewer) {
        this.viewer = viewer;
    }

    public void stateChanged(ChangeEvent e) {

        this.viewer.setStatus(" ");
     
     
        String currentTab = this.viewer.tabs.getTitleAt( this.viewer.tabs.getSelectedIndex() );

        if (currentTab.equals( ProjectViewer.FOLDERS )) {


            
        } else if (currentTab.equals( ProjectViewer.FILES )) {



        } else if (currentTab.equals( ProjectViewer.WORKING_FILES )) {

            if (this.viewer.projectCombo.getSelectedItem().equals(ProjectViewer.ALL_PROJECTS)) {

                this.viewer.buildAllTrees( this.viewer.workingfiles, 
                                           ProjectViewer.DISPLAY_WORKING_FILES );
               
            
             
            } else {
                this.viewer.buildTree( this.viewer.getCurrentProject(), 
                                       ProjectViewer.DISPLAY_WORKING_FILES );
            }
                                       

        } 

        
        //if the user selects the "Options" tab disable the enhanced options
        

        String selectedProject = (String)this.viewer.projectCombo.getSelectedItem() ;



    }

}
