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

import java.awt.Component;
import java.io.File;
import javax.swing.JTree;
import javax.swing.tree.*;
import org.gjt.sp.jedit.jEdit;


// Custom cell renderer to be able to use the icons from TaskList plugin.
public class TaskTreeCellRenderer extends DefaultTreeCellRenderer {

    public Component getTreeCellRendererComponent(
        JTree tree,
        Object value,                         // this will be a DefaultMutableTreeNode
        boolean selected,
        boolean expanded,
        boolean leaf,
        int row,
        boolean hasFocus ) {


        DefaultMutableTreeNode treeNode = ( DefaultMutableTreeNode ) value;
        if ( treeNode.equals( tree.getModel().getRoot() ) ) {
            super.getTreeCellRendererComponent( tree, value, selected, expanded, leaf, row, hasFocus );
            return this;
        }

        // the user object is either a string containing the name of the
        // file or a Task
        Object obj = treeNode.getUserObject();
        if ( obj == null ) {
            return null;
        }
        if ( obj instanceof String ) {
            // file name node
            super.getTreeCellRendererComponent( tree, value, selected, expanded, leaf, row, hasFocus );
            String bufferDisplay;
            String displayType = jEdit.getProperty( "tasklist.buffer.display", "" );
            if ( displayType.equals( jEdit.getProperty( "options.tasklist.general.buffer.display.fullpath" ) ) ) {
                bufferDisplay = ( String ) obj;
            }
            else if ( displayType.equals( jEdit.getProperty( "options.tasklist.general.buffer.display.nameonly" ) ) ) {
                File file = new File( ( String ) obj );
                bufferDisplay = file.getName();
            }
            else {
                // filename (directory)
                File file = new File( ( String ) obj );
                bufferDisplay = file.getName() + " (" + file.getParent() + ")";
            }
            setText( bufferDisplay );
            setIcon( null );
            return this;
        }

        // must be a task node
        Task task = ( Task ) obj;
        setIcon( task.getIcon() );
        setIconTextGap( 0 );
        StringBuilder html = new StringBuilder();
        html.append( "<html><table><tr><td width=\"50\" align=\"right\">" );
        html.append( task.getLineNumber() + 1 );
        html.append( "</td><td>" );
        html.append( task.getText() );
        html.append( "</td></tr></table></html>" );
        setText( html.toString() );
        return this;
    }
}