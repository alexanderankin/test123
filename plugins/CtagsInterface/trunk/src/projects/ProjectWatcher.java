package projects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import options.ProjectsOptionPane;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

import projectviewer.ProjectManager;
import projectviewer.ProjectViewer;
import projectviewer.event.ProjectEvent;
import projectviewer.event.ProjectListener;
import projectviewer.event.ProjectViewerAdapter;
import projectviewer.event.ProjectViewerEvent;
import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;
import ctags.CtagsInterfacePlugin;
import db.TagDB;

public class ProjectWatcher extends ProjectViewerAdapter implements ProjectListener {

	Set<String> watched;
	Set<View> views;
	
	public ProjectWatcher() {
		watched = new HashSet<String>();
		views = new HashSet<View>();
		if (ProjectsOptionPane.getAutoUpdateProjects())
			updateWatchers();
		if (ProjectsOptionPane.getTrackProjectList())
			setProjectListTracking(true);
	}

	public void setProjectListTracking(boolean on) {
		Iterator<View> it = views.iterator();
		while (it.hasNext()) {
			View view = it.next();
			ProjectViewer.removeProjectViewerListener(this, view);
		}
		views.clear();
		if (on) {
			View [] v = jEdit.getViews();
			for (int i = 0; i < v.length; i++) {
				ProjectViewer.addProjectViewerListener(this, v[i]);
				views.add(v[i]);
			}
		}
	}
	
	public void projectAdded(ProjectViewerEvent evt) {
		CtagsInterfacePlugin.insertOrigin(TagDB.PROJECT_ORIGIN, evt.getProject().getName());
	}

	public void projectRemoved(ProjectViewerEvent evt) {
		CtagsInterfacePlugin.deleteOrigin(TagDB.PROJECT_ORIGIN, evt.getProject().getName());
	}

	public void updateWatchers() {
		Vector<String> projects = ProjectsOptionPane.getProjects();
		Iterator<String> all = getProjects().iterator();
		while (all.hasNext()) {
			String project = all.next();
			if (projects.contains(project)) {
				if (! watched.contains(project)) {
					watchProject(project);
					watched.add(project);
				}
			}
			else if (watched.contains(project)) {
				unwatchProject(project);
				watched.remove(project);
			}
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
	
	private void watchProject(String project) {
		System.err.println("Watch " + project);
		VPTProject proj = ProjectManager.getInstance().getProject(project);
		if (proj == null)
			return;
		proj.addProjectListener(this);
	}
	private void unwatchProject(String project) {
		System.err.println("Unwatch " + project);
		VPTProject proj = ProjectManager.getInstance().getProject(project);
		if (proj == null)
			return;
		proj.removeProjectListener(this);
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
