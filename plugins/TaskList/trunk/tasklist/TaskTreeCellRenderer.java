package tasklist;

import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.*;

// Custom cell renderer to be able to use the icons from TaskList plugin.
public class TaskTreeCellRenderer extends DefaultTreeCellRenderer {

    public Component getTreeCellRendererComponent(
        JTree tree,
        Object value,                       // this will be a DefaultMutableTreeNode
        boolean selected,
        boolean expanded,
        boolean leaf,
        int row,
        boolean hasFocus ) {

        // the user object is either a string containing the name of the
        // file or a Task
        
        Object obj = ( ( DefaultMutableTreeNode ) value ).getUserObject();
        if (obj == null ) {
            return null;   
        }
        if ( obj instanceof String ) {
            // file name node
            super.getTreeCellRendererComponent( tree, value, selected, expanded, leaf, row, hasFocus );
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