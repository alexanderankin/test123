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


import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.tree.*;

/**
Listens to the project JTree and responds to file selections.

@author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
@version $Revision$
*/


public class ProjectTreeSelectionListener implements TreeSelectionListener, MouseListener {

    //represents he current project...
    private Project                     project         = null;
    private ProjectViewer               viewer          = null;
    private Launcher                    launcher        = null;
    private File                        currentFile     = null;
    private File                        previousFile    = null;
    
    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    */
    public ProjectTreeSelectionListener(ProjectViewer instance, Launcher launcher) {

        this.viewer = instance;
        this.launcher = launcher;

    }
    
    
    /**
    MouseListener interface.

    Determines when the user clicks on the JTree.

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    */
    public void mouseClicked(MouseEvent evt) {



        if(evt.getClickCount() == 2) {

            
            //only pay attention if the current object is a File.
            if (this.viewer.getCurrentNode().getUserObject() instanceof net.sourceforge.jedit.projectviewer.File == false) {


                return;
            }

            if ( this.currentFile.isSubscribed() )  {



                //determine whether to close it or to show it...
                if ( previousFile != null && 
                     previousFile.equals(currentFile) && 
                     this.currentFile.get().equals( this.viewer.getView().getBuffer().getPath() ) ) {

                     launcher.closeFile( this.currentFile );
                    
                } else {
                    previousFile = currentFile;
                    launcher.showFile( this.currentFile );
                }
                
                
                
                //TODO... unsubscribe from the file...


                //if the current tab is the working files.. and something was
                //unsubscribed... remove it.
                

                
                
            } else {

                launcher.launchFile( this.currentFile );

                this.viewer.getCurrentProject().setLastFile(this.currentFile);


            }
        }


    }

    
    
    public void mousePressed(MouseEvent evt)  { }
    public void mouseReleased(MouseEvent evt) { }
    public void mouseEntered(MouseEvent evt)  { }
    public void mouseExited(MouseEvent evt)   { }



    //TreeSelectionListener interface

    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    */
    public void valueChanged(TreeSelectionEvent e) {
        


        
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)this.viewer.getCurrentTree().getLastSelectedPathComponent();
        //only leaf nodes matter here...

        if (node == null)
            return;
        
        this.viewer.getCurrentTree().repaint();

        this.viewer.setCurrentNode(node);
        
        if (node.getUserObject() instanceof File) {

            File file = (File)node.getUserObject();

            //if you single click on an item that isn't the last item you double 
            //clicked on... then set the previous double clicked item to null
            if ( (previousFile != null) && (! file.get().equals( this.previousFile.get() ) ) ) {
                this.previousFile = null;
            }

            
            
            this.viewer.setCurrentProject( file.getProject() );




            this.viewer.getCurrentProject().setLastFile(file);


            this.viewer.setCurrentDirectory( file.getDirectory() );

            this.viewer.currentlySelectedFile = node;

            this.viewer.importFilesBtn.setEnabled(true);
            this.viewer.removeFileBtn.setEnabled(true);
            this.viewer.removeAllFilesBtn.setEnabled(true);
            this.viewer.deleteProjectBtn.setEnabled(true);
            this.viewer.addFileBtn.setEnabled(true);
            this.viewer.openAllBtn.setEnabled(true);                                
            this.currentFile = file;
            this.viewer.currentFile = file;

            this.viewer.setStatus( file.get() );
            
        } else if (node.getUserObject() instanceof Project) {

            //keep track of the current project.
            this.project = (Project)node.getUserObject();

            this.viewer.setCurrentProject(this.project);
            this.viewer.setCurrentDirectory( this.project.getRoot() );

            this.viewer.currentlySelectedProject = node;
            this.viewer.currentlySelectedFile = null;

            this.viewer.importFilesBtn.setEnabled(true);
            this.viewer.removeFileBtn.setEnabled(false);
            this.viewer.removeAllFilesBtn.setEnabled(true);
            this.viewer.addFileBtn.setEnabled(true);
            this.viewer.deleteProjectBtn.setEnabled(true);
            this.viewer.openAllBtn.setEnabled(true);

            //don't launch the project just when someone clicks on the node... only do it when necessary
            //launcher.launchProject( new Project( node.getUserObject().toString() ) );

            this.viewer.setStatus( project.get() + " (" + project.getRoot().get() + ")");
            
        } else {

            //it if is a directory... nothing has changed
            if (node.getUserObject() instanceof Directory) {

                Directory dir = (Directory)node.getUserObject();
                this.viewer.setCurrentProject( dir.getProject() );    
                this.viewer.setCurrentDirectory( dir );
                this.viewer.openAllBtn.setEnabled(true);
                this.viewer.importFilesBtn.setEnabled(true);
                this.viewer.removeFileBtn.setEnabled(false);
                this.viewer.removeAllFilesBtn.setEnabled(true);
                this.viewer.addFileBtn.setEnabled(true);
                this.viewer.deleteProjectBtn.setEnabled(true);
                this.viewer.setStatus( dir.get() );

                return;
            }

            this.viewer.currentlySelectedProject = null;
            this.viewer.currentlySelectedFile = null;

            this.viewer.importFilesBtn.setEnabled(false);
            this.viewer.removeFileBtn.setEnabled(false);
            this.viewer.removeAllFilesBtn.setEnabled(false);
            this.viewer.addFileBtn.setEnabled(false);
            this.viewer.deleteProjectBtn.setEnabled(false);
            this.viewer.openAllBtn.setEnabled(false);

        }

        
    }
}

