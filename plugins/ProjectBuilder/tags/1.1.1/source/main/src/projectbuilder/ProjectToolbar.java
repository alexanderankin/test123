package projectbuilder;
// imports {{{
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.Box;
import java.util.HashMap;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTProject;
import projectbuilder.command.Entry;
// }}} imports
public class ProjectToolbar extends JPanel {
	private JButton build_btn;
	private JButton run_btn;
	private JComboBox build_box;
	private JComboBox run_box;
	private View view;
	private VPTProject proj;
	public ProjectToolbar(View view, VPTProject proj) {
		this.view = view;
		this.proj = proj;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(Box.createHorizontalGlue());
		add(build_box = new JComboBox());
		add(Box.createRigidArea(new Dimension(10, 0)));
		add(build_btn = new JButton("Build Project"));
		//add(Box.createRigidArea(new Dimension(20, 0)));
		add(Box.createHorizontalGlue());
		//add(new JSeparator(SwingConstants.VERTICAL));
		//add(Box.createRigidArea(new Dimension(20, 0)));
		add(Box.createHorizontalGlue());
		add(run_box = new JComboBox());
		add(Box.createRigidArea(new Dimension(10, 0)));
		add(run_btn = new JButton("Run Project"));
		add(Box.createHorizontalGlue());
		updateBoxes(proj);
		listen();
	}
	
	private void listen() {
		build_box.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (proj == null) return;
				if (build_box.getSelectedItem() == null) {
					//proj.removeProperty("projectBuilder.command.build");
					return;
				}
				proj.setProperty("projectBuilder.command.build",
					((Entry) build_box.getSelectedItem()).getProp());
			}
		});
		run_box.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (proj == null) return;
				if (run_box.getSelectedItem() == null) {
					//proj.removeProperty("projectBuilder.command.run");
					return;
				}
				proj.setProperty("projectBuilder.command.run",
					((Entry) run_box.getSelectedItem()).getProp());
			}
		});
		build_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ProjectBuilderPlugin plugin = (ProjectBuilderPlugin)
					jEdit.getPlugin("projectbuilder.ProjectBuilderPlugin");
				if (proj == null)
					plugin.buildProject(view);
				else
					plugin.buildProject(view, proj);
			}
		});
		run_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ProjectBuilderPlugin plugin = (ProjectBuilderPlugin)
					jEdit.getPlugin("projectbuilder.ProjectBuilderPlugin");
				if (proj == null)
					plugin.runProject(view);
				else
					plugin.runProject(view, proj);
			}
		});
	}
	
	public void updateBoxes(VPTProject p) {
		build_box.removeAllItems();
		run_box.removeAllItems();
		//proj = ProjectViewer.getActiveProject(view);
		if (p == null) {
			build_box.setSelectedItem(null);
			run_box.setSelectedItem(null);
			return;
		}
		String saved = p.getProperty("projectBuilder.command.build");
		boolean boxIsValid = false;
		for (int i = 0; true; i++) {
			String prop = p.getProperty("projectBuilder.command.build."+i);
			if (prop == null) break;
			Entry entry = new Entry(prop);
			build_box.addItem(entry);
			if (prop.equals(saved)) {
				build_box.setSelectedItem(entry);
				boxIsValid = true;
			}
		}
		if (!boxIsValid) build_box.setSelectedItem(null);
		
		saved = p.getProperty("projectBuilder.command.run");
		boxIsValid = false;
		for (int i = 0; true; i++) {
			String prop = p.getProperty("projectBuilder.command.run."+i);
			if (prop == null) break;
			Entry entry = new Entry(prop);
			run_box.addItem(entry);
			if (prop.equals(saved)) {
				run_box.setSelectedItem(entry);
				boxIsValid = true;
			}
		}
		if (!boxIsValid) run_box.setSelectedItem(null);
		
	}

	// Static methods {{{
	public static void create(View view, VPTProject proj) {
		if (viewMap.get(view) != null) return;
		ProjectToolbar toolbar = new ProjectToolbar(view, proj);
		viewMap.put(view, toolbar);
		view.addToolBar(toolbar);
	}
	public static void remove(View view) {
		ProjectToolbar toolbar = viewMap.get(view);
		if (toolbar != null) {
			view.removeToolBar(toolbar);
			viewMap.put(view, null);
		}
	}
	public static final HashMap<View, ProjectToolbar> viewMap =
		new HashMap<View, ProjectToolbar>();
	// }}} Static methods
}
