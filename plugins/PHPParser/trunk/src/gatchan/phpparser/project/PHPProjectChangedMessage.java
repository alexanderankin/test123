package gatchan.phpparser.project;

import org.gjt.sp.jedit.EBMessage;

/**
 * @author Matthieu Casanova
 */
public final class PHPProjectChangedMessage extends EBMessage {
  private final AbstractProject selectedProject;

  public PHPProjectChangedMessage(Object source,AbstractProject selectedProject) {
    super(source);
    this.selectedProject = selectedProject;
  }

  public AbstractProject getSelectedProject() {
    return selectedProject;
  }
}
