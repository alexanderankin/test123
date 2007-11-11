/*
Copyright (c) 2007, Dale Anson
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.
* Neither the name of the author nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package ise.plugin.svn.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
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
import ise.plugin.svn.gui.RevisionDialog;
import ise.plugin.svn.library.FileUtilities;
import ise.plugin.svn.library.GUIUtils;
import ise.plugin.svn.library.PropertyComboBox;
import ise.plugin.svn.library.PasswordHandler;
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

                                // ask the user for the revision they want
                                RevisionDialog rd = new RevisionDialog( BrowseRepositoryPanel.this.view, "Select Revision to View" );
                                GUIUtils.center( BrowseRepositoryPanel.this.getView(), rd );
                                rd.setVisible(true);
                                SVNRevision revision = rd.getData();
                                if ( revision == null ) {
                                    return ;
                                }

                                // fetch the file contents
                                BrowseRepository br = new BrowseRepository();
                                File outfile = br.getFile( url, filepath, revision, data.getUsername(), data.getPassword() );
                                if ( outfile != null ) {
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

        // create the context menu
        popupMenu = createPopupMenu();

        // create the control buttons
        Icon new_icon = GUIUtilities.loadIcon( "New.png" );
        JButton new_btn = new JButton( new_icon );
        Dimension dim = new Dimension( new_icon.getIconWidth() + ( new_btn.getInsets().top * 2 ), new_icon.getIconHeight() + ( new_btn.getInsets().top * 2 ) );
        new_btn.setSize( dim );
        new_btn.setPreferredSize( dim );
        new_btn.setMaximumSize( dim );
        new_btn.setToolTipText( "Add new repository" );
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
        Icon refresh_icon = GUIUtilities.loadIcon( "Reload.png" );
        JButton refresh_btn = new JButton( refresh_icon );
        dim = new Dimension( refresh_icon.getIconWidth() + ( refresh_btn.getInsets().top * 2 ), refresh_icon.getIconHeight() + ( refresh_btn.getInsets().top * 2 ) );
        refresh_btn.setSize( dim );
        refresh_btn.setPreferredSize( dim );
        refresh_btn.setMaximumSize( dim );
        refresh_btn.setToolTipText( "Refresh" );
        refresh_btn.addActionListener( al );

        // create a panel to hold the buttons
        JPanel button_panel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 1 ) );
        button_panel.add( new_btn );
        button_panel.add( refresh_btn );

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
            String pwd = data.getPassword();    // password should already be encrypted
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
                        GUIUtils.center( getView(), dialog );
                        dialog.setVisible( true );
                        data = dialog.getValues();  // null indicates user cancelled
                        if ( data != null ) {
                            saveData( data );
                            DirTreeNode root = new DirTreeNode( data.getURL(), false );
                            tree.setModel( new DefaultTreeModel( root ) );
                            BrowseRepositoryAction action = new BrowseRepositoryAction( getView(), tree, root, data );
                            action.actionPerformed( ae );
                        }
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
                        TreePath[] tree_paths = tree.getSelectionPaths();
                        if ( tree_paths.length == 0 ) {
                            return ;
                        }
                        if ( tree_paths.length > 1 ) {
                            JOptionPane.showMessageDialog( view, "Please select a single entry.", "Too many selections", JOptionPane.ERROR_MESSAGE );
                            return ;
                        }

                        TreePath path = tree_paths[ 0 ];
                        DirTreeNode node = ( DirTreeNode ) path.getLastPathComponent();
                        Properties props = node.getProperties();
                        if ( props == null ) {
                            JOptionPane.showMessageDialog( view, "This item has no SVN properties.", "No Properties", JOptionPane.INFORMATION_MESSAGE );
                            return ;
                        }
                        PropertiesAction action = new PropertiesAction( view, node.toString(), props );
                        action.actionPerformed( ae );

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
                String text = node.toString();
                if ( node.isExternal() ) {
                    text = "<html><font color=blue>" + node.toString();
                    label.setToolTipText( "<html><b>External: </b> " + node.getRepositoryLocation() );
                }
                if ( node.hasProperties() ) {
                    text += " *";
                }
                label.setText( text );
            }
            return r;
        }
    }
}
