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

public class MoveDialog extends JDialog {
    // instance fields
    private View view = null;
    private List<File> toCopy = null;
    private List<String> urlsToCopy = null;
    private JTextField path = null;
    private TableModel fileTableModel = null;
    private JTextArea comment = null;
    private PropertyComboBox commentList = null;

    private String defaultLocalDestination = null;
    private String defaultRemoteDestination = null;
    private SVNRevision revision = SVNRevision.HEAD;

    private boolean local = true;  // if true, copying to local file system

    private boolean canceled = true;

    /**
     * working copy to working copy move
     * @param view parent frame
     * @param files the local files to copy
     * @param defaultLocalDestination local file system destination
     */
    public MoveDialog( View view, List<File> files, String defaultLocalDestination ) {
        super( ( JFrame ) view, "Move", true );
        if ( files == null || files.size() == 0 ) {
            throw new IllegalArgumentException( "no source file(s) to move" );
        }
        this.view = view;
        this.toCopy = files;
        this.defaultLocalDestination = defaultLocalDestination == null ? "" : defaultLocalDestination;
        local = true;
        init();
    }

    /**
     * remote to remote move
     * @param view parent frame
     * @param defaultRemoteDestination remote repository destination
     * @param files the remote urls to copy
     */
    public MoveDialog( View view, String defaultRemoteDestination, List<String> urls ) {
        super( ( JFrame ) view, "Move", true );
        if ( urls == null || urls.size() == 0 ) {
            throw new IllegalArgumentException( "no source url(s) to move" );
        }
        this.view = view;
        this.urlsToCopy = urls;
        this.defaultRemoteDestination = defaultRemoteDestination == null ? "" : defaultRemoteDestination;
        local = false;
        init();
    }

    protected void init() {
        KappaLayout layout = new KappaLayout();
        JPanel panel = new JPanel( layout );
        panel.setBorder( new EmptyBorder( 6, 6, 6, 6 ) );

        JLabel to_copy_label = null;
        if ( local ) {
            to_copy_label = new JLabel( "Move " + ( toCopy.size() == 1 ? "this file:" : "these files:" ) );
        }
        else {
            to_copy_label = new JLabel( "Move " + ( urlsToCopy.size() == 1 ? "this URL:" : "these URLs:" ) );
        }

        BestRowTable file_table = new BestRowTable();

        // create table model
        fileTableModel = new DefaultTableModel( new String[] {
                    ( local ? "File" : "URL" )
                }
                , local ? toCopy.size() : urlsToCopy.size() ) ;

        // fill table model.  If directory, add a checkbox defaulting to checked
        // indicating the copy should recursively copy the directory.
        if ( local ) {
            for ( int row = 0; row < toCopy.size(); row++ ) {
                File file = toCopy.get( row );
                fileTableModel.setValueAt( file.getAbsolutePath(), row, 0 );
            }
        }
        else {
            for ( int row = 0; row < urlsToCopy.size(); row++ ) {
                String url = urlsToCopy.get( row );
                fileTableModel.setValueAt( url, row, 0 );
            }
        }
        file_table.setModel( fileTableModel );
        file_table.getColumnModel().getColumn( 0 ).setPreferredWidth( 500 );
        file_table.packRows();

        // revision selection panel
        final RevisionSelectionPanel revision_panel = new RevisionSelectionPanel( "Move from this revision:" );
        revision_panel.setLayout(SwingConstants.HORIZONTAL);
        revision_panel.setShowWorking(true);

        // destination
        JLabel path_label = new JLabel( "To this location:" );
        path = new JTextField( defaultLocalDestination , 30 );
        JButton browse_local_btn = new JButton( "Browse Local..." );
        browse_local_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        String[] dirs = GUIUtilities.showVFSFileDialog( view, defaultLocalDestination, toCopy == null ? VFSBrowser.OPEN_DIALOG : toCopy.size() == 1 ? VFSBrowser.OPEN_DIALOG : VFSBrowser.CHOOSE_DIRECTORY_DIALOG, false );
                        if ( dirs != null && dirs.length > 0 ) {
                            String filename = dirs[ 0 ];
                            File f = new File( filename );
                            if ( f.exists() && f.isFile() ) {
                                int overwrite = JOptionPane.showConfirmDialog( view, "File exists, okay to overwrite?", "File exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE );
                                if ( overwrite == JOptionPane.NO_OPTION ) {
                                    return ;
                                }
                            }
                            path.setText( f.getAbsolutePath() );
                            local = true;
                        }
                    }
                }
                                          );
        JButton browse_remote_btn = new JButton( "Browse Remote..." );
        browse_remote_btn.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    final JDialog dialog = new JDialog( view, "Select Repository Destination" );
                    dialog.setModal( true );
                    JPanel panel = new JPanel( new LambdaLayout() );
                    panel.setBorder( BorderFactory.createEmptyBorder( 6, 6, 6, 6 ) );
                    final BrowseRepositoryPanel burp = new BrowseRepositoryPanel( view, defaultRemoteDestination, false );
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
                                    local = false;
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

        JLabel comment_label = new JLabel( "Enter comment for this move:" );
        comment = new JTextArea( 3, 30 );
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
                        if ( path == null || path.getText().length() == 0 ) {
                            JOptionPane.showMessageDialog( MoveDialog.this, "Directory is required.", "Error", JOptionPane.ERROR_MESSAGE );
                            return ;
                        }
                        revision = revision_panel.getRevision();
                        canceled = false;
                        MoveDialog.this.setVisible( false );
                        MoveDialog.this.dispose();
                    }
                }
                                );

        cancel_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        canceled = true;
                        MoveDialog.this.setVisible( false );
                        MoveDialog.this.dispose();
                    }
                }
                                    );


        // add the components to the option panel
        panel.add( "0, 0, 8, 1, W,  , 3", to_copy_label );
        JScrollPane file_scroller = new JScrollPane( file_table );
        file_scroller.getViewport().setPreferredSize( new Dimension( 500, Math.min( file_table.getBestHeight(), 50 ) ) );
        panel.add( "0, 1, 8, 1, W, w, 3", file_scroller );


        panel.add( "0, 2, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 10, true ) );

        panel.add( "0, 3, 8, 1, 0, w, 3", revision_panel );

        panel.add( "0, 4, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 10, true ) );

        panel.add( "0, 5, 1, 1, W,  , 3", path_label );
        panel.add( "0, 6, 8, 1, 0, w, 3", path );
        if ( local ) {
            panel.add( "0, 7, 1, 1, 0, w, 3", browse_local_btn );
        }
        else {
            panel.add( "0, 7, 1, 1, 0, w, 3", browse_remote_btn );

            panel.add( "0, 8, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 11, true ) );

            panel.add( "0, 9, 8, 1, W,  , 3", comment_label );
            panel.add( "0, 10, 8, 1, W, wh, 3", new JScrollPane( comment ) );

            if ( commentList != null && commentList.getModel().getSize() > 0 ) {
                commentList.setPreferredSize( new Dimension( 500, commentList.getPreferredSize().height ) );
                panel.add( "0, 11, 8, 1, W,  , 3", new JLabel( "Select a previous comment:" ) );
                panel.add( "0, 12, 8, 1, W, w, 3", commentList );
            }
        }

        panel.add( "0, 13, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 11, true ) );
        panel.add( "0, 14, 8, 1, E,  , 0", btn_panel );

        setContentPane( panel );
        pack();

    }

    public CopyData getData() {
        if ( canceled ) {
            return null;
        }
        CopyData cd = new CopyData();
        if ( toCopy != null ) {
            List<File> files = new ArrayList<File>();
            for ( int row = 0; row < fileTableModel.getRowCount(); row++ ) {
                String filename = ( String ) fileTableModel.getValueAt( row, 0 );
                files.add( new File( filename ) );
            }

            cd.setSourceFiles( files );
        }
        else {
            List<SVNURL> urls = new ArrayList<SVNURL>();
            for ( int row = 0; row < fileTableModel.getRowCount(); row++ ) {
                String url = ( String ) fileTableModel.getValueAt( row, 0 );
                try {
                    SVNURL source = SVNURL.parseURIDecoded( url );
                    urls.add( source );
                }
                catch ( Exception e ) {
                    e.printStackTrace();
                }
            }

            cd.setSourceURLs( urls );
        }

        cd.setRevision( revision );

        if ( local ) {
            cd.setDestinationFile( new File( path.getText() ) );
        }
        else {
            try {
                cd.setDestinationURL( SVNURL.parseURIDecoded( path.getText() ) );
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }
        }

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
