package projectbuilder.actions;
// imports {{{
import projectbuilder.ProjectBuilderPlugin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Properties;
import java.util.Enumeration;
import java.io.File;
import java.io.FileInputStream;

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
 * This class provides a context menu for ProjectViewer that lets the  user
 * convert an existing project to a type defined by a template
 */

public class ConvertProjectMenu extends projectviewer.action.Action {
	private HashMap<JMenuItem, String> map;
	private VPTProject project;
	public String getText() {
		return "Convert Project";
	}
	public JComponent getMenuItem() {
		if (cmItem == null) {
			cmItem = new JMenu(getText());
		}
		return cmItem;
	}
	public void prepareForNode(VPTNode node) {
		if (!(node instanceof VPTProject)) {
			cmItem.setVisible(false);
			return;
		}
		project = (VPTProject) node;
		JMenu menu = (JMenu) cmItem;
		menu.removeAll();
		menu.setVisible(true);
		map = new HashMap<JMenuItem, String>();
		JMenuItem none = new JMenuItem("None");
		none.addActionListener(this);
		menu.add(none);
		map.put(none, "null");
		if (project.getProperty("project.type") == null)
			none.setEnabled(false);
		ArrayList list = projectbuilder.ProjectBuilderPlugin.getTemplateNames();
		for (int i = 0; i < list.size(); i++) {
			String name = (String) list.get(i);
			JMenuItem item = new JMenuItem(name.replace("_", " "));
			item.addActionListener(this);
			menu.add(item);
			map.put(item, name);
			if (name.equals(project.getProperty("project.type")))
				item.setEnabled(false);
		}
	}
	public void actionPerformed(ActionEvent e) {
		JMenuItem item = (JMenuItem) e.getSource();
		String name = map.get(item);
		if (name.equals("null")) {
			// Remove existing type
			project.removeProperty("project.type");
			project.setIconPath(null);
		} else {
			String template_dir = projectbuilder.ProjectBuilderPlugin.findTemplateDir(name);
			if (template_dir != null) {
				try {
					Properties new_props = new Properties();
					new_props.load(new FileInputStream(template_dir+"/project.props"));
					for (Enumeration en = new_props.propertyNames(); en.hasMoreElements(); ) {
						String prop = (String) en.nextElement();
						project.setProperty(prop, new_props.getProperty(prop));
					}
					project.setProperty("project.type", name);
					project.setProperty("project.template.dir", template_dir);
					project.setIconPath(template_dir+"/menu-icon.png");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
}
