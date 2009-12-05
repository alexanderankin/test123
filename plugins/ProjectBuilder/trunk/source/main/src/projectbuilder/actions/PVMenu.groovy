package projectbuilder.actions
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.JMenu
import javax.swing.JMenuItem
import javax.swing.JComponent
import projectviewer.ProjectViewer
import projectviewer.vpt.VPTNode
import projectviewer.vpt.VPTProject
import org.gjt.sp.jedit.jEdit as JEDIT
import org.gjt.sp.jedit.Macros
/**
 * This class provides a small context menu for Project Viewer
 * with menu items for building and running the active project
 */

public class PVMenu extends projectviewer.action.Action {
	protected JMenu menu
	protected JMenuItem build
	protected JMenuItem run
	protected VPTProject proj
	public PVMenu() {
		menu = new JMenu("Project")
		build = new JMenuItem("Build")
		run = new JMenuItem("Run")
		build.addActionListener(this)
		run.addActionListener(this)
		menu.add(build)
		menu.add(run)
	}
	public String getText() {
		return "Project Builder"
	}
	
	public JComponent getMenuItem() {
		return menu
	}
	
	public void prepareForNode(VPTNode node) {
		// FIXME: saving the project here in an instance variable doesn't work for some reason
		/*
		proj = VPTNode.findProjectFor(node)
		if (proj == null) {
			proj = ProjectViewer.getActiveProject(JEDIT.getActiveView())
		}
		*/
	}
	
	public void actionPerformed(ActionEvent e) {
		// Use this code if proj can ever be anything besides null
		/*
		if (e.getSource() == build) {
			JEDIT.getPlugin("projectbuilder.ProjectBuilderPlugin").buildProject(JEDIT.getActiveView(), proj)
		} else if (e.getSource() == run) {
			JEDIT.getPlugin("projectbuilder.ProjectBuilderPlugin").runProject(JEDIT.getActiveView(), proj)
		}
		*/
		if (e.getSource() == build) {
			JEDIT.getPlugin("projectbuilder.ProjectBuilderPlugin").buildProject(JEDIT.getActiveView())
		} else if (e.getSource() == run) {
			JEDIT.getPlugin("projectbuilder.ProjectBuilderPlugin").runProject(JEDIT.getActiveView())
		}
	}
}
