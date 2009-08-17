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
import java.io.File;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;

import projectviewer.*;
import projectviewer.event.ViewerUpdate;
import projectviewer.vpt.*;

import common.swingworker.*;

public class ProjectTaskList extends JPanel implements EBComponent {

    private View view = null;
    private JTree tree = null;

    public ProjectTaskList( View view ) {
        this.view = view;
        setLayout( new BorderLayout() );
        loadProjectFiles( ProjectViewer.getActiveProject( view ) );
        EditBus.addToBus( this );
    }

    public void removeNotify() {
        super.removeNotify();
        EditBus.removeFromBus( this );
    }

    // finds the tasks in all project files using a SwingWorker so as not to impact
    // performance of the UI.
    private void loadProjectFiles( final VPTProject project ) {

        class Runner extends SwingWorker<TreeModel, Object> {

            @Override
            public TreeModel doInBackground() {
                try {
                    SwingUtilities.invokeLater(
                        new Runnable() {
                            public void run() {
                                setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
                                removeAll();
                                add( new JLabel( jEdit.getProperty( "tasklist.projectfiles.wait", "Please wait, loading project tasks..." ) ) );
                                repaint();
                            }
                        }
                    );
                    return buildTreeModel( project );
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
        if ( jEdit.getBooleanProperty( "tasklist.show-project-files" ) ) {
            Runner runner = new Runner();
            runner.execute();
        }
    }

    protected List<String> getBuffersToScan( VPTProject project ) {
        List<String> toScan = new ArrayList<String>();
        Collection nodes = project.getOpenableNodes();
        for ( Iterator it = nodes.iterator(); it.hasNext(); ) {
            VPTNode node = ( VPTNode ) it.next();

            // I'm only handling file nodes, which probably covers
            // better than 99.9% of the nodes in ProjectViewer
            if ( node.isFile() ) {
                VPTFile file_node = ( VPTFile ) node;
                if ( file_node.getFile() == null ) {
                    continue;
                }

                String path = file_node.getFile().getPath();

                // added this check for binary files just to speed things up.
                // Initially, I'm just checking filename extension for standard
                // image filename extensions, plus .class and .jar files.  There
                // could be others.
                if ( isBinary( path ) ) {
                    continue;
                }
                System.out.println("+++++ will scan: " + path);
                toScan.add( path );
            }
        }
        return toScan;
    }

    // Helper method to determine binary files.
    String[] exts = new String[] {".jpg", ".gif", ".png", ".ico", ".bmp", ".class", ".jar", ".war"};
    boolean isBinary( String file ) {
        String filename = file.toLowerCase();
        for ( String ext : exts ) {
            if ( filename.endsWith( ext ) ) {
                return true;
            }
        }
        return false;
    }

    protected TreeModel buildTreeModel( VPTProject project ) {

        DefaultMutableTreeNode root = new DefaultMutableTreeNode( jEdit.getProperty("tasklist.projectfiles.project", "Project:") + " " + project.getName() );
        SortableTreeModel model = new SortableTreeModel( root, new TreeNodeStringComparator() );

        List<String> toScan = getBuffersToScan( project );
        for ( String path : toScan ) {
            File file = new File( path );

            // the buffer could already be open in jEdit.  If so, don't
            // close it below.
            Buffer buffer = jEdit.getBuffer( file.getAbsolutePath() );
            boolean can_close = false;
            if ( buffer == null ) {
                // file is not open, so open it.  Note that the mode must be
                // set explicitly since openTemporary won't actually set the mode
                // and TaskList will fail if the mode is missing.  openTemporary
                // is preferred over openFile since openTemporary won't send EditBus
                // messages nor is the buffer added to the buffer list.
                buffer = jEdit.openTemporary( jEdit.getActiveView(), file.getParent(), file.getName(), false );
                Mode mode = findMode( file );
                if ( mode == null ) {
                    continue;
                }
                buffer.setMode( mode );

                // files open this way can be closed when TaskList parsing is complete.
                can_close = true;
            }
            try {
                while(buffer.isLoading()) {
                    Thread.currentThread().sleep(5);                    
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            DefaultMutableTreeNode buffer_node = getNodeForBuffer( buffer );
            if ( buffer_node == null ) {
                continue;
            }
            model.insertNodeInto( buffer_node, root );
            // TODO: I sent email to the dev list asking about the proper way to
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

    // Helper method to find the mode for the given file.
    // TODO: is mode really necessary?  TaskList needs mode set, but is just
    // using "text" good enough? -- yes it is.  TaskListPlugin will only parse
    // buffers with modes as selected by the user in the plugin options.
    private Mode findMode( File file ) {
        return jEdit.getMode( "text" );
        /*
        try {
            BufferedReader reader = new BufferedReader( new FileReader( file ) );
            String firstLine = reader.readLine();
            reader.close();
            Mode[] modes = jEdit.getModes();
            for ( Mode mode : modes ) {
                if ( mode.accept( file.getAbsolutePath(), firstLine ) ) {
                    return mode;
                }
            }
    }
        catch ( Exception e ) {}        // NOPMD

        return null;
        */
    }

    public void handleMessage( EBMessage msg ) {
        if ( msg.getClass().getName().equals( "projectviewer.event.ViewerUpdate" ) ) {
            ViewerUpdate vu = ( ViewerUpdate ) msg;
            if ( ViewerUpdate.Type.PROJECT_LOADED.equals( vu.getType() ) && vu.getView().equals( view ) ) {
                VPTProject project = ( VPTProject ) vu.getNode();
                if ( project != null ) {
                    loadProjectFiles( project );
                }
            }
        }
        else if ( msg instanceof BufferUpdate ) {
            BufferUpdate bu = ( BufferUpdate ) msg;

            // only handle messages for our view
            if ( !view.equals( bu.getView() ) ) {
                return ;
            }
            
            if ( BufferUpdate.SAVED.equals( bu.getWhat() ) || ParseBufferMessage.DO_PARSE.equals( bu.getWhat() ) ) {
                Buffer buffer = bu.getBuffer();
                removeBuffer( buffer );
                addBuffer( buffer );
                repaint();
            }
            else if ( ParseBufferMessage.DO_PARSE_ALL.equals( bu.getWhat() ) ) {
                loadProjectFiles( ProjectViewer.getActiveProject( view ) );
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