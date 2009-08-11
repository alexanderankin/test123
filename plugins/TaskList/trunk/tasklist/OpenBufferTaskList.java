package tasklist;

import java.util.HashMap;
import javax.swing.*;
import javax.swing.tree.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.BufferUpdate;

/**
 * A list of TaskListTables, one table per open buffer.
 */
public class OpenBufferTaskList extends JPanel implements EBComponent {

    private View view = null;

    private HashMap<Buffer, TaskListTable> model = new HashMap<Buffer, TaskListTable>();

    public OpenBufferTaskList( View view ) {
        this.view = view;
        init();
    }

    private void init() {
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

        EditPane[] editPanes = view.getEditPanes();
        for ( EditPane editPane : editPanes ) {
            Buffer[] buffers = editPane.getBufferSet().getAllBuffers();
            for ( Buffer buffer : buffers ) {
                TaskListTable table = new TaskListTable( view, buffer );
                add( table );
                model.put( buffer, table );
            }
        }
        EditBus.addToBus( this );
    }

    public void handleMessage( EBMessage msg ) {
        if ( msg instanceof BufferUpdate ) {
            BufferUpdate bu = ( BufferUpdate ) msg;

            // only handle messages for our view
            if ( !view.equals( bu.getView() ) ) {
                return ;
            }

            // add or remove buffers from our model
            Buffer buffer = bu.getBuffer();
            if ( BufferUpdate.CLOSED.equals( bu.getWhat() ) ) {
                TaskListTable table = model.get( buffer );
                if ( table != null ) {
                    remove( table );
                    repaint();
                }
            }
            else if ( BufferUpdate.LOADED.equals( bu.getWhat() ) && !model.keySet().contains( buffer ) ) {
                TaskListTable table = new TaskListTable( view, buffer );
                model.put( buffer, table );
                add( table );
                repaint();
            }
        }
    }
}