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


import org.gjt.sp.jedit.MiscUtilities;


/**
 * A comparator to compare two <code>JavaPackage</code> objects.
 *
 * @see org.gjt.sp.jedit.MiscUtilities.Compare
 * @see buildtools.java.packagebrowser.JavaPackage
 * @author Andre Kaplan
 * @version $Id$
 */
public class JavaPackageComparator implements MiscUtilities.Compare {

    public int compare(Object obj1, Object obj2) {
        JavaPackage one = (JavaPackage) obj1;
        JavaPackage two = (JavaPackage) obj2;
        return one.getName().compareTo(two.getName());
    }

}


