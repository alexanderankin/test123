package console;


import java.util.LinkedList;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.gjt.sp.jedit.BeanShell;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.util.Log;

import bsh.NameSpace;
import bsh.UtilEvalError;

import projectviewer.ProjectViewer;
import projectviewer.event.ProjectViewerEvent;
import projectviewer.event.ProjectViewerListener;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;

/**
 * 
 * Listener of ProjectViewer node selection 
 * events, to trigger beanshell scripts as actions in response.
 *
 * @author ezust
 *
 */

public class PVListener  implements TreeSelectionListener 
{
	boolean onProjectChange;
	boolean onNodeSelection;
	ProjectViewer projectViewer;
	NameSpace nameSpace;
	JTree projectTree;
	VPTNode lastNode;
	VPTProject lastProject;
	View view;
	
	private static LinkedList<PVListener> oldList = new LinkedList<PVListener>();

	/**
	 * Loads persistent properties and resets the listening action states.
	 *
	 */
	public static void reset() {
		for(PVListener listeners: oldList) {
			try {
			   listeners.finalize();
			}
			catch (Throwable e) {}
			oldList.clear();
			for (View v: jEdit.getViews() ){
				  PVListener pva = new PVListener(v);
				  oldList.add(pva);
			}
		}
	}
	
	private PVListener(View v) {
		view=v;
		DockableWindowManager dwm = view.getDockableWindowManager();
		projectViewer = (ProjectViewer) dwm.getDockable("projectviewer");
		lastNode = null;
		if (projectViewer == null) {
			Log.log(Log.ERROR, PVListener.class, "No ProjectViewer found.");
			return;
		}
		nameSpace = org.gjt.sp.jedit.BeanShell.getNameSpace();
	    Console con = (Console) dwm.getDockable("console");
	    Output output = con.getOutput();
	    try {
	    	nameSpace.setVariable("console",con);
	    	nameSpace.setVariable("output",output);
	    }
	    catch (UtilEvalError uee) {
	    	Log.log(Log.WARNING, PVListener.class, uee);
	    }
		onProjectChange = jEdit.getBooleanProperty("console.changedir.pvchange");
		onNodeSelection = jEdit.getBooleanProperty("console.changedir.pvselect");
		
//		projectViewer.addProjectViewerListener(this, view);
		projectTree = projectViewer.getCurrentTree();
		projectTree.addTreeSelectionListener(this);
		
	}
/*
	public void groupActivated(ProjectViewerEvent evt)
	{	
		Log.log(Log.ERROR, PVListener.class, evt.toString());
		ProjectViewer newViewer = evt.getProjectViewer();
		VTPProject newProject =  newViewer.getActiveProject(view)

		VPTNode newNode = newViewer.getSelectedNode();
		
		if (onNodeSelection && (newNode != lastNode) ) {
			BeanShell.eval(view, nameSpace, "changeToPvCurrent();");
			lastNode = newNode;
		}
		if (onProjectChange && (newProject  != lastProject)) {
			BeanShell.eval(view, nameSpace, "changeToPvRoot();");
			projectViewer = newViewer;
		}
	}
	*/
	protected void finalize() throws Throwable
	{
		if (projectViewer == null) return;
		projectTree.removeTreeSelectionListener(this);
	}


	public void valueChanged(TreeSelectionEvent e)
	{
		VPTProject newProject = projectViewer.getActiveProject(view);
		VPTNode newNode = projectViewer.getSelectedNode();
		
		if (onNodeSelection && (newNode != lastNode) ) {
			BeanShell.eval(view, nameSpace, "changeToPvCurrent();");
			lastNode = newNode;
		}
		else if (onProjectChange && (newProject != lastProject)) {
			BeanShell.eval(view, nameSpace, "changeToPvRoot();");
			lastProject = newProject;
		}
	}    
}

