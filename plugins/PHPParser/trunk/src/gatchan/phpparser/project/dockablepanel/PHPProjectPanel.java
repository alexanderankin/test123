package gatchan.phpparser.project.dockablepanel;

import gatchan.phpparser.project.PHPProjectChangedMessage;
import gatchan.phpparser.project.Project;
import gatchan.phpparser.project.ProjectManager;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Vector;

/**
 * The dockable php project manager.
 *
 * @author Matthieu Casanova
 */
public final class PHPProjectPanel extends JPanel implements EBComponent {

  //private DefaultComboBoxModel phpProjectList;
  private final ProjectTabbedPane tabs = new ProjectTabbedPane();

  /** The project manager. */
  private final ProjectManager projectManager;

  private final JButton buttonDel = new JButton("Del");
  private final JButton closeProject = new JButton(GUIUtilities.loadIcon("Cancel.png"));
  private final JComboBox listProjects;
  private final MyComboBoxModel listModel;

  public PHPProjectPanel() {
    super(new BorderLayout());
    buttonDel.setEnabled(false);
    projectManager = ProjectManager.getInstance();
    final JToolBar toolbar = new JToolBar();
    toolbar.setFloatable(false);
    final JButton newProject = new JButton(GUIUtilities.loadIcon("New.png"));
    final JButton openProject = new JButton(GUIUtilities.loadIcon("Open.png"));
    newProject.setToolTipText("Create a new project");


    final java.util.List projectList = projectManager.getProjectList();
    if (projectList == null) {
      listModel = new MyComboBoxModel();
    } else {
      listModel = new MyComboBoxModel(projectList.toArray());
    }
    listProjects = new JComboBox(listModel);

    listProjects.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          final Project project = (Project) e.getItem();
          if (projectManager.getProject() != project) {
            projectManager.openProject(project);
          }
        }
      }
    });
    final MyActionListener myActionListener = new MyActionListener(newProject,
                                                                   openProject,
                                                                   closeProject,
                                                                   buttonDel);
    newProject.addActionListener(myActionListener);
    toolbar.add(newProject);

    openProject.setToolTipText("Open a project");
    openProject.addActionListener(myActionListener);

    closeProject.addActionListener(myActionListener);

    //   toolbar.add(openProject);
    toolbar.add(closeProject);


    add(toolbar, BorderLayout.NORTH);
    final JPanel panel = new JPanel(new BorderLayout());
    final JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));

    buttonDel.setToolTipText("Delete the current project");
    buttonDel.addActionListener(myActionListener);
    toolbar.add(buttonDel);

    closeProject.setToolTipText("Close the current project");
    final JLabel projectName = new JLabel("Project : ");
    setProject(projectManager.getProject());
    panelTop.add(projectName);
    panelTop.add(new JScrollPane(listProjects));
    panel.add(panelTop, BorderLayout.NORTH);
    panel.add(tabs, BorderLayout.CENTER);
    add(panel, BorderLayout.CENTER);
  }

  public void addNotify() {
    super.addNotify();
    EditBus.addToBus(this);
    setProject(projectManager.getProject());
  }

  public void removeNotify() {
    EditBus.removeFromBus(this);
    super.removeNotify();
    setProject(null);
  }

  private void setProject(Project project) {
    if (project == null) {
      tabs.setProject(null);
      buttonDel.setEnabled(false);
      closeProject.setEnabled(false);
      listProjects.setSelectedItem(null);
    } else {
      listModel.addElement(project);
      buttonDel.setEnabled(true);
      closeProject.setEnabled(true);
      listProjects.getModel().setSelectedItem(project);
      tabs.setProject(project);
      validate();
    }
  }

  public void handleMessage(EBMessage message) {
    if (message instanceof PHPProjectChangedMessage) {
      final PHPProjectChangedMessage projectChangedMessage = (PHPProjectChangedMessage) message;
      final Project selectedProject = projectChangedMessage.getSelectedProject();
      if (projectChangedMessage.getWhat() == PHPProjectChangedMessage.SELECTED || projectChangedMessage.getWhat() == PHPProjectChangedMessage.UPDATED) {
        setProject(selectedProject);
      } else if (projectChangedMessage.getWhat() == PHPProjectChangedMessage.DELETED) {
        setProject(null);
        listModel.removeElement(selectedProject);
      }
    }
  }

  private static final class MyActionListener implements ActionListener {
    private final JButton newProject;
    private final JButton openProject;
    private final JButton closeProject;
    private final JButton buttonDel;

    MyActionListener(JButton newProject,
                     JButton openProject,
                     JButton closeProject,
                     JButton buttonDel) {
      this.newProject = newProject;
      this.openProject = openProject;
      this.closeProject = closeProject;
      this.buttonDel = buttonDel;
    }

    public void actionPerformed(ActionEvent e) {
      final ProjectManager projectManager = ProjectManager.getInstance();
      final Object source = e.getSource();
      if (source == newProject) {
        projectManager.createProject();
      } else if (source == openProject) {
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(ProjectManager.projectDirectory));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setApproveButtonText("Open");
        fileChooser.setDialogTitle("Open project ...");
        fileChooser.setFileFilter(new FileFilter() {
          public boolean accept(File f) {
            return f.isDirectory() || (f.isFile() && f.getName().endsWith(".project.props"));
          }

          public String getDescription() {
            return "Directories and projects";
          }
        });
        final int returnVal = fileChooser.showOpenDialog(jEdit.getActiveView());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          final File projectFile = fileChooser.getSelectedFile();
          projectManager.openProject(projectFile);
        }
      } else if (source == closeProject) {
        projectManager.closeProject();
      } else if (source == buttonDel) {
        final Project project = projectManager.getProject();
        final int ret = JOptionPane.showConfirmDialog(closeProject,
                                                      "Do you really want to remove the project " + project.getName());
        if (ret == JOptionPane.YES_OPTION) {
          Log.log(Log.DEBUG, this, "Removing project requested by user");
          projectManager.deleteProject(project);
        }
      }
    }
  }

  private static final class MyComboBoxModel extends AbstractListModel implements MutableComboBoxModel {
    private final Vector objects;
    private Object selectedObject;

    /** Constructs an empty DefaultComboBoxModel object. */
    MyComboBoxModel() {
      objects = new Vector();
    }

    /**
     * Constructs a DefaultComboBoxModel object initialized with
     * an array of objects.
     *
     * @param items an array of Object objects
     */
    MyComboBoxModel(Object[] items) {
      objects = new Vector();
      objects.ensureCapacity(items.length);

      int i;
      final int c;
      for (i = 0, c = items.length; i < c; i++)
        objects.addElement(items[i]);

      if (getSize() > 0) {
        selectedObject = getElementAt(0);
      }
    }

    // implements javax.swing.ComboBoxModel
    /**
     * Set the value of the selected item. The selected item may be null.
     * <p/>
     *
     * @param anObject The combo box value or null for no selection.
     */
    public void setSelectedItem(Object anObject) {
      if ((selectedObject != null && !selectedObject.equals(anObject)) ||
          selectedObject == null && anObject != null) {
        selectedObject = anObject;
        fireContentsChanged(this, -1, -1);
      }
    }

    // implements javax.swing.ComboBoxModel
    public Object getSelectedItem() {
      return selectedObject;
    }

    // implements javax.swing.ListModel
    public int getSize() {
      return objects.size();
    }

    // implements javax.swing.ListModel
    public Object getElementAt(int index) {
      if (index >= 0 && index < objects.size())
        return objects.elementAt(index);
      else
        return null;
    }

    // implements javax.swing.MutableComboBoxModel
    public void addElement(Object anObject) {
      if (!objects.contains(anObject)) {
        objects.addElement(anObject);
        fireIntervalAdded(this, objects.size() - 1, objects.size() - 1);
        if (objects.size() == 1 && selectedObject == null && anObject != null) {
          setSelectedItem(anObject);
        }
      }
    }

    // implements javax.swing.MutableComboBoxModel
    public void insertElementAt(Object anObject, int index) {
      objects.insertElementAt(anObject, index);
      fireIntervalAdded(this, index, index);
    }

    // implements javax.swing.MutableComboBoxModel
    public void removeElementAt(int index) {
      if (getElementAt(index) == selectedObject) {
        if (index == 0) {
          setSelectedItem(getSize() == 1 ? null : getElementAt(index + 1));
        } else {
          setSelectedItem(getElementAt(index - 1));
        }
      }

      objects.removeElementAt(index);

      fireIntervalRemoved(this, index, index);
    }

    // implements javax.swing.MutableComboBoxModel
    public void removeElement(Object anObject) {
      final int index = objects.indexOf(anObject);
      if (index != -1) {
        removeElementAt(index);
      }
    }
  }
}
