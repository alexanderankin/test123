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

    /**
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

    public DiffAction( View view, String path, String revision1, String revision2, String username, String password ) {
        if ( view == null )
            throw new IllegalArgumentException( "view may not be null" );
        if ( path == null || path.length() == 0 )
            throw new IllegalArgumentException( "path may not be null" );
        this.view = view;
        this.path = path;
        this.revision1 = revision1;
        this.revision2 = revision2;
        this.username = username;
        this.password = password;
    }

    public void actionPerformed( ActionEvent ae ) {
        if ( path != null && path.length() > 0 ) {
            final DiffData data;
            if ( revision1 == null ) {
                // diffing a working copy against a repository version
                dialog = new DiffDialog( view, path );
                GUIUtils.center( view, dialog );
                dialog.setVisible( true );
                data = dialog.getData();
                if ( data == null ) {
                    return ;     // null means user cancelled
                }
            }
            else {
                // diffing two repository versions
                if (revision2 == null) {
                    // need 2 revisions to diff
                    return;
                }
                data = new DiffData();
                data.addPath(path);
                data.setRevision1(SVNRevision.parse(revision1));
                data.setRevision2(SVNRevision.parse(revision2));
            }

            if ( username != null && password != null ) {
                data.setUsername( username );
                data.setPassword( password );
            }
            data.setOut( new ConsolePrintStream( view ) );

            view.getDockableWindowManager().showDockableWindow( "subversion" );
            final OutputPanel panel = SVNPlugin.getOutputPanel( view );
            panel.showConsole( );
            Logger logger = panel.getLogger();
            logger.log( Level.INFO, "Preparing to diff ..." );
            for ( Handler handler : logger.getHandlers() ) {
                handler.flush();
            }

            class Runner extends SwingWorker<SVNInfo, Object> {

                @Override
                public SVNInfo doInBackground() {
                    try {
                        // fetch info about the file to get repository and path
                        Info info = new Info( );
                        List<SVNInfo> infos = info.getInfo( data );
                        if ( infos.size() > 0 ) {
                            return infos.get( 0 );
                        }
                        return null;
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
                        SVNInfo info = get();
                        SVNURL url = info.getRepositoryRootURL();
                        String svn_path = info.getPath();
                        /* needs work, convert -1 to HEAD before comparison?
                        if ( info.getRevision().equals( data.getRevision1() ) ) {
                            JOptionPane.showMessageDialog( view, "There is no difference between the local copy and the repository copy", "No Difference", JOptionPane.INFORMATION_MESSAGE );
                            return ;
                        }
                        */

                        BrowseRepository br = new BrowseRepository();
                        // there should always be one remote revision to fetch for diffing against a working copy
                        // or for diffing against another revision
                        File remote1 = br.getFile( url.toString(), svn_path, data.getRevision1().getNumber(), data.getUsername(), data.getPassword() );

                        // there may be a second remote revision for diffing between 2 remote revisions
                        File remote2 = null;
                        if (data.getRevision2() != null) {
                            remote2 = br.getFile( url.toString(), svn_path, data.getRevision2().getNumber(), data.getUsername(), data.getPassword() );
                        }

                        // show JDiff
                        view.unsplit();
                        DualDiff.toggleFor( view );

                        // set the edit panes in the view
                        EditPane[] editPanes = view.getEditPanes();
                        if (remote2 != null) {
                            // show the 2nd remote revision in the first edit pane
                            editPanes[0].setBuffer( jEdit.openFile( view, remote2.getAbsolutePath()));
                        }
                        else {
                            // or show the local working copy in the first edit pane
                            editPanes[ 0 ].setBuffer( jEdit.openFile( view, path ) );
                        }
                        // always show the 1st remote revision in the 2nd edit pane
                        editPanes[ 1 ].setBuffer( jEdit.openFile( view, remote1.getAbsolutePath() ) );
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
