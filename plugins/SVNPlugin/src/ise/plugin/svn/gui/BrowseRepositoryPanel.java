package ise.plugin.svn.gui;


import java.awt.BorderLayout;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;
import ise.plugin.svn.action.*;
import ise.plugin.svn.data.CheckoutData;
import ise.plugin.svn.data.SVNData;
import ise.plugin.svn.library.FileUtilities;
import ise.plugin.svn.library.PropertyComboBox;
import ise.plugin.svn.library.PasswordHandler;
import ise.plugin.svn.action.SVNAction;
import ise.plugin.svn.action.BrowseRepositoryAction;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;
import projectviewer.ProjectViewer;
import org.gjt.sp.jedit.GUIUtilities;

import org.tmatesoft.svn.core.io.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.wc.*;

public class BrowseRepositoryPanel extends JPanel {

    private View view = null;
    private PropertyComboBox chooser = null;
    private JTree tree = null;
    private JPopupMenu popupMenu = null;
    private String username = null;
    private String password = null;

    public BrowseRepositoryPanel( View view ) {
        super( new BorderLayout() );
        this.view = view;
        String project_name = getProjectName( view );

        chooser = new PropertyComboBox( "ise.plugins.svn.repository." );
        chooser.setEditable( true );

        if ( chooser.getItemCount() == 1 ) {
            String url = jEdit.getProperty( SVNAction.PREFIX + project_name + ".url" );
            if ( url != null ) {
                chooser.addItem( url );
            }
        }
        if ( chooser.getItemCount() > 1 ) {
            chooser.setSelectedItem( 1 );
        }

        tree = new JTree( new DefaultTreeModel( new DefaultMutableTreeNode( "SVN Browser" ) ) );

        tree.addTreeExpansionListener( new TreeExpansionListener() {
                    public void treeCollapsed( TreeExpansionEvent event ) {}

                    public void treeExpanded( TreeExpansionEvent event ) {
                        TreePath path = event.getPath();
                        DefaultMutableTreeNode node = ( DefaultMutableTreeNode ) path.getLastPathComponent();
                        if ( node.getChildCount() == 0 ) {
                            CheckoutData data = createData();
                            Object[] parts = path.getPath();
                            StringBuilder sb = new StringBuilder();
                            sb.append( parts[ 0 ] );
                            for ( int i = 1; i < parts.length; i++ ) {
                                sb.append( "/" ).append( parts[ i ].toString() );
                            }
                            String url = sb.toString();
                            data.setURL( url );
                            BrowseRepositoryAction action = new BrowseRepositoryAction( getView(), tree, node, data );
                            action.actionPerformed( null );
                        }
                    }
                }
                                     );

        tree.addMouseListener( new MouseAdapter() {
                    public void mouseClicked( MouseEvent me ) {
                        if ( me.getClickCount() == 2 ) {
                            // for double-click on a text file, open the file in jEdit
                            TreePath path = tree.getClosestPathForLocation( me.getX(), me.getY() );
                            DefaultMutableTreeNode node = ( DefaultMutableTreeNode ) path.getLastPathComponent();
                            if ( node.isLeaf() ) {
                                // leaf nodes should be files, not directories.
                                // get url and path for the selected file
                                Object[] parts = path.getPath();
                                StringBuilder sb = new StringBuilder();
                                for ( int i = 1; i < parts.length; i++ ) {
                                    sb.append( "/" ).append( parts[ i ].toString() );
                                }
                                CheckoutData data = createData();
                                String filepath = sb.toString().substring( 1 );
                                String url = data.getURL();

                                // fetch the file contents
                                NodeActor.setupLibrary();
                                SVNRepository repository = null;
                                try {
                                    repository = SVNRepositoryFactory.create( SVNURL.parseURIEncoded( url ) );
                                    ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager( data.getUsername() , data.getPassword() );
                                    repository.setAuthenticationManager( authManager );

                                    SVNNodeKind nodeKind = repository.checkPath( filepath , -1 );
                                    if ( nodeKind == SVNNodeKind.NONE || nodeKind == SVNNodeKind.DIR ) {
                                        return ;
                                    }
                                    Map fileproperties = new HashMap( );
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream( );
                                    repository.getFile( filepath , -1 , fileproperties , baos );

                                    String mimeType = ( String ) fileproperties.get( SVNProperty.MIME_TYPE );
                                    boolean isTextType = SVNProperty.isTextMimeType( mimeType );

                                    // ignore non-text files for now
                                    if ( isTextType ) {
                                        // copy the file contents to a temp file.  Preserve
                                        // the file name so that jEdit can apply highlighting
                                        StringReader reader = new StringReader( baos.toString() );
                                        File outfile = new File( System.getProperty( "java.io.tmpdir" ), parts[ parts.length - 1 ].toString() );
                                        outfile.deleteOnExit();     // automatic cleanup
                                        BufferedWriter writer = new BufferedWriter( new FileWriter( outfile ) );
                                        FileUtilities.copy( reader, writer );
                                        writer.close();
                                        jEdit.openFile( getView(), outfile.getAbsolutePath() );
                                    }
                                }
                                catch ( Exception e ) {
                                    // ignored
                                }
                            }
                        }

                    }
                }
                             );

        tree.addMouseListener( new TreeMouseListener() );
        popupMenu = createPopupMenu();

        JButton go = new JButton( "Go" );
        go.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        CheckoutData data = createData();
                        DefaultMutableTreeNode root = new DefaultMutableTreeNode( data.getURL() );
                        tree.setModel( new DefaultTreeModel( root ) );
                        BrowseRepositoryAction action = new BrowseRepositoryAction( getView(), tree, root, data );
                        action.actionPerformed( ae );
                        chooser.addValue( chooser.getSelectedItem().toString() );
                        chooser.save();
                    }
                }
                            );

        JPanel top_panel = new JPanel( new BorderLayout() );
        top_panel.add( chooser, BorderLayout.CENTER );
        top_panel.add( go, BorderLayout.EAST );
        add( top_panel, BorderLayout.NORTH );
        add( new JScrollPane( tree ), BorderLayout.CENTER );

    }

    private CheckoutData createData() {
        CheckoutData data = new CheckoutData();
        String value = ( String ) chooser.getSelectedItem();
        if ( value != null ) {
            data.setURL( value );
        }
        username = jEdit.getProperty( SVNAction.PREFIX + getProjectName( getView() ) + ".username" );
        password = jEdit.getProperty( SVNAction.PREFIX + getProjectName( getView() ) + ".password" );
        if ( password != null && password.length() > 0 ) {
            try {
                PasswordHandler ph = new PasswordHandler();
                password = ph.decrypt( password );
            }
            catch ( Exception e ) {
                password = "";
            }
        }
        data.setUsername( username );
        data.setPassword( password );
        return data;
    }

    public void setRoot( DefaultMutableTreeNode root ) {
        if ( root != null ) {
            tree.setModel( new DefaultTreeModel( root ) );
        }
    }

    public void removeNotify() {
        super.removeNotify();
        if ( chooser != null ) {
            chooser.save();
        }
    }

    public View getView() {
        return view;
    }

    private String getProjectName( View view ) {
        VPTProject project = ProjectViewer.getActiveProject( view );
        return project == null ? "" : project.getName();
    }

    /**
     * MouseListener to popup context menu on the tree.
     */
    class TreeMouseListener extends MouseAdapter {
        public void mouseReleased( MouseEvent me ) {
            handleClick( me );
        }

        public void mousePressed( MouseEvent me ) {
            handleClick( me );
        }

        private void handleClick( MouseEvent me ) {
            if ( me.isPopupTrigger() ) {
                if ( tree.getSelectionCount() == 0 ) {
                    TreePath path = tree.getClosestPathForLocation( me.getX(), me.getY() );
                    tree.addSelectionPath( path );
                }
                GUIUtilities.showPopupMenu( popupMenu, BrowseRepositoryPanel.this, me.getX(), me.getY() );
            }
        }
    }

    /**
     * Create the context menu.
     */
    private JPopupMenu createPopupMenu() {
        // update, commit, revert, add, log, need to add others as appropriate
        final JPopupMenu pm = new JPopupMenu();

        JMenuItem mi = new JMenuItem( "Checkout" );
        pm.add( mi );
        mi.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                    }
                }
                            );

        mi = new JMenuItem( "Info" );
        pm.add( mi );
        mi.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        TreePath[] tree_paths = tree.getSelectionPaths();
                        if ( tree_paths.length == 0 ) {
                            return ;
                        }
                        List<String> paths = new ArrayList<String>();
                        for ( TreePath path : tree_paths ) {
                            if ( path != null ) {
                                Object[] parts = path.getPath();
                                StringBuilder sb = new StringBuilder();
                                sb.append( parts[ 0 ] );
                                for ( int i = 1; i < parts.length; i++ ) {
                                    sb.append( "/" ).append( parts[ i ].toString() );
                                }
                                String url = sb.toString();
                                paths.add( url );
                            }
                        }
                        SVNData data = new SVNData();
                        data.setPaths( paths );
                        data.setPathsAreURLs( true );
                        data.setUsername( username );
                        data.setPassword( password );
                        InfoAction action = new InfoAction( view, data );
                        action.actionPerformed( ae );
                    }
                }
                            );

        mi = new JMenuItem( "Log" );
        pm.add( mi );
        mi.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        TreePath[] tree_paths = tree.getSelectionPaths();
                        if ( tree_paths.length == 0 ) {
                            return ;
                        }
                        List<String> paths = new ArrayList<String>();
                        for ( TreePath path : tree_paths ) {
                            if ( path != null ) {
                                Object[] parts = path.getPath();
                                StringBuilder sb = new StringBuilder();
                                sb.append( parts[ 0 ] );
                                for ( int i = 1; i < parts.length; i++ ) {
                                    sb.append( "/" ).append( parts[ i ].toString() );
                                }
                                String url = sb.toString();
                                paths.add( url );
                            }
                        }
                        SVNData data = new SVNData();
                        data.setPaths( paths );
                        data.setPathsAreURLs( true );
                        data.setUsername( username );
                        data.setPassword( password );
                        LogAction action = new LogAction( view, data );
                        action.actionPerformed( ae );
                    }
                }
                            );

        JMenuItem mi = new JMenuItem( "Properties" );
        pm.add( mi );
        mi.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                    }
                }
                            );

        return pm;
    }
}
