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
import db.TagDB;

@SuppressWarnings("serial")
public class ProjectsOptionPane extends AbstractOptionPane {
	
	private static final String PROJECT_ORIGIN = TagDB.PROJECT_ORIGIN;
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
		if (pvi == null) {
			add.setEnabled(false);
			tag.setEnabled(false);
		}
		else {
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
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int i = projects.getSelectedIndex();
				if (i >= 0)
					projectsModel.removeElementAt(i);
			}
		});
	}

	public void save() {
		Vector<String> names = new Vector<String>();
		int nProjects = projectsModel.size(); 
		for (int i = 0; i < nProjects; i++)
			names.add((String) projectsModel.getElementAt(i));
		CtagsInterfacePlugin.getDB().updateOrigins(PROJECT_ORIGIN, names);
		jEdit.setBooleanProperty(AUTO_UPDATE, autoUpdate.isSelected());
		jEdit.setBooleanProperty(ACTIVE_ONLY, activeOnly.isSelected());
	}
	
	static public Vector<String> getProjects() {
		return CtagsInterfacePlugin.getDB().getOrigins(PROJECT_ORIGIN);
	}
	static public boolean getAutoUpdateProjects() {
		return jEdit.getBooleanProperty(AUTO_UPDATE);
	}
	static public boolean getSearchActiveProjectOnly() {
		return jEdit.getBooleanProperty(ACTIVE_ONLY);
	}
}
