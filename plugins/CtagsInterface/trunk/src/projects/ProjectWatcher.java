package projects;

import java.util.Iterator;
import java.util.Vector;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.jEdit;

import ctags.CtagsInterfacePlugin;

import projectviewer.ProjectManager;
import projectviewer.vpt.VPTProject;

public class ProjectWatcher {

	ProjectManager pm;
	
	public ProjectWatcher() {
		EditPlugin p = jEdit.getPlugin("projectviewer.ProjectPlugin",false);
		if(p != null) {
			pm = ProjectManager.getInstance();
			Iterator<VPTProject> it = pm.getProjects();
			Vector<String> projects = new Vector<String>();
			while (it.hasNext()) {
				VPTProject proj = it.next();
				projects.add(proj.getName());
			}
			CtagsInterfacePlugin.setProjects(projects);
		}

	}
}
