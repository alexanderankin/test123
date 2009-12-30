package projectbuilder;
// imports {{{
import org.gjt.sp.jedit.jEdit;
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
// }}} imports
public class NewProjectMenu implements DynamicMenuProvider {
	public boolean updateEveryTime() { return false; }
	public void update(JMenu menu) {
		JMenu projects = new JMenu("New Project");
		String templates = EditPlugin.getPluginHome(ProjectBuilderPlugin.class).getPath()+"/templates";
		File templateDir = null;
		try { templateDir = new File(templates); }
		catch (Exception e) { return; }
		
		File[] dirs = templateDir.listFiles(new DirFilter());
		
		String[] tNames = new String[dirs.length];
		for (int i = 0; i<dirs.length; i++) {
			tNames[i] = dirs[i].getName().replace("_", " ");
		}
		
		for (int j = 0; j<tNames.length; j++) {
			String iconUrl = dirs[j].getPath()+"/menu-icon.png";
			JMenuItem p = new JMenuItem(tNames[j], new ImageIcon(iconUrl));
			String t = tNames[j].replace(" ", "_");
			p.addActionListener(new ItemListener(t));
			projects.add(p);
		}
		menu.add(projects);
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
