package projects;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

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
import ctags.CtagsInterfacePlugin;

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

	private interface DependencyAsker {
		String getDependency();
	}
	protected void _init() {
		projectsModel = getListModel(PROJECT_DEPENDENCY);
		projects = createList("Projects:", projectsModel, new DependencyAsker () {
			public String getDependency() {
				return showProjectSelectionDialog();
			}
		});
		addSeparator();
		treesModel = getListModel(TREE_DEPENDENCY);
		trees = createList("Trees:", treesModel, new DependencyAsker () {
			public String getDependency() {
				return JOptionPane.showInputDialog("Source tree:");
			}
		});
	}

	private void setListModel(String propertyName, DefaultListModel model) {
		Vector<String> list = new Vector<String>();
		for (int i = 0; i < model.size(); i++)
			list.add((String) model.getElementAt(i));
		setListProperty(propertyName, list);
	}
	private DefaultListModel getListModel(String propertyName) {
		Vector<String> list = getListProperty(propertyName);
		DefaultListModel model = new DefaultListModel();
		for (int i = 0; i < list.size(); i++)
			model.addElement(list.get(i));
		return model;
	}

	private Vector<String> getListProperty(String propertyName) {
		Vector<String> list = new Vector<String>();
		VPTProject project = ProjectOptions.getProject();
		int i = 0;
		while (true) {
			String value = project.getProperty(propertyName + i);
			if (value == null)
				break;
			list.add(value);
			i++;
		}
		return list;
	}
	private void setListProperty(String propertyName, Vector<String> list) {
		VPTProject project = ProjectOptions.getProject();
		for (int i = 0; i < list.size(); i++)
			project.setProperty(propertyName + i, list.get(i));
		for (int i = list.size(); true; i++) {
			String prop = propertyName + i;
			if (project.getProperty(prop) == null)
				break;
			project.removeProperty(prop);
		}
	}
	
	private JList createList(String title, final DefaultListModel model, final DependencyAsker da) {
		addComponent(new JLabel("Projects:"));
		final JList list = new JList(model);
		addComponent(new JScrollPane(list), GridBagConstraints.HORIZONTAL);
		JPanel buttons = new JPanel();
		JButton add = new JButton("+");
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = da.getDependency();
				if (s != null)
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

	private String showProjectSelectionDialog() {
		Vector<String> nameVec = CtagsInterfacePlugin.getProjectWatcher().getProjects();
		String [] names = new String[nameVec.size()];
		nameVec.toArray(names);
		String selected = (String) JOptionPane.showInputDialog(this, "Select project:",
			"Projects", JOptionPane.QUESTION_MESSAGE, null, names, names[0]);
		return selected;
	}
	protected void _save() {
		setListModel(PROJECT_DEPENDENCY, projectsModel);
		setListModel(TREE_DEPENDENCY, treesModel);
	}
}
