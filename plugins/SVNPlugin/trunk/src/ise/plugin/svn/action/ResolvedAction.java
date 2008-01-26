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

package ise.plugin.svn.action;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import projectviewer.vpt.VPTNode;
import ise.plugin.svn.gui.OutputPanel;

import ise.plugin.svn.SVNPlugin;
import ise.plugin.svn.command.Resolved;
import ise.plugin.svn.data.SVNData;
import ise.plugin.svn.data.AddResults;
import ise.plugin.svn.library.GUIUtils;
import ise.plugin.svn.library.swingworker.*;
import ise.plugin.svn.gui.AddResultsPanel;
import ise.plugin.svn.gui.LoginDialog;
import ise.plugin.svn.io.ConsolePrintStream;

import org.tmatesoft.svn.core.wc.SVNInfo;

import org.gjt.sp.jedit.View;


/**
 * ActionListener to perform an svn add.
 * This is not dependent on ProjectViewer.
 */
public class ResolvedAction implements ActionListener {

    private View view = null;
    private List<String> paths = null;
    private String username = null;
    private String password = null;

    /**
     * @param view the View in which to display results
     * @param paths a list of paths to be added
     * @param username the username for the svn repository
     * @param password the password for the username
     */
    public ResolvedAction( View view, List<String> paths, String username, String password ) {
        if ( view == null )
            throw new IllegalArgumentException( "view may not be null" );
        if ( paths == null )
            throw new IllegalArgumentException( "paths may not be null" );
        this.view = view;
        this.paths = paths;
        this.username = username;
        this.password = password;
    }

    public void actionPerformed( ActionEvent ae ) {
        if ( paths != null && paths.size() > 0 ) {
            final SVNData data = new SVNData();

            // get the paths
            boolean recursive = false;
            for ( String path : paths ) {
                File f = new File( path );
                if ( f.exists() && f.isDirectory() ) {
                    recursive = true;
                    break;
                }
            }

            // user confirmations
            if ( recursive ) {
                // have the user verify they want a recursive resolve
                int response = JOptionPane.showConfirmDialog( view, "Recursively resolve all files in selected directories?", "Recursive Resolved?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE );
                if ( response == JOptionPane.CANCEL_OPTION ) {
                    return ;
                }
                recursive = response == JOptionPane.YES_OPTION;
            }
            else {
                // have the user confirm they really want to resolve
                if ( username == null || username.length() == 0 ) {
                    LoginDialog ld = new LoginDialog( view, "Confirm Resolve", "Are you sure you want to mark as resolved?", paths.get( 0 ) );
                    GUIUtils.center( view, ld );
                    ld.setVisible( true );
                    if ( ld.getCanceled() == true ) {
                        return ;
                    }
                    username = ld.getUsername();
                    password = ld.getPassword();
                }
                else {
                    int response = JOptionPane.showConfirmDialog( view, "Resolve selected files?", "Confirm Resolve", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE );
                    if ( response == JOptionPane.NO_OPTION ) {
                        return ;
                    }
                }
            }

            data.setPaths( paths );

            if ( username != null && password != null ) {
                data.setUsername( username );
                data.setPassword( password );
            }

            data.setOut( new ConsolePrintStream( view ) );

            view.getDockableWindowManager().showDockableWindow( "subversion" );
            final OutputPanel panel = SVNPlugin.getOutputPanel( view );
            panel.showConsole();
            Logger logger = panel.getLogger();
            logger.log( Level.INFO, "Resolving ..." );
            for ( Handler handler : logger.getHandlers() ) {
                handler.flush();
            }

            class Runner extends SwingWorker<AddResults, Object> {

                @Override
                public AddResults doInBackground() {
                    try {
                        Resolved resolve = new Resolved();
                        return resolve.resolve( data );
                    }
                    catch ( Exception e ) {
                        data.getOut().printError( e.getMessage() );
                    }
                    finally {
                        data.getOut().close();
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        JPanel results_panel = new AddResultsPanel( get(), AddResultsPanel.RESOLVED, view, username, password );
                        panel.addTab( "Resolved", results_panel );
                    }
                    catch ( Exception e ) {
                        System.err.println( e.getMessage() );
                    }
                }
            }
            ( new Runner() ).execute();

        }
    }
}
