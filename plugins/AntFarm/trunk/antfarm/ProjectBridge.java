/*
 *  ProjectBridge.java - Plugin for running Ant builds from jEdit.
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
import javax.swing.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.util.Log;

import plugin.integration.PluginBridge;
import projectviewer.*;
import projectviewer.event.*;
import projectviewer.vpt.*;

/**
 * Bridge AntFarm to ProjectViewer.
 *
 * @author steinbeck
 * @created 27. August 2001
 * @version $Id$
 */
public class ProjectBridge implements ProjectViewerListener
{

    public ProjectBridge()
    {
    }

    public void nodeMoved(ProjectViewerEvent evt)
    {
    }

    public void nodeSelected(ProjectViewerEvent evt)
    {
    }

    public void groupActivated(ProjectViewerEvent evt)
    {
    }

    public void groupRemoved(ProjectViewerEvent evt)
    {
    }

    public void groupAdded(ProjectViewerEvent evt)
    {
    }

    public void projectRemoved(ProjectViewerEvent evt)
    {
    }

    public void projectAdded(ProjectViewerEvent evt)
    {
    }

    public void projectLoaded(ProjectViewerEvent evt)
    {
        Log.log(Log.DEBUG, this, "Project Loaded: " + evt.getProject());
        if (!AntFarmPlugin.useProjectBridge())
            return;

        // Get the project from the event
        VPTProject project = evt.getProject();

        // Now the build file
        File buildFile = getBuildFile(project);

        if (buildFile != null)
        {
            AntFarmPlugin.getAntFarm(jEdit.getActiveView()).addAntBuildFile(buildFile.getAbsolutePath());
            addWindow(ProjectPlugin.NAME);

            //check if the main project build file is set
            //if not, set it to this file
            String cbf = project.getProperty(AntFarmPlugin.OPTION_PREFIX + "pv.projectAntScript");
            if ((cbf == null) || (cbf.equals("")))
            {
                project.setProperty(AntFarmPlugin.OPTION_PREFIX + "pv.projectAntScript", buildFile);
                Log.log(Log.DEBUG, this, "Project Build File Set: " + buildFile);
            }

        }
    }

    /**
     *
     * @param proj
     *                The projectviewer project
     * @return the build.xml file located in the root of the the project.
     */
    private File getBuildFile(VPTProject proj)
    {
        return new File(proj.getRootPath(), "build.xml");
    }

    /**
     * A shortcut to
     * <code>view.getDockableWindowManager().getDockableWindow(String)</code> .
     *
     * @param name
     *                The name of the window to be returned
     * @return A dockable window with the given name
     */
    private JComponent getWindow(String name)
    {
        return jEdit.getActiveView().getDockableWindowManager().getDockableWindow(name);
    }

    /**
     * A shortcut to
     * <code>view.getDockableWindowManager().addDockableWindow(String)</code> .
     *
     * @param name
     *                The name of the window to be added
     */
    private void addWindow(String name)
    {
        jEdit.getActiveView().getDockableWindowManager().addDockableWindow(name);
    }
}
