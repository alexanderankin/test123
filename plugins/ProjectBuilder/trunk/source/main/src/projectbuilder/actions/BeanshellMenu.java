package projectbuilder.actions;
// imports {{{
import projectbuilder.ProjectBuilderPlugin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.StringTokenizer;
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
import org.gjt.sp.jedit.bsh.NameSpace;
// }}} imports
/**
 * This class provides a small context menu for Project Viewer
 * with menu items for running template-defined beanshell scripts
 */

public class BeanshellMenu extends projectviewer.action.Action {
	private VPTProject project;
	private HashMap<JMenuItem, String> map;
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
		map = new HashMap<JMenuItem, String>();
		menu.setText(type.replace("_", " "));
		String scripts = project.getProperty("projectbuilder.bsh.menu");
		if (scripts == null) {
			JMenuItem none = new JMenuItem("No scripts found");
			none.setEnabled(false);
			menu.add(none);
			return;
		}
		StringTokenizer tokenizer = new StringTokenizer(scripts);
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (token.equals("-")) {
				menu.addSeparator();
				continue;
			}
			String prefix = "projectbuilder.bsh.";
			JMenuItem item = new JMenuItem(project.getProperty(prefix+token+".label"));
			item.addActionListener(this);
			map.put(item, project.getProperty(prefix+token+".script"));
			menu.add(item);
		}
	}
	public void actionPerformed(ActionEvent e) {
		try {
			JMenuItem item = (JMenuItem) e.getSource();
			String script = project.getProperty("project.template.dir")+File.separator+map.get(item);
			NameSpace namespace = BeanShell.getNameSpace();
			namespace.setVariable("project", project);
			BeanShell.runScript(viewer.getView(), script, null, namespace);
		} catch (Exception _e) {}
	}
}
