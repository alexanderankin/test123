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


/**
 * A class representing a Java class.
 *
 * @author Kevin A. Burton
 * @version $Id$
 */
public class JavaClass {

    public static final String PACKAGE_SEPARATOR = ".";

    private String name;
    private String source;


    /**
     * Creates a JavaClass
     *
     * @param name  The name of this class fully qualified
     * @param source  The source where this class can be found.
     *                This should be either a directory or a .jar file.
     */
    public JavaClass(String name, String source) {
        this.name = name;
        this.source = source;
    }


    /**
     * Returns the fully-qualified name of the class.
     */
    public String getName() {
        return name;
    }


    public String getClassName() {
        int start = name.lastIndexOf(PACKAGE_SEPARATOR);
        int end = name.length();
        if (start == -1) {
            return name;
        } else {
            return name.substring(start + 1, end);
        }
    }


    /**
     * Returns the source of this class.
     */
    public String getSource() {
        return source;
    }


    public String toString() {
        return getClassName();
    }


    /**
     * Returns <code>true</code> if this class equals the other class.
     * Two classes are considered equal if their name and source are equal.
     *
     * @author Dirk Moebius
     */
    public boolean equals(Object other) {
        if (other == null)
            return false;
        if (other == this)
            return true;
        if (other instanceof JavaClass) {
            JavaClass otherClass = (JavaClass) other;
            return name.equals(otherClass.name) && source.equals(otherClass.source);
        }
        return false;
    }

}

