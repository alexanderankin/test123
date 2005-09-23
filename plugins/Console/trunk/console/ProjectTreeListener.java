/*
 * ProjectTreeListener.java - for listening to ProjectViewer
 * events in the Console.
   
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2001, 2003 Slava Pestov
 * Java 1.5 version (c) 2005 by Alan Ezust
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package console;

// {{{ imports
import javax.swing.JTree;

import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.PluginJAR;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.util.Log;

import bsh.NameSpace;
import bsh.UtilEvalError;

import projectviewer.ProjectViewer;
import projectviewer.event.ProjectViewerAdapter;
import projectviewer.event.ProjectViewerEvent;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;
// }}}

// {{{ ProjectTreeListener class 
/**
 * 
 * Listener of ProjectViewer node selection events, to trigger console beanshell
 * scripts as actions in response.
 * 
 * @author ezust
 * 
 */

public class ProjectTreeListener extends ProjectViewerAdapter
{

	// {{{ Reset() - static singleton
	/**
	 * Creates if necessary loads properties Registers if necessary
	 * 
	 */
	public static void reset()
	{
		if (instance == null)
		{
			instance = new ProjectTreeListener();
		}
		instance.update();
	}
	// }}}

	// {{{ projectLoaded()
	/**
	 * On project change...
	 */
	public void projectLoaded(ProjectViewerEvent evt)
	{
		if (!onProjectChange)
			return;
		update();
		new Thread()
		{
			public void run()
			{
				try
				{
					sleep(500);
				}
				catch (InterruptedException ie)
				{
				}
				View view = jEdit.getActiveView();
				EditAction action = jEdit
						.getAction("chdir-pv-root");
				action.invoke(view);
			}
		}.start();
	}
	// }}}
	
	// {{{ nodeSelected ()
	
	public void nodeSelected(ProjectViewerEvent evt)
	{
		if (!onNodeSelection)
			return;
		update();
		VPTNode newNode = evt.getNode();
		// VPTProject newProject = newNode.findProjectFor(newNode);
		// VPTProject newProject = evt.getProject();
		// VPTProject newProject = projectViewer.getActiveProject(view);
		// VPTNode newNode = projectViewer.getSelectedNode();

		if (onNodeSelection && (newNode != lastNode))
		{
			View view = jEdit.getActiveView();
			EditAction action = jEdit
					.getAction("chdir-pv-selected");
			action.invoke(view);
			lastNode = newNode;
		}
	}
	// }}}
	
	// {{{ private ctor
	private ProjectTreeListener()
	{
		update();
	}
	// }}}
	
	// {{{ Register() 
	private void register()
	{
		if (registered)
			return;
		try
		{
			View view = jEdit.getActiveView();
			DockableWindowManager wm = view
					.getDockableWindowManager();
			ProjectViewer viewer = (ProjectViewer) wm
					.getDockable("projectviewer");
			ProjectTreeListener[] oldListeners = viewer
					.getListeners(ProjectTreeListener.class);
			PluginJAR jar = jEdit
					.getPlugin("console.ConsolePlugin")
					.getPluginJAR();
			viewer.removeProjectViewerListeners(jar);
			for (ProjectTreeListener ptl : oldListeners)
			{
				viewer.removeProjectViewerListener(ptl, null);
			}
			viewer.addProjectViewerListener(this, null);
			registered = true;
		}
		catch (Exception e)
		{
		}

	}
	// }}}
	
	// {{{ update() 
	private void update()
	{
		onProjectChange = jEdit
				.getBooleanProperty("console.changedir.pvchange");
		onNodeSelection = jEdit
				.getBooleanProperty("console.changedir.pvselect");
		register();

	}
	// }}}
	
	// {{{ Static members 
	static boolean registered = false;

	static ProjectTreeListener instance;

	static boolean onProjectChange;

	static boolean onNodeSelection;

	// }}}
	// {{{ private members 
	private VPTNode lastNode;

	private VPTProject lastProject;
	// }}}
	
}

// }}}
