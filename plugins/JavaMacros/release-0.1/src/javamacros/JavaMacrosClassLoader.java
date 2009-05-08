package javamacros;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.HashMap;

class JavaMacrosClassLoader extends ClassLoader {

    private final File[] roots;
    private Map<String, ClassDetails> loadedClasses = new HashMap<String, ClassDetails>();

    JavaMacrosClassLoader(ClassLoader parent, File[] roots) {
        super(parent);
        if (parent == null) {
            throw new NullPointerException("parent");
        }
        this.roots = roots;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (loadedClasses.containsKey(name)) {
            final ClassDetails classDetails = loadedClasses.get(name);
            if (classDetails.lastModified != classDetails.file.lastModified()) {
                try {
                    doLoadClass(name, classDetails);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return super.loadClass(name, resolve);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        final String relativeFileName = name.replace('.', File.separatorChar) + ".class";
        for (File root : roots) {
            final File file = new File(root, relativeFileName);
            if (file.exists()) {
                final ClassDetails classDetails = new ClassDetails(file.lastModified(), file);
                try {
                    doLoadClass(name, classDetails);
                    loadedClasses.put(name, classDetails);
                    return classDetails.clazz;
                } catch (FileNotFoundException e) {
                    throw new IllegalStateException(); // should not get here since file is reported to exist
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        throw new ClassNotFoundException();
    }

    boolean isClassLoaded(String className) {
        return loadedClasses.containsKey(className);
    }

    ClassDetails getClassDetails(String className) {
        return loadedClasses.get(className);
    }

    private void doLoadClass(String name, ClassDetails classDetails) throws IOException {
        final InputStream in = new FileInputStream(classDetails.file);
        final int size = in.available(); // since we use FileInputStream, available() returns exact file size
        final byte[] buf = new byte[size];
        int read = in.read(buf);
        if (read != size) {
            throw new IllegalStateException(); // should not get here
        }
        classDetails.clazz = defineClass(name, buf, 0, size);
    }

    static class ClassDetails {

        public final long lastModified;
        public final File file;
        public Class<?> clazz;

        private ClassDetails(long lastModified, File file) {
            this.lastModified = lastModified;
            this.file = file;
        }

    }

}