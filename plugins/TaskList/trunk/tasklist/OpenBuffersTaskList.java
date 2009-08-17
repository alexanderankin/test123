/**
* A macro to show all of the tasks that the TaskList plugin would show
* if the TaskList plugin had any concept of ProjectViewer.  This macro
* gets the list of files from ProjectViewer for the current project,
* passes each of them to TaskList to find the tasks for each file, and
* combines them all into a single tree display.  This puts all the tasks
* for the entire project in a single display.
*
* @author Dale Anson, 3 Nov 2008
*/
package tasklist;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;


import common.swingworker.*;

public class OpenBuffersTaskList extends JPanel implements EBComponent {

    private View view = null;
    private JTree tree = null;

    public OpenBuffersTaskList( View view ) {
        this.view = view;
        setLayout( new BorderLayout() );
        loadOpenFiles();
        EditBus.addToBus( this );
    }

    public void removeNotify() {
        super.removeNotify();
        EditBus.removeFromBus( this );
    }

    // finds the tasks in all open files using a SwingWorker so as not to impact
    // performance of the UI.
    private void loadOpenFiles() {

        class Runner extends SwingWorker<TreeModel, Object> {

            @Override
            public TreeModel doInBackground() {
                try {
                    SwingUtilities.invokeLater(
                        new Runnable() {
                            public void run() {
                                setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
                                removeAll();
                                add( new JLabel( jEdit.getProperty( "tasklist.openfiles.wait", "Please wait, loading tasks from open files..." ) ) );
                                repaint();
                            }
                        }
                    );
                    return buildTreeModel();
                }
                catch ( Exception e ) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void done() {
                final TreeModel model;
                try {
                    model = ( TreeModel ) get();
                    if ( model == null ) {
                        return ;
                    }
                }
                catch ( Exception e ) {
                    setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
                    return ;
                }
                SwingUtilities.invokeLater(
                    new Runnable() {
                        public void run() {
                            // build the display
                            removeAll();
                            if ( model.getChildCount( model.getRoot() ) > 0 ) {
                                tree = new JTree( model );
                                for ( int i = tree.getRowCount(); i > 0; i-- ) {
                                    tree.expandRow( i );
                                }
                                tree.addMouseListener( new TreeMouseListener( view, tree ) );
                                tree.setCellRenderer( new TaskTreeCellRenderer() );
                                add( new JScrollPane( tree ) );
                            }
                            else {
                                add( new JLabel( jEdit.getProperty( "tasklist.no-tasks-found", "No tasks found." ) ) );
                            }
                            repaint();
                            setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
                        }
                    }
                );

            }
        }
        if ( jEdit.getBooleanProperty( "tasklist.show-open-files" ) ) {
            Runner runner = new Runner();
            runner.execute();
        }
    }

    protected List<Buffer> getBuffersToScan() {
        // fetch all open buffers
        List<Buffer> openBuffers = new ArrayList<Buffer>();
        EditPane[] editPanes = view.getEditPanes();
        for ( EditPane editPane : editPanes ) {
            Buffer[] buffers = editPane.getBufferSet().getAllBuffers();
            for ( Buffer buffer : buffers ) {
                openBuffers.add( buffer );
            }
        }
        return openBuffers;
    }
    
    protected TreeModel buildTreeModel() {
        
        List<Buffer> openBuffers = getBuffersToScan();
        
        DefaultMutableTreeNode root = new DefaultMutableTreeNode( jEdit.getProperty( "tasklist.openfiles.open-files", "Open Files:" ) );
        SortableTreeModel model = new SortableTreeModel( root, new TreeNodeStringComparator() );

        // check each open buffer for tasks
        for ( Buffer buffer : openBuffers ) {
            DefaultMutableTreeNode buffer_node = getNodeForBuffer( buffer );
            if ( buffer_node == null ) {
                continue;
            }
            model.insertNodeInto( buffer_node, root );
        }
        return model;
    }

    private DefaultMutableTreeNode getNodeForBuffer( Buffer buffer ) {
        DefaultMutableTreeNode buffer_node = null;
        try {
            // pass the buffer to TaskList for parsing, add tree nodes for each buffer
            // and child nodes for each task found.  Use "parseBuffer" rather than
            // "extractTasks" since extractTasks just calls parseBuffer in a swing
            // thread, and I'm already in a swing thread.  Also, parseBuffer will
            // only parse buffers of the modes allowed by the TaskList mode configuration.
            TaskListPlugin.parseBuffer( buffer );
            HashMap<Integer, Task> tasks = TaskListPlugin.requestTasksForBuffer( buffer );

            if ( tasks != null && tasks.size() > 0 ) {
                // tasks were found for this buffer, so create the tree node for the buffer itself,
                // then add tree nodes for the individual tasks.
                // TODO: TaskList has some display options that need to be supported here
                buffer_node = new DefaultMutableTreeNode( buffer.toString() );

                // the "tasks" hashtable has the line number as the key, so putting
                // "tasks" into a TreeMap sorts by line number
                // TODO: TaskList has other sort options than line number, those need to be
                // supported here.
                TreeMap<Integer, Task> sorted_tasks = new TreeMap<Integer, Task>( tasks );


                for ( Iterator tli = sorted_tasks.values().iterator(); tli.hasNext(); ) {
                    Task task = ( Task ) tli.next();
                    DefaultMutableTreeNode task_node = new DefaultMutableTreeNode( task );
                    buffer_node.add( task_node );
                }
            }
        }
        catch ( Exception e ) {     // NOPMD
            // ignore any exception, there really isn't anything to do about
            // it.  The most likely cause is the buffer didn't get loaded by
            // jEdit before TaskList tried to parse it.
            e.printStackTrace();
        }
        return buffer_node;
    }


    public void handleMessage( EBMessage msg ) {
        if ( msg instanceof BufferUpdate ) {
            BufferUpdate bu = ( BufferUpdate ) msg;

            // only handle messages for our view
            if ( !view.equals( bu.getView() ) ) {
                return ;
            }

            final Buffer buffer = bu.getBuffer();

            if ( BufferUpdate.CLOSED.equals( bu.getWhat() ) ) {
                removeBuffer( buffer );
                repaint();
            }
            else if ( BufferUpdate.LOADED.equals( bu.getWhat() ) ) {
                addBuffer( buffer );
                repaint();
            }
            else if ( BufferUpdate.SAVED.equals( bu.getWhat() ) || ParseBufferMessage.DO_PARSE.equals( bu.getWhat()) ) {
                removeBuffer( buffer );
                addBuffer( buffer );
                repaint();
            }
            else if ( ParseBufferMessage.DO_PARSE_ALL.equals( bu.getWhat() ) ) {
                loadOpenFiles();
            }
        }
    }

    private void addBuffer( Buffer buffer ) {
        DefaultMutableTreeNode buffer_node = getNodeForBuffer( buffer );
        if ( buffer_node == null ) {
            return ;
        }
        SortableTreeModel model = ( SortableTreeModel ) tree.getModel();
        model.insertNodeInto( buffer_node, ( DefaultMutableTreeNode ) model.getRoot() );
        model.nodeStructureChanged( ( DefaultMutableTreeNode ) model.getRoot() );
        for ( int i = tree.getRowCount(); i > 0; i-- ) {
            tree.expandRow( i );
        }
    }

    //
    private void removeBuffer( Buffer buffer ) {
        SortableTreeModel model = ( SortableTreeModel ) tree.getModel();
        for ( int i = 0; i < model.getChildCount( model.getRoot() ); i++ ) {
            DefaultMutableTreeNode node = ( DefaultMutableTreeNode ) model.getChild( model.getRoot(), i );
            String buffer_name = ( String ) node.getUserObject();
            if ( buffer_name.equals( buffer.toString() ) ) {
                model.removeNodeFromParent( node );
                model.nodeStructureChanged( ( DefaultMutableTreeNode ) model.getRoot() );
                for ( int j = tree.getRowCount(); j > 0; j-- ) {
                    tree.expandRow( j );
                }
                break;
            }
        }
    }
}