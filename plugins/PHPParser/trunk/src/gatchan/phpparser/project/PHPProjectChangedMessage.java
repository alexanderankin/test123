package gatchan.phpparser.project;

import org.gjt.sp.jedit.EBMessage;

/**
 * @author Matthieu Casanova
 */
public final class PHPProjectChangedMessage extends EBMessage {
  private final Project selectedProject;

  public static final Object SELECTED = "SELECTED";
  public static final Object DELETED = "DELETED";

  private Object what;
  public PHPProjectChangedMessage(Object source,Project selectedProject,Object what) {
    super(source);
    this.selectedProject = selectedProject;
    this.what = what;
  }

  public Project getSelectedProject() {
    return selectedProject;
  }

  public Object getWhat() {
    return what;
  }

  public String toString() {
    return "PHPProjectChangedMessage "+what+' '+selectedProject;
  } //}}}
}
