/*
 *  AntFarmPlugin.java - Plugin for running Ant builds from jEdit.
 *  Copyright (C) 2001 Brian Knowles
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package antfarm;

import java.io.File;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.Collection;
import console.Console;
import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.PluginJAR;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;
import projectviewer.ProjectManager;
import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTProject;
import sidekick.java.PVHelper;
import projectviewer.vpt.VPTNode;

public class AntFarmPlugin extends EditPlugin
{

    public final static String NAME = "antfarm";
    public final static String OPTION_PREFIX = "options." + NAME + ".";
    public final static String ANT_HOME = "ant.home";
    public final static AntFarmShell ANT_SHELL = new AntFarmShell();

    private static DefaultErrorSource _errorSource;


    public static DefaultErrorSource getErrorSource()
    {
        return _errorSource;
    }


    public static AntFarmShell getAntFarmShell() {
        return ANT_SHELL;
    }

    static Console getConsole( View view )
    {
        return getConsole( view, true );
    }


    static Console getConsole( View view, boolean bringToFront )
    {
        DockableWindowManager mgr = view.getDockableWindowManager();
        String CONSOLE = "console";
        // Get the current console instance
        Console console =
            (Console) mgr.getDockable( CONSOLE );
        if(console == null)
        {
            mgr.addDockableWindow( CONSOLE );
            console = (Console) mgr.getDockable( CONSOLE );
        }

        console.setShell( AntFarmPlugin.ANT_SHELL );

        if ( bringToFront )
            view.getDockableWindowManager().showDockableWindow( CONSOLE );

        return console;
    }


    static Properties getGlobalProperties()
    {
        Properties properties = new Properties();

        String name;
        int counter = 1;
        while ( ( name = jEdit.getProperty( PropertiesOptionPane.PROPERTY + counter + PropertiesOptionPane.NAME ) ) != null ) {
            properties.put( name, jEdit.getProperty( PropertiesOptionPane.PROPERTY + counter + PropertiesOptionPane.VALUE ) );
            counter++;
        }
        return properties;
    }

    public void start()
    {
        //IntegrationManager integration = new IntegrationManager( this );
        //integration.addBridge( "projectviewer.ProjectPlugin", "antfarm.ProjectBridge" );

        boolean useSameJvm
             = jEdit.getBooleanProperty( AntFarmPlugin.OPTION_PREFIX + "use-same-jvm", true );

        // initalize error source
        _errorSource = new DefaultErrorSource( NAME );
        ErrorSource.registerErrorSource(_errorSource);

        // check whether tools.jar is available on JDK 1.2 or higher:
        if ( !MiscUtilities.isToolsJarAvailable() && useSameJvm) {
            String warning = "This will cause problems when trying to use the modern and classic compilers.\n" +
                "If you want AntFarm to work properly, please make sure tools.jar is\n" +
                "in one of the above locations, and restart jEdit.";
            Log.log( Log.WARNING, this, warning );
            GUIUtilities.message(null, "tools-jar-missing", null);
        }

        // Register the ant shell with the console plugin.
        // Shell.registerShell( ANT_SHELL );

        // put the ant home in the environment for scripts to use.
        System.getProperties().put( ANT_HOME, getAntFarmPath() );

        loadCustomClasspath();
    }

    public void stop()
    {
        // Shell.unregisterShell( ANT_SHELL );
    }

    static void loadCustomClasspath()
    {
        String classpath = jEdit.getProperty( AntFarmPlugin.OPTION_PREFIX
             + "classpath" );
        if ( classpath == null )
            return;

        StringTokenizer st = new StringTokenizer( classpath, File.pathSeparator );
        while ( st.hasMoreTokens() ) {
            String path = st.nextToken();

            PluginJAR jar;
            jar = jEdit.getPluginJAR( path );
            if ( jar == null ) {
                Log.log( Log.DEBUG, null,
                    "- adding " + path + " to jEdit plugins." );
                jEdit.addPluginJAR(path);
            }
            else
                Log.log( Log.DEBUG, null,
                    "- has been loaded before: " + path );
        }
    }

    String getAntFarmPath()
    {
        PluginJAR jar = getPluginJAR();
        String path = MiscUtilities.getParentOfPath( jar.getPath() );
        Log.log( Log.DEBUG, this, "path: " + path );
        return MiscUtilities.constructPath( path, "AntFarm" );
    }


    static boolean useProjectBridge()
    {
        return jEdit.getBooleanProperty(
            AntFarmPlugin.OPTION_PREFIX + "use-project-bridge"
             );
    }

    static boolean supressSubTargets()
    {
        return jEdit.getBooleanProperty(
            AntFarmPlugin.OPTION_PREFIX + "supress-sub-targets"
             );
    }

    /**
     * Returns the AntFram instance assocaited with the given View.
     * Not sure if there is a better way to do this, but this was taken from the
     * ProjectBridge class
     */
    static AntFarm getAntFarm(View view)
    {
        //ensure the dockable is registered with the view
        view.getDockableWindowManager().addDockableWindow(AntFarmPlugin.NAME);
        //get the dockable instance
        return (AntFarm) view.getDockableWindowManager().getDockableWindow(AntFarmPlugin.NAME);
    }

    /**
     * Checks if the current file is in a project, and if so attempts
     * to run the target identified by the given param against
     * the build file associated with the project.
     *
     * @param view the current jEdit View object
     * @param targetID A string that can be used to find the property
     *          value that will contain the target name
     *              should be either 'A', 'B', 'C', or 'D'
     */
    public static void runProjectTarget(View view, String targetID)
    {
        //check that the current file is in a project
        VPTProject currentProj = getCurrentProject(view);
        if (currentProj != null)
        {
            String buildFile = currentProj.getProperty(AntFarmPlugin.OPTION_PREFIX
                    + "pv.projectAntScript");
            String targetName = currentProj.getProperty(AntFarmPlugin.OPTION_PREFIX
                    + "pv.target" + targetID);
            if ((buildFile != null) && !buildFile.equals("") &&
                (targetName != null) && !targetName.equals(""))
            {
                //use the ant console shell to load the build file, and run the specified target
                Console console = getConsole(view);
                console.run(console.getShell(), console.getOutput(), "+" + buildFile);
                console.run(console.getShell(), console.getOutput(), "!" + targetName);

            }
            else
            {
                GUIUtilities.error(view, "antfarm.pv.settings-missing", new String[] {targetID});
            }
        }
    }

    /**
     * Checks if the current file is in a project, and if so
     * provides a list of the targets in this projects defined
     * build file.  The selected target is then run.
     *
     * @param view the current jEdit View object
     */
    public static void selectAndRunProjectTarget(View view)
    {
        //check that the current file is in a project
        VPTProject currentProj = getCurrentProject(view);
        if (currentProj != null)
        {
            String buildFile = currentProj.getProperty(AntFarmPlugin.OPTION_PREFIX
                    + "pv.projectAntScript");
            if ((buildFile != null) && !buildFile.equals(""))
            {
                //check EBrowser installed
                if(jEdit.getPlugin("ebrowse.EBrowsePlugin",false) != null)
                {
                    AntTaskSelector antTask = new AntTaskSelector();
                    ebrowse.EBrowsePlugin.openBrowser(view, "Run Ant Task:", antTask, antTask, 1);
                }
                else
                {
                    GUIUtilities.error(view, "antfarm.pv.ebrowse-missing", null);
                }
            }
            else
            {
                GUIUtilities.error(view, "antfarm.pv.buildfile-missing", null);
            }
        }
    }

    /**
     * Returns the current ProjectViewer project for the given view.
     * This works by finding the path of the current buffer in the given view,
     * and then checking this path to see if it is in a project.
     * If JavaSideKick is available, the PVHelper method this provides will
     * be used, as this provides caching of lookups!
     * If ProjectViewer is not available, or the file isnt in a project,
     * error messages will be displayed to the user.
     */
    static VPTProject getCurrentProject(View view)
    {
        //check project viewer available
        if(jEdit.getPlugin("projectviewer.ProjectPlugin",false) != null)
        {
            VPTProject proj = null;

            //if javasidekick is available then use the project lookup functions
            //from there as it has caching of results etc.
            //QUESTION: Should we just make this a dependency???
            if (jEdit.getPlugin("sidekick.java.JavaSideKickPlugin",false) != null)
            {
                String projName = PVHelper.getProjectNameForFile(view.getBuffer().getPath());
                if (projName != null)
                    proj = ProjectManager.getInstance().getProject(projName);
            }
            else
            {
                //based on code from JavaSideKick PVHelper class by Dale Anson
                ProjectManager pm = ProjectManager.getInstance();
                for ( Iterator it = pm.getProjects(); it.hasNext(); ) {
                    VPTProject project = ( VPTProject ) it.next();
                    Collection nodes = project.getOpenableNodes();
                    for ( Iterator iter = nodes.iterator(); iter.hasNext(); ) {
                        VPTNode node = ( VPTNode ) iter.next();
                        if ( node != null && view.getBuffer().getPath().equals( node.getNodePath() ) ) {
                            proj = project;
                            break;
                        }
                    }
                    if (proj != null)
                        break;
                }
            }

            if (proj == null)
            {
                GUIUtilities.error(view, "antfarm.pv.file-not-in-project", null);
            }

            return proj;
        }
        else
        {
           GUIUtilities.error(view, "antfarm.pv.required", null);
           return null;
        }
    }
}

