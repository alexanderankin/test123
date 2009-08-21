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

/**
* This code is based on:
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
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
    private TaskComparator taskComparator = new TaskComparator();
    private int sortColumn = jEdit.getIntegerProperty( "tasklist.table.sort-column", 1 );
    private boolean sortAscending = jEdit.getBooleanProperty( "tasklist.table.sort-ascending", true );
    private JButton stopButton;
    private JButton startButton;
    private Runner runner = null;

    public ProjectTaskList( View view ) {
        this.view = view;
        setLayout( new BorderLayout() );
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
        startButton = new JButton( jEdit.getProperty( "tasklist.projectfiles.start", "Start" ) );
        startButton.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    if ( runner != null ) {
                        loadProjectFiles( ProjectViewer.getActiveProject( ProjectTaskList.this.view ) );
                    }
                }
            }
        );
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
        if ( project == null ) {
            // it is possible there is no active project even if ProjectViewer is installed.
            add( new JLabel( jEdit.getProperty( "tasklist.projectfiles.noproject", "No project is open." ) ) );
            return ;
        }

        if ( jEdit.getBooleanProperty( "tasklist.show-project-files" ) ) {
            if ( runner != null ) {
                runner.cancel( true );
            }
            runner = new Runner( project );
            runner.execute();
        }
    }

    class Runner extends SwingWorker<TreeModel, Object> {

        private VPTProject project;
        private JProgressBar progressBar = new JProgressBar(0, 100);

        public Runner( VPTProject project ) {
            this.project = project;
        }

        @Override
        public TreeModel doInBackground() {
            try {
                SwingUtilities.invokeLater(
                    new Runnable() {
                        public void run() {
                            ProjectTaskList.this.removeAll();

                            JPanel progressPanel = new JPanel();
                            progressBar.setStringPainted(true);
                            progressPanel.add( progressBar, BorderLayout.CENTER);
                            JPanel btnPanel = new JPanel();
                            btnPanel.add( stopButton );

                            ProjectTaskList.this.add( new JLabel( jEdit.getProperty( "tasklist.projectfiles.wait", "Please wait, loading tasks for project" ) + " " + project.getName() ), BorderLayout.NORTH );
                            ProjectTaskList.this.add( progressPanel, BorderLayout.CENTER );
                            ProjectTaskList.this.add(btnPanel, BorderLayout.SOUTH);
                            ProjectTaskList.this.invalidate();
                            ProjectTaskList.this.validate();
                        }
                    }
                );
                addPropertyChangeListener(
                    new PropertyChangeListener() {
                        public void propertyChange(PropertyChangeEvent evt) {
                            if ("progress".equals(evt.getPropertyName())) {
                                progressBar.setValue((Integer)evt.getNewValue());
                            }
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
        public boolean cancel( boolean mayInterruptIfRunning ) {
            boolean cancelled = super.cancel( mayInterruptIfRunning );
            if ( cancelled ) {
                SwingUtilities.invokeLater(
                    new Runnable() {
                        public void run() {
                            ProjectTaskList.this.removeAll();
                            JPanel btnPanel = new JPanel();
                            btnPanel.add(startButton);
                            ProjectTaskList.this.add( btnPanel, BorderLayout.SOUTH );
                            ProjectTaskList.this.invalidate();
                            ProjectTaskList.this.validate();
                        }
                    }
                );
            }
            return cancelled;
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
                return ;
            }
            SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        // build the display
                        ProjectTaskList.this.removeAll();
                        if ( model.getChildCount( model.getRoot() ) > 0 ) {
                            tree = new JTree( model );
                            for ( int i = tree.getRowCount(); i > 0; i-- ) {
                                tree.expandRow( i );
                            }
                            tree.addMouseListener( new TreeMouseListener( view, tree ) );
                            tree.setCellRenderer( new TaskTreeCellRenderer() );
                            ProjectTaskList.this.add( new JScrollPane( tree ) );
                        }
                        else {
                            ProjectTaskList.this.add( new JLabel( jEdit.getProperty( "tasklist.no-tasks-found", "No tasks found." ) ) );
                        }
                        ProjectTaskList.this.invalidate();
                        ProjectTaskList.this.validate();
                    }
                }
            );
            runner = null;
        }

        protected TreeModel buildTreeModel( VPTProject project ) {

            DefaultMutableTreeNode root = new DefaultMutableTreeNode( jEdit.getProperty( "tasklist.projectfiles.project", "Project:" ) + " " + project.getName() );
            SortableTreeModel model = new SortableTreeModel( root, new TreeNodeStringComparator() );

            List<String> toScan = getBuffersToScan( project );
            for ( int i = 0; i < toScan.size(); i++ ) {
                if (isCancelled()) {
                    return model;
                }
                String path = toScan.get(i);

                setProgress(100 * i / toScan.size());

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
                    Mode mode = TaskListPlugin.getMode( file );
                    if ( mode == null ) {
                        continue;
                    }
                    buffer.setMode( mode );

                    // files open this way can be closed when TaskList parsing is complete.
                    can_close = true;
                }
                try {
                    while ( buffer.isLoading() ) {
                        Thread.currentThread().sleep( 5 );
                    }
                }
                catch ( Exception e ) {
                    e.printStackTrace();
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
                if ( tree == null ) {
                    return ;     // can happen if tree model is still loading when message is received
                }
                Buffer buffer = bu.getBuffer();
                removeBuffer( buffer );
                addBuffer( buffer );
                repaint();
            }
            else if ( ParseBufferMessage.DO_PARSE_ALL.equals( bu.getWhat() ) ) {
                loadProjectFiles( ProjectViewer.getActiveProject( view ) );
            }
        }
        if ( msg instanceof PropertiesChanged ) {
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
                for ( int i = tree.getRowCount(); i > 0; i-- ) {
                    tree.expandRow( i );
                }
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
            if ( buffer_name.equals( buffer.getPath() ) ) {
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