package projects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import options.ProjectsOptionPane;

import org.gjt.sp.jedit.View;

import projectviewer.ProjectManager;
import projectviewer.ProjectViewer;
import projectviewer.event.ProjectEvent;
import projectviewer.event.ProjectListener;
import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;
import ctags.CtagsInterfacePlugin;

public class ProjectWatcher implements ProjectListener {

	public ProjectWatcher() {
		if (ProjectsOptionPane.getAutoUpdateProjects()) {
			Vector<String> projects = ProjectsOptionPane.getProjects();
			for (int i = 0; i < projects.size(); i++)
				watchProject(projects.get(i));
		}
	}

	@SuppressWarnings("unchecked")
	public Vector<String> getFiles(String project) {
		ProjectManager pm = ProjectManager.getInstance();
		VPTProject p = pm.getProject(project);
		if (p == null)
			return null;
		Vector<String> files = new Vector<String>();
		Iterator<VPTNode> nodes = p.getOpenableNodes().iterator();
		while (nodes.hasNext()) {
			VPTNode node = nodes.next();
			files.add(node.getNodePath());
		}
		return files;
	}

	@SuppressWarnings("unchecked")
	public Vector<String> getProjects() {
		ProjectManager pm = ProjectManager.getInstance();
		Vector<String> projects = new Vector<String>();
		Iterator<VPTProject> it = pm.getProjects();
		while (it.hasNext()) {
			VPTProject proj = it.next();
			projects.add(proj.getName());
		}
		return projects;
	}

	public String getActiveProject(View view) {
		VPTProject project = ProjectViewer.getActiveProject(view);
		if (project == null)
			return null;
		return project.getName();
	}
	
	public void watchProject(String project) {
		VPTProject proj = ProjectManager.getInstance().getProject(project);
		if (proj == null)
			return;
		proj.addProjectListener(this);
	}
	public void unwatchProject(String project) {
		
	}
	
	// ProjectListener methods
	
	public void fileAdded(ProjectEvent pe) {
		Vector<String> added = new Vector<String>();
		added.add(pe.getAddedFile().getNodePath());
		CtagsInterfacePlugin.updateProject(pe.getProject().getName(),
			added, null);
	}

	public void fileRemoved(ProjectEvent pe) {
		Vector<String> removed = new Vector<String>();
		removed.add(pe.getRemovedFile().getNodePath());
		CtagsInterfacePlugin.updateProject(pe.getProject().getName(),
			null, removed);
	}

	@SuppressWarnings("unchecked")
	public void filesAdded(ProjectEvent pe) {
		Vector<String> added = new Vector<String>();
		ArrayList<VPTFile> nodes = pe.getAddedFiles();
		for (int i = 0; i < nodes.size(); i++)
			added.add(nodes.get(i).getNodePath());
		CtagsInterfacePlugin.updateProject(pe.getProject().getName(),
			added, null);
	}

	@SuppressWarnings("unchecked")
	public void filesRemoved(ProjectEvent pe) {
		Vector<String> removed = new Vector<String>();
		ArrayList<VPTFile> nodes = pe.getRemovedFiles();
		for (int i = 0; i < nodes.size(); i++)
			removed.add(nodes.get(i).getNodePath());
		CtagsInterfacePlugin.updateProject(pe.getProject().getName(),
			null, removed);
	}

	public void propertiesChanged(ProjectEvent pe) {
		// TODO Auto-generated method stub
		
	}
}
