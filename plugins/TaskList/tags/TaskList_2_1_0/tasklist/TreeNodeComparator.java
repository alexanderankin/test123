/*
* Copyright (C) 2009, Dale Anson
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
* 
*/

package tasklist;

import java.io.File;
import java.util.Comparator;
import javax.swing.tree.DefaultMutableTreeNode;

import org.gjt.sp.jedit.jEdit;

/**
 * For TaskList, the user objects in the tree nodes are either strings
 * representing buffer paths or Tasks.  In this comparator, only nodes
 * containing strings should be parameters to the compare method. Tasks
 * are not sorted here, they are sorted when the buffer itself is scanned
 * for tasks. This comparator compares based on the user selected display.
 */
public class TreeNodeComparator implements Comparator<DefaultMutableTreeNode> {
    public int compare( DefaultMutableTreeNode o1, DefaultMutableTreeNode o2 ) {
        if ( o1 == null && o2 == null ) {
            return 0;
        }
        if ( o1 == null && o2 != null ) {
            return -1;
        }
        if ( o1 != null && o2 == null ) {
            return 1;
        }

        String objectA = ( ( DefaultMutableTreeNode ) o1 ).getUserObject().toString();
        String objectB = ( ( DefaultMutableTreeNode ) o2 ).getUserObject().toString();
        String displayType = jEdit.getProperty( "tasklist.buffer.display", "" );
        if ( displayType.equals( jEdit.getProperty( "options.tasklist.general.buffer.display.fullpath" ) ) ) {
            // display full path means sort by full path
            return objectA.compareTo(objectB);
        }
        else {
            // display by file name only or filename (directory) means sort by file name
            File fileA = new File( objectA );
            File fileB = new File( objectB );
            return fileA.getName().compareTo( fileB.getName() );
        }
    }
}