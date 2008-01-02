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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.io.File;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.EmptyBorder;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.*;
import org.gjt.sp.jedit.browser.VFSBrowser;

import ise.java.awt.*;
import ise.plugin.svn.pv.SVNAction;
import ise.plugin.svn.data.*;
import ise.plugin.svn.command.*;
import ise.plugin.svn.library.*;

import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNRevision;

/**
 * Pretty much a clone of CopyDialog but with a few minor GUI changes.  This
 * interface assumes the user wants to copy a remote url to a remote url, so
 * only allows a single source and destination.
 */
public class TagBranchDialog extends JDialog {
    // instance fields
    private View view = null;
    private String toCopy = null;
    private JTextField path = null;
    private TableModel fileTableModel = null;
    private String defaultDestination = null;
    private JTextArea comment = null;
    private PropertyComboBox commentList = null;

    private SVNURL source = null;
    private SVNURL destination = null;
    private SVNRevision revision = SVNRevision.HEAD;

    public static final int TAG_DIALOG = 1;
    public static final int BRANCH_DIALOG = 2;
    private int type = TAG_DIALOG;
    public static String TAG = "Tag";
    public static String BRANCH = "Branch";

    private boolean cancelled = false;

    /**
     * @param view the parent frame
     * @param type one of TAG_DIALOG or BRANCH_DIALOG
     * @param url remote repository url to copy from
     * @param defaultDestination a destination to use by default, can be null or empty
     */
    public TagBranchDialog( View view, int type, String url, String defaultDestination ) {
        super( ( JFrame ) view, type == TAG_DIALOG ? TAG : BRANCH, true );
        if ( url == null || url.length() == 0 ) {
            throw new IllegalArgumentException( "no source file(s) to copy" );
        }
        if ( type != TAG_DIALOG && type != BRANCH_DIALOG ) {
            throw new IllegalArgumentException( "invalid type value: " + type );
        }
        this.view = view;
        this.type = type;
        this.toCopy = url;
        this.defaultDestination = defaultDestination == null ? "" : defaultDestination;
        init();
    }

