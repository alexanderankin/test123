package javamacros;

import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Test;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.io.FileFilter;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class JavaMacrosClassLoaderTest {

    @After
    public void tearDown() {
        for (File file : new File(".").listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getName().startsWith("_generated_") && pathname.getName().endsWith(".class");
            }
        })) {
            if (!file.delete()) {
                System.err.println("Cleanup: cannot delete " + file.getAbsolutePath());
            }
        }
    }

    @Test
    public void testLoadClass() throws Exception {
        compile("_generated_1", "1");
        final JavaMacrosClassLoader cl = new JavaMacrosClassLoader(this.getClass().getClassLoader(), new File[] { new File(".") });
        final Class<?> clazz = cl.loadClass("_generated_1");
        final Object obj = clazz.newInstance();
        final Object value = clazz.getMethod("eval").invoke(obj);
        assertEquals(1, value);
    }

    @Test
    public void testReload() throws Exception {
        compile("_generated_1", "1");
        JavaMacrosClassLoader cl = new JavaMacrosClassLoader(this.getClass().getClassLoader(), new File[] { new File(".") });
        Class<?> clazz = cl.loadClass("_generated_1");
        Object obj = clazz.newInstance();
        Object value = clazz.getMethod("eval").invoke(obj);
        assertEquals(1, value);
        compile("_generated_1", "2");
        cl = new JavaMacrosClassLoader(this.getClass().getClassLoader(), new File[] { new File(".") }); 
        clazz = cl.loadClass("_generated_1");
        obj = clazz.newInstance();
        value = clazz.getMethod("eval").invoke(obj);
        assertEquals(2, value);
    }

    private void compile(String className, String expression) {
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        final List<JavaFileObject> classes = new ArrayList<JavaFileObject>();
        classes.add(createEvaluatable(className, expression));
        final JavaCompiler.CompilationTask task = javaCompiler.getTask(null, null, null, null, null, classes);
        if (!task.call()) {
            fail();
        }
    }

    private JavaFileObject createEvaluatable(String className, String expression) {
        final String text = MessageFormat.format(
                "public class {0} '{' public int eval() '{' return {1}; '}' '}'", className, expression);
        try {
            return new StringJavaFileObject(className, text);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static class StringJavaFileObject extends SimpleJavaFileObject {

        private final String text;

        public StringJavaFileObject(String className, String text) throws URISyntaxException {
            super(new URI(className + ".java"), Kind.SOURCE);
            this.text = text;
        }

        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            return text;
        }

    }

}