package gatchan.phpparser.project.dockablepanel;

import gatchan.phpparser.project.PHPProjectChangedMessage;
import gatchan.phpparser.project.Project;
import gatchan.phpparser.project.ProjectManager;
import gatchan.phpparser.project.ProjectList;
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

  /** This button will delete a project. */
  private final JButton buttonDel = new JButton("Del");

  /** The button to close the project. */
  private final JButton closeProject = new JButton(GUIUtilities.loadIcon("Cancel.png"));

  /** a combo that will contains the list of projects to switch between them. */
  private final JComboBox listProjects;
  private final ProjectList projectList;

  public PHPProjectPanel() {
    super(new BorderLayout());
    buttonDel.setEnabled(false);
    projectManager = ProjectManager.getInstance();
    final JToolBar toolbar = new JToolBar();
    toolbar.setFloatable(false);
    final JButton newProject = new JButton(GUIUtilities.loadIcon("New.png"));
    final JButton openProject = new JButton(GUIUtilities.loadIcon("Open.png"));
    newProject.setToolTipText("Create a new project");


    projectList = projectManager.getProjectList();
    listProjects = new JComboBox(this.projectList);

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
      projectList.addElement(project);
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
        projectList.removeElement(selectedProject);
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
}