    protected void init() {
        KappaLayout layout = new KappaLayout();
        JPanel panel = new JPanel( layout );
        panel.setBorder( BorderFactory.createEmptyBorder( 6, 6, 6, 6 ) );

        // source for tag/branch
        JLabel to_copy_label = new JLabel( "Create " + (type == TAG_DIALOG ? "tag" : "branch") + " from:" );
        JTextField source_file = new JTextField(toCopy);
        source_file.setEditable(false);
        source_file.setBackground(Color.WHITE);

        // revision selection panel
        final RevisionSelectionPanel tag_revision_panel = new RevisionSelectionPanel( "Create " + (type == TAG_DIALOG ? "tag" : "branch") + " from this revision:", SwingConstants.HORIZONTAL, false );

        JPanel source_panel = new JPanel(new LambdaLayout());
        //source_panel.setBorder(BorderFactory.createEtchedBorder());
        source_panel.add("0, 0, 1, 1, W, w, 3", to_copy_label);
        source_panel.add("0, 1, 1, 1, W, w, 3", source_file);
        source_panel.add("0, 2, 1, 1, 0, w, 3", tag_revision_panel);

        // destination
        JLabel path_label = new JLabel( "Create " + (type == TAG_DIALOG ? "tag" : "branch") + " at this location:" );
        path = new JTextField( defaultDestination , 30 );
        JButton browse_remote_btn = new JButton( "Browse Remote..." );
        browse_remote_btn.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    final JDialog dialog = new JDialog( view, "Select Repository Destination" );
                    dialog.setModal( true );
                    JPanel panel = new JPanel( new LambdaLayout() );
                    panel.setBorder( BorderFactory.createEmptyBorder( 6, 6, 6, 6 ) );
                    final BrowseRepositoryPanel burp = new BrowseRepositoryPanel( view );
                    panel.add( "0, 0, 1, 1, 0, wh, 3", burp );
                    KappaLayout btn_layout = new KappaLayout();
                    JPanel button_panel = new JPanel( btn_layout );
                    JButton ok_btn = new JButton( "OK" );
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
                    JButton cancel_btn = new JButton( "Cancel" );
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
                    dialog.setVisible( true );
                }
            }
        );



        JLabel comment_label = new JLabel( "Enter comment for this " + (type == TAG_DIALOG ? "tag:" : "branch:") );
        comment = new JTextArea( 3, 50 );
        comment.setLineWrap( true );
        comment.setWrapStyleWord( true );

        // list for previous comments
        final PropertyComboBox commentList = new PropertyComboBox( "ise.plugin.svn.comment." );
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

        // ok and cancel buttons
        KappaLayout kl = new KappaLayout();
        JPanel btn_panel = new JPanel( kl );
        JButton ok_btn = new JButton( "Ok" );
        JButton cancel_btn = new JButton( "Cancel" );
        btn_panel.add( "0, 0, 1, 1, 0, w, 3", ok_btn );
        btn_panel.add( "1, 0, 1, 1, 0, w, 3", cancel_btn );
        kl.makeColumnsSameWidth( 0, 1 );

        ok_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        try {
                            source = SVNURL.parseURIDecoded( toCopy );
                        }
                        catch ( Exception e ) {
                            JOptionPane.showMessageDialog( TagBranchDialog.this, "Source URL is invalid.", "Error", JOptionPane.ERROR_MESSAGE );
                            return ;
                        }
                        try {
                            destination = SVNURL.parseURIDecoded( path.getText() );
                        }
                        catch ( Exception e ) {
                            JOptionPane.showMessageDialog( TagBranchDialog.this, "Destination URL is invalid.", "Error", JOptionPane.ERROR_MESSAGE );
                            return ;
                        }
                        revision = tag_revision_panel.getRevision();
                        cancelled = false;
                        TagBranchDialog.this._save();
                        TagBranchDialog.this.setVisible( false );
                        TagBranchDialog.this.dispose();
                    }
                }
                                );

        cancel_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        cancelled = true;
                        TagBranchDialog.this.setVisible( false );
                        TagBranchDialog.this.dispose();
                    }
                }
                                    );


        // add the components to the option panel
        panel.add( "0, 0, 8, 1, W, w, 0", source_panel );

        panel.add( "0, 4, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 11, true ) );

        panel.add( "0, 5, 1, 1, W,  , 3", path_label );
        panel.add( "0, 6, 8, 1, 0, w, 3", path );
        panel.add( "0, 7, 1, 1, 0, w, 3", browse_remote_btn );

        panel.add( "0, 8, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 11, true ) );

        panel.add( "0, 9, 8, 1, W,  , 3", comment_label );
        panel.add( "0, 10, 8, 1, W, wh, 3", new JScrollPane( comment ) );

        if ( commentList != null && commentList.getModel().getSize() > 0 ) {
            commentList.setPreferredSize( new Dimension( 600, commentList.getPreferredSize().height ) );
            panel.add( "0, 11, 8, 1, W,  , 3", new JLabel( "Select a previous comment:" ) );
            panel.add( "0, 12, 8, 1, W, w, 3", commentList );
        }
        panel.add( "0, 13, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 11, true ) );

        panel.add( "0, 14, 8, 1, E,  , 0", btn_panel );

        setContentPane( panel );
        pack();

    }

    protected void _save() {
        if ( commentList != null ) {
            commentList.save();
        }
    }

    public CopyData getData() {
        if ( cancelled ) {
            return null;
        }
        CopyData cd = new CopyData();
        List<SVNURL> urls = new ArrayList<SVNURL>();
        urls.add( source );
        cd.setSourceURLs( urls );
        cd.setRevision( revision );
        cd.setDestinationURL( destination );
        String msg = comment.getText();
        if ( msg == null || msg.length() == 0 ) {
            msg = "no comment";
        }
        else {
            if ( commentList != null ) {
                commentList.addValue( msg );
            }
        }
        cd.setMessage( msg );
        return cd;
    }
}
