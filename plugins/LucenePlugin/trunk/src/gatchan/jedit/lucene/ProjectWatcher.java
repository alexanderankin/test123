package gatchan.jedit.lucene;

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.jEdit;

import projectviewer.event.StructureUpdate;

public class ProjectWatcher implements EBComponent
{
	public ProjectWatcher() {
		EditBus.addToBus(this);
	}

	public void handleMessage(EBMessage message) {
		if (message instanceof StructureUpdate) {
			StructureUpdate su = (StructureUpdate) message;
			if (su.getType() == StructureUpdate.Type.PROJECT_REMOVED)
				checkRemoveProjectIndex(su.getNode().getName());
		}
	}

	private void checkRemoveProjectIndex(String project) {
		Index index = LucenePlugin.instance.getIndex(project);
		if (index == null)
			return;
		int res = JOptionPane.showConfirmDialog(jEdit.getActiveView(), 
			"Remove Lucene index of project '" + project + "'?",
			"Lucene plugin", JOptionPane.YES_NO_OPTION);
		if (res == JOptionPane.YES_OPTION)
			LucenePlugin.instance.removeIndex(project);
	}
	
	public void stop() {
		EditBus.removeFromBus(this);
	}
}
