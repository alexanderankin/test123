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

import console.Console;
import console.ConsolePlugin;
import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.PluginJAR;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;

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
    
    static Project parseBuildFile(String buildFilePath) throws Exception
    {
        File buildFile = new File(buildFilePath);
        Project project = new Project();
        try
        {
            if (buildFile.exists())
            {
                project.init();

                // black magic for xerces 1.4.4 loading
                Thread.currentThread().setContextClassLoader(
                    AntFarm.class.getClassLoader());

                // first use the ProjectHelper to create the
                // project object
                // from the given build file.

                ProjectHelper.configureProject(project, buildFile);
            }
            else
            {
                throw new Exception(jEdit.getProperty(AntFarmPlugin.NAME
                    + ".project.missing")
                    + buildFile.getAbsolutePath());
            }
        }
        catch (BuildException be)
        {
            addAntError(be.toString(), project.getBaseDir().toString());
            Log.log(Log.DEBUG, project, be);
            throw be;
        }
        catch (Exception e)
        {
            Log.log(Log.ERROR, project, e);
            throw e;
        }
        return project;
    }
    
    static void addAntError(String exceptionString, String baseDir)
    {
        ConsolePlugin.parseLine(jEdit.getActiveView(), exceptionString, baseDir, AntFarmPlugin
            .getErrorSource());
    }

    public void start()
    {
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

}

