/*
Copyright (c) 2010, François Rey, Dale Anson (copied from SVNPlugin)
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

package launcher.integration.pv;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import launcher.LauncherUtils;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

import projectviewer.ProjectManager;
import projectviewer.ProjectViewer;
import projectviewer.importer.RootImporter;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;

/**
 * Some methods to help work with ProjectViewer, originally copied from
 * SVNPlugin.
 * Most of the methods here are not used by any Launcher, but they're
 * still here because they give an example of what is available and
 * useful for developing Launchers that work with PV.
 * Of particular interest are the methods {@link #isProjectViewerAvailable()}
 * and other <code>getProject*()</code> methods which make it possible
 * for Launchers to test if a file to be processed is part of a PV project
 * from which additional information could be retrieved (e.g. SVN repository
 * information, project homepage for testing, etc.)
 * 
 * Some argue we should remove any unused code.
 * I'd rather leave it here so the info can be passed along.
 */
public class PVHelper {

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
        VPTProject project = getProjectForFile( filename );
        return project == null ? "" : project.getRootPath();
    }

    /**
     * @return the project containing the given filename, or null if
     * no project contains the file.
     */
    public static VPTProject getProjectForFile( String filename ) {
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
        if (project == null) {
            return null;
        }
        if (project.isInProject(filename) || ( project.getRootPath() != null && filename.startsWith( project.getRootPath()))) {
            projectForFile.put(filename, project);
            return project;
        }
        
        ProjectManager pm = ProjectManager.getInstance();
        for ( VPTProject proj : pm.getProjects() ) {
            if (proj.equals(project)) {
                continue;   // already checked in active project, no need to check again.   
            }
            if ( proj.isInProject( filename ) || ( proj.getRootPath() != null && filename.startsWith( proj.getRootPath() ) )) {
                projectForFile.put(filename, proj);
                return proj;
            }
        }
        return null;
    }
    
    public static File[] resolveToFileArray(Collection<VPTNode> nodes,
    										boolean keepDir) {
    	int numberOfNodes = nodes.size();
    	File[] files = new File[numberOfNodes];
    	int numberOfFiles = 0;
    	for (VPTNode node : nodes) {
    		if (node.isFile() || (node.isDirectory() && keepDir)) {
    			String nodePath = node.getNodePath();
    			File file = LauncherUtils.resolveToFile(nodePath);
    			if (file == null)
    				return null;
    			else
    				files[numberOfFiles++] = file;
    		}
    	}
    	if (numberOfFiles < numberOfNodes) {
    		File[] newFileArray = new File[numberOfFiles];
    		System.arraycopy(files, 0, newFileArray, 0, numberOfFiles);
    		files = newFileArray;
    	}
    	return files;
    }

    /**
     *  Receives a collection of TreePath objects and returns the underlying
     *  objects selected, removing a child when its parent has also been
     *  selected.
     */
    private static List<VPTNode> getSelectedArtifacts( TreePath[] paths ) {
        TreePath last = null;
        List<VPTNode> objs = new ArrayList<VPTNode>();

        for ( int i = 0; i < paths.length; i++ ) {
            if ( last != null && !last.isDescendant( paths[ i ] ) ) {
                last = null;
            }

            if ( last == null ) {
                last = paths[ i ];
                objs.add( ( VPTNode ) paths[ i ].getLastPathComponent() );
            }
        }
        
        return objs;
    }
    

    /**
	 * Return the list of selected nodes in the given ProjectViewer instance.<br />
	 * 
	 * @param viewer
	 *            the ProjectViewer instance to retrieve selected nodes from.
	 * @return the selected nodes where no node has its parent selected (if a
	 *         directory is selected, none of its child node will be listed)
	 */
    public static List<VPTNode> getSelectedNodes(ProjectViewer viewer) {
        List<VPTNode> list = new ArrayList<VPTNode>();
        JTree tree = viewer.getCurrentTree();

        switch ( tree.getSelectionCount() ) {
            case 0:
                // no Selection, shouldn't happen, but just in case...
                break;

            case 1: {
                    // single selection
                    list.add( ( VPTNode ) tree.getLastSelectedPathComponent() );
                    break;
                }

            default: {
                    list = getSelectedArtifacts( tree.getSelectionPaths() );
                    break;
                }
        }
        return list;
    }

}