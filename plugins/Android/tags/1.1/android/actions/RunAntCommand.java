package android.actions;

import java.io.*;

import projectviewer.vpt.VPTProject;
import projectviewer.ProjectViewer;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;

// Executes an Ant target from the build.xml in the root directory of the
// current project. Output goes to the console.
public class RunAntCommand {
    private View view;

    public void execute( View view, String target ) {
        this.view = view;
        executeTarget(target);
    }

    protected void executeTarget(String target) {

        if ( !isProjectViewerAvailable() ) {
            Util.showError(view, jEdit.getProperty("android.Error", "Error"), jEdit.getProperty("android.ProjectViewer_and_build.xml_not_available.", "ProjectViewer and build.xml not available."));
            return;
        }

        String projectDir = getProjectRoot();
        if ( "".equals( projectDir ) ) {
            Util.showError(view, jEdit.getProperty("android.Error", "Error"), jEdit.getProperty("android.Can_not_find_project_base_directory_for_current_project.", "Can not find project base directory for current project."));
            return;
        }
        File buildFile = new File( projectDir, "build.xml" );
        if ( !buildFile.exists() ) {
            Util.showError(view, jEdit.getProperty("android.Error", "Error"), jEdit.getProperty("android.build.xml_not_found_in_", "build.xml not found in ") + projectDir);
            return;
        }

                String antPath = jEdit.getProperty("android.ant.path", "");
                if (!antPath.isEmpty()) {
                    antPath += "/bin/";   
                }
        String command = antPath + "ant -f " + buildFile.getAbsolutePath() + " " + target;
        Util.runInSystemShell(view, command);
    }

    boolean isProjectViewerAvailable() {
        EditPlugin pv = jEdit.getPlugin( "projectviewer.ProjectPlugin", false );
        return pv != null;
    }

    String getProjectRoot() {
        VPTProject project = ProjectViewer.getActiveProject( view );
        return project == null ? "" : project.getRootPath();
    }
}