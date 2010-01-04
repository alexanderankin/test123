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
import projectviewer.ProjectViewer;
import projectviewer.ProjectManager;
import projectviewer.importer.RootImporter;
import javax.swing.SwingUtilities;

/**
 * Some methods to help work with ProjectViewer.
 */
public class PVHelper {
    // prefix for properties stored for Subversion per ProjectViewer project,
    // property names are PREFIX + project name + username/password
    public final static String PREFIX = "ise.plugin.svn.pv.";

    // filename to project name lookup, caching these to improve performance, but
    // I have no idea if the performance hit of going to PV every time is significant...
    private static HashMap<String, VPTProject> projectForFile = new HashMap<String, VPTProject>();


    /**
     * @return true if the ProjectViewer plugin is loaded
     */
    public static boolean isProjectViewerAvailable() {
        EditPlugin pv = jEdit.getPlugin( "projectviewer.ProjectPlugin", false );
        return pv != null;
    }

    /**
     * @return the name of the active project, if any, in the given view.  Returns an
     * empty string if there is no active project in the view.
     */
    public static String getProjectName( View view ) {
        VPTProject project = ProjectViewer.getActiveProject( view );
        return project == null ? "" : project.getName();
    }

    /**
     * Reimport the files for the active project in the given view.
     */
    public static void reimportProjectFiles( View view ) {
        VPTProject project = ProjectViewer.getActiveProject( view );
        if ( project == null ) {
            return ;
        }
        RootImporter importer = new RootImporter( project, ProjectViewer.getViewer( view ), true );
        SwingUtilities.invokeLater( importer );
    }

    /**
     * @param view the view showing a ProjectViewer
     * @return the path to the root of the active project, if any, in the given view.
     * Returns an empty string if there is no active project in the view.
     */
    public static String getProjectRoot( View view ) {
        VPTProject project = ProjectViewer.getActiveProject( view );
        return project == null ? "" : project.getRootPath();
    }

    /**
     * @param filename the name of a file possibly contained in a ProjectViewer project.
     * @return the path to the root of the active project, if any, in the given view.
     * Returns an empty string if there is no active project in the view or if no
     * project contains the file.
     */
    public static String getProjectRoot( String filename ) {
        VPTProject project = getProjectNameForFile( filename );
        return project == null ? "" : project.getRootPath();
    }

    /**
     * @return the name of the project containing the given filename, or null if
     * no project contains the file.
     */
    public static VPTProject getProjectNameForFile( String filename ) {
        // the simplest check
        if ( filename == null ) {
            return null;
        }

        // check the cache
        VPTProject project = projectForFile.get( filename );
        if ( project != null ) {
            return project;
        }

        // another simple check
        if ( !isProjectViewerAvailable() ) {
            return null;
        }

        // check ProjectViewer. Check active project first, then other projects.
        project = ProjectViewer.getActiveProject( jEdit.getActiveView() );
        if ( project == null ) {
            return null;
        }
        if ( project.isInProject( filename ) || ( project.getRootPath() != null && filename.startsWith( project.getRootPath() ) ) ) {
            projectForFile.put( filename, project );
            return project;
        }

        ProjectManager pm = ProjectManager.getInstance();
        for ( VPTProject proj : pm.getProjects() ) {
            if ( proj.equals( project ) ) {
                continue;   // already checked in active project, no need to check again.
            }
            if ( proj.isInProject( filename ) || ( proj.getRootPath() != null && filename.startsWith( proj.getRootPath() ) ) ) {
                projectForFile.put( filename, proj );
                return proj;
            }
        }
        return null;
    }

    /**
     * @return {username, encrypted_password} The returned array will never be null,
     * but username and/or password can be null.
     */
    public static String[] getSVNLogin( String filename ) {
        VPTProject project = getProjectNameForFile( filename );
        if ( project == null ) {
            return new String[] {null, null};
        }
        String project_name = project.getName();
        String username = jEdit.getProperty( PVHelper.PREFIX + project_name + ".username" );
        String password = jEdit.getProperty( PVHelper.PREFIX + project_name + ".password" );
        return new String[] {username, password};
    }
}