package tasklist;


import java.awt.BorderLayout;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;

import common.swingworker.*;
import ise.java.awt.KappaLayout;

/**
 * Base class of all task list trees. Subclasses need to supply 
 * <code>getBuffersToScan</code>.  This class handles all scanning, tree building,
 * tree filtering, and so on.
 */
public abstract class AbstractTreeTaskList extends JPanel implements EBComponent {
    protected View view = null;
    protected JTree tree = null;
    protected TreeModel fullModel = null;
    protected TaskComparator taskComparator = new TaskComparator();
    protected int sortColumn = jEdit.getIntegerProperty( "tasklist.table.sort-column", 1 );
    protected boolean sortAscending = jEdit.getBooleanProperty( "tasklist.table.sort-ascending", true );
    protected JButton stopButton;
    protected Runner runner = null;
    protected String rootDisplayName = "Tasks:";
    protected MouseListener mouseListener = null;

    /**
     * @param view the View this task list is being displayed in.
     * @param rootDisplayName the name to be displayed in the root node of the task list tree.
     */
    public AbstractTreeTaskList( View view, String rootDisplayName ) {
        this.view = view;
        if ( rootDisplayName != null ) {
            this.rootDisplayName = rootDisplayName;
        }
        init();
        EditBus.addToBus( this );
    }

