package projects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.gjt.sp.jedit.AbstractOptionPane;

import projectviewer.config.ProjectOptions;
import projectviewer.vpt.VPTProject;

@SuppressWarnings("serial")
public class ProjectDependencies extends AbstractOptionPane {

	private static final String PROJECT_DEPENDENCY = "projectDependency";
	private static final String TREE_DEPENDENCY = "treeDependency";
	JList projects;
	JList trees;
	DefaultListModel projectsModel;
	DefaultListModel treesModel;
	public ProjectDependencies() {
		super("CtagsInterface-ProjectDependencies");
	}

	protected void _init() {
		VPTProject project = ProjectOptions.getProject();
		projectsModel = (DefaultListModel)
			project.getObjectProperty(PROJECT_DEPENDENCY);
		if (projectsModel == null)
			projectsModel = new DefaultListModel();
		projects = createList("Projects:", projectsModel);
		addSeparator();
		treesModel = (DefaultListModel)
			project.getObjectProperty(TREE_DEPENDENCY);
		if (treesModel == null)
			treesModel = new DefaultListModel();
		trees = createList("Trees:", treesModel);
	}

	private JList createList(String title, final DefaultListModel model) {
		addComponent(new JLabel("Projects:"));
		final JList list = new JList(model);
		addComponent(new JScrollPane(list));
		JPanel buttons = new JPanel();
		JButton add = new JButton("+");
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = JOptionPane.showInputDialog("Enter value:");
				model.addElement(s);
			}
		});
		JButton remove = new JButton("-"); 
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = list.getSelectedIndex();
				if (index >= 0)
					model.removeElementAt(index);
				if (index < model.size())
					list.setSelectedIndex(index);
				else if (! model.isEmpty())
					list.setSelectedIndex(model.size() - 1);
			}
		});
		buttons.add(add);
		buttons.add(remove);
		addComponent(buttons);
		return list;
	}

	protected void _save() {
		VPTProject project = ProjectOptions.getProject();
		project.setProperty(PROJECT_DEPENDENCY, projectsModel);
		project.setProperty(TREE_DEPENDENCY, treesModel);
	}
}
