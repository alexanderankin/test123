package gatchan.phpparser.project;

import org.gjt.sp.jedit.EBMessage;

/**
 * @author Matthieu Casanova
 */
public final class PHPProjectChangedMessage extends EBMessage {
  private final Project selectedProject;

  public PHPProjectChangedMessage(Object source,Project selectedProject) {
    super(source);
    this.selectedProject = selectedProject;
  }

  public Project getSelectedProject() {
    return selectedProject;
  }
}
