/*
Copyright (c) 2008, Dale Anson
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
import java.io.File;
import java.net.URL;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.DocumentListener;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.*;
import org.gjt.sp.jedit.gui.HistoryTextField;
import org.gjt.sp.jedit.browser.VFSBrowser;

import ise.java.awt.*;
import ise.plugin.svn.data.*;
import ise.plugin.svn.command.*;
import ise.plugin.svn.library.*;
import static ise.plugin.svn.gui.HistoryModelNames.*;
import ise.plugin.svn.gui.component.*;

/**
 * A panel to let the user pick a local directory or a directory in a remote
 * Subversion repository.  There is no check that the user has actually
 * selected a directory in the repository.
 */
public class BrowseLocalRemotePanel extends JPanel {

    private View view = null;
    private String startPath = null;
    private String endPath = null;
    private HistoryTextField path = null;

    public BrowseLocalRemotePanel( View view, String labelText, String start_path, String end_path ) {
        this( view, labelText, start_path, end_path, true );
    }

    public BrowseLocalRemotePanel( View v, String labelText, String start_path, String end_path, boolean show_remote ) {
        view = v;
        startPath = start_path;
        endPath = end_path;

        LambdaLayout layout = new LambdaLayout();
        setLayout( layout );

        JLabel path_label = new JLabel( labelText );

        path = new HistoryTextField( MERGE_PATH );

        if ( startPath == null ) {
            startPath = System.getProperty( "user.home" );
        }
        path.setText( startPath );
        path.setColumns( 30 );
        JButton browse_local_btn = new JButton( getProperty( "ips.Browse_Local...", "Local..." ) );
        browse_local_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        String[] dirs = GUIUtilities.showVFSFileDialog( view, startPath, VFSBrowser.CHOOSE_DIRECTORY_DIALOG, false );
                        if ( dirs != null && dirs.length > 0 ) {
                            String filename = dirs[ 0 ];
                            File f = new File( filename );
                            path.setText( f.getAbsolutePath() );
                            path.addCurrentToHistory();
                        }
                    }
                }
                                          );

        JButton browse_remote_btn = browse_remote_btn = new JButton( getProperty( "ips.Browse_Remote...", "Remote..." ) );
        browse_remote_btn.setVisible( show_remote );
        browse_remote_btn.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    final JDialog dialog = new JDialog( view, getProperty( "ips.Select_Repository_Location", "Select Repository Location" ) );
                    dialog.setModal( true );
                    JPanel panel = new JPanel( new LambdaLayout() );
                    panel.setBorder( BorderFactory.createEmptyBorder( 6, 6, 6, 6 ) );
                    final BrowseRepositoryPanel burp = new BrowseRepositoryPanel( view, endPath, false );
                    panel.add( "0, 0, 1, 1, 0, wh, 3", burp );
                    KappaLayout btn_layout = new KappaLayout();
                    JPanel button_panel = new JPanel( btn_layout );
                    JButton ok_btn = new JButton( getProperty( "ips.Ok", "Ok" ) );
                    ok_btn.addActionListener(
                        new ActionListener() {
                            public void actionPerformed( ActionEvent ae ) {
                                String selection = burp.getSelectionPath();
                                dialog.setVisible( false );
                                dialog.dispose();
                                if ( selection != null && selection.length() > 0 ) {
                                    path.setText( selection );
                                    path.addCurrentToHistory();
                                }
                            }
                        }
                    );
                    JButton cancel_btn = new JButton( getProperty( "ips.Cancel", "Cancel" ) );
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

        KappaLayout button_panel_layout = new KappaLayout();
        JPanel buttonPanel = new JPanel( button_panel_layout );
        buttonPanel.add( "0, 0, 1, 1, W, w, 3", browse_local_btn );
        buttonPanel.add( "1, 0, 1, 1, E, w, 3", browse_remote_btn );
        button_panel_layout.makeColumnsSameWidth( 0, 1 );

        if ( labelText != null ) {
            add( "0, 0, 1, 1, W,  , 3", path_label );
        }
        add( "0, 1, 1, 1, W, w, 3", path );
        add( "1, 1, 1, 1, E, w, 3", buttonPanel );
    }

    // for testing, jEdit.getProperty doesn't work when this is ran outside of jEdit
    private String getProperty( String name, String default_value ) {
        try {
            return jEdit.getProperty( name, default_value );
        }
        catch ( Exception e ) {
            return default_value;
        }
    }

    public String getPath() {
        String p = path.getText();
        return p == null || p.length() == 0 ? null : p;
    }

    public void setPath( String p ) {
        if ( p != null ) {
            path.setText( p );
        }
    }

    public boolean isDestinationLocal() {
        try {
            new URL(getPath());
            return false;
        }
        catch(Exception e) {
            return true;
        }
    }

    public void addDocumentListener( DocumentListener listener ) {
        if ( listener != null ) {
            path.getDocument().addDocumentListener( listener );
        }
    }
}