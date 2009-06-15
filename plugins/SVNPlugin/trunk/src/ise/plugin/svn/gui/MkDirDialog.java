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

// imports
import java.awt.Dimension;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.border.EmptyBorder;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.HistoryTextField;
import org.gjt.sp.jedit.jEdit;

import ise.java.awt.KappaLayout;
import ise.java.awt.LambdaLayout;
import ise.plugin.svn.data.CommitData;
import ise.plugin.svn.library.GUIUtils;
import ise.plugin.svn.library.PropertyComboBox;
import static ise.plugin.svn.gui.HistoryModelNames.*;


/**
 * Dialog for obtaining a comment for a commit.
 */
public class MkDirDialog extends JDialog {
    // instance fields
    private View view = null;
    private String defaultDestination;

    private HistoryTextField path = null;
    private JTextArea comment = null;
    private PropertyComboBox commentList = null;

    private CommitData commitData = null;


    public MkDirDialog( View view, String defaultDestination ) {
        super( ( JFrame ) view, jEdit.getProperty( "ips.mkdir", "mkdir" ), true );
        this.view = view;
        this.defaultDestination = defaultDestination;
        init();
    }

    protected void init() {

        commitData = new CommitData();

        JPanel panel = new JPanel( new LambdaLayout() );
        panel.setBorder( new EmptyBorder( 6, 6, 6, 6 ) );

        // let user pick the directory to create
        JLabel path_label = new JLabel( jEdit.getProperty( "ips.Create_new_directory_at_this_location>", "Create new directory at this location:" ) );
        path = new HistoryTextField( MKDIR_PATH );
        path.setText( defaultDestination == null ? "" : defaultDestination );
        path.setColumns( 30 );
        JButton browse_remote_btn = new JButton( jEdit.getProperty( "ips.Browse_Remote...", "Browse Remote..." ) );
        browse_remote_btn.setMnemonic( KeyEvent.VK_R );
        browse_remote_btn.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    final JDialog dialog = new JDialog( view, jEdit.getProperty( "ips.Select_Repository_Parent_Directory", "Select Repository Parent Directory" ) );
                    dialog.setModal( true );
                    JPanel panel = new JPanel( new LambdaLayout() );
                    panel.setBorder( BorderFactory.createEmptyBorder( 6, 6, 6, 6 ) );
                    final BrowseRepositoryPanel burp = new BrowseRepositoryPanel( view, defaultDestination, false );
                    panel.add( "0, 0, 1, 1, 0, wh, 3", burp );
                    KappaLayout btn_layout = new KappaLayout();
                    JPanel button_panel = new JPanel( btn_layout );
                    JButton ok_btn = new JButton( jEdit.getProperty( "ips.Ok", "Ok" ) );
                    ok_btn.setMnemonic( KeyEvent.VK_O );
                    ok_btn.addActionListener(
                        new ActionListener() {
                            public void actionPerformed( ActionEvent ae ) {
                                String selection = burp.getSelectionPath();
                                dialog.setVisible( false );
                                dialog.dispose();
                                if ( selection != null && selection.length() > 0 ) {
                                    path.setText( selection );
                                }
                            }
                        }
                    );
                    JButton cancel_btn = new JButton( jEdit.getProperty( "ips.Cancel", "Cancel" ) );
                    cancel_btn.setMnemonic( KeyEvent.VK_C );
                    cancel_btn.addActionListener(
                        new ActionListener() {
                            public void actionPerformed( ActionEvent ae ) {
                                dialog.setVisible( false );
                                dialog.dispose();
                            }
                        }
                    );
                    button_panel.add( "0, 0, 1, 1, 0, w, 3", ok_btn );
                    button_panel.add( "1, 0, 1, 1, 0, w, 3", cancel_btn );
                    btn_layout.makeColumnsSameWidth( 0, 1 );

                    panel.add( "0, 1, 1, 1", KappaLayout.createStrut( 350, 11, false ) );
                    panel.add( "0, 2, 1, 1, E, , 3", button_panel );
                    dialog.setContentPane( panel );
                    dialog.pack();
                    GUIUtils.center( view, dialog );
                    dialog.getRootPane().setDefaultButton( ok_btn );
                    ok_btn.requestFocus();
                    dialog.setVisible( true );
                }
            }
        );

        JLabel label = new JLabel( jEdit.getProperty( "ips.Enter_commit_comment>", "Enter commit comment:" ) );
        comment = new JTextArea( 5, 50 );
        comment.setLineWrap( true );
        comment.setWrapStyleWord( true );

        // list for previous comments
        commentList = new PropertyComboBox( "ise.plugin.svn.comment." );
        commentList.setEditable( false );
        commentList.addItemListener( new ItemListener() {
                    public void itemStateChanged( ItemEvent e ) {
                        if ( PropertyComboBox.SELECT.equals( commentList.getSelectedItem().toString() ) ) {
                            return ;
                        }
                        comment.setText( commentList.getSelectedItem().toString() );
                    }
                }
                                   );

        // buttons
        KappaLayout kl = new KappaLayout();
        JPanel btn_panel = new JPanel( kl );
        JButton ok_btn = new JButton( jEdit.getProperty( "ips.Ok", "Ok" ) );
        ok_btn.setMnemonic( KeyEvent.VK_O );
        JButton cancel_btn = new JButton( jEdit.getProperty( "ips.Cancel", "Cancel" ) );
        cancel_btn.setMnemonic( KeyEvent.VK_C );
        btn_panel.add( "0, 0, 1, 1, 0, w, 3", ok_btn );
        btn_panel.add( "1, 0, 1, 1, 0, w, 3", cancel_btn );
        kl.makeColumnsSameWidth( 0, 1 );

        ok_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        MkDirDialog.this.setVisible( false );
                        MkDirDialog.this.dispose();
                        path.addCurrentToHistory();
                        MkDirDialog.this._save();
                    }
                }
                                );

        cancel_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        commitData = null;
                        MkDirDialog.this.setVisible( false );
                        MkDirDialog.this.dispose();
                    }
                }
                                    );

        panel.add( "0, 0, 6, 1, W,  , 3", path_label );
        panel.add( "0, 1, 6, 1, W, w, 3", path );
        panel.add( "0, 2, 6, 1, W,  , 3", browse_remote_btn );
        panel.add( "0, 3, 1, 1, 0,  , 3", KappaLayout.createVerticalStrut( 11, true ) );

        panel.add( "0, 4, 6, 1, W,  , 3", label );
        panel.add( "0, 5, 6, 1, W, wh, 3", new JScrollPane( comment ) );

        if ( commentList != null && commentList.getModel().getSize() > 0 ) {
            commentList.setPreferredSize( new Dimension( 600, commentList.getPreferredSize().height ) );
            panel.add( "0, 6, 6, 1, W,  , 3", new JLabel( jEdit.getProperty( "ips.Select_a_previous_comment>", "Select a previous comment:" ) ) );
            panel.add( "0, 7, 6, 1, W, w, 3", commentList );
        }
        panel.add( "0, 8, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 11, true ) );

        panel.add( "0, 9, 6, 1, E,  , 0", btn_panel );

        setContentPane( panel );
        pack();

        getRootPane().setDefaultButton( ok_btn );
        ok_btn.requestFocus();
    }

    protected void _save() {
        if ( commentList != null ) {
            String msg = comment.getText();
            if ( msg == null || msg.length() == 0 ) {
                msg = jEdit.getProperty( "ips.no_comment", "no comment" );
            }
            commentList.addValue( msg );
            commentList.save();
        }
    }

    public CommitData getData() {
        List<String> paths = new ArrayList<String>();
        String url = path.getText();
        if ( url != null && url.length() > 0 && commitData != null ) {
            paths.add( url );
            commitData.setPaths( paths );
            String msg = comment.getText();
            if ( msg == null || msg.length() == 0 ) {
                msg = jEdit.getProperty( "ips.no_comment", "no comment" );
            }
            commitData.setCommitMessage( msg );
        }
        return commitData;
    }

    public static void main ( String[] args ) {
        MkDirDialog d = new MkDirDialog( null, null );
        d.setVisible( true );
    }
}