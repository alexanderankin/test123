package projectbuilder;
// imports {{{
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.Macros;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.menu.DynamicMenuProvider;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileFilter;

import projectviewer.ProjectViewer;
// }}} imports
public class NewProjectMenu implements DynamicMenuProvider {
	private JMenu projects;
	//private JMenu files;
	public boolean updateEveryTime() {
		return false;
	}
	public void update(JMenu menu) {
		// New Projects menu
		JMenu projects = new JMenu("New Project");
		String templates = EditPlugin.getPluginHome(ProjectBuilderPlugin .class).getPath() + "/templates";
		File templateDir = null;
		File userTemplateDir = null;
		try {
			templateDir = new File(templates);
			userTemplateDir = new File(ProjectBuilderPlugin.userTemplateDir);
		} catch (Exception e) {
			return;
		}
		
		File[] dirs = templateDir.listFiles(new DirFilter());
		File[] userDirs = userTemplateDir.listFiles(new DirFilter());
		
		int total = dirs.length + ((userDirs == null) ? 0 : userDirs.length);
		
		String[] tNames = new String [total];
		for (int i = 0; i < total; i++) {
			File dir = (i<dirs.length) ? dirs[i] : userDirs[i-dirs.length];
			String name = dir.getName();
			tNames[i] = name.replace("_", " ");
		}
		
		for (int j = 0; j < tNames.length; j++) {
			File dir = (j<dirs.length) ? dirs[j] : userDirs[j-dirs.length];
			String iconUrl = dir.getPath() + "/menu-icon.png";
			JMenuItem p = new JMenuItem(tNames[j], new ImageIcon(iconUrl));
			String t = tNames[j].replace(" ", "_");
			p.addActionListener(new ItemListener(t));
			projects.add(p);
		}
		menu.add(projects, 0);
	}
}
class DirFilter implements FileFilter {
	public boolean accept(File file) {
		return file.isDirectory();
	}
}
class ItemListener implements ActionListener {
	private String t;
	public ItemListener(String t) {
		this.t = t;
	}
	public void actionPerformed(ActionEvent ae) {
		try {
			ProjectBuilderPlugin plugin = (ProjectBuilderPlugin) jEdit.getPlugin("projectbuilder.ProjectBuilderPlugin");
			plugin.createNewProject(jEdit.getActiveView(), t);
		} catch (Exception e) {
			Macros.message(jEdit.getActiveView(), e.toString());
		}
	}
}
