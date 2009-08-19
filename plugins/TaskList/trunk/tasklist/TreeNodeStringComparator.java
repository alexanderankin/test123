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

import java.util.Comparator;
import javax.swing.tree.DefaultMutableTreeNode;

public class TreeNodeStringComparator implements Comparator<DefaultMutableTreeNode> {
    public int compare( DefaultMutableTreeNode o1, DefaultMutableTreeNode o2 ) {
        if (o1 == null && o2 == null) {
            return 0;   
        }
        if (o1 == null && o2 != null) {
            return -1;   
        }
        if (o1 != null && o2 == null) {
            return 1;   
        }
        String a = ( ( DefaultMutableTreeNode ) o1 ).getUserObject().toString();
        String b = ( ( DefaultMutableTreeNode ) o2 ).getUserObject().toString();
        return a.compareTo( b );
    }
}