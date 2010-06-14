package projectbuilder.actions;
//{{{ imports
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.BeanShell;
import org.gjt.sp.jedit.bsh.NameSpace;
import projectviewer.vpt.VPTProject;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.JButton;
//}}}
public class BeanshellToolbar {
	private static HashMap<View, JToolBar> map = new HashMap<View, JToolBar>(); // keeps track of existing toolbars
	public static void create(final View view, final VPTProject project) {
		if (map.get(view) != null) {
			view.removeToolBar(map.get(view));
			map.remove(view);
		}
		ArrayList<ArrayList<String>> list = projectbuilder.ProjectBuilderPlugin.getBeanshellScripts(project);
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(true);
		boolean empty = true;
		for (ArrayList<String> script : list) {
			String name = script.get(0);
			if (name.equals("-")) {
				toolbar.addSeparator();
				continue;
			}
			if (script.get(1) == null) continue;
			JButton item = new JButton(name);
			item.setMargin(new Insets(1, 2, 1, 2));
			final String bsh = MiscUtilities.constructPath(project.getProperty("project.template.dir"), script.get(1));
			item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
							NameSpace namespace = new NameSpace(BeanShell.getNameSpace(), "Project Builder Script");
							namespace.setVariable("project", project);
							BeanShell._runScript(view, bsh, null, namespace);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
			});
			toolbar.add(item);
			empty = false;
		}
		System.out.println("toolbar: "+toolbar+", empty = "+empty);
		if (!empty) {
			view.addToolBar(toolbar);
			map.put(view, toolbar);
		}
	}
	public static void remove(View view) {
		if (map.get(view) != null) {
			view.removeToolBar(map.get(view));
			map.remove(view);
		}
	}
	public static boolean exists(View view) {
		return map.get(view) != null;
	}
}
