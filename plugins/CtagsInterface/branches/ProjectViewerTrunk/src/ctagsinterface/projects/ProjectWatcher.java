package ctagsinterface.projects;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.View;

import projectviewer.ProjectManager;
import projectviewer.ProjectViewer;
import projectviewer.event.ProjectUpdate;
import projectviewer.event.StructureUpdate;
import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;
import ctagsinterface.db.TagDB;
import ctagsinterface.main.CtagsInterfacePlugin;
import ctagsinterface.options.ProjectsOptionPane;

public class ProjectWatcher implements EBComponent {

	Set<String> watched;
	
	public ProjectWatcher() {
		watched = new HashSet<String>();
		if (ProjectsOptionPane.getAutoUpdateProjects())
			updateWatchers();
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

	public Vector<String> getProjects() {
		ProjectManager pm = ProjectManager.getInstance();
		Vector<String> projects = new Vector<String>();
		Iterator<VPTProject> it = pm.getProjects().iterator();
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
		VPTProject proj = ProjectManager.getInstance().getProject(project);
		if (proj == null)
			return;
		watched.add(project);
	}
	private void unwatchProject(String project) {
		VPTProject proj = ProjectManager.getInstance().getProject(project);
		if (proj == null)
			return;
		watched.remove(project);
	}
	
	// ProjectListener methods
	
	public void handleFilesChanged(ProjectUpdate pu) {
		Vector<String> removed = new Vector<String>();
		List<VPTFile> nodes = pu.getRemovedFiles();
		for (int i = 0; i < nodes.size(); i++)
			removed.add(nodes.get(i).getNodePath());
		Vector<String> added = new Vector<String>();
		nodes = pu.getAddedFiles();
		for (int i = 0; i < nodes.size(); i++)
			added.add(nodes.get(i).getNodePath());
		CtagsInterfacePlugin.updateProject(pu.getProject().getName(),
			added, removed);
	}

	public void handlePropertiesChanged(ProjectUpdate pu) {
		// TODO Auto-generated method stub
	}

	public void handleStructureUpdate(StructureUpdate su) {
		if (su.getType() == StructureUpdate.Type.PROJECT_ADDED) {
			String name = su.getNode().getName();
			CtagsInterfacePlugin.insertOrigin(TagDB.PROJECT_ORIGIN, name);
			watched.add(name);
		}
		else if (su.getType() == StructureUpdate.Type.PROJECT_REMOVED) {
			String name = su.getNode().getName();
			CtagsInterfacePlugin.deleteOrigin(TagDB.PROJECT_ORIGIN, name);
			watched.remove(name);
		}
	}
	
	public void handleMessage(EBMessage message) {
		if (message instanceof ProjectUpdate) {
			ProjectUpdate pu = (ProjectUpdate) message;
			String name = ((ProjectUpdate) message).getProject().getName();
			if (! watched.contains(name))
				return;
			if (pu.getType() == ProjectUpdate.Type.FILES_CHANGED)
				handleFilesChanged(pu);
			else if (pu.getType() == ProjectUpdate.Type.PROPERTIES_CHANGED)
				handlePropertiesChanged(pu);
		} else if (message instanceof StructureUpdate) {
			if (! ProjectsOptionPane.getTrackProjectList())
				return;
			StructureUpdate su = (StructureUpdate) message;
			if (su.getType() == StructureUpdate.Type.PROJECT_ADDED)
				handleStructureUpdate(su);
		}
	}

}
