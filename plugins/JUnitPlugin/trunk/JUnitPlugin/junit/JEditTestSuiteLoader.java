package junit;

import junit.runner.*;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.jEdit;


/**
 * The jEdit test suite loader. It can only load the same class once.
 */
public class JEditTestSuiteLoader implements TestSuiteLoader {
    /**
     * Uses the system class loader to load the test class
     */
    public Class load(String suiteClassName) throws ClassNotFoundException {
        return junit.JUnitPlugin.class.getClassLoader().loadClass(suiteClassName);
    }


    /**
     * Uses the system class loader to load the test class
     */
    public Class reload(Class aClass) throws ClassNotFoundException {
        return aClass;
    }
}
