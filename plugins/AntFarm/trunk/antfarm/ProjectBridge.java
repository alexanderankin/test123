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

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.util.Log;

import plugin.integration.PluginBridge;
import projectviewer.*;
import projectviewer.event.*;

/**
 *  Bridge AntFarm to ProjectViewer.
 *
 * @author     steinbeck
 * @created    27. August 2001
 */
public class ProjectBridge
	 implements PluginBridge, ProjectViewerListener
{

	private AntFarmPlugin plugin;
	private View view;


	/**
	 *  Enable the bridge.
	 *
	 * @param  srcPlugin  The source plugin
	 * @param  tgtPlugin  The target plugin
	 * @param  aView      A view to work on
	 * @return            True if the ProjectViewer plugin is visible
	 */
	public boolean enable( EditPlugin srcPlugin, EditPlugin tgtPlugin, View aView )
	{
		view = aView;
		if ( !isProjectViewerVisible() ) {
			return false;
		}
		plugin = (AntFarmPlugin) srcPlugin;

		ProjectViewer projectViewer = (ProjectViewer) getWindow( ProjectPlugin.NAME );
		projectViewer.addProjectViewerListener( this );
		return true;
	}


	/**
	 *  Receive notification that the project is loaded.
	 *
	 * @param  evt  A ProjectViewerEvent to evaluate
	 */
	public void projectLoaded( ProjectViewerEvent evt )
	{
		//Log.log( Log.DEBUG, this, "Project Loaded: " + evt.getProject() );
		if ( !plugin.useProjectBridge() )
			return;

		Project project = evt.getProject();
		ProjectFile buildFile = project.getRoot().getFile( "build.xml" );
		if ( buildFile != null ) {
			getAntFarm().addAntBuildFile( buildFile.toFile().getAbsolutePath() );
			addWindow( ProjectPlugin.NAME );
		}
	}


	/**
	 *  A shortcut to <code>view.getDockableWindowManager().getDockableWindow(String)</code>
	 *  .
	 *
	 * @param  name  The name of the window to be returned
	 * @return       A dockable window with the given name
	 */
	protected DockableWindow getWindow( String name )
	{
		return view.getDockableWindowManager().getDockableWindow( name );
	}


	/**
	 *  A shortcut to <code>view.getDockableWindowManager().isDockableWindowVisible(String)</code>
	 *  .
	 *
	 * @param  name  The name of the window which is checked to be visible
	 * @return       true, if the window is visible
	 */
	protected boolean isWindowVisible( String name )
	{
		return view.getDockableWindowManager().isDockableWindowVisible( ProjectPlugin.NAME );
	}


	/**
	 *  A shortcut to <code>view.getDockableWindowManager().addDockableWindow(String)</code>
	 *  .
	 *
	 * @param  name  The name of the window to be added
	 */
	protected void addWindow( String name )
	{
		view.getDockableWindowManager().addDockableWindow( name );
	}


	/**
	 *  Returns the instance of {@link AntFarm} for the given view.
	 *
	 * @return    The AntFarm object
	 */
	private AntFarm getAntFarm()
	{
		addWindow( AntFarmPlugin.NAME );
		return (AntFarm) getWindow( AntFarmPlugin.NAME );
	}


	/**
	 *  Returns <code>true</code> if the project viewer window is opened.
	 *
	 * @return    True, if the ProjectViewer is visible
	 */
	private boolean isProjectViewerVisible()
	{
		return isWindowVisible( ProjectPlugin.NAME );
	}

}

