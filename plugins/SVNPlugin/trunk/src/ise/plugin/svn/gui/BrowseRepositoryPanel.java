package ise.plugin.svn.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
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
import ise.plugin.svn.command.BrowseRepository;
import ise.plugin.svn.data.CheckoutData;
import ise.plugin.svn.data.LogData;
import ise.plugin.svn.data.SVNData;
import ise.plugin.svn.library.FileUtilities;
import ise.plugin.svn.library.GUIUtils;
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

    private String PREFIX = "ise.plugins.svn.repository.";

    public BrowseRepositoryPanel( View view ) {
        super( new BorderLayout() );
        this.view = view;

        // repository chooser
        chooser = new PropertyComboBox( PREFIX );
        ActionListener al = new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        CheckoutData data = createData();
                        DirTreeNode root = new DirTreeNode( data.getURL(), false );
                        tree.setModel( new DefaultTreeModel( root ) );
                        BrowseRepositoryAction action = new BrowseRepositoryAction( getView(), tree, root, data );
                        action.actionPerformed( ae );
                    }
                };
        chooser.addActionListener( al );

        // the repository tree.  This is lazy loaded.
        tree = new JTree( new DefaultTreeModel( new DirTreeNode( "SVN Browser", false ) ) );
        tree.setCellRenderer( new CellRenderer() );
        ToolTipManager.sharedInstance().registerComponent( tree );

        // on expansion, call the repository and fetch the children
        tree.addTreeExpansionListener( new TreeExpansionListener() {
                    public void treeCollapsed( TreeExpansionEvent event ) {}

                    public void treeExpanded( TreeExpansionEvent event ) {
                        TreePath path = event.getPath();
                        DirTreeNode node = ( DirTreeNode ) path.getLastPathComponent();
                        if ( node.getChildCount() == 0 ) {
                            CheckoutData data = createData();
                            String url;
                            if ( node.isExternal() ) {
                                url = node.getRepositoryLocation();
                            }
                            else {
                                Object[] parts = path.getPath();
                                StringBuilder sb = new StringBuilder();
                                sb.append( parts[ 0 ] );
                                for ( int i = 1; i < parts.length; i++ ) {
                                    sb.append( "/" ).append( parts[ i ].toString() );
                                }
                                url = sb.toString();
                            }
                            data.setURL( url );
                            BrowseRepositoryAction action = new BrowseRepositoryAction( getView(), tree, node, data );
                            action.actionPerformed( null );
                        }
                    }
                }
                                     );

        // on double click on a text file, fetch the file contents and show the file in jEdit
        tree.addMouseListener( new MouseAdapter() {
                    public void mouseClicked( MouseEvent me ) {
                        if ( me.getClickCount() == 2 ) {
                            // for double-click on a text file, open the file in jEdit
                            TreePath path = tree.getClosestPathForLocation( me.getX(), me.getY() );
                            DirTreeNode node = ( DirTreeNode ) path.getLastPathComponent();
                            if ( node.isLeaf() ) {
                                // show the wait cursor
                                tree.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
                                tree.setEditable( false );

                                // leaf nodes should be files, not directories.
                                // get url and path for the selected file
                                CheckoutData data = createData();
                                String url;
                                String filepath;
                                Object[] parts = path.getPath();
                                StringBuilder sb = new StringBuilder();
                                for ( int i = 1; i < parts.length; i++ ) {
                                    sb.append( "/" ).append( parts[ i ].toString() );
                                }
                                filepath = sb.toString().substring( 1 );
                                if ( node.isExternal() ) {
                                    String rep = node.getRepositoryLocation();
                                    url = rep.substring( 0, rep.lastIndexOf( "/" ) );
                                    filepath = rep.substring( rep.lastIndexOf( "/" ) + 1 );
                                }
                                else {
                                    url = data.getURL();
                                }

                                // fetch the file contents, -1 shorthand for HEAD revision
                                BrowseRepository br = new BrowseRepository();
                                File outfile = br.getFile( url, filepath, -1, data.getUsername(), data.getPassword() );
                                if (outfile != null) {
                                    jEdit.openFile( getView(), outfile.getAbsolutePath() );
                                }
                                tree.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
                                tree.setEditable( true );
                            }
                        }

                    }
                }
                             );
        tree.addMouseListener( new TreeMouseListener() );

        // create the ocntext menu
        popupMenu = createPopupMenu();

        // create the control buttons
        Icon new_icon = GUIUtilities.loadIcon("New.png");
        JButton new_btn = new JButton( new_icon );
        new_btn.setSize(new_icon.getIconWidth(), new_icon.getIconHeight());
        new_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        AddRepositoryDialog dialog = new AddRepositoryDialog( getView() );
                        GUIUtils.center( getView(), dialog );
                        dialog.setVisible( true );
                        CheckoutData data = dialog.getValues();
                        chooser.addValue( data.getURL() );
                        DirTreeNode root = new DirTreeNode( data.getURL(), false );
                        tree.setModel( new DefaultTreeModel( root ) );
                        BrowseRepositoryAction action = new BrowseRepositoryAction( getView(), tree, root, data );
                        action.actionPerformed( ae );
                    }
                }
                                 );
        Icon refresh_icon = GUIUtilities.loadIcon("Reload.png");
        JButton refresh_btn = new JButton( refresh_icon );
        refresh_btn.setSize(refresh_icon.getIconWidth(), refresh_icon.getIconHeight());
        refresh_btn.addActionListener( al );

        // create a panel to hold the buttons
        JPanel button_panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 1));
        button_panel.add(new_btn);
        button_panel.add(refresh_btn);

        // create a panel to hold the buttons and the repository chooser
        JPanel top_panel = new JPanel( new BorderLayout() );
        top_panel.add( button_panel, BorderLayout.NORTH );
        top_panel.add( chooser, BorderLayout.CENTER );

        // fill in the main panel
        add( top_panel, BorderLayout.NORTH );
        add( new JScrollPane( tree ), BorderLayout.CENTER );
    }

    private void saveData( CheckoutData data ) {
        String url = data.getURL();
        if ( url == null || url.length() == 0 ) {
            return ;
        }
        int index = chooser.getSelectedIndex();
        jEdit.setProperty( PREFIX + index, url );
        if ( data.getUsername() != null && data.getPassword() != null ) {
            jEdit.setProperty( PREFIX + "username." + index, data.getUsername() );
            String pwd = null;
            try {
                PasswordHandler ph = new PasswordHandler();
                pwd = ph.encrypt( data.getPassword() );
            }
            catch ( Exception e ) {
                // ignored
            }
            if ( pwd != null ) {
                jEdit.setProperty( PREFIX + "password." + index, pwd );
            }
        }
    }

    private CheckoutData createData() {
        CheckoutData data = new CheckoutData();
        String value = ( String ) chooser.getSelectedItem();
        int index = chooser.getSelectedIndex();
        data.setURL( value );

        username = jEdit.getProperty( PREFIX + "username." + index );
        password = jEdit.getProperty( PREFIX + "password." + index );
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

    public void setRoot( DirTreeNode root ) {
        if ( root != null ) {
            tree.setModel( new DefaultTreeModel( root, false ) );
        }
    }

    public View getView() {
        return view;
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

        JMenuItem mi = new JMenuItem( "Edit" );
        pm.add( mi );
        mi.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        CheckoutData data = createData();
                        AddRepositoryDialog dialog = new AddRepositoryDialog( getView(), data );
                        dialog.setVisible( true );
                        data = dialog.getValues();
                        saveData( data );
                        DirTreeNode root = new DirTreeNode( data.getURL(), false );
                        tree.setModel( new DefaultTreeModel( root ) );
                        BrowseRepositoryAction action = new BrowseRepositoryAction( getView(), tree, root, data );
                        action.actionPerformed( ae );
                    }
                }
                            );

        mi = new JMenuItem( "Remove" );
        pm.add( mi );
        mi.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        CheckoutData data = createData();
                        int delete = JOptionPane.showConfirmDialog( view, "Remove repository location " + data.getURL() + " ?\nThis only removes this repository from the browser, it does not delete any files.", "Confirm Remove", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE );
                        if ( delete == JOptionPane.YES_OPTION ) {
                            chooser.setSelectedItem( data.getURL() );
                            int index = chooser.getSelectedIndex();
                            chooser.removeItem( data.getURL() );
                            jEdit.unsetProperty( PREFIX + "username." + index );
                            jEdit.unsetProperty( PREFIX + "password." + index );
                        }
                    }
                }
                            );

        mi = new JMenuItem( "Checkout" );
        pm.add( mi );
        mi.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        TreePath[] tree_paths = tree.getSelectionPaths();
                        if ( tree_paths.length == 0 ) {
                            return ;
                        }
                        if ( tree_paths.length > 1 ) {
                            JOptionPane.showMessageDialog( view, "Please select a single entry.", "Too many selections", JOptionPane.ERROR_MESSAGE );
                            return ;
                        }
                        String url = null;
                        for ( TreePath path : tree_paths ) {
                            if ( path != null ) {
                                Object[] parts = path.getPath();
                                StringBuilder sb = new StringBuilder();
                                sb.append( parts[ 0 ] );
                                for ( int i = 1; i < parts.length; i++ ) {
                                    sb.append( "/" ).append( parts[ i ].toString() );
                                }
                                url = sb.toString();
                                break;
                            }
                        }
                        CheckoutData data = new CheckoutData();
                        data.setURL( url );
                        data.setUsername( username );
                        data.setPassword( password );
                        CheckoutAction action = new CheckoutAction( view, data );
                        action.actionPerformed( ae );
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
                        LogData data = new LogData();
                        data.setPaths( paths );
                        data.setPathsAreURLs( true );
                        data.setUsername( username );
                        data.setPassword( password );
                        LogAction action = new LogAction( view, data );
                        action.actionPerformed( ae );
                    }
                }
                            );

        mi = new JMenuItem( "Properties" );
        pm.add( mi );
        mi.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        JOptionPane.showMessageDialog( view, "Sorry, this feature is not yet implemented.", "Darn", JOptionPane.ERROR_MESSAGE );
                        return ;
                    }
                }
                            );

        return pm;
    }

    class CellRenderer extends DefaultTreeCellRenderer {
        public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean sel,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus ) {

            Component r = super.getTreeCellRendererComponent(
                        tree, value, sel,
                        expanded, leaf, row,
                        hasFocus );

            if ( r instanceof JLabel ) {
                JLabel label = ( JLabel ) r;
                DirTreeNode node = ( DirTreeNode ) value;
                if ( node.isExternal() ) {
                    label.setText( "<html><font color=blue>" + node.toString() );
                    label.setToolTipText( "<html><b>External: </b> " + node.getRepositoryLocation() );
                }
            }
            return r;
        }
    }
}
