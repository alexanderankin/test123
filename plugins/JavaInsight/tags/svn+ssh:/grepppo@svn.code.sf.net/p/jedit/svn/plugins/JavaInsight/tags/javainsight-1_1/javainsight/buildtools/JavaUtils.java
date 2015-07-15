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

package javainsight.buildtools;


import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;


/**
 * Miscelleanous Java utilities.
 *
 * @author Kevin A. Burton
 * @version $Id$
 */
public class JavaUtils
{
    private static final Object mutex = new Object();

    private static String[] classpath = null;

    /**
     * make the constructor private.
     */
    private JavaUtils() { }


     /**
      * Get the current classpath as an array of strings.
      *
      * This implementation evaluates the contents of the following system
      * properties, if they exist:
      *
      * <UL>
      *   <LI>
      *     <CODE>java.class.path</CODE>
      *     - should be available on all platforms.
      *   <LI>
      *     <CODE>sun.boot.class.path</CODE>
      *     - should be available on JDK 1.2 or higher platforms (the IBM
      *     JDK 1.2/1.3 defines it, too). If this property exists, it's
      *     entries are added first in the resulting array.
      * </UL>
      *
      * <B>Note:</B> contrary to system classpaths, here non-existent
      * entries are removed from the resulting array. All entries are checked
      * using <CODE>java.io.File.exists()</CODE>. This is because
      * Jode, the decompiler library, has a bug with non-existent entries
      * in the classpath.
      *
      * @return an array of classpath entries
      */
    public static String[] getClasspath() {
        if (classpath == null) {
            String[] newClasspath = computeClasspath();
            synchronized(mutex) {
                classpath = newClasspath;
            }
        }
        return classpath;
    }
    
    
    private static String[] computeClasspath() {
        ArrayList<String> cp = new ArrayList<String>();
        String pathSep = System.getProperty("path.separator");

        // add entries from sun.boot.class.path:
        String bootpath = System.getProperty("sun.boot.class.path");
        if (bootpath != null) {
            StringTokenizer tokenizer = new StringTokenizer(bootpath, pathSep);
            while (tokenizer.hasMoreElements()) {
                String entry = tokenizer.nextElement().toString();
                if (new File(entry).exists())
                    cp.add(entry);
            }
        }

        // add entries from java.class.path:
        String classpath = System.getProperty("java.class.path");
        StringTokenizer tokenizer = new StringTokenizer(classpath, pathSep);
        while (tokenizer.hasMoreElements()) {
            String entry = tokenizer.nextElement().toString();
            if (new File(entry).exists())
                cp.add(entry);
        }

        return cp.toArray(new String[cp.size()]);
    }


    /**
     * <p>Given a java class name (ie. org.apache.jetspeed.Test) return a
     * full filename (ie. org/apache/jetspeed/Test.java).</p>
     *
     * Substitutes all dots ('.') by the file separator char and appends
     * ".java" to the resulting name.
     */
    public static final String getJavaFile(String classname) {
        return classname.replace('.', File.separatorChar).concat(".java");
    }

}
