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

import org.gjt.sp.jedit.jEdit;
import java.util.Comparator;


public class TaskComparator implements Comparator<Task> {
    public int compare( Task a, Task b ) {
        int sortColumn = jEdit.getIntegerProperty( "tasklist.table.sort-column", 1 );
        boolean sortAscending = jEdit.getBooleanProperty( "tasklist.table.sort-ascending", true );

        switch ( sortColumn ) {
            case 2:       // task type
                int value = a.getIdentifier().compareTo( b.getIdentifier() );
                if (value == 0) {
                    // sort by line number if task type is the same
                    return a.getLineIndex() - b.getLineIndex();
                }
                return sortAscending ? value : value * -1;
            default:       // line number
                value = a.getLineIndex() - b.getLineIndex();
                return sortAscending ? value : value * -1;
        }
    }
}