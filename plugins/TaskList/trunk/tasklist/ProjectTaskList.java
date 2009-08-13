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
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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

    public ProjectTaskList( View view ) {
        this.view = view;
        setLayout( new BorderLayout() );
        loadProjectFiles( ProjectViewer.getActiveProject( view ) );
        EditBus.addToBus( this );
    }
    
    public void removeNotify() {
        super.removeNotify();
        EditBus.removeFromBux(this);
    }

    private void loadProjectFiles( final VPTProject project ) {

        class Runner extends SwingWorker<DefaultMutableTreeNode, Object> {

            @Override
            public DefaultMutableTreeNode doInBackground() {
                try {
                    SwingUtilities.invokeLater( 
                        new Runnable() {
                                public void run() {
                                    setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
                                    removeAll();
                                    add( new JLabel( "Please wait, loading project tasks..." ) );
                                    repaint();
                                }
                            }
                                              );
                    return buildTree( project );
                }
                catch ( Exception e ) {
                    return null;
                }
            }

            @Override
            protected void done() {
                final DefaultMutableTreeNode root;
                try {
                    root = ( DefaultMutableTreeNode ) get();
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
                            if ( root.getChildCount() > 0 ) {
                                JTree tree = new JTree( root );
                                for ( int i = tree.getRowCount(); i > 0; i-- ) {
                                    tree.expandRow( i );
                                }
                                tree.addMouseListener( new TreeMouseListener( tree ) );
                                tree.setCellRenderer( new TaskRenderer() );
                                add( new JScrollPane( tree ) );
                            }
                            else {
                                add( new JLabel( "No tasks found." ) );
                            }
                            repaint();
                            setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
                        }
                    }
                );

            }
        }
        Runner runner = new Runner();
        runner.execute();
    }


    private DefaultMutableTreeNode buildTree( VPTProject project ) {
        // get the openable nodes of the project
        Collection nodes = project.getOpenableNodes();

        DefaultMutableTreeNode root = new DefaultMutableTreeNode( "Project: " + project.getName() );

        // check each openable node for tasks
        for ( Iterator it = nodes.iterator(); it.hasNext(); ) {
            VPTNode node = ( VPTNode ) it.next();

            // I'm only handling file nodes, which probably covers
            // better than 99.9% of the nodes in ProjectViewer
            if ( node.isFile() ) {
                VPTFile file_node = ( VPTFile ) node;
                if ( file_node.getFile() == null ) {
                    continue;
                }
                File file = new File( file_node.getFile().getPath() );

                // added this check for binary files just to speed things up.
                // Initially, I'm just checking filename extension for standard
                // image filename extensions, plus .class and .jar files.  There
                // could be others.
                if ( isBinary( file ) ) {
                    continue;
                }

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
                    // pass the buffer to TaskList for parsing, add tree nodes for each buffer
                    // and child nodes for each task found.  Use "parseBuffer" rather than
                    // "extractTasks" since extractTasks just calls parseBuffer in a swing
                    // thread, and I'm already in a swing thread.  Also, parseBuffer will
                    // only parse buffers of the modes allowed by the TaskList mode configuration.
                    HashMap<Integer, Task> tasks = TaskListPlugin.requestTasksForBuffer( buffer );
                    if ( tasks == null || tasks.isEmpty() ) {
                        TaskListPlugin.parseBuffer( buffer );
                        tasks = TaskListPlugin.requestTasksForBuffer( buffer );
                    }

                    if ( tasks != null && tasks.size() > 0 ) {
                        // tasks were found for this buffer, so create the tree node for the buffer itself,
                        // then add tree nodes for the individual tasks.
                        // TODO: TaskList has some display options that need to be supported here
                        DefaultMutableTreeNode buffer_node = new DefaultMutableTreeNode( file.getName() + "(" + file.getParent() + ")" );
                        root.add( buffer_node );

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
                }

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
        }
        return root;
    }

    // Helper method to determine binary files.
    String[] exts = new String[] {".jpg", ".gif", ".png", ".ico", ".bmp", ".class", ".jar", ".war"};
    boolean isBinary( File file ) {
        String filename = file.getName().toLowerCase();
        for ( String ext : exts ) {
            if ( filename.endsWith( ext ) ) {
                return true;
            }
        }
        return false;
    }

    // Helper method to find the mode for the given file.
    Mode findMode( File file ) {
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
    // mouse listener for the tree so clicking on a tree node shows the corresponding
    // line in the edit pane
    class TreeMouseListener extends MouseAdapter {
        JTree tree = null;
        public TreeMouseListener( JTree tree ) {
            this.tree = tree;
        }
        public void mouseReleased( MouseEvent me ) {
            handleClick( me );
        }

        public void mousePressed( MouseEvent me ) {
            handleClick( me );
        }

        private void handleClick( MouseEvent e ) {
            if ( e.getClickCount() == 2 || ( e.getClickCount() == 1 && TaskListPlugin.getAllowSingleClickSelection() ) ) {
                javax.swing.tree.TreePath path = tree.getClosestPathForLocation( e.getX(), e.getY() );
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
    }

    // Custom cell renderer to be able to use the icons from TaskList plugin.
    class TaskRenderer extends DefaultTreeCellRenderer {

        public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,                     // this will be a DefaultMutableTreeNode
            boolean selected,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus ) {

            // the user object is either a string containing the name of the
            // file or a Task
            Object obj = ( ( DefaultMutableTreeNode ) value ).getUserObject();
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
            html.append( task.getLineNumber() );
            html.append( "</td><td>" );
            html.append( task.getText() );
            html.append( "</td></tr></table></html>" );
            setText( html.toString() );
            return this;
        }
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

    }

}