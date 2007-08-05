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
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.EmptyBorder;
import ise.java.awt.LambdaLayout;
import ise.plugin.svn.data.StatusData;
import ise.plugin.svn.action.*;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.GUIUtilities;

/**
 * Shows the results of a status check.
 */
public class StatusResultsPanel extends JPanel {

    private View view = null;
    private String username = null;
    private String password = null;

    private JTree tree = null;
    private JPopupMenu popupMenu = null;

    public StatusResultsPanel( StatusData results, View view, String username, String password ) {
        super( new LambdaLayout() );

        this.view = view;
        this.username = username;
        this.password = password;

        setBorder( new EmptyBorder( 3, 3, 3, 3 ) );

        LambdaLayout.Constraints con = LambdaLayout.createConstraint();
        con.a = LambdaLayout.W;
        con.y = 0;
        con.s = "w";
        con.p = 3;

        JLabel label = new JLabel( "Status checked against revision: " + results.getRevision() );
        add( label, con );
        con.s = "wh";

        boolean added = false;

        DefaultMutableTreeNode root = new DefaultMutableTreeNode();

        List<SVNStatus> list = results.getConflicted();
        if ( list != null ) {
            root.add( createNode( "Files with conflicts (must merged):", list ) );
            added = true;
        }

        list = results.getOutOfDate();
        if ( list != null ) {
            root.add( createNode( "Out of date files (need updated?):", list ) );
            added = true;
        }

        list = results.getModified();
        if ( list != null ) {
            root.add( createNode( "Modified files (need committed?):", list ) );
            added = true;
        }

        list = results.getAdded();
        if ( list != null ) {
            root.add( createNode( "Added files (need committed?):", list ) );
            added = true;
        }

        list = results.getUnversioned();
        if ( list != null ) {
            root.add( createNode( "Unversioned files (need added?):", list ) );
            added = true;
        }

        list = results.getDeleted();
        if ( list != null ) {
            root.add( createNode( "Deleted files (need committed?):", list ) );
            added = true;
        }

        list = results.getMissing();
        if ( list != null ) {
            root.add( createNode( "Missing files (need deleted?):", list ) );
            added = true;
        }

        if ( added ) {
            ++con.y;
            tree = new JTree( root );
            tree.setRootVisible( false );
            for ( int i = 0; i < tree.getRowCount(); i++ ) {
                tree.expandRow( i );
            }
            tree.addMouseListener( new TreeMouseListener() );
            add( tree, con );
            popupMenu = createPopupMenu();
        }
        else {
            label.setText( label.getText() + " (All files up to date.)" );
        }
    }

    private DefaultMutableTreeNode createNode( String title, List<SVNStatus> values ) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode( title );
        for ( SVNStatus status : values ) {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode( status.getFile().toString() );
            node.add( child );
        }
        return node;
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

        private void handleClick( MouseEvent e ) {
            if ( e.isPopupTrigger() && tree.getSelectionCount() > 0 ) {
                GUIUtilities.showPopupMenu( popupMenu, StatusResultsPanel.this, e.getX(), e.getY() );
            }
        }
    }

    /**
     * Create the context menu.
     */
    private JPopupMenu createPopupMenu() {
        // update, commit, revert, add, log, need to add others as appropriate
        final JPopupMenu pm = new JPopupMenu();

        JMenuItem mi = new JMenuItem( "Update" );
        pm.add( mi );
        mi.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        TreePath[] tree_paths = tree.getSelectionPaths();
                        if ( tree_paths.length == 0 ) {
                            return ;
                        }
                        List<String> paths = new ArrayList<String>();
                        for ( TreePath path : tree_paths ) {
                            if ( path != null && path.getPathCount() > 2 ) {
                                paths.add( ( String ) ( ( DefaultMutableTreeNode ) path.getLastPathComponent() ).getUserObject() );
                            }
                        }
                        UpdateAction action = new UpdateAction( view, paths, username, password );
                        action.actionPerformed( ae );
                    }
                }
                            );

        mi = new JMenuItem( "Commit" );
        pm.add( mi );
        mi.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        TreePath[] tree_paths = tree.getSelectionPaths();
                        if ( tree_paths.length == 0 ) {
                            return ;
                        }
                        List<String> paths = new ArrayList<String>();
                        for ( TreePath path : tree_paths ) {
                            if ( path != null && path.getPathCount() > 2 ) {
                                paths.add( ( String ) ( ( DefaultMutableTreeNode ) path.getLastPathComponent() ).getUserObject() );
                            }
                        }
                        CommitAction action = new CommitAction( view, paths, username, password );
                        action.actionPerformed( ae );
                    }
                }
                            );

        mi = new JMenuItem( "Revert" );
        pm.add( mi );
        mi.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        TreePath[] tree_paths = tree.getSelectionPaths();
                        if ( tree_paths.length == 0 ) {
                            return ;
                        }
                        List<String> paths = new ArrayList<String>();
                        for ( TreePath path : tree_paths ) {
                            if ( path != null && path.getPathCount() > 2 ) {
                                paths.add( ( String ) ( ( DefaultMutableTreeNode ) path.getLastPathComponent() ).getUserObject() );
                            }
                        }
                        RevertAction action = new RevertAction( view, paths, username, password );
                        action.actionPerformed( ae );
                    }
                }
                            );

        mi = new JMenuItem( "Diff" );
        pm.add( mi );
        mi.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        TreePath[] tree_paths = tree.getSelectionPaths();
                        if ( tree_paths.length != 1 ) {
                            JOptionPane.showMessageDialog( view, "Please select a single entry.", "Too many selections", JOptionPane.ERROR_MESSAGE );
                            return ;
                        }
                        String path = ( String ) ( ( DefaultMutableTreeNode ) tree_paths[0].getLastPathComponent() ).getUserObject();
                        DiffAction action = new DiffAction(view, path, username, password);
                        action.actionPerformed(ae);
                    }
                }
                            );

        mi = new JMenuItem( "Add" );
        pm.add( mi );
        mi.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        TreePath[] tree_paths = tree.getSelectionPaths();
                        if ( tree_paths.length == 0 ) {
                            return ;
                        }
                        List<String> paths = new ArrayList<String>();
                        for ( TreePath path : tree_paths ) {
                            if ( path != null && path.getPathCount() > 2 ) {
                                paths.add( ( String ) ( ( DefaultMutableTreeNode ) path.getLastPathComponent() ).getUserObject() );
                            }
                        }
                        AddAction action = new AddAction( view, paths, username, password );
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
                            if ( path != null && path.getPathCount() > 2 ) {
                                paths.add( ( String ) ( ( DefaultMutableTreeNode ) path.getLastPathComponent() ).getUserObject() );
                            }
                        }
                        LogAction action = new LogAction( view, paths, username, password );
                        action.actionPerformed( ae );
                    }
                }
                            );
        return pm;
    }
}
