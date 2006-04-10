/*
 * ProjectTreeListener.java - for listening to ProjectViewer
 * events in the Console.
   
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * (c) 2005 by Alan Ezust
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

import org.gjt.sp.jedit.BeanShell;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.PluginJAR;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.DockableWindowManager;

import bsh.NameSpace;

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
	
	public static void reset() {
		if (instance == null) {
			new ProjectTreeListener();
		}
		instance.update();
	}
	
	public ProjectTreeListener()
	{
		update();
		instance = this;
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
		// if (evt.getProject() == lastProject) return;
		lastProject = evt.getProject();
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
				if (view == null || !view.isVisible()) return;
				String code = "changeToPvRoot(view);";
				NameSpace namespace =  BeanShell.getNameSpace();
				BeanShell.eval(view, namespace, code);
			}
		}.start();
	}
	// }}}
	
	// {{{ nodeSelected ()

	/** called when Nodes are selected */

	public void nodeSelected(ProjectViewerEvent evt)
	{
		if (!onNodeSelection)
			return;
		update();
		VPTNode newNode = evt.getNode();

		if (onNodeSelection && (newNode != lastNode))
		{
			View view = jEdit.getActiveView();
			EditAction action = jEdit.getAction("chdir-pv-selected");
			action.invoke(view);
			lastNode = newNode;
		}
	}
	// }}}
	

	
	// {{{ update()
	/** Reloads properties and updates flags */
	private void update()
	{
		onProjectChange = jEdit
				.getBooleanProperty("console.changedir.pvchange");
		onNodeSelection = jEdit
				.getBooleanProperty("console.changedir.pvselect");


	}
	// }}}
	
	// {{{ Data Members 
	// {{{ Static members 

	static ProjectTreeListener instance;

	static boolean onProjectChange;

	static boolean onNodeSelection;

	// }}}
	// {{{ private members 
	private VPTNode lastNode;

	private VPTProject lastProject;
	// }}}
	// }}}
}

// }}}
