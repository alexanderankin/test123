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


import java.util.Vector;
import org.gjt.sp.util.Log;


/**
 * A class representing a Java package, holding an array of Java classes.
 *
 * @author Kevin A. Burton
 * @version $Id$
 */
public class JavaPackage {

    private String name;
    private String source;
    private Vector classes = new Vector();


    /**
     * Creates a JavaPackage
     *
     * @param name    The name of this package fully qualified
     * @param source  The source where this package can be found.
     *                This should be either a directory or a .jar file.
     */
    public JavaPackage(String name, String source) {
        this.name = name;
        this.source = source;
    }


    /**
     * Returns the fully-qualified name of this package
     */
    public String getName() {
        return name;
    }


    /**
     * Returns all sub packages and classes off of this JavaPackage
     */
    public JavaClass[] getClasses() {
        JavaClass[] array = new JavaClass[classes.size()];
        classes.copyInto(array);
        return array;
    }


    /**
     * Adds a class to this package, if it's not yet there.
     */
    public void addClass(JavaClass javaclass) {
        if (!hasClass(javaclass))
            classes.addElement(javaclass);
    }


    /**
     * Tests to see of this package has the given class.
     */
    boolean hasClass(JavaClass javaclass) {
        return classes.contains(javaclass);
    }


    /**
     * Returns the source of this package
     */
    public String getSource() {
        return source;
    }


    public String toString() {
        return getName();
    }


    /**
     * dump all classes within this package
     */
    public void dump() {
        JavaClass[] array = getClasses();
        for (int i = 0; i < array.length; ++i) {
            Log.log(Log.DEBUG, this, "    CLASS: " + array[i].getClassName());
        }
    }

}

