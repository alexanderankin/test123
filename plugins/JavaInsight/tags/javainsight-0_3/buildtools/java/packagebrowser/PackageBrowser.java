/*
 * jEdit edit mode settings:
 * :mode=java:tabSize=4:indentSize=4:noTabs=true:maxLineLen=0:
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package buildtools.java.packagebrowser;


import buildtools.JavaUtils;
import buildtools.MiscUtils;

import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.io.IOException;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.util.Log;


/**
 * A classpath package browser.
 *
 * @author Kevin A. Burton
 * @version $Id$
 */
public class PackageBrowser {

    public static final char PACKAGE_SEPARATOR_CHAR = '.';

    private static boolean parsed = false;
    private static Hashtable packages = new Hashtable();
    private static Hashtable classpath = new Hashtable();


    /**
     * Return all known packages
     */
    public static JavaPackage[] getPackages() {
        parse();

        JavaPackage[] pkgs = new JavaPackage[packages.size()];
        Enumeration enum = packages.elements();
        int element = 0;

        while(enum.hasMoreElements()) {
            pkgs[element] = (JavaPackage) enum.nextElement();
            ++element;
        }

        return pkgs;
    }


    public static ClasspathEntry[] getPackagesAsClasspath() {
        JavaPackage[] packages = getPackages();

        for (int i = 0; i < packages.length; ++i) {
            String source = packages[i].getSource();

            // require that this source has a classpath entry.
            if (!classpath.containsKey(source)) {
                classpath.put(source, new ClasspathEntry(source));
            }

            ClasspathEntry entry = (ClasspathEntry) classpath.get(source);

            if (!entry.containsJavaPackage(packages[i])) {
                entry.addJavaPackage(packages[i]);
            }
        }

        // convert the hashtable into an array
        ClasspathEntry[] classpathEntries = new ClasspathEntry[classpath.size()];
        Enumeration enum = classpath.elements();
        int element = 0;

        while(enum.hasMoreElements()) {
            classpathEntries[element] = (ClasspathEntry) enum.nextElement();
            ++element;
        }

        return classpathEntries;
    }


    /**
     * Parse out the current classpath and get all packages and classes.
     */
    public static synchronized void parse() {
        if (!parsed) {
            String[] classpath = JavaUtils.getClasspath();
            for (int i = 0; i < classpath.length; ++i) {
                File entry = new File(classpath[i]);
                if (entry.isFile()) {
                    parseJAR(entry);
                }
            }
            parsed = true;
        }
    }


    /**
     * Parse out a JAR file and added it to the known packages
     */
    private static void parseJAR(File jar) {
        try {
            ZipFile archive = new ZipFile(jar.getCanonicalPath());
            Enumeration elements = archive.entries();
            char fileSep = '/';

            while (elements.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) elements.nextElement();
                String ext = MiscUtilities.getFileExtension(entry.getName());

                if (ext != null && ext.equals(".class")) {
                    // if the entry is an inner class, ignore it
                    if (entry.getName().indexOf("$") != -1) {
                        continue;
                    }

                    String pkg = getPackage(entry.getName(), fileSep);

                    if (pkg != null) {
                        String className = getFullyQualifiedClassName(entry.getName(), fileSep);
                        JavaPackage javaPackage = getJavaPackage(pkg, jar.getCanonicalPath());
                        javaPackage.addClass(new JavaClass(className, jar.getCanonicalPath()));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }


    /**
     * Given a package name, return its JavaPackage from the stack or create a
     * new one if necessary.
     */
    private static JavaPackage getJavaPackage(String packageName, String source) {
        JavaPackage entry = (JavaPackage) packages.get(packageName);

        if (entry == null) {
            entry = new JavaPackage(packageName, source);
            packages.put(packageName, entry);
        }

        return entry;
    }


    /**
     * Given a classname, get its package or null if it is not part of a package.
     */
    private static String getPackage(String classname, char fileSep) {
        String ext = MiscUtilities.getFileExtension(classname);
        if (ext != null && ext.equals(".class")) {
            int start = 0;
            int end = classname.lastIndexOf(fileSep);

            if (end == -1) {
                return null;
            }

            String rawpackage = classname.substring(start, end);
            return rawpackage.replace(fileSep, PACKAGE_SEPARATOR_CHAR);
        } else {
            throw new IllegalArgumentException("this method only excepts .class files");
        }
    }


    /**
     * Given a file, return the full class path.
     */
    private static String getFullyQualifiedClassName(String file, char fileSep) {
        int start = 0;
        int end = file.lastIndexOf(".class");

        if (end != -1) {
            file = file.substring(start, end);
        }

        return file.replace(fileSep, PACKAGE_SEPARATOR_CHAR);
    }


    /**
     * dump all packages and classes
     */
    public static void dump() {
        JavaPackage[] packages = getPackages();
        for (int i = 0; i < packages.length; ++i) {
            Log.log(Log.DEBUG, PackageBrowser.class, "PACKAGE -> " + packages[i].getName());
            packages[i].dump();
        }
    }

}

