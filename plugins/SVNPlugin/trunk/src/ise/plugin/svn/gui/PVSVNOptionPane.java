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
import java.io.PrintStream;
import java.util.*;
import javax.swing.*;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.util.Log;

import projectviewer.config.ProjectOptions;
import ise.java.awt.KappaLayout;
import ise.plugin.svn.library.PasswordHandler;
import ise.plugin.svn.library.PasswordHandlerException;
import ise.plugin.svn.data.*;
import ise.plugin.svn.command.*;
import ise.plugin.svn.io.*;

import org.tmatesoft.svn.core.wc.SVNInfo;

/**
 * Option pane for setting the url, username, and password for subversion via
 * ProjectViewer.
 */
public class PVSVNOptionPane extends AbstractOptionPane {
    // instance fields
    public static String PREFIX = "ise.plugin.svn.pv.";

    private JLabel url_label;
    private JTextField url;
    private JLabel username_label;
    private JTextField username;
    private JLabel password_label;
    private JPasswordField password;

    public PVSVNOptionPane() {
        super( "ise.plugin.svn" );
        setLayout( new KappaLayout() );
    }

    /** Initialises the option pane. */
    protected void _init() {
        String project_name = getProjectName();

        // url field
        url_label = new JLabel( jEdit.getProperty( PREFIX + "url.label" ) );
        url = new JTextField( jEdit.getProperty( PREFIX + project_name + ".url" ), 30 );

        // username field
        username_label = new JLabel( jEdit.getProperty( PREFIX + "username.label" ) );
        username = new JTextField( jEdit.getProperty( PREFIX + project_name + ".username" ), 30 );

        // password field
        password_label = new JLabel( jEdit.getProperty( PREFIX + "password.label" ) );
        String pwd = jEdit.getProperty( PREFIX + project_name + ".password" );
        if ( pwd != null && pwd.length() > 0 ) {
            try {
                PasswordHandler ph = new PasswordHandler();
                pwd = ph.decrypt( pwd );
            }
            catch ( Exception e ) {
                pwd = "";
            }
        }
        password = new JPasswordField( pwd, 30 );

        // initially, some parts are not visible, they are made visible in the
        // swing worker thread.
        url.setVisible( false );
        username_label.setVisible( false );
        username.setVisible( false );
        password_label.setVisible( false );
        password.setVisible( false );

        // add the components to the option panel
        add( "0, 0, 3, 1, W,  , 3", new JLabel( "<html><b>Subversion Settings</b>" ) );

        add( "0, 1, 1, 1, E,  , 3", url_label );
        add( "1, 1, 2, 1, 0, w, 3", url );

        add( "0, 2, 1, 1, E,  , 3", username_label );
        add( "1, 2, 2, 1, 0, w, 3", username );

        add( "0, 3, 1, 1, E,  , 3", password_label );
        add( "1, 3, 2, 1, 0, w, 3", password );

        ( new Runner() ).execute();
    }

    // #_save() : void
    /** Saves properties from the option pane. */
    protected void _save() {
        String name = getProjectName();
        jEdit.setProperty(
            PREFIX + name + ".url",
            ( url == null ? "" : url.getText() )
        );
        jEdit.setProperty(
            PREFIX + name + ".username",
            ( username == null ? "" : username.getText() )
        );

        char[] pwd_chars = password == null ? new char[ 0 ] : password.getPassword();
        String pwd = new String( pwd_chars );
        for ( int i = 0; i < pwd_chars.length; i++ ) {
            pwd_chars[ i ] = '0';
        }
        try {
            if ( pwd.length() > 0 ) {
                PasswordHandler ph = new PasswordHandler();
                pwd = ph.encrypt( pwd );
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        jEdit.setProperty(
            PREFIX + name + ".password",
            pwd
        );
    }

    private String getProjectName() {
        String project_name = "";
        if ( ProjectOptions.getProject().getName() != null ) {
            project_name = ProjectOptions.getProject().getName();
        }
        return project_name;
    }

    private String getProjectRoot() {
        String project_root = "";
        if ( ProjectOptions.getProject().getRootPath() != null ) {
            project_root = ProjectOptions.getProject().getRootPath();
        }
        return project_root;
    }


    class Runner extends SwingWorker<List<SVNInfo>, Object> {

        @Override
        public List<SVNInfo> doInBackground() {
            try {
                // adjust the UI while fetching the svn info
                url_label.setText( "Just a moment, attempting to fetch current SVN info..." );

                // fetch any existing svn info
                java.util.List<String> info_path = new java.util.ArrayList<String>();
                info_path.add( getProjectRoot() );
                SVNData info_data = new SVNData();
                info_data.setOut( new ConsolePrintStream( new NullOutputStream() ) );
                info_data.setPaths( info_path );
                Info info = new Info();
                List<SVNInfo> results = info.info( info_data );
                info_data.getOut().close();
                return results;
            }
            catch ( Exception e ) {}
            return null;
        }

        @Override
        protected void done() {
            try {
                // populate url field from existing svn info, if available
                url_label.setText( jEdit.getProperty( PREFIX + "url.label" ) );
                List<SVNInfo> info_results = get();
                String url_text = null;
                if ( info_results != null && info_results.size() > 0 ) {
                    SVNInfo svn_info = info_results.get( 0 );
                    if ( svn_info != null && svn_info.getURL() != null ) {
                        url_text = svn_info.getURL().toString();
                    }
                }
                if ( url_text != null ) {
                    url.setText( url_text );
                }

                // make the UI visible
                url.setVisible( true );
                username_label.setVisible( true );
                username.setVisible( true );
                password_label.setVisible( true );
                password.setVisible( true );
            }
            catch ( Exception e ) {
                // ignored
            }
        }
    }

}
