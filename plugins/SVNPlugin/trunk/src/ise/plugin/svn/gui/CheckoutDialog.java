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
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.gui.HistoryTextField;

import ise.java.awt.KappaLayout;
import ise.java.awt.LambdaLayout;
import ise.plugin.svn.PVHelper;
import ise.plugin.svn.pv.SVNAction;
import ise.plugin.svn.data.*;
import ise.plugin.svn.command.*;
import ise.plugin.svn.library.GUIUtils;
import ise.plugin.svn.library.PasswordHandler;
import static ise.plugin.svn.gui.HistoryModelNames.*;

import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.internal.wc.admin.SVNAdminAreaFactory;
import org.tmatesoft.svn.core.internal.wc17.db.ISVNWCDb;

/**
 * Dialog for obtaining the url and local directory for a checkout from a
 * subversion repository, and optionally a username, and password.
 */
public class CheckoutDialog extends JDialog {
    // instance fields
    private View view = null;
    private String _url = null;

    private HistoryTextField url = null;
    private HistoryTextField path = null;
    private JComboBox<String> fileformat = null;
    private HistoryTextField username = null;
    private JPasswordField password = null;

    private boolean canceled = false;

    public CheckoutDialog( View view, String url ) {
        super( ( JFrame ) view, jEdit.getProperty( "ips.Checkout", "Checkout" ), true );
        this.view = view;
        this._url = url;
        _init();
    }

