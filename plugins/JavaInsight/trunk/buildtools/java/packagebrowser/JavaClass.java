/*
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


public class JavaClass {
    
    public static final String PACKAGE_SEPARATOR = ".";

    private String  name;
    private String  source;
    

    /**
     * Creates a JavaClass
     *
     * @param name The name of this package/class fully qualified
     * @param source The source where this package/class can be found.  This 
     *               should be either a directory or a .jar file.
     * @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
     * @version $Id$
     */
    public JavaClass(String name, String source) {

        this.name = name;
        this.source = source;
    }
    
    /**
     * Returns the fully-qualified name of the class
     * @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
     * @version $Id$
     */
    public String getName() {
        return this.name;
    }

    public String getClassName() {

        int start = this.name.lastIndexOf( PACKAGE_SEPARATOR );
        int end = this.name.length();

        if (start == -1) {
            return this.name;
        } else {
            return this.name.substring(start + 1, end);
        }

    }
    
    /**
     * Returns the source of this class
     * @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
     * @version $Id$
     */
    public String getSource() {
        return this.source;
    }

    public String toString() {
        return this.getClassName();
    }
    
}

