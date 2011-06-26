package junit;

/**
* A TestSuite loader that can reload classes.
* It's used by junit.jeditui.TestRunner.
* TODO: verify that it is actually capable of loading a new version of the tests
*/
public class JEditReloadingTestSuiteLoader  {
        private String classPath;
        
        public JEditReloadingTestSuiteLoader() {
                this(System.getProperty("java.class.path"));
        }
        
        public JEditReloadingTestSuiteLoader(String classPath) {
                this.classPath = classPath;
        }
        
        public Class load(String suiteClassName) throws ClassNotFoundException {
                JEditTestCaseClassLoader loader = new JEditTestCaseClassLoader(this.classPath);
                return loader.loadClass(suiteClassName, true);
        }
        
        public Class reload(Class aClass) throws ClassNotFoundException {
                JEditTestCaseClassLoader loader = new JEditTestCaseClassLoader(this.classPath);
                return loader.loadClass(aClass.getName(), true);
        }
}
