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

//jedit stuff
import org.gjt.sp.jedit.*;

import java.util.Vector;
import javax.swing.tree.*;

/**
 * Provides a neutral way to launch projects and files.
 * @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
 * @version $Revision$
 */

public class Launcher {

    private View                    view                        = null;
    private Project                 currentlyLaunchedProject    = null;
    private ProjectViewer           viewer                      = null;
    private Vector                  openFiles                   = new Vector();

    /**
    @parameter view         The view that Launcher should open files with.
    @parameter viewer       An instance of ProjectViewer
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public Launcher( View view, ProjectViewer viewer) {
        this.view = view;
        this.viewer = viewer;
    }


    

    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public void launchProject( Project project ) {


        if (this.view == null)  {
            System.err.println("Now views open.. JEdit is probably not running.");
            return;
        }


        //close down the current project
        this.closeProject( this.viewer.getCurrentProject() );

        this.viewer.setCurrentProject(project);

        this.currentlyLaunchedProject = project;


        //open all the files...
        File[] files = ProjectResources.getFiles( project );
        System.err.println("found " + files.length + " files");

        for ( int i = 0; i < files.length; ++i ) {
            if ( files[i].isSubscribed() )
                this.launchFile(files[i]);
        }

        Logger.log("test", 200);

        //if the user has edited a file previously... set it as the buffer.
        File show = project.getLastFile();

        if (show != null) {
            Logger.log("Displaying last file: " + show.toString(), 200);

            this.showFile( show );
        } else {
            Logger.log("Last file unknown", 200);
        }


    }


    /**
    Takes a file and opens it up in jEdit.

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public  void launchFile( File file ) {



        String fileName = file.get();

        String parent = fileName.substring( 0, fileName.lastIndexOf(System.getProperty("file.separator")) );
        String openFile   = fileName.substring( fileName.lastIndexOf(System.getProperty("file.separator")) + 1, fileName.length() );


        if (this.view != null) {
            Buffer buffer = jEdit.openFile( this.view, null, fileName, false, false);
            this.showFile(file);


            file.setSubscribed(true);
            this.viewer.getCurrentTree().repaint();            
            this.openFiles.addElement( file );

            //ProjectResources.updateFile(file);


        }
        


    }

    /**
    Takes a given file and highlights it in the current view.

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public void showFile(File file) {
        Buffer buffer = file.getBuffer();

        if (buffer != null) {
            this.view.setBuffer( buffer );
        }


    }



    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public void closeFile( File file ) {


        
        if (this.view != null) {

            if ( file.isSubscribed() ) {

                if ( file.getBuffer() != null) {

                    jEdit.closeBuffer( this.view, file.getBuffer() );

     
                    String currentTab = this.viewer.tabs.getTitleAt( this.viewer.tabs.getSelectedIndex() );
                    if (currentTab.equals(ProjectViewer.WORKING_FILES)) {
                            ((DefaultTreeModel)this.viewer.workingfiles.getModel()).removeNodeFromParent( this.viewer.getCurrentNode() );
                    }
                    
                    this.openFiles.removeElement(file);

                }

            }

        } 

        //now remove the file from jEdit...
        
        file.setSubscribed(false);
        this.viewer.getCurrentTree().repaint();

    
        //ProjectResources.updateFile(file);
        
    }



    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */
    public void closeProject(Project project) {

        if (project != null) {
            //remove all subscribed buffers from jEdit...
            //make sure jEdit is running
            File[] files = ProjectResources.getFiles( project );
    

            //close all the opened files

            for (int i = 0; i < this.openFiles.size(); ++i) {

                File file = (File)this.openFiles.elementAt(i);

                Buffer buffer = file.getBuffer();
                if ( buffer != null) {
                    jEdit.closeBuffer( this.view, buffer );

                }

                this.openFiles.removeElementAt( i );


            }

            this.openFiles.removeAllElements();

        }
    }
    
}
