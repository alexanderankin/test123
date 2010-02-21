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
	public ProjectToolbar(View view) {
		this.view = view;
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
		updateBoxes();
		build_box.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (proj == null) return;
				if (build_box.getSelectedItem() == null) {
					proj.removeProperty("projectBuilder.command.build");
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
					proj.removeProperty("projectBuilder.command.run");
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
					plugin.buildProject(jEdit.getActiveView());
				else
					plugin.buildProject(jEdit.getActiveView(), proj);
			}
		});
		run_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ProjectBuilderPlugin plugin = (ProjectBuilderPlugin)
					jEdit.getPlugin("projectbuilder.ProjectBuilderPlugin");
				if (proj == null)
					plugin.runProject(jEdit.getActiveView());
				else
					plugin.runProject(jEdit.getActiveView(), proj);
			}
		});
	}
	
	public void updateBoxes() {
		build_box.removeAllItems();
		run_box.removeAllItems();
		proj = ProjectViewer.getActiveProject(view);
		if (proj == null) {
			build_box.setSelectedItem(null);
			run_box.setSelectedItem(null);
			return;
		}
		String saved = proj.getProperty("projectBuilder.command.build");
		boolean boxIsValid = false;
		for (int i = 0; true; i++) {
			String prop = proj.getProperty("projectBuilder.command.build."+i);
			if (prop == null) break;
			Entry entry = new Entry(prop);
			build_box.addItem(entry);
			if (prop.equals(saved)) {
				build_box.setSelectedItem(entry);
				boxIsValid = true;
			}
		}
		if (!boxIsValid) build_box.setSelectedItem(null);
		
		saved = proj.getProperty("projectBuilder.command.run");
		boxIsValid = false;
		for (int i = 0; true; i++) {
			String prop = proj.getProperty("projectBuilder.command.run."+i);
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
	public static void create(View view) {
		ProjectToolbar toolbar = new ProjectToolbar(view);
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
