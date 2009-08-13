package tasklist;

import java.awt.BorderLayout;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.BufferUpdate;
import projectviewer.*;
import projectviewer.event.ViewerUpdate;
import projectviewer.vpt.*;

/**
 * A list of TaskListTables, one table per open buffer.
 * TODO: figure out how to sort the display per buffer name
 */
public class OpenBufferTaskList extends JPanel implements EBComponent {

    private View view = null;

    private Map<String, TaskListTable> model = null;

    private JPanel contentPane = null;

    public OpenBufferTaskList( View view ) {
        this.view = view;
        init();
    }

    private void init() {
        setLayout( new BorderLayout() );
        contentPane = new JPanel();
        contentPane.setLayout( new BoxLayout( contentPane, BoxLayout.Y_AXIS ) );

        model = new TreeMap<String, TaskListTable>();
        EditPane[] editPanes = view.getEditPanes();
        for ( EditPane editPane : editPanes ) {
            Buffer[] buffers = editPane.getBufferSet().getAllBuffers();
            for ( Buffer buffer : buffers ) {
                TaskListTable table = new TaskListTable( view, buffer, false );
                contentPane.add( table );
                model.put( buffer.toString(), table );
            }
        }

        JScrollPane scrollPane = new JScrollPane( contentPane );
        scrollPane.getVerticalScrollBar().setUnitIncrement( 10 );
        add( scrollPane, BorderLayout.CENTER );

        EditBus.addToBus( this );
    }
    
    public void removeNotify() {
        super.removeNotify();
        EditBus.removeFromBux(this);
    }

    public void handleMessage( EBMessage msg ) {
        if ( msg.getClass().getName().equals( "projectviewer.event.ViewerUpdate" ) ) {
            ViewerUpdate vu = ( ViewerUpdate ) msg;
            if ( ViewerUpdate.Type.PROJECT_LOADED.equals( vu.getType() ) && vu.getView().equals( view ) ) {
                remove( contentPane );
                init();
            }
        }
        else if ( msg instanceof BufferUpdate ) {
            BufferUpdate bu = ( BufferUpdate ) msg;

            // only handle messages for our view
            if ( !view.equals( bu.getView() ) ) {
                return ;
            }

            // add or remove buffers from our model
            final Buffer buffer = bu.getBuffer();
            if ( BufferUpdate.CLOSED.equals( bu.getWhat() ) ) {
                final TaskListTable table = model.get( buffer.toString() );
                if ( table != null ) {
                    model.remove( buffer.toString() );
                    SwingUtilities.invokeLater(
                        new Runnable() {
                            public void run() {
                                contentPane.remove( table );
                                contentPane.repaint();
                            }
                        }
                    );
                }
            }
            else if ( BufferUpdate.LOADED.equals( bu.getWhat() ) && !model.keySet().contains( buffer ) ) {
                SwingUtilities.invokeLater(
                    new Runnable() {
                        public void run() {
                            TaskListTable table = new TaskListTable( view, buffer, false );
                            model.put( buffer.toString(), table );
                            contentPane.add( table );
                            contentPane.repaint();
                        }
                    }
                );
            }
        }
    }
}