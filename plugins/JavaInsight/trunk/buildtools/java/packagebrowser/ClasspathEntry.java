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

import java.util.Hashtable;
import java.util.Enumeration;

public class ClasspathEntry {
    

    private String      name        = null;
    private Hashtable   packages    = new Hashtable();
    
    /**
     * Creates a ClasspathEntry
     *
     * @param name The name of this classpath entry
     * @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
     * @version $Id$
     */
    public ClasspathEntry(String name) {
        this.name = name;
    }

    /**
     * Add an JavaPackage to this ClasspathEntry
     *
     * @param name The name of this classpath entry
     * @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
     * @version $Id$
     */
    public void addJavaPackage( JavaPackage javaPackage ) {
        this.packages.put(javaPackage.getName(), javaPackage);
    }

    public boolean containsJavaPackage(  JavaPackage javaPackage ) {
        return this.packages.containsKey( javaPackage.getName() );
    }
    
    /**
     * Return the packages within this ClasspathEntry
     *
     * @param name The name of this classpath entry
     * @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
     * @version $Id$
     */
    public JavaPackage[] getPackages() {

        //now convert the hashtable into an array 
        JavaPackage[] packages = new JavaPackage[this.packages.size()];
        
        Enumeration enum = this.packages.elements();
        int element = 0;
        while(enum.hasMoreElements() ) {
            packages[element] = (JavaPackage)enum.nextElement();
            ++element;
        }

        return packages;
    }

    /**
     * Return the name of this
     *
     * @param name The name of this classpath entry
     * @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
     * @version $Id$
     */
    public String getName() {
        return this.name;
    }

    /**
     * Return this ClasspathEntry as a String
     *
     * @param name The name of this classpath entry
     * @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
     * @version $Id$
     */
    public String toString() {
        return this.getName();
    }
    
}

