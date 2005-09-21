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
 * Listener of ProjectViewer node selection 
 * events, to trigger console beanshell scripts as actions in response.
 *
 * @author ezust
 *
 */

public class ProjectTreeListener  extends ProjectViewerAdapter
{
	static boolean onProjectChange;
	static boolean onNodeSelection;
	ProjectViewer projectViewer;
	NameSpace nameSpace;
	JTree projectTree;
	VPTNode lastNode;
	VPTProject lastProject;
	static ProjectTreeListener instance;
	boolean isValid;

	private ProjectTreeListener() 
	{
			init();
	}
	
	public static void reset() 
	{
		if (instance == null)   {
			instance = new ProjectTreeListener();
			instance.projectViewer.addProjectViewerListener(instance, null);
		}
		else {
			update();
			return;
		}
		if (!instance.isValid) try
		{
				instance.init();
		}
		catch (Exception e) {}
	}

	static void update() {
		onProjectChange = jEdit.getBooleanProperty("console.changedir.pvchange");
		onNodeSelection = jEdit.getBooleanProperty("console.changedir.pvselect");
	}
	
	void init() 
	{
		try {
			isValid = false;
			View view=jEdit.getActiveView();
			if (view == null) return;
			DockableWindowManager dwm = view.getDockableWindowManager();
			projectViewer = (ProjectViewer) dwm.getDockable("projectviewer");
			lastNode = null;
			if (projectViewer == null) {
				Log.log(Log.WARNING, this, "No ProjectViewer found.");
				return;
			}
/*			nameSpace = org.gjt.sp.jedit.BeanShell.getNameSpace(); */
/*		    Console con = (Console) dwm.getDockable("console");
		    if (con == null) return;
		    Output output = con.getOutput();
		    try {
		    	nameSpace.setVariable("console",con);
		    	nameSpace.setVariable("output",output);
		    }
		    catch (UtilEvalError uee) {
		    	Log.log(Log.WARNING, ProjectTreeListener.class, uee);
		    }
		    */
		    
		    update();
			
			isValid = true;
		}
		catch (Exception e) {
			Log.log( Log.WARNING, e, "Unable to start TreeListener ");
		}
	}

	
	/**
	 * On project change...
	 */
	public void projectLoaded(ProjectViewerEvent evt)
	{
		if (!isValid) 
		{
			init(); 
		}
		if (!onProjectChange ) return;
		new Thread() {
			public void run() {
				try {
					sleep (500);
				} catch (InterruptedException ie) {}
				View view=jEdit.getActiveView();
				EditAction action = jEdit.getAction("chdir-pv-root");
				action.invoke(view);
			}
		}.start();
	}

	public void nodeSelected(ProjectViewerEvent evt)
	{
		if (!isValid) 
		{
			init(); 
		}
		if (!onNodeSelection ) return;
		VPTNode newNode = evt.getNode();
//		VPTProject newProject = newNode.findProjectFor(newNode);
//		VPTProject newProject = evt.getProject();
//		VPTProject newProject = projectViewer.getActiveProject(view);
//		VPTNode newNode = projectViewer.getSelectedNode();
		
		if (onNodeSelection && (newNode != lastNode) ) 
		{
			View view = jEdit.getActiveView();
			EditAction action = jEdit.getAction("chdir-pv-selected");
			action.invoke(view);
			lastNode = newNode;
		}
	}
}

