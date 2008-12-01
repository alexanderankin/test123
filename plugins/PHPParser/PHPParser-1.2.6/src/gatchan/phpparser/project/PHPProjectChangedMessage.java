package gatchan.phpparser.project;

import org.gjt.sp.jedit.EBMessage;

/**
 * The event that will inform everybody with php project status change.
 *
 * @author Matthieu Casanova
 */
public final class PHPProjectChangedMessage extends EBMessage {
  /** The concerned. */
  private final Project selectedProject;

  /** SELECTED event. */
  public static final Object SELECTED = "SELECTED";
  /** UPDATED event. */
  public static final Object UPDATED = "UPDATED";
  /** DELETED event. */
  public static final Object DELETED = "DELETED";

  /** The current action. */
  private final Object what;

  /**
   * Instantiate a message.
   *
   * @param source          the source object
   * @param selectedProject the concerned project
   * @param what            the action
   */
  public PHPProjectChangedMessage(Object source, Project selectedProject, Object what) {
    super(source);
    this.selectedProject = selectedProject;
    this.what = what;
  }

  /**
   * Returns the selected project.
   *
   * @return the project. it could be null
   */
  public Project getSelectedProject() {
    return selectedProject;
  }

  /**
   * Returns the action.
   *
   * @return the action
   */
  public Object getWhat() {
    return what;
  }

  public String toString() {
    return "PHPProjectChangedMessage " + what + ' ' + selectedProject;
  } //}}}
}