    /** Initialises the option pane. */
    protected void _init() {
        JPanel panel = new JPanel( new KappaLayout() );
        panel.setBorder( new EmptyBorder( 6, 6, 6, 6 ) );
        String project_name = PVHelper.getProjectName( view );

        // subversion repository url field
        JLabel url_label = new JLabel( jEdit.getProperty( SVNAction.PREFIX + "url.label" ) );
        url = new HistoryTextField( URL );
        url.setText( _url != null ? _url : jEdit.getProperty( SVNAction.PREFIX + project_name + ".url" ) );
        url.setColumns( 30 );

        // populate url field from existing svn info, if available
        List<String> info_path = new ArrayList<String>();
        info_path.add( PVHelper.getProjectRoot( view ) );
        SVNData info_data = new SVNData();
        info_data.setPaths( info_path );
        String url_text = null;
        List<SVNInfo> info_results = null;
        try {
            info_results = new Info().getInfo( info_data );
        } catch ( Exception e ) {
            info_results = null;
        }
        if ( info_results != null && info_results.size() > 0 ) {
            SVNInfo svn_info = info_results.get( 0 );
            if ( svn_info != null && svn_info.getURL() != null ) {
                url_text = svn_info.getURL().toString();
            }
        }
        if ( url_text != null ) {
            url.setText( url_text );
        }

        // browse for url
        JButton browse_remote_btn = new JButton( jEdit.getProperty( "ips.Browse...", "Browse..." ) );
        browse_remote_btn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent ae ) {
                final JDialog dialog = new JDialog( view, jEdit.getProperty( "ips.Select_Repository", "Select Repository" ) );
                dialog.setModal( true );
                JPanel panel = new JPanel( new LambdaLayout() );
                panel.setBorder( BorderFactory.createEmptyBorder( 6, 6, 6, 6 ) );
                final BrowseRepositoryPanel burp = new BrowseRepositoryPanel( view, false );
                panel.add( "0, 0, 1, 1, 0, wh, 3", burp );
                KappaLayout btn_layout = new KappaLayout();
                JPanel button_panel = new JPanel( btn_layout );
                JButton ok_btn = new JButton( jEdit.getProperty( "ips.Ok", "Ok" ) );
                ok_btn.setMnemonic( KeyEvent.VK_O );
                ok_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        String selection = burp.getSelectionPath();
                        dialog.setVisible( false );
                        dialog.dispose();
                        if ( selection != null && selection.length() > 0 ) {
                            url.setText( selection );
                        }
                    }
                }
                );
                JButton cancel_btn = new JButton( jEdit.getProperty( "ips.Cancel", "Cancel" ) );
                cancel_btn.setMnemonic( KeyEvent.VK_C );
                cancel_btn.addActionListener( new ActionListener() {
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
                panel.add( "0, 2, 1, 1, E,, 3", button_panel );
                dialog.setContentPane( panel );
                dialog.pack();
                GUIUtils.center( view, dialog );
                dialog.getRootPane().setDefaultButton( ok_btn );
                ok_btn.requestFocus();
                dialog.setVisible( true );
            }
        }
        );

        // local destination directory
        JLabel path_label = new JLabel( jEdit.getProperty( SVNAction.PREFIX + "path.label" ) );
        path = new HistoryTextField( PATH );
        // TODO: can I do some munging here to get the project name out of the url?
        path.setText( PVHelper.getProjectRoot( view ) );
        path.setColumns( 30 );
        JButton browse_btn = new JButton( jEdit.getProperty( "ips.Browse...", "Browse..." ) );
        browse_btn.setMnemonic( KeyEvent.VK_B );
        browse_btn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent ae ) {
                String[] dirs = GUIUtilities.showVFSFileDialog( view, PVHelper.getProjectRoot( view ), VFSBrowser.CHOOSE_DIRECTORY_DIALOG, false );
                if ( dirs != null && dirs.length > 0 ) {
                    path.setText( dirs[0] );
                }
            }
        }
        );

        // working copy format
        JLabel fileformat_label = new JLabel( jEdit.getProperty( "ips.Subversion_file_format>", "Subversion file format:" ) );
        fileformat = new JComboBox<String>( new String[] {"1.4", "1,5", "1.6", "1.7", "1.8"} );
        fileformat.setEditable( false );
        int wc_format = jEdit.getIntegerProperty( "ise.plugin.svn.defaultWCVersion" );
        switch ( wc_format ) {
            case SVNAdminAreaFactory.WC_FORMAT_14:
                fileformat.setSelectedIndex( 0 );
                break;
            case SVNAdminAreaFactory.WC_FORMAT_15:
                fileformat.setSelectedIndex( 1 );
                break;
            case SVNAdminAreaFactory.WC_FORMAT_16:
                fileformat.setSelectedIndex( 2 );
            case ISVNWCDb.WC_FORMAT_18:
                fileformat.setSelectedIndex( 4 );
                break;
            case ISVNWCDb.WC_FORMAT_17:
            default:
                fileformat.setSelectedIndex( 3 );
                break;
        }

        // username field
        JLabel username_label = new JLabel( jEdit.getProperty( SVNAction.PREFIX + "username.label" ) );
        username = new HistoryTextField( USERNAME );
        username.setText( jEdit.getProperty( SVNAction.PREFIX + project_name + ".username" ) );
        username.setColumns( 30 );

        // password field
        JLabel password_label = new JLabel( jEdit.getProperty( SVNAction.PREFIX + "password.label" ) );
        String pwd = jEdit.getProperty( SVNAction.PREFIX + project_name + ".password" );
        pwd = PasswordHandler.decryptPassword( pwd );
        password = new JPasswordField( pwd, 30 );

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
                if ( url == null || url.getText().length() == 0 ) {
                    JOptionPane.showMessageDialog( CheckoutDialog.this, jEdit.getProperty( "ips.URL_is_required.", "URL is required." ), jEdit.getProperty( "ips.Error", "Error" ), JOptionPane.ERROR_MESSAGE );
                    return;
                }
                if ( path == null || path.getText().length() == 0 ) {
                    JOptionPane.showMessageDialog( CheckoutDialog.this, jEdit.getProperty( "ips.Directory_is_required.", "Directory is required." ), jEdit.getProperty( "ips.Error", "Error" ), JOptionPane.ERROR_MESSAGE );
                    return;
                }
                canceled = false;
                CheckoutDialog.this.setVisible( false );
                CheckoutDialog.this.dispose();

                url.addCurrentToHistory();
                path.addCurrentToHistory();
                username.addCurrentToHistory();
            }
        }
        );

        cancel_btn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent ae ) {
                canceled = true;
                CheckoutDialog.this.setVisible( false );
                CheckoutDialog.this.dispose();
            }
        }
        );

        // add the components to the option panel
        panel.add( "0, 0, 1, 1, E,, 3", url_label );
        panel.add( "1, 0, 2, 1, 0, w, 3", url );
        panel.add( "3, 0, 1, 1, 0, w, 3", browse_remote_btn );

        panel.add( "0, 1, 1, 1, E,, 3", path_label );
        panel.add( "1, 1, 2, 1, 0, w, 3", path );
        panel.add( "3, 1, 1, 1, 0, w, 3", browse_btn );

        panel.add( "0, 2, 1, 1, E,, 3", fileformat_label );
        panel.add( "1, 2, 2, 1, 0, w, 3", fileformat );

        panel.add( "0, 3, 1, 1, E,, 3", username_label );
        panel.add( "1, 3, 2, 1, 0, w, 3", username );

        panel.add( "0, 4, 1, 1, E,, 3", password_label );
        panel.add( "1, 4, 2, 1, 0, w, 3", password );

        panel.add( "0, 5, 1, 1, 0,, 0", KappaLayout.createVerticalStrut( 10, true ) );
        panel.add( "0, 6, 4, 1, E,, 0", btn_panel );

        setContentPane( panel );
        pack();

        getRootPane().setDefaultButton( ok_btn );
        ok_btn.requestFocus();
    }

    public CheckoutData getValues() {
        if ( canceled ) {
            return null;
        }
        CheckoutData cd = new CheckoutData();
        cd.setURL( url.getText() );
        cd.setUsername( username.getText() );
        cd.setPassword( PasswordHandler.encryptPassword( new String( password.getPassword() ) ) );
        List<String> paths = new ArrayList<String>();
        paths.add( path.getText() );
        cd.setPaths( paths );
        int format;
        switch ( fileformat.getSelectedIndex() ) {
            case 0:
                format = SVNAdminAreaFactory.WC_FORMAT_14;
                break;
            case 1:
                format = SVNAdminAreaFactory.WC_FORMAT_15;
                break;
            case 2:
                format = SVNAdminAreaFactory.WC_FORMAT_16;
                break;
            case 4:
                format = ISVNWCDb.WC_FORMAT_18;
                break;
            case 3:
            default:
                format = ISVNWCDb.WC_FORMAT_17;
        }


        cd.setWorkingCopyFormat( format );
        return cd;
    }

}