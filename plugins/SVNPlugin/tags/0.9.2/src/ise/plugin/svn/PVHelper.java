/*
Copyright (c) 2007, Dale Anson
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.
* Neither the name of the author nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package ise.plugin.svn;

import java.util.*;

import org.gjt.sp.jedit.*;
import projectviewer.vpt.VPTProject;
import projectviewer.vpt.VPTNode;
import projectviewer.ProjectViewer;
import projectviewer.ProjectManager;
import projectviewer.importer.RootImporter;
import javax.swing.SwingUtilities;
import ise.plugin.svn.library.PasswordHandler;

public class PVHelper {
    // prefix for properties stored for PV settings
    public final static String PREFIX = "ise.plugin.svn.pv.";

    // filename to project name lookup
    private static HashMap<String, VPTProject> projectForFile = new HashMap<String, VPTProject>();


    /**
     * @return true if the ProjectViewer plugin is loaded
     */
    public static boolean isProjectViewerAvailable() {
        EditPlugin pv = jEdit.getPlugin( "projectviewer.ProjectPlugin", false );
        return pv != null;
    }

    public static String getProjectName( View view ) {
        VPTProject project = ProjectViewer.getActiveProject( view );
        return project == null ? "" : project.getName();
    }

    public static void reimportProjectFiles( View view ) {
        VPTProject project = ProjectViewer.getActiveProject( view );
        RootImporter importer = new RootImporter( project, ProjectViewer.getViewer( view ), true );
        SwingUtilities.invokeLater( importer );
    }

    public static String getProjectRoot( View view ) {
        VPTProject project = ProjectViewer.getActiveProject( view );
        return project == null ? "" : project.getRootPath();
    }

    /**
     * @return the name of the project containing the given filename
     */
    public static VPTProject getProjectNameForFile( String filename ) {
        if ( filename == null ) {
            return null;
        }
        VPTProject project = projectForFile.get( filename );
        if ( project != null ) {
            return project;
        }
        if ( !isProjectViewerAvailable() ) {
            return null;
        }
        ProjectManager pm = ProjectManager.getInstance();
        for ( Iterator it = pm.getProjects(); it.hasNext(); ) {
            project = ( VPTProject ) it.next();
            Collection nodes = project.getOpenableNodes();
            for ( Iterator iter = nodes.iterator(); iter.hasNext(); ) {
                VPTNode node = ( VPTNode ) iter.next();
                if ( node != null && filename.equals( node.getNodePath() ) ) {
                    projectForFile.put( filename, project );
                    return project;
                }
            }
        }
        return null;
    }

    public static String[] getSVNLogin(String filename) {
        VPTProject project = getProjectNameForFile(filename);
        if (project == null) {
            return new String[]{null, null};
        }
        String project_name = project.getName();
        String username = jEdit.getProperty( PVHelper.PREFIX + project_name + ".username" );
        String password = jEdit.getProperty( PVHelper.PREFIX + project_name + ".password" );
        if ( password != null && password.length() > 0 ) {
            try {
                PasswordHandler ph = new PasswordHandler();
                password = ph.decrypt( password );
            }
            catch ( Exception e ) {
                password = "";
            }
        }
        return new String[]{username, password};
    }
}
