package sidekick.java;

import java.util.*;
import org.gjt.sp.jedit.*;
import projectviewer.*;
import projectviewer.vpt.*;
import sidekick.java.util.*;


public class PVHelper {
    
    public static String getProjectNameForFile( String filename ) {
        if (!isProjectViewerAvailable())
            return null;
        ProjectManager pm = ProjectManager.getInstance();
        for ( Iterator it = pm.getProjects(); it.hasNext(); ) {
            VPTProject project = ( VPTProject ) it.next();
            VPTNode node = project.getChildNode( filename );
            if ( node != null ) {
                return project.getName();
            }
        }
        return null;
    }

    public static boolean isProjectViewerAvailable() {
        EditPlugin pv = jEdit.getPlugin( "projectviewer.ProjectPlugin", false );
        return pv != null;
    }
    
    public static Path getClassPathForProject(String projectName) {
        boolean useJavaClasspath = jEdit.getBooleanProperty( "sidekick.java.pv." + projectName + ".useJavaClasspath" );
        System.out.println("=-=-=-=-=-= useJavaClasspath = " + useJavaClasspath);
        String classpath = jEdit.getProperty("sidekick.java.pv." + projectName + ".optionalClasspath", "");
        Path path = new Path(classpath);
        if (useJavaClasspath) {
            path.concatSystemClassPath();   
        }
        System.out.println("+++++ PVHelper, classpath for project " + projectName + " = " + path.toString());
        return path;
    }
}
