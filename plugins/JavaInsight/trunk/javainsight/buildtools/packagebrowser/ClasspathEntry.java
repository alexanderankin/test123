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

package javainsight.buildtools.packagebrowser;


import java.util.Hashtable;
import java.util.Enumeration;


/**
 * A classpath entry.
 *
 * @author Kevin A. Burton
 * @version $Id$
 */
public class ClasspathEntry {

    private String name = null;
    private Hashtable packages = new Hashtable();


    /**
     * Creates a ClasspathEntry
     */
    public ClasspathEntry(String name) {
        this.name = name;
    }


    /**
     * Add an JavaPackage to this ClasspathEntry
     *
     * @param name The name of this classpath entry
     */
    public void addJavaPackage(JavaPackage javaPackage) {
        packages.put(javaPackage.getName(), javaPackage);
    }


    public boolean containsJavaPackage(JavaPackage javaPackage) {
        return packages.containsKey(javaPackage.getName());
    }


    /**
     * Return the packages within this ClasspathEntry
     *
     * @param name The name of this classpath entry
     */
    public JavaPackage[] getPackages() {
        // convert the hashtable into an array
        JavaPackage[] array = new JavaPackage[packages.size()];
        Enumeration enum = packages.elements();
        int element = 0;

        while(enum.hasMoreElements())
            array[element++] = (JavaPackage) enum.nextElement();

        return array;
    }


    /**
     * Return the name of this.
     *
     * @param name The name of this classpath entry
     */
    public String getName() {
        return name;
    }


    /**
     * Return this ClasspathEntry as a String
     *
     * @param name The name of this classpath entry
     */
    public String toString() {
        return getName();
    }

}

