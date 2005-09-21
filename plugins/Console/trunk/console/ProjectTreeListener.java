package console;

import javax.swing.JTree;

import org.gjt.sp.jedit.EditAction;
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
	static boolean registered = false;
	static boolean onProjectChange;
	static ProjectTreeListener instance;
	static boolean onNodeSelection;
	VPTNode lastNode;
	VPTProject lastProject;
	

	boolean isValid;

	private ProjectTreeListener()
	{
		update();
	}

	private void register() {
		try 
		{
			View view = jEdit.getActiveView();
			DockableWindowManager wm = view.getDockableWindowManager();
			ProjectViewer viewer = (ProjectViewer) wm.getDockable("projectviewer");
			viewer.addProjectViewerListener(this, null);
			registered = true;
		}
		catch (Exception e)
		{}

	}
	
	public static void reset()
	{
		if (instance == null)
		{
			instance = new ProjectTreeListener();
		}
	}

	private void update()
	{
		onProjectChange = jEdit
				.getBooleanProperty("console.changedir.pvchange");
		onNodeSelection = jEdit
				.getBooleanProperty("console.changedir.pvselect");
		register();
		
	}

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
				} catch (InterruptedException ie)
				{
				}
				View view = jEdit.getActiveView();
				EditAction action = jEdit.getAction("chdir-pv-root");
				action.invoke(view);
			}
		}.start();
	}

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
			EditAction action = jEdit.getAction("chdir-pv-selected");
			action.invoke(view);
			lastNode = newNode;
		}
	}
}
