package gatchan.phpparser.project.dockablepanel;

import gatchan.phpparser.project.Project;

import javax.swing.*;

/**
 * @author Matthieu Casanova
 */
public class ProjectTabbedPane extends JTabbedPane {

  private ProjectOptionsPanel options = new ProjectOptionsPanel();

  public ProjectTabbedPane() {
    addTab("Options",options);
    setEnabled(false);
  }

  public void setProject(Project project) {
    options.setProject(project);
    setEnabled(true);
  }
}
