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

import java.util.Vector;

import org.gjt.sp.util.Log;


public class JavaPackage {

    private String  name;
    private String  source;
    private Vector  classes = new Vector();

    /**
     * Creates a JavaPackage
     *
     * @param name The name of this package fully qualified
     * @param source The source where this package can be found.  This
     *               should be either a directory or a .jar file.
     * @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
     * @version $Id$
     */
    public JavaPackage(String name, String source) {

        this.name = name;
        this.source = source;
    }



    /**
     * Returns the fully-qualified name of this package
     * @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
     * @version $Id$
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns all sub packages and classes off of this JavaPackage
     * @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
     * @version $Id$
     */
    public JavaClass[] getClasses() {
        JavaClass[] classes = new JavaClass[this.classes.size()];
        this.classes.copyInto(classes);
        return classes;
    }

    /**
     * Adds a class to this package
     * @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
     * @version $Id$
     */
    public void addClass(JavaClass javaclass) {
        //make sure you don't already have this class before adding it.
        if ( this.hasClass(javaclass) == false ) {
            this.classes.addElement( javaclass );
        }
    }

    /**
     * Tests to see of this package has the given class.
     * @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
     * @version $Id$
     */
    boolean hasClass(JavaClass javaclass) {
        return this.classes.contains(javaclass);
    }

    /**
     * Returns the source of this package
     * @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
     * @version $Id$
     */
    public String getSource() {
        return this.source;
    }

    public String toString() {
        return this.getName();
    }


    /**
     * dump all classes within this package
     * @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
     * @version $Id$
     */
    public void dump() {

        JavaClass[] classes = this.getClasses();

        for ( int i = 0; i < classes.length; ++i ) {
            Log.log( Log.DEBUG, this, "\t CLASS: " + classes[i].getClassName() );
        }

    }

}

