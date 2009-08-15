package tasklist;



import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import org.gjt.sp.jedit.*;



// mouse listener for the tree so clicking on a tree node shows the corresponding
// line in the edit pane
public class TreeMouseListener extends MouseAdapter {
    
    private View view = null;
    private JTree tree = null;
    
    public TreeMouseListener( View view, JTree tree ) {
        this.view = view;
        this.tree = tree;
    }
    public void mouseReleased( MouseEvent me ) {
        handleClick( me );
    }

    public void mousePressed( MouseEvent me ) {
        handleClick( me );
    }

    private void handleClick( MouseEvent e ) {
        if ( e.getClickCount() == 1 && e.isPopupTrigger() ) {
            e.consume();
            showPopup( e.getPoint() );
        }
        if ( e.getClickCount() == 2 || ( e.getClickCount() == 1 && TaskListPlugin.getAllowSingleClickSelection() ) ) {
            TreePath path = tree.getClosestPathForLocation( e.getX(), e.getY() );
            Task task = null;
            if ( path.getPathCount() > 2 ) {
                task = ( Task ) ( ( DefaultMutableTreeNode ) path.getLastPathComponent() ).getUserObject();
                Buffer buffer = task.getBuffer();
                int line_number = task.getLineNumber();
                int start_offset = task.getStartOffset();
                jEdit.openFile( jEdit.getActiveView(), buffer.getPath() );
                EditPane edit_pane = jEdit.getActiveView().showBuffer( buffer );
                edit_pane.getTextArea().scrollTo( line_number, start_offset, true );
                edit_pane.getTextArea().setCaretPosition( task.getStartPosition().getOffset() );
            }
        }
    }

    private void showPopup( Point p ) {
        TaskListPopup popup = new TaskListPopup( view, tree, p );
        // NOTE: keep within screen limits; use task list panel, not table
        SwingUtilities.convertPointToScreen( p, tree );
        SwingUtilities.convertPointFromScreen( p, tree );
        Dimension dt = tree.getSize();
        Dimension dp = popup.getPreferredSize();
        if ( p.x + dp.width > dt.width )
            p.x = dt.width - dp.width;
        if ( p.y + dp.height > dt.height )
            p.y = dt.height - dp.height;
        popup.show( tree, p.x + 1, p.y + 1 );

    }
}