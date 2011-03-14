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
    private static HashMap<String, VPTProject> projectForFile = new HashMap<String, VPTProject>();

    /**
     * @return the name of the project containing the given filename
     */
    public static VPTProject getProjectForFile(String filename) {
        if (filename == null) {
            return null;
        }
        VPTProject project = projectForFile.get(filename);
        if (project != null) {
            return project;
        }
        if (!isProjectViewerAvailable()) {
            return null;
        }
        ProjectManager pm = ProjectManager.getInstance();
        for (VPTProject _project : pm.getProjects()) {
            Collection nodes = _project.getOpenableNodes();
            for (Iterator iter = nodes.iterator(); iter.hasNext();) {
                VPTNode node = (VPTNode) iter.next();
                if (node != null && filename.equals(node.getNodePath())) {
                    projectForFile.put(filename, _project);
                    return _project;
                }
            }
        }
        return null;
    }

    /**
     * @return true if the ProjectViewer plugin is loaded
     */
    public static boolean isProjectViewerAvailable() {
        EditPlugin pv = jEdit.getPlugin("projectviewer.ProjectPlugin", false);
        return pv != null;
    }

    /**
     * @param projectName Get the class path information for this project.
     * @return a Path containing the classpath as set in ProjectViewer for the given project
     */
    public static Path getClassPathForProject(VPTProject proj) {
        return getClassPathForProject(proj, true);
    }

    /**
     * @param projectName Get the class path information for this project.
     * @param withJavaClasspath If true, include the paths for System.getProperty("java.class.path").
     * @return a Path containing the classpath as set in ProjectViewer for the given project
     */
    public static Path getClassPathForProject(VPTProject proj, boolean withJavaClasspath) {
        boolean useJavaClasspath = useJavaClasspath(proj);
        String classpath = proj.getProperty("java.optionalClasspath");
        if (classpath == null) {
            classpath = "";
        }
        Path path = new Path(classpath);
        if (useJavaClasspath && withJavaClasspath) {
            path.concatSystemClassPath();
        }
        String buildpath = getBuildOutputPathForProject(proj);
        if (buildpath.length() > 0) {
            path.concat(buildpath, true);
        }
        return path;
    }

    public static boolean useJavaClasspath(VPTProject proj) {
        if (proj == null) {
            return true;
        }
        String prop = proj.getProperty("java.useJavaClasspath");
        return (prop != null && prop.equals("true"));
    }

    public static String getBuildOutputPathForProject(VPTProject proj) {
        String prop = proj.getProperty("java.optionalBuildpath");
        if (prop == null) {
            prop = "";
        }
        return prop;
    }

    /**
     * @return a Path containing the sourcepath as set in ProjectViewer for the given project
     */
    public static Path getSourcePathForProject(VPTProject proj) {
        String prop = proj.getProperty("java.optionalSourcepath");
        if (prop == null) {
            prop = "";
        }
        Path path = new Path(prop);
        return path;
    }

    /**
     * @return a string representing the path to the root of the current project
     * in the given View.
     */
    public static String getProjectRoot(View view) {
        VPTProject project = ProjectViewer.getActiveProject(view);
        return project == null ? "" : project.getRootPath();
    }

    /**
     * @return the name of the current project in the given view.
     */
    public static String getProjectName(View view) {
        VPTProject project = ProjectViewer.getActiveProject(view);
        return project == null ? "" : project.getName();
    }

    /**
     * @return the current project in the given view.
     */
    public static VPTProject getProject(View view) {
        if (!isProjectViewerAvailable()) {
            return null;
        }
        return ProjectViewer.getActiveProject(view);
    }

}
