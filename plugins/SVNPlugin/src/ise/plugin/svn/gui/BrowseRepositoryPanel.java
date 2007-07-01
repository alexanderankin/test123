package ise.plugin.svn.gui;


import java.awt.BorderLayout;
import java.awt.event.*;
import java.io.ByteArrayOutputStream;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;
import ise.plugin.svn.action.NodeActor;
import ise.plugin.svn.data.CheckoutData;
import ise.plugin.svn.library.PropertyComboBox;
import ise.plugin.svn.library.PasswordHandler;
import ise.plugin.svn.action.SVNAction;
import ise.plugin.svn.action.BrowseRepositoryAction;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;
import projectviewer.ProjectViewer;

import org.tmatesoft.svn.core.io.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.wc.*;

public class BrowseRepositoryPanel extends JPanel {

    private View view = null;
    private PropertyComboBox chooser = null;
    private JTree tree = null;

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
                            for ( int i = 1; i < parts.length; i++ ) {
                                sb.append( "/" ).append( parts[ i ].toString() );
                            }
                            String url = chooser.getSelectedItem() + sb.toString();
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
                            NodeActor.setupLibrary();
                            TreePath path = tree.getClosestPathForLocation( me.getX(), me.getY() );
                            DefaultMutableTreeNode node = ( DefaultMutableTreeNode ) path.getLastPathComponent();
                            if ( node.isLeaf() ) {
                                Object[] parts = path.getPath();
                                StringBuilder sb = new StringBuilder();
                                for ( int i = 1; i < parts.length; i++ ) {
                                    sb.append( "/" ).append( parts[ i ].toString() );
                                }
                                CheckoutData data = createData();
                                String filepath = sb.toString().substring(1);
                                String url = data.getURL();
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

                                    if ( isTextType ) {
                                        Buffer buffer = jEdit.newFile( getView() );
                                        buffer.beginCompoundEdit();
                                        buffer.remove( 0, buffer.getLength() );
                                        buffer.insert( 0, baos.toString() );
                                        buffer.endCompoundEdit();
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
        String username = jEdit.getProperty( SVNAction.PREFIX + getProjectName( getView() ) + ".username" );
        String password = jEdit.getProperty( SVNAction.PREFIX + getProjectName( getView() ) + ".password" );
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

}
