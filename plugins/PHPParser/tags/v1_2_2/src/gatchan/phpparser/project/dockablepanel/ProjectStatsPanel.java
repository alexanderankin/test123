package gatchan.phpparser.project.dockablepanel;

import gatchan.phpparser.project.Project;

import javax.swing.*;
import java.awt.*;

/** @author Matthieu Casanova */
public class ProjectStatsPanel extends JPanel {
  private final JTextField classesCount = new JTextField();
  private final JTextField methodsCount = new JTextField();
  private final JTextField filesCount = new JTextField();

  public ProjectStatsPanel() {
    super(new GridLayout(3, 2));
    setBorder(BorderFactory.createEtchedBorder());
    classesCount.setEditable(false);
    methodsCount.setEditable(false);
    filesCount.setEditable(false);


    add(new JLabel("Classes : "));
    add(classesCount);
    add(new JLabel("Methods : "));
    add(methodsCount);
    add(new JLabel("Files : "));
    add(filesCount);
  }

  /**
   * This method is called by {@link gatchan.phpparser.project.dockablepanel.ProjectOptionsPanel#setProject(gatchan.phpparser.project.Project)}
   *
   * @param project the project
   */
  public void updateProject(Project project) {
    if (project == null) {
      classesCount.setText(null);
      methodsCount.setText(null);
      filesCount.setText(null);
    } else {
      classesCount.setText(Integer.toString(project.getClassCount()));
      methodsCount.setText(Integer.toString(project.getMethodCount()));
      filesCount.setText(Integer.toString(project.getFileCount()));
    }
  }
}