    private void init() {
        setLayout( new BorderLayout() );

        // the stop button is used to stop parsing files, like for a project
        // that has a lot of files and the user doesn't want to wait until
        // they are all done.
        stopButton = new JButton( jEdit.getProperty( "tasklist.projectfiles.stop", "Stop" ) );
        stopButton.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    if ( runner != null ) {
                        runner.cancel( true );
                    }
                }
            }
        );

        loadFiles();
    }

    /**
     * Remove this task list tree from the EditBus.
     */
    public void removeNotify() {
        super.removeNotify();
        EditBus.removeFromBus( this );
    }


    /**
     * Finds the tasks in all files using a SwingWorker so as not to impact
     * performance of the UI.  Subclasses may need to override this method, but
     * should call super.loadFiles to get the Runner to go.
     */
    protected void loadFiles() {
        if ( canRun() ) {
            if ( runner != null ) {
                runner.cancel( true );
            }
            runner = new Runner();
            runner.execute();
        }
    }

    // creates a new tree model from fullModel containing only those task types
    // specified by the active task types for the parent TaskList.
    protected void filterTree() {
        Set<TaskType> activeTypes = TaskListPlugin.getTaskList( view ).getActiveTaskTypes();
        DefaultMutableTreeNode filteredRoot = new DefaultMutableTreeNode( rootDisplayName );
        SortableTreeModel filteredModel = new SortableTreeModel( filteredRoot, new TreeNodeComparator() );

        DefaultMutableTreeNode root = ( DefaultMutableTreeNode ) fullModel.getRoot();
        int taskCount = 0;
        for ( int i = 0; i < root.getChildCount(); i++ ) {
            DefaultMutableTreeNode bufferNode = ( DefaultMutableTreeNode ) root.getChildAt( i );
            DefaultMutableTreeNode filteredBufferNode = new DefaultMutableTreeNode( bufferNode.getUserObject() );
            for ( int j = 0; j < bufferNode.getChildCount(); j++ ) {
                DefaultMutableTreeNode taskNode = ( DefaultMutableTreeNode ) bufferNode.getChildAt( j );
                Task task = ( Task ) taskNode.getUserObject();
                TaskType type = TaskListPlugin.getTaskType( task );
                if ( activeTypes.contains( type ) ) {
                    filteredBufferNode.add( new DefaultMutableTreeNode( task ) );
                    ++taskCount;
                }
            }
            if ( filteredBufferNode.getChildCount() > 0 ) {
                filteredRoot.add( filteredBufferNode );
            }
        }
        String rootDisplay = filteredRoot.getUserObject().toString() + " (" + taskCount + " task" + (taskCount > 1 ? "s)" : ")"); 
        filteredRoot.setUserObject(rootDisplay);
        tree.setModel( filteredModel );
        expandTree();
        invalidate();
        validate();

    }

    /**
     * Default implementation returns <code>true</code>.  Subclasses may override
     * this based on their particular needs, for example, the user may set
     * plugin option settings to prevent a particular subclass from displaying or
     * running.
     * @return true if this class is allowed to load and display tasks for files.
     */
    protected boolean canRun() {
        return true;
    }

    /**
     * Subclasses can override this to return false if they don't want the
     * progress bar shown during parsing.  This default implementation
     * returns true.
     */
    protected boolean showProgress() {
        return true;
    }

    // this worker parses files for tasks and creates the tree for display
    private class Runner extends common.swingworker.SwingWorker<TreeModel, Object> {

        private JProgressBar progressBar = new JProgressBar( 0, 100 );
        
        public Runner() {
            super();
            TaskListPlugin.addRunner(this);
        }

        @Override
        public TreeModel doInBackground() {
            try {
                // build the tree model
                if ( showProgress() ) {
                    SwingUtilities.invokeLater(
                        new Runnable() {
                            public void run() {
                                // show a progress bar while the model is loading
                                removeAll();

                                progressBar.setStringPainted( true );
                                JPanel progressPanel = new JPanel( new KappaLayout() );
                                progressPanel.add( "0, 0, 1, 1, 0, w, 3", new JLabel( jEdit.getProperty( "tasklist.loadingtasksfromfiles.", "Please wait, loading tasks from files..." ) ) );
                                progressPanel.add( "0, 1, 1, 1, 0, w, 3", progressBar );
                                JPanel btnPanel = new JPanel();
                                btnPanel.add( stopButton );
                                progressPanel.add( "0, 2, 1, 1, 0, 0, 3", btnPanel );

                                add( progressPanel, BorderLayout.CENTER );
                                invalidate();
                                validate();
                            }
                        }
                    );
                    addPropertyChangeListener(
                        new PropertyChangeListener() {
                            public void propertyChange( PropertyChangeEvent evt ) {
                                if ( "progress".equals( evt.getPropertyName() ) ) {
                                    progressBar.setValue( ( Integer ) evt.getNewValue() );
                                }
                            }
                        }
                    );
                }
                return buildTreeModel();
            }
            catch ( Exception e ) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public boolean cancel( boolean mayInterruptIfRunning ) {
            boolean cancelled = super.cancel( mayInterruptIfRunning );
            if ( cancelled ) {
                SwingUtilities.invokeLater(
                    new Runnable() {
                        public void run() {
                            removeAll();
                            invalidate();
                            validate();
                        }
                    }
                );
            }
            TaskListPlugin.removeRunner(this);
            return cancelled;
        }

        @Override
        protected void done() {
            try {
                fullModel = ( TreeModel ) get();
                if ( fullModel == null ) {
                    return ;
                }
            }
            catch ( Exception e ) {
                return ;
            }
            SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        // build the display
                        removeAll();
                        if ( fullModel.getChildCount( fullModel.getRoot() ) > 0 ) {
                            tree = new JTree( fullModel );
                            filterTree();
                            expandTree();
                            tree.addMouseListener( new TreeMouseListener( view, tree ) );
                            tree.addKeyListener( new TreeKeyListener( view, tree ) );
                            tree.setCellRenderer( new TaskTreeCellRenderer() );
                            add( new JScrollPane( tree ) );
                        }
                        else {
                            JLabel label = new JLabel( jEdit.getProperty( "tasklist.no-tasks-found", "No tasks found." ) );
                            add( label );
                        }
                        invalidate();
                        validate();
                    }
                }
            );
            TaskListPlugin.removeRunner(this);
            runner = null;
        }

        // This tree model is only 3 levels deep:
        // 1. Root node user object is a string that contains the title of the tree.
        // Root node is the parent for the buffer nodes.
        // 2. Buffer node user object is a string containing the results of
        // Buffer.getPath().  Buffer nodes are the parents for task nodes.
        // 3. Task node user objects are Tasks.
        private TreeModel buildTreeModel() {

            DefaultMutableTreeNode root = new DefaultMutableTreeNode( rootDisplayName );
            SortableTreeModel model = new SortableTreeModel( root, new TreeNodeComparator() );

            List<String> toScan = getBuffersToScan();
            if ( toScan == null ) {
                return model;
            }
            
            Buffer[] buffers = jEdit.getBuffers();
            HashMap<String, Buffer> openBuffers = new HashMap<String, Buffer>();
            for (Buffer b : buffers) {
                openBuffers.put(b.getPath(), b);   
            }
            
            for ( int i = 0; i < toScan.size(); i++ ) {
                if ( isCancelled() ) {
                    return model;
                }
                String path = toScan.get( i );

                setProgress( 100 * i / toScan.size() );

                // the buffer could already be open in jEdit.  If so, don't
                // close it below.
                Buffer buffer = openBuffers.get( path );
                
                boolean can_close = false;
                if ( buffer == null && !Binary.isBinary(path) ) {
                    // file is not open, so open it.  Note that the mode must be
                    // set explicitly since openTemporary won't actually set the mode
                    // and TaskList will fail if the mode is missing.  openTemporary
                    // is preferred over openFile since openTemporary won't send EditBus
                    // messages nor is the buffer added to the buffer list.
                    buffer = jEdit.openTemporary( jEdit.getActiveView(), null, path, false );
                    Mode mode = TaskListPlugin.setMode( buffer );
                    if ( mode == null ) {
                        continue;
                    }
                    if (Binary.isBinary(buffer)) {
                        continue;   
                    }
                    
                    // files open this way can be closed when TaskList parsing is complete.
                    can_close = true;
                }
                DefaultMutableTreeNode buffer_node = getNodeForBuffer( buffer );
                if ( buffer_node == null ) {
                    continue;
                }
                model.insertNodeInto( buffer_node, root );

                // I sent email to the dev list asking about the proper way to
                // close a temporary buffer. For now all I'm doing to close the buffer
                // if it wasn't already open is set it to null.  If can_close is true,
                // then the buffer was opened with openTemporary, so just set it to null
                // and let the garbage collector handle it.  Calling any of the jEdit
                // 'close buffer' methods with a temporary buffer confuses the internal
                // jEdit buffer lists, which causes lots of problems, plus the 'close
                // buffer' methods all send EditBus messages, which I want to avoid.
                if ( can_close ) {
                    buffer = null;
                }
            }
            return model;
        }
    }

    /**
     * Subclasses must override this method to return a list of the names of buffers to scan.
     * It is assumed that these are absolute file paths.
     * @return a list of buffers to scan for tasks, or null if no buffers match
     * the criteria for this tree to display.
     */
    protected abstract List<String> getBuffersToScan();

    /**
     * Create a tree node for the given buffer.  The node will display the
     * name of the buffer, the buffer will be scanned for tasks, and one child
     * node will be added for each task found in the buffer.
     * @param buffer The buffer to search for tasks.
     * @return A tree node with tasks as children or null if no tasks found.
     */
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
                buffer_node = new DefaultMutableTreeNode( buffer.getPath() );

                ArrayList<Task> sorted_tasks = new ArrayList<Task>( tasks.values() );
                Collections.sort( sorted_tasks, taskComparator );
                for ( Task task : sorted_tasks ) {
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

    /**
     * Subclasses may override this to handle specific messages, and should call
     * super.handleMessage() for any message the subclass can't handle.
     */
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
        }
        else if ( msg instanceof ParseBufferMessage ) {
            ParseBufferMessage pbm = ( ParseBufferMessage)msg;
            if ( ParseBufferMessage.DO_PARSE.equals( pbm.getWhat() ) ) {
                removeBuffer( pbm.getBuffer() );
                addBuffer( pbm.getBuffer() );
                repaint();
            }
            else if ( ParseBufferMessage.DO_PARSE_ALL.equals( pbm.getWhat() ) ) {
                loadFiles();
            }
            else if ( ParseBufferMessage.APPLY_FILTER.equals( pbm.getWhat() ) ) {
                filterTree();
            }
        }
        else if ( msg instanceof PropertiesChanged ) {
            int _sortColumn = jEdit.getIntegerProperty( "tasklist.table.sort-column", 1 );
            boolean _sortAscending = jEdit.getBooleanProperty( "tasklist.table.sort-ascending", true );
            if ( sortColumn != _sortColumn || sortAscending != _sortAscending ) {
                DefaultMutableTreeNode root = ( DefaultMutableTreeNode ) tree.getModel().getRoot();
                Enumeration bufferNodes = root.children();
                while ( bufferNodes.hasMoreElements() ) {
                    DefaultMutableTreeNode bufferNode = ( DefaultMutableTreeNode ) bufferNodes.nextElement();
                    ArrayList<Task> tasks = new ArrayList<Task>();
                    Enumeration taskNodes = bufferNode.children();
                    while ( taskNodes.hasMoreElements() ) {
                        DefaultMutableTreeNode taskNode = ( DefaultMutableTreeNode ) taskNodes.nextElement();
                        tasks.add( ( Task ) taskNode.getUserObject() );
                    }
                    bufferNode.removeAllChildren();
                    Collections.sort( tasks, taskComparator );
                    for ( Task task : tasks ) {
                        bufferNode.add( new DefaultMutableTreeNode( task ) );
                    }
                }
                ( ( DefaultTreeModel ) tree.getModel() ).nodeStructureChanged( root );
                expandTree();
            }
            TaskListPlugin.propertiesChanged();
        }
    }

    private void addBuffer( Buffer buffer ) {
        DefaultMutableTreeNode buffer_node = getNodeForBuffer( buffer );
        if ( buffer_node == null || tree == null ) {
            return ;
        }
        SortableTreeModel model = ( SortableTreeModel ) tree.getModel();
        model.insertNodeInto( buffer_node, ( DefaultMutableTreeNode ) model.getRoot() );
        model.nodeStructureChanged( ( DefaultMutableTreeNode ) model.getRoot() );
        filterTree();
        expandTree();
    }

    private void removeBuffer( Buffer buffer ) {
        if ( tree == null ) {
            return ;
        }
        SortableTreeModel model = ( SortableTreeModel ) tree.getModel();
        for ( int i = 0; i < model.getChildCount( model.getRoot() ); i++ ) {
            DefaultMutableTreeNode node = ( DefaultMutableTreeNode ) model.getChild( model.getRoot(), i );
            String buffer_name = ( String ) node.getUserObject();
            if ( buffer_name.equals( buffer.getPath() ) ) {
                model.removeNodeFromParent( node );
                model.nodeStructureChanged( ( DefaultMutableTreeNode ) model.getRoot() );
                expandTree();
                break;
            }
        }
    }

    private void expandTree() {
        for ( int i = tree.getRowCount(); i > 0; i-- ) {
            tree.expandRow( i );
        }
    }

}