package projectbuilder.actions;
// imports {{{
import projectbuilder.ProjectBuilderPlugin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JComponent;

import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Macros;
// }}} imports
/**
 * This class provides a small context menu for Project Viewer
 * with menu items for building and running the active project
 */

public class PVMenu extends projectviewer.action.Action {
	protected JMenu menu;
	protected JMenuItem build;
	protected JMenuItem run;
	protected JMenuItem buildSettings;
	protected JMenuItem runSettings;
	protected VPTProject proj;
	public PVMenu() {
		menu = new JMenu("Project");
		build = new JMenuItem("Build Project");
		run = new JMenuItem("Run Project");
		buildSettings = new JMenuItem("Edit Build Settings...");
		runSettings = new JMenuItem("Edit Run Settings...");
		build.addActionListener(this);
		run.addActionListener(this);
		buildSettings.addActionListener(this);
		runSettings.addActionListener(this);
		menu.add(build);
		menu.add(run);
		menu.addSeparator();
		menu.add(buildSettings);
		menu.add(runSettings);
	}
	public String getText() {
		return "Project Builder";
	}

	public JComponent getMenuItem() {
		return menu;
	}

	public void prepareForNode(VPTNode node) {
		/*
		proj = VPTNode.findProjectFor(node);
		Macros.message(jEdit.getActiveView(), "Set: "+proj);
		if (proj == null) {
			proj = ProjectViewer.getActiveProject(jEdit.getActiveView());
	}
		*/
	}

	public void actionPerformed(ActionEvent e) {
		// Use this code if proj can ever be anything besides null
		/*
		if (e.getSource() == build) {
			jEdit.getPlugin("projectbuilder.ProjectBuilderPlugin").buildProject(jEdit.getActiveView(), proj);
	} else if (e.getSource() == run) {
			jEdit.getPlugin("projectbuilder.ProjectBuilderPlugin").runProject(jEdit.getActiveView(), proj);
	} else if (e.getSource() == buildSettings) {
			jEdit.getPlugin("projectbuilder.ProjectBuilderPlugin").runProject(jEdit.getActiveView(), proj);
		*/
		/*
		if (e.getSource() == build) {
			ProjectBuilderPlugin plugin =
			    (ProjectBuilderPlugin) jEdit.getPlugin("projectbuilder.ProjectBuilderPlugin");
			plugin.buildProject(jEdit.getActiveView());
		} else if (e.getSource() == run) {
			ProjectBuilderPlugin plugin =
			    (ProjectBuilderPlugin) jEdit.getPlugin("projectbuilder.ProjectBuilderPlugin");
			plugin.runProject(jEdit.getActiveView());
		} else if (e.getSource() == buildSettings) {
			ProjectBuilderPlugin plugin =
			    (ProjectBuilderPlugin) jEdit.getPlugin("projectbuilder.ProjectBuilderPlugin");
			plugin.editBuildSettings(jEdit.getActiveView());
		} else if (e.getSource() == runSettings) {
			ProjectBuilderPlugin plugin =
			    (ProjectBuilderPlugin) jEdit.getPlugin("projectbuilder.ProjectBuilderPlugin");
			plugin.editRunSettings(jEdit.getActiveView());
		}
		*/
	}
}
