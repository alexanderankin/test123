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
import ise.plugin.svn.command.Add;
import ise.plugin.svn.command.BrowseRepository;
import ise.plugin.svn.command.Info;
import ise.plugin.svn.data.DiffData;
import ise.plugin.svn.data.AddResults;
import ise.plugin.svn.gui.DiffDialog;
import ise.plugin.svn.gui.AddResultsPanel;
import ise.plugin.svn.gui.SVNInfoPanel;
import ise.plugin.svn.io.ConsolePrintStream;
import ise.plugin.svn.library.GUIUtils;
import ise.plugin.svn.library.swingworker.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EditPane;
import jdiff.DualDiff;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNInfo;

/**
 * ActionListener to perform sort of an svn diff.  While subversion can do a diff,
 * I'm delegating to the JDiff plugin to create and display the diff.
 * This is not dependent on ProjectViewer.
 */
public class DiffAction implements ActionListener {

    private DiffDialog dialog = null;

    private View view = null;
    private String path = null;
    private String revision1 = null;
    private String revision2 = null;
    private String username = null;
    private String password = null;

    private Logger logger = null;

    /**
     * Diff a file against a previous version of the same file.
     * @param view the View in which to display results
     * @param path the name of a local file to be diffed.  A dialog will be shown
     * to let the user pick the revision to diff against.
     * @param username the username for the svn repository
     * @param password the password for the username
     */
    public DiffAction( View view, String path, String username, String password ) {
        if ( view == null )
            throw new IllegalArgumentException( "view may not be null" );
        if ( path == null || path.length() == 0 )
            throw new IllegalArgumentException( "path may not be null" );
        this.view = view;
        this.path = path;
        this.username = username;
        this.password = password;
    }

    /**
     * Do a diff of a single file given 2 different revisions of the file.
     * @param view the View in which to display results
     * @param path the name of a local file to be diffed. No dialog will be shown
     * here, the revisions must have already been selected.
     * @param revision1 a revision of path
     * @param revision2 another revision of path
     * @param username the username for the svn repository
     * @param password the password for the username
     */
    public DiffAction( View view, String path, String revision1, String revision2, String username, String password ) {
        if ( view == null )
            throw new IllegalArgumentException( "view may not be null" );
        if ( path == null || path.length() == 0 )
            throw new IllegalArgumentException( "path may not be null" );
        if ( revision1 == null || revision2 == null ) {
            throw new IllegalArgumentException( "neither revision may be null, " + ( revision1 == null ? "revision1" : "revision2" ) + " is null." );
        }
        this.view = view;
        this.path = path;
        this.revision1 = revision1;
        this.revision2 = revision2;
        this.username = username;
        this.password = password;
    }

    /**
     * Do a diff of a two files at a particular revision for each file.  This is
     * useful for diffing the trunk version of a file and a branched or tagged
     * version of the same file.
     * @param view the View in which to display results
     * @param url1 the name of a local file to be diffed. No dialog will be shown
     * here, the revisions must have already been selected.
     * @param revision1 a revision of path
     * @param revision2 another revision of path
     * @param username the username for the svn repository
     * @param password the password for the username
     */
    public DiffAction( View view, String url1, String url2, String username, String password ) {
    }

    private void log( String msg ) {
        logger.log( Level.INFO, msg );
        for ( Handler handler : logger.getHandlers() ) {
            handler.flush();
        }
    }

    public void actionPerformed( ActionEvent ae ) {
        if ( path != null && path.length() > 0 ) {
            final DiffData data;

            // pick or set the revisions
            if ( revision1 == null ) {
                // if here, then the first constructor was called, the user is
                // wanting to diff a local file against a remote version of the
                // file. Show a DiffDialog to get the revision of the remote file.
                dialog = new DiffDialog( view, path );
                GUIUtils.center( view, dialog );
                dialog.setVisible( true );
                data = dialog.getData();
                if ( data == null ) {
                    return ;     // null means user canceled
                }
            }
            else {
                // diffing two repository versions
                data = new DiffData();
                data.addPath( path );

                data.setRevision1( SVNRevision.parse( revision1 ) );
                data.setRevision2( SVNRevision.parse( revision2 ) );
            }

            if ( username != null && password != null ) {
                data.setUsername( username );
                data.setPassword( password );
            }

            // set up the console output
            data.setOut( new ConsolePrintStream( view ) );

            // show the svn console
            view.getDockableWindowManager().showDockableWindow( "subversion" );
            final OutputPanel panel = SVNPlugin.getOutputPanel( view );
            panel.showConsole( );

            logger = panel.getLogger();
            log( "Preparing to diff..." );

            class Runner extends SwingWorker < File[], Object > {

                @Override
                public File[] doInBackground() {
                    try {
                        // fetch repository and path info about the file
                        Info info = new Info( );
                        List<SVNInfo> infos = info.getInfo( data );
                        if ( infos.size() == 0 ) {
                            return null;
                        }
                        SVNInfo svn_info = infos.get( 0 );
                        SVNURL url = svn_info.getRepositoryRootURL();
                        String svn_path = svn_info.getPath();

                        BrowseRepository br = new BrowseRepository();

                        // there should always be one remote revision to fetch for diffing against a working copy
                        // or for diffing against another revision
                        log("Diff, fetching file data...");
                        File remote1 = br.getFile( url.toString(), svn_path, data.getRevision1(), data.getUsername(), data.getPassword() );

                        // there may be a second remote revision for diffing between 2 remote revisions
                        File remote2 = null;
                        if ( data.getRevision2() != null ) {
                            log("Diff, fetching revision data...");
                            remote2 = br.getFile( url.toString(), svn_path, data.getRevision2(), data.getUsername(), data.getPassword() );
                        }

                        File[] files = new File[ 2 ];
                        files[ 0 ] = remote1;
                        files[ 1 ] = remote2;
                        return files;
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
                        File[] files = get();
                        if ( files == null ) {
                            JOptionPane.showMessageDialog( view, "Unable to fetch contents for comparison.", "Error", JOptionPane.ERROR_MESSAGE );
                            return ;
                        }
                        File remote1 = files[ 0 ];
                        File remote2 = files[ 1 ];

                        if ( remote1 == null && remote2 == null ) {
                            JOptionPane.showMessageDialog( view, "Unable to fetch contents for comparison.", "Error", JOptionPane.ERROR_MESSAGE );
                            return ;
                        }
                        if ( ( remote1 != null && remote1.isDirectory() ) || ( remote2 != null && remote2.isDirectory() ) ) {
                            JOptionPane.showMessageDialog( view, "Unable to compare directories.", "Error", JOptionPane.ERROR_MESSAGE );
                            return ;
                        }

                        // show JDiff
                        view.unsplit();
                        DualDiff.toggleFor( view );

                        // set the edit panes in the view
                        EditPane[] editPanes = view.getEditPanes();

                        // always show the 1st remote revision in the left edit pane
                        editPanes[ 0 ].setBuffer( jEdit.openFile( view, remote1.getAbsolutePath() ) );

                        if ( remote2 != null ) {
                            // show the 2nd remote revision in the right edit pane
                            editPanes[ 1 ].setBuffer( jEdit.openFile( view, remote2.getAbsolutePath() ) );
                        }
                        else {
                            // or show the local working copy in the right edit pane
                            editPanes[ 1 ].setBuffer( jEdit.openFile( view, path ) );
                        }

                        // do an explicit repaint of the view to clean up the display
                        view.repaint();
                    }
                    catch ( Exception e ) {
                        // ignored
                        e.printStackTrace();
                    }
                }
            }
            ( new Runner() ).execute();
        }
    }
}
