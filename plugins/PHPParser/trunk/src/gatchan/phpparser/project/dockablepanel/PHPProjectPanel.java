package gatchan.phpparser.project.dockablepanel;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import gatchan.phpparser.project.dockablepanel.ProjectTabbedPane;
import gatchan.phpparser.project.*;

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
  private final JTextField projectNameField = new JTextField();
  private final JButton closeProject = new JButton(GUIUtilities.loadIcon("Cancel.png"));

  public PHPProjectPanel() {
    super(new BorderLayout());
    buttonDel.setEnabled(false);
    projectManager = ProjectManager.getInstance();
    final JToolBar toolbar = new JToolBar();
    final JButton newProject = new JButton(GUIUtilities.loadIcon("New.png"));
    newProject.setToolTipText("Create a new project");
    newProject.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        projectManager.createProject();
      }
    });
    toolbar.add(newProject);

    final JButton openProject = new JButton(GUIUtilities.loadIcon("Open.png"));
    openProject.setToolTipText("Open a project");
    openProject.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
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
      }
    });
    toolbar.add(openProject);

    closeProject.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        projectManager.closeProject();
      }
    });
    toolbar.add(closeProject);


    add(toolbar, BorderLayout.NORTH);
    final JPanel panel = new JPanel(new BorderLayout());
    final JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));

    buttonDel.setToolTipText("Delete the current project");
    buttonDel.addActionListener(
            //replace with a single action listener
            new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        final Project project = (Project) projectManager.getProject();
        final int ret = JOptionPane.showConfirmDialog(PHPProjectPanel.this,
                                                      "Do you really want to remove the project " + project.getName());
        if (ret == JOptionPane.YES_OPTION) {
          Log.log(Log.DEBUG, this, "Removing project requested by user");
          projectManager.deleteProject(project);
        }
      }
    });
    toolbar.add(buttonDel);

    closeProject.setToolTipText("Close the current project");
    final JLabel projectName = new JLabel("Project : ");
    projectNameField.setEditable(false);
    setProject(projectManager.getProject());
    panelTop.add(projectName);
    panelTop.add(projectNameField);
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
    super.removeNotify();
    EditBus.removeFromBus(this);
  }

  private void setProject(AbstractProject project) {
    if (project == null) {
      projectNameField.setText(null);
      tabs.setProject(null);
      buttonDel.setEnabled(false);
      closeProject.setEnabled(false);
    } else {
      buttonDel.setEnabled(true);
      closeProject.setEnabled(true);
      projectNameField.setText(project.getName());
      if (project instanceof DummyProject) {
        tabs.setProject(null);
      } else {
        tabs.setProject((Project) project);
      }
      validate();
    }
  }

  public void handleMessage(EBMessage message) {
    if (message instanceof PHPProjectChangedMessage) {
      final PHPProjectChangedMessage projectChangedMessage = (PHPProjectChangedMessage) message;
      setProject(projectChangedMessage.getSelectedProject());
    }
  }
}
