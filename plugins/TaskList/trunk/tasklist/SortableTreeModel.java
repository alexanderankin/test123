/*
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

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

class SortableTreeModel extends DefaultTreeModel {
    private Comparator comparator;

    public SortableTreeModel( TreeNode node, Comparator c ) {
        super( node );
        comparator = c;
    }

    public SortableTreeModel( TreeNode node, boolean asksAllowsChildren, Comparator c ) {
        super( node, asksAllowsChildren );
        comparator = c;
    }

    public void insertNodeInto( MutableTreeNode child, MutableTreeNode parent ) {
        if (child == null || parent == null) {
            return;   
        }
        int index = findIndexFor( child, parent );
        super.insertNodeInto( child, parent, index );
    }

    public void insertNodeInto( MutableTreeNode child, MutableTreeNode par, int i ) {
        // The index is useless in this model, so just ignore it.
        insertNodeInto( child, par );
    }

    private int findIndexFor( MutableTreeNode child, MutableTreeNode parent ) {
        int cc = parent.getChildCount();
        if ( cc == 0 ) {
            return 0;
        }
        if ( cc == 1 ) {
            return comparator.compare( child, parent.getChildAt( 0 ) ) <= 0 ? 0 : 1;
        }
        return findIndexFor( child, parent, 0, cc - 1 );
    }

    private int findIndexFor( MutableTreeNode child, MutableTreeNode parent, int i1, int i2 ) {
        if ( i1 == i2 ) {
            return comparator.compare( child, parent.getChildAt( i1 ) ) <= 0 ? i1 : i1 + 1;
        }
        int half = ( i1 + i2 ) / 2;
        if ( comparator.compare( child, parent.getChildAt( half ) ) <= 0 ) {
            return findIndexFor( child, parent, i1, half );
        }
        return findIndexFor( child, parent, half + 1, i2 );
    }
}

