package projectbuilder.actions;
// imports {{{
import projectbuilder.ProjectBuilderPlugin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.io.File;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JComponent;

import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.Macros;
import org.gjt.sp.jedit.BeanShell;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.bsh.NameSpace;
// }}} imports
/**
 * This class provides a small context menu for Project Viewer
 * with menu items for running template-defined beanshell scripts
 */

public class BeanshellContextMenu extends projectviewer.action.Action {
	private VPTProject project;
	public String getText() {
		return "Project Builder";
	}
	public JComponent getMenuItem() {
		if (cmItem == null) {
			cmItem = new JMenu(getText());
		}
		return cmItem;
	}
	public void prepareForNode(VPTNode node) {
		JMenu menu = (JMenu) cmItem;
		if (node instanceof VPTProject) {
			project = (VPTProject) node;
		} else {
			project = viewer.getActiveProject(viewer.getView());
			if (project == null) {
				menu.setVisible(false);
				return;
			}
		}
		String type = project.getProperty("project.type");
		if (type == null) {
			menu.setVisible(false);
			return;
		}
		menu.setVisible(true);
		menu.removeAll();
		menu.setText(type.replace("_", " "));
		ArrayList<ArrayList<String>> list = projectbuilder.ProjectBuilderPlugin.getBeanshellScripts(project);
		for (ArrayList<String> script : list) {
			String name = script.get(0);
			if (name.equals("-")) {
				menu.addSeparator();
				continue;
			}
			JMenuItem item = new JMenuItem(name);
			if (script.get(1) == null) {
				item.setEnabled(false);
			} else {
				final String bsh = MiscUtilities.constructPath(project.getProperty("project.template.dir"), script.get(1));
				item.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							try {
								NameSpace namespace = BeanShell.getNameSpace();
								namespace.setVariable("project", project);
								BeanShell._runScript(viewer.getView(), bsh, null, namespace);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
				});
			}
			menu.add(item);
		}
	}
	public void actionPerformed(ActionEvent e) {}
}
