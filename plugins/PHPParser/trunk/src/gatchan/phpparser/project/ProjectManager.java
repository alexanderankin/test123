package gatchan.phpparser.project;

import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * The project Manager.
 *
 * @author Matthieu Casanova
 */
public final class ProjectManager {
  /** The current project. */
  private Project project;

  /** The instance of the project manager. */
  private static ProjectManager instance;

  /** jEdit settings directory. */
  public static final String settingsDirectory = jEdit.getSettingsDirectory();

  /** PHPParser project directory. */
  public static final String projectDirectory = settingsDirectory + File.separator + "PHPParserPlugin" + File.separator + "projects";

  /** PHPParser project version. */
  private final String projectVersion = jEdit.getProperty("plugin.gatchan.phpparser.projects.version");
  private static final String PROJECT_NAME_PROPERTY = "gatchan.phpparser.project.file";

  /** Instantiate the project manager. */
  private ProjectManager() {
    final String projectFilePath = jEdit.getProperty(PROJECT_NAME_PROPERTY);
    if (projectFilePath != null) {
      Log.log(Log.DEBUG, this, "Opening project " + projectFilePath);
      final File projectFile = new File(projectDirectory + File.separator + projectFilePath + ".project.props");
      openProject(projectFile);
    }
  }

  /**
   * Returns the current project.
   *
   * @return the current project
   */
  public Project getProject() {
    return project;
  }

  /**
   * Return the instance of the project manager.
   *
   * @return the project manager
   */
  public static ProjectManager getInstance() {
    if (instance == null) {
      instance = new ProjectManager();
    }
    return instance;
  }

  /**
   * Get the list of the projects.
   *
   * @return a {@link List} containing {@link Project} or null if there is no settingsDirectory
   */
  public List getProjectList() {
    if (settingsDirectory != null) {
      final File projectDirFile = new File(projectDirectory);
      if (projectDirFile.exists()) {
        final String[] projectsNames = projectDirFile.list(new FilenameFilter() {
          public boolean accept(File dir, String name) {
            return name.endsWith(".project.props");
          }
        });
        final List list = new ArrayList(projectsNames.length);
        for (int i = 0; i < projectsNames.length; i++) {
          final File projectFile = new File(projectDirectory, projectsNames[i]);
          if (projectFile.isFile()) {
            try {
              list.add(new Project(projectFile));
            } catch (InvalidProjectPropertiesException e) {
              Log.log(Log.WARNING,
                      this,
                      "Warning the file " + projectFile.getAbsolutePath() + " is not a valid project");
            } catch (FileNotFoundException e) {
              Log.log(Log.ERROR, this, "This error should never happens !!!!");
              Log.log(Log.ERROR, this, e);
            }
          }
        }
        return list;
      } else {
        projectDirFile.mkdirs();
      }
    }
    return null;
  }

  /** Dispose the project manager. it will set the project name in the jEdit properties and close the current project. */
  public void dispose() {
    instance = null;
    if (project != null) {
      jEdit.setProperty(PROJECT_NAME_PROPERTY, project.getName());
    }
    closeProject();
  }

  /** Create a project. */
  public void createProject() {
    final String projectName = JOptionPane.showInputDialog("Project name : ");
    final Project project;
    if (projectName == null) {
      Log.log(Log.DEBUG, this, "Project creation cancelled");
    } else if (projectName.length() == 0) {
      JOptionPane.showMessageDialog(jEdit.getActiveView(), "The project name cannot be empty");
    } else {
      project = new Project(projectName, projectVersion);
      project.save();
      EditBus.send(new PHPProjectChangedMessage(this, project,PHPProjectChangedMessage.SELECTED));
      this.project = project;
    }
  }

  /**
   * delete a project.
   *
   * @param project the project to be deleted
   */
  public void deleteProject(Project project) {
    project.delete();
    if (project == this.project) {
      this.project = null;
      EditBus.send(new PHPProjectChangedMessage(this, project,PHPProjectChangedMessage.DELETED));
    }
  }

  /** Close the current project. */
  public void closeProject() {
    if (project != null && project.needSave()) {
      project.save();
    }
    project = null;
    EditBus.send(new PHPProjectChangedMessage(this, null,PHPProjectChangedMessage.SELECTED));
  }

  /**
   * open a project.
   *
   * @param projectFile the file of the project
   */
  public void openProject(File projectFile) {
    if (this.project != null) {
      closeProject();
    }
    final Project project;
    try {
      project = new Project(projectFile);
      project.load();
      // todo : add an option for that
      browseToProjectRoot(project);
      this.project = project;
    } catch (InvalidProjectPropertiesException e) {
      Log.log(Log.ERROR, this, e.getMessage());
      this.project = null;
    } catch (FileNotFoundException e) {
      Log.log(Log.ERROR, this, e.getMessage());
      this.project = null;
    }
    EditBus.send(new PHPProjectChangedMessage(this, this.project,PHPProjectChangedMessage.SELECTED));
  }

  public void openProject(Project project) {
    if (this.project != null) {
      closeProject();
    }
    project.load();
    browseToProjectRoot(project);
    this.project = project;

    EditBus.send(new PHPProjectChangedMessage(this, this.project,PHPProjectChangedMessage.SELECTED));
  }

  /**
   * This will move the File system browser to the project root.
   *
   * @param project the project
   */
  private static void browseToProjectRoot(Project project) {
    // todo : add an option for that
    final View activeView = jEdit.getActiveView();
    final String root = project.getRoot();
    if (activeView != null && root != null) {
      VFSBrowser.browseDirectory(activeView, root);
    }
  }
}
