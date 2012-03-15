/*
* Copyright (C) 2009, Dale Anson
*
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
*
*/

/**
* This code is based on:
* A macro to show all of the tasks that the TaskList plugin would show
* if the TaskList plugin had any concept of ProjectViewer.  This macro
* gets the list of files from ProjectViewer for the current project,
* passes each of them to TaskList to find the tasks for each file, and
* combines them all into a single tree display.  This puts all the tasks
* for the entire project in a single display.
*
* @author Dale Anson, 3 Nov 2008
*/
package tasklist;

import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;

import java.awt.event.*;
import projectviewer.*;
import projectviewer.event.ViewerUpdate;
import projectviewer.vpt.*;

import common.swingworker.*;

public class ProjectTaskList extends AbstractTreeTaskList {

    // reference to the current project to list tasks for
    private VPTProject project = null;
    
    /**
     * @param view The View containing the ProjectViewer to find tasks for    
     */
    public ProjectTaskList( View view ) {
        super( view, ProjectViewer.getActiveProject( view ) != null ? ProjectViewer.getActiveProject( view ).getName() : null );
        putClientProperty( "isCloseable", Boolean.FALSE );
    }

    // finds the tasks in all files using a SwingWorker so as not to impact
    // performance of the UI.
    @Override
    protected void loadFiles() {
        if ( project == null ) {
            project = ProjectViewer.getActiveProject( view );
        }
        if ( project != null ) {
            rootDisplayName = jEdit.getProperty( "tasklist.projectfiles.project", "Project:" ) + " " + project.getName();
        }
        super.loadFiles();
    }

    @Override
    protected boolean canRun() {
        if ( project == null ) {
            // it is possible there is no active project even if ProjectViewer is installed.
            add( new JLabel( jEdit.getProperty( "tasklist.projectfiles.noproject", "No project is open." ) ) );
            return false;
        }
        return jEdit.getBooleanProperty( "tasklist.show-project-files", true );
    }

    @Override
    protected List<String> getBuffersToScan() {
        if ( project == null ) {
            return null;
        }
        List<String> toScan = new ArrayList<String>();
        Collection nodes = project.getOpenableNodes();
        for ( Iterator it = nodes.iterator(); it.hasNext(); ) {
            VPTNode node = ( VPTNode ) it.next();

            // I'm only handling file nodes, which probably covers
            // better than 99.9% of the nodes in ProjectViewer
            if ( node.isFile() ) {
                VPTFile file_node = ( VPTFile ) node;
                String path = file_node.getNodePath();
                if ( Binary.isBinary( path ) ) {
                    continue;
                }
                toScan.add( path );
            }
        }
        return toScan;
    }


    public void handleMessage( EBMessage msg ) {
        if ( msg.getClass().getName().equals( "projectviewer.event.ViewerUpdate" ) ) {
            ViewerUpdate vu = ( ViewerUpdate ) msg;
            if ( ViewerUpdate.Type.PROJECT_LOADED.equals( vu.getType() ) && vu.getView().equals( view ) ) {
                project = ( VPTProject ) vu.getNode();
                if ( project != null ) {
                    loadFiles();
                }
            }
        }
        else {
            super.handleMessage( msg );
        }
    }
}