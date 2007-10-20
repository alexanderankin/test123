package options;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.RolloverButton;

import projects.ProjectWatcher;
import ctags.CtagsInterfacePlugin;

@SuppressWarnings("serial")
public class ProjectsOptionPane extends AbstractOptionPane {
	
	static public final String OPTION = CtagsInterfacePlugin.OPTION;
	static public final String MESSAGE = CtagsInterfacePlugin.MESSAGE;
	static public final String PROJECTS = OPTION + "projects.";
	static public final String AUTO_UPDATE = OPTION + "autoUpdateProjects";
	static public final String ACTIVE_ONLY = OPTION + "searchActiveProjectOnly";
	JList projects;
	DefaultListModel projectsModel;
	ProjectWatcher pvi;
	JCheckBox autoUpdate;
	JCheckBox activeOnly;
	
	public ProjectsOptionPane() {
		super("CtagsInterface-Projects");
		setBorder(new EmptyBorder(5, 5, 5, 5));

		projectsModel = new DefaultListModel();
		Vector<String> trees = getProjects();
		for (int i = 0; i < trees.size(); i++)
			projectsModel.addElement(trees.get(i));
		projects = new JList(projectsModel);
		JScrollPane scroller = new JScrollPane(projects);
		scroller.setBorder(BorderFactory.createTitledBorder(
				jEdit.getProperty(MESSAGE + "projects")));
		addComponent(scroller, GridBagConstraints.HORIZONTAL);
		JPanel buttons = new JPanel();
		JButton add = new RolloverButton(GUIUtilities.loadIcon("Plus.png"));
		buttons.add(add);
		JButton remove = new RolloverButton(GUIUtilities.loadIcon("Minus.png"));
		buttons.add(remove);
		JButton tag = new JButton("Tag");
		buttons.add(tag);
		addComponent(buttons);
		autoUpdate = new JCheckBox(jEdit.getProperty(MESSAGE + "autoUpdateProjects"),
			jEdit.getBooleanProperty(AUTO_UPDATE));
		addComponent(autoUpdate);
		activeOnly = new JCheckBox(jEdit.getProperty(MESSAGE + "searchActiveProjectOnly"),
			jEdit.getBooleanProperty(ACTIVE_ONLY));
		addComponent(activeOnly);

		pvi = CtagsInterfacePlugin.getProjectWatcher();
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Vector<String> nameVec = pvi.getProjects();
				String [] names = new String[nameVec.size()];
				nameVec.toArray(names);
				String selected = (String) JOptionPane.showInputDialog(
					ProjectsOptionPane.this, "Select project:", "Projects",
					JOptionPane.QUESTION_MESSAGE, null, names, names[0]);
				if (selected != null)
					projectsModel.addElement(selected);
			}
		});
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int i = projects.getSelectedIndex();
				if (i >= 0)
					projectsModel.removeElementAt(i);
			}
		});
		tag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int i = projects.getSelectedIndex();
				if (i >= 0) {
					String project = (String) projectsModel.getElementAt(i);
					CtagsInterfacePlugin.tagProject(project);
				}
			}
		});
	}

	public void save() {
		int nProjects = projectsModel.size(); 
		jEdit.setIntegerProperty(PROJECTS + "size", nProjects);
		for (int i = 0; i < nProjects; i++)
			jEdit.setProperty(PROJECTS + i, (String)projectsModel.getElementAt(i));
		jEdit.setBooleanProperty(AUTO_UPDATE, autoUpdate.isSelected());
		jEdit.setBooleanProperty(ACTIVE_ONLY, activeOnly.isSelected());
	}
	
	static public Vector<String> getProjects() {
		Vector<String> projects = new Vector<String>();
		int nProjects = jEdit.getIntegerProperty(PROJECTS + "size");
		for (int i = 0; i < nProjects; i++)
			projects.add(jEdit.getProperty(PROJECTS + i));
		return projects;
	}
	static public boolean getAutoUpdateProjects() {
		return jEdit.getBooleanProperty(AUTO_UPDATE);
	}
	static public boolean getSearchActiveProjectOnly() {
		return jEdit.getBooleanProperty(ACTIVE_ONLY);
	}
}
