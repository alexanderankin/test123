package junit;

/*
import java.util.*;
import java.io.*;
import java.net.URL;
import java.util.zip.*;
*/
import junit.runner.*;

import org.gjt.sp.jedit.*;


public class JEditTestCaseClassLoader extends TestCaseClassLoader {

    public JEditTestCaseClassLoader() {
        super();
    }

    public JEditTestCaseClassLoader(String classPath) {
        super(classPath);
    }

    public synchronized Class loadClass(String name, boolean resolve)
        throws ClassNotFoundException
    {
        Class c= findLoadedClass(name);
        if (c != null)
            return c;
        //
        // Delegate the loading of excluded classes to the
        // standard class loader.
        //
        if (isExcluded(name)) {
            try {
                c= findSystemClass(name);
                return c;
            } catch (ClassNotFoundException e) {
                // keep searching
            }

            try {
                c= ((JARClassLoader) junit.JUnitPlugin.class.getClassLoader()).loadClass(name, resolve);
                return c;
            } catch (ClassNotFoundException e) {
                // keep searching
            }
        }

        return super.loadClass(name, resolve);
    }
}
