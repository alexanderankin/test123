package projects;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;

import ctags.CtagsInterfacePlugin;

import projectviewer.ProjectManager;
import projectviewer.event.ProjectUpdate;
import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;

public class ProjectWatcher implements EBComponent {

	public ProjectWatcher() {
		EditBus.addToBus(this);
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
		Iterator<VPTProject> it = pm.getProjects();
		while (it.hasNext()) {
			VPTProject proj = it.next();
			projects.add(proj.getName());
		}
		return projects;
	}

	private Vector<String> getFileList(List<VPTFile> list) {
		if (list == null)
			return null;
		Vector<String> files = new Vector<String>();
		Iterator<VPTFile> it = list.iterator();
		while (it.hasNext()) {
			VPTFile f = it.next();
			files.add(f.getNodePath());
		}
		return files;
	}
	public void handleMessage(EBMessage message) {
		if (message instanceof ProjectUpdate) {
			ProjectUpdate msg = (ProjectUpdate) message;
			String project = msg.getProject().getName();
			if (msg.getType() == ProjectUpdate.Type.FILES_CHANGED) {
				CtagsInterfacePlugin.updateProject(project,
					getFileList(msg.getAddedFiles()),
					getFileList(msg.getRemovedFiles()));
			}
		}
		return;
	}
}
