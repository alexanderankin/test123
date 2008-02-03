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

import ise.plugin.svn.gui.OutputPanel;

import ise.plugin.svn.SVNPlugin;
import ise.plugin.svn.command.Delete;
import ise.plugin.svn.data.AddResults;
import ise.plugin.svn.data.DeleteData;
import ise.plugin.svn.data.DeleteResults;
import ise.plugin.svn.gui.AddResultsPanel;
import ise.plugin.svn.gui.DeleteDialog;
import ise.plugin.svn.gui.RemoteDeleteDialog;
import ise.plugin.svn.gui.SVNInfoPanel;
import ise.plugin.svn.io.ConsolePrintStream;
import ise.plugin.svn.library.GUIUtils;
import ise.plugin.svn.library.PasswordHandler;
import ise.plugin.svn.library.swingworker.SwingWorker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;

/**
 * ActionListener to perform an svn delete.
 * This is not dependent on ProjectViewer.
 */
public class DeleteAction implements ActionListener {

    private View view = null;
    private List<String> paths = null;
    private DeleteData data = null;
    private String username = null;
    private String password = null;

    /**
     * @param view the View in which to display results
     * @param paths a list of paths to be deleted
     * @param username the username for the svn repository
     * @param password the password for the username
     */
    public DeleteAction( View view, List<String> paths, String username, String password ) {
        if ( view == null )
            throw new IllegalArgumentException( "view may not be null" );
        if ( paths == null )
            throw new IllegalArgumentException( "paths may not be null" );
        this.view = view;
        this.paths = paths;
        this.username = username;
        this.password = password;

        data = new DeleteData();
        data.setPaths( paths );
        data.setUsername( username );
        data.setPassword( password );
    }

    public DeleteAction( View view, DeleteData data ) {
        if ( view == null )
            throw new IllegalArgumentException( "view may not be null" );
        if ( data == null )
            throw new IllegalArgumentException( "data may not be null" );
        this.view = view;
        this.data = data;
    }


    public void actionPerformed( ActionEvent ae ) {
        if ( data.getPaths() != null && data.getPaths().size() > 0 ) {

            data.setOut( new ConsolePrintStream( view ) );

            // show dialog
            if ( !data.pathsAreURLs() ) {
                // working copy delete
                DeleteDialog dialog = new DeleteDialog( view, data, data.getUsername() == null );
                GUIUtils.center( view, dialog );
                dialog.setVisible( true );
                data = dialog.getData();
                if ( data == null ) {
                    return ;     // null data signals user canceled
                }
            }
            else {
                // remote copy delete -- show path(s) to delete and commit
                // message textbox and dropdown.
                RemoteDeleteDialog dialog = new RemoteDeleteDialog( view, data );
                GUIUtils.center( view, dialog );
                dialog.setVisible( true );
                data = dialog.getData();
                if ( data == null ) {
                    return ;     // null data signals user canceled
                }
            }

            view.getDockableWindowManager().showDockableWindow( "subversion" );
            final OutputPanel panel = SVNPlugin.getOutputPanel( view );
            panel.showConsole();
            Logger logger = panel.getLogger();
            logger.log( Level.INFO, "Deleting ..." );
            for ( Handler handler : logger.getHandlers() ) {
                handler.flush();
            }

            username = data.getUsername();
            password = data.getPassword();
            if ( password != null && password.length() > 0 ) {
                try {
                    PasswordHandler ph = new PasswordHandler();
                    password = ph.decrypt( password );
                }
                catch ( Exception e ) {
                    password = "";
                }
            }
            data.setPassword(password);

            class Runner extends SwingWorker<DeleteResults, Object> {

                @Override
                public DeleteResults doInBackground() {
                    try {
                        Delete delete = new Delete( );
                        return delete.delete( data );
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
                        AddResults results = ( AddResults ) get();
                        JPanel results_panel = new AddResultsPanel( results, data.pathsAreURLs() ? AddResultsPanel.REMOTE_DELETE : AddResultsPanel.DELETE, view, username, password );
                        panel.addTab( "Delete", results_panel );
                    }
                    catch ( Exception e ) {
                        e.printStackTrace();
                    }
                }
            }
            ( new Runner() ).execute();

        }
    }

    public DeleteData getData() {
        return data;
    }
}
