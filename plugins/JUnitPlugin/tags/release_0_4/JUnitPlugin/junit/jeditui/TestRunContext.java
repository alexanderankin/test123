package junit.jeditui;

import javax.swing.ListModel;
import junit.framework.Test;

/**
 * The interface for accessing the Test run context. Test run views should use
 * this interface rather than accessing the TestRunner directly.
 */
public interface TestRunContext {

  /**
   * Run the current test.
   */
  public void runSelectedTest(Test test);

  /**
   * Handles the selection of a Test.
   */
  public void handleTestSelected(Test test);

  /**
   * Returns the failure model
   */
  public ListModel getFailures();
}
