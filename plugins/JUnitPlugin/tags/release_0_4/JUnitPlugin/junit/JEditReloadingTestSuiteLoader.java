package junit;

import junit.runner.TestCaseClassLoader;
import junit.runner.TestSuiteLoader;

/**
 * A TestSuite loader that can reload classes.
 */
public class JEditReloadingTestSuiteLoader implements TestSuiteLoader {
  private String classPath;

  public JEditReloadingTestSuiteLoader() {
    this(System.getProperty("java.class.path"));
  }

  public JEditReloadingTestSuiteLoader(String classPath) {
    this.classPath = classPath;
  }

  public Class load(String suiteClassName) throws ClassNotFoundException {
    TestCaseClassLoader loader = new JEditTestCaseClassLoader(this.classPath);
    return loader.loadClass(suiteClassName, true);
  }

  public Class reload(Class aClass) throws ClassNotFoundException {
    TestCaseClassLoader loader = new JEditTestCaseClassLoader(this.classPath);
    return loader.loadClass(aClass.getName(), true);
  }
}
