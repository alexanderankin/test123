/*
Copyright (c) 21st century, Dale Anson
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.
* Neither the name of the <SOME OBSCURE ORGANIZATION> nor the names of its
contributors (whoever they may be) may be used to endorse or promote products
derived from this software without specific prior written permission.

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

package sidekick.java;

import java.util.*;
import org.gjt.sp.jedit.*;
import projectviewer.*;
import projectviewer.vpt.*;
import sidekick.java.util.*;


public class PVHelper {

    // filename to project name lookup
    private static HashMap<String, String> projectNameForFile = new HashMap<String, String>();

    /**
     * @return the name of the project containing the given filename
     */
    public static String getProjectNameForFile( String filename ) {
        if ( filename == null ) {
            return null;
        }
        String project_name = projectNameForFile.get( filename );
        if ( project_name != null ) {
            return project_name;
        }
        if ( !isProjectViewerAvailable() ) {
            return null;
        }
        ProjectManager pm = ProjectManager.getInstance();
        for ( Iterator it = pm.getProjects(); it.hasNext(); ) {
            VPTProject project = ( VPTProject ) it.next();
            Collection nodes = project.getOpenableNodes();
            for ( Iterator iter = nodes.iterator(); iter.hasNext(); ) {
                VPTNode node = ( VPTNode ) iter.next();
                if ( node != null && filename.equals( node.getNodePath() ) ) {
                    project_name = project.getName();
                    projectNameForFile.put( filename, project_name );
                    return project_name;
                }
            }
        }
        return null;
    }

    /**
     * @return true if the ProjectViewer plugin is loaded
     */
    public static boolean isProjectViewerAvailable() {
        EditPlugin pv = jEdit.getPlugin( "projectviewer.ProjectPlugin", false );
        return pv != null;
    }

    /**
     * @return a Path containing the classpath as set in ProjectViewer for the given project
     */
    public static Path getClassPathForProject( String projectName ) {
        boolean useJavaClasspath = jEdit.getBooleanProperty( "sidekick.java.pv." + projectName + ".useJavaClasspath" );
        String classpath = jEdit.getProperty( "sidekick.java.pv." + projectName + ".optionalClasspath", "" );
        Path path = new Path( classpath );
        if ( useJavaClasspath ) {
            path.concatSystemClassPath();
        }
        return path;
    }

    /**
     * @return a Path containing the sourcepath as set in ProjectViewer for the given project
     */
    public static Path getSourcePathForProject( String projectName ) {
        String sourcepath = jEdit.getProperty( "sidekick.java.pv." + projectName + ".optionalSourcepath", "" );
        Path path = new Path( sourcepath );
        return path;
    }
}
