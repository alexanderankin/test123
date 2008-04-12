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
import ise.plugin.svn.command.BrowseRepository;
import ise.plugin.svn.command.Diff;
import ise.plugin.svn.command.Info;
import ise.plugin.svn.data.DiffData;
import ise.plugin.svn.gui.DiffDialog;
import ise.plugin.svn.io.ConsolePrintStream;
import ise.plugin.svn.library.GUIUtils;
import ise.plugin.svn.library.swingworker.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
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
public class DiffAction extends SVNAction {

    private DiffDialog dialog = null;

    private List<String> paths = null;
    private String path1 = null;
    private String path2 = null;
    private String revision1 = null;
    private String revision2 = null;

    private DiffData data;
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
        super( view, "Diff" );
        if ( path == null || path.length() == 0 )
            throw new IllegalArgumentException( "path may not be null" );
        this.path1 = path;
        setUsername( username );
        setPassword( password );
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
        super( view, "Diff" );
        if ( path == null || path.length() == 0 )
            throw new IllegalArgumentException( "path may not be null" );
        if ( revision1 == null || revision2 == null ) {
            throw new IllegalArgumentException( "neither revision may be null, " + ( revision1 == null ? "revision1" : "revision2" ) + " is null." );
        }
        this.path1 = path;
        this.revision1 = revision1;
        this.revision2 = revision2;
        setUsername( username );
        setPassword( password );
    }

    public DiffAction( View view, DiffData data ) {
        super( view, "Diff" );
        this.data = data;
        this.paths = data.getPaths();
        if ( paths == null || paths.size() == 0 ) {
            throw new IllegalArgumentException( "paths may not be null" );
        }

        // if data only has 1 path, set path1 to that path
        if ( paths.size() == 1 ) {
            path1 = paths.get( 0 );
            this.paths = null;
        }
        setUsername( data.getUsername() );
        setPassword( data.getPassword() );
    }

    private void log( String msg ) {
        logger.log( Level.INFO, msg );
        for ( Handler handler : logger.getHandlers() ) {
            handler.flush();
        }
    }

    public void actionPerformed( ActionEvent ae ) {
        if ( ( path1 != null && path1.length() > 0 ) || data != null ) {

            // pick or set the revisions
            if ( paths != null ) {
                // if here, then the 3rd constructor was called.  The user has
                // selected to diff more than one file, which means all that can
                // be done is an svn diff.
                dialog = new DiffDialog( getView(), data );
                GUIUtils.center( getView(), dialog );
                dialog.setVisible( true );
                data = dialog.getData();
                if ( data == null ) {
                    return ;     // null means user canceled
                }
            }
            else if ( revision1 == null && path2 == null ) {
                // if here, then the first constructor was called, the user is
                // wanting to diff a local file against a remote version of the
                // file. Show a DiffDialog to get the revision of the remote file.
                dialog = new DiffDialog( getView(), path1 );
                GUIUtils.center( getView(), dialog );
                dialog.setVisible( true );
                data = dialog.getData();
                if ( data == null ) {
                    return ;     // null means user canceled
                }
            }
            else {
                // diffing two repository versions of the same file
                data = new DiffData();
                data.addPath( path1 );
                data.setRevision1( SVNRevision.parse( revision1 ) );
                data.setRevision2( SVNRevision.parse( revision2 ) );
            }

            if ( getUsername() == null && data.getUsername() == null ) {
                verifyLogin( data.getPaths() == null ? null : data.getPaths().get( 0 ) );
                if ( isCanceled() ) {
                    return ;
                }
                data.setUsername( getUsername() );
                data.setPassword( getPassword() );
            }
            else if ( data.getUsername() != null ) {
                setUsername( data.getUsername() );
                setPassword( data.getPassword() );
            }
            else {
                data.setUsername( getUsername() );
                data.setPassword( getPassword() );
            }

            // set up the console output
            data.setOut( new ConsolePrintStream( getView() ) );

            // show the svn console
            getView().getDockableWindowManager().showDockableWindow( "subversion" );
            final OutputPanel panel = SVNPlugin.getOutputPanel( getView() );
            panel.showConsole( );

            logger = panel.getLogger();

            if ( data.getSvnDiff() ) {
                new SVNDiffRunner().execute();
            }
            else {
                new JDiffRunner().execute();
            }
        }
    }

    private boolean lessThan( SVNRevision rev1, SVNRevision rev2 ) {
        if ( rev1.getDate() != null && rev2.getDate() != null ) {
            return rev1.getDate().getTime() < rev2.getDate().getTime();
        }
        return rev1.getNumber() < rev2.getNumber();
    }

    /**
     * Uses JDiff to show the differences between 2 files.
     */
    class JDiffRunner extends SwingWorker < File[], Object > {

        @Override
        public File[] doInBackground() {
            try {
                log( "Preparing to diff..." );
                SVNURL url = null;
                String svn_path = null;
                File remote1 = null;
                File remote2 = null;

                // fetch repository and path info about the file
                Info info = new Info( );
                List<SVNInfo> infos = info.getInfo( data );
                if ( infos.size() == 0 ) {
                    return null;
                }
                SVNInfo svn_info = infos.get( 0 );
                url = svn_info.getRepositoryRootURL();
                svn_path = svn_info.getPath();
                BrowseRepository br = new BrowseRepository();

                // there should always be one remote revision to fetch for diffing against a working copy
                // or for diffing against another revision
                log( "Diff, fetching file data..." );
                remote1 = br.getFile( url.toString(), svn_path, data.getRevision1(), data.getUsername(), data.getPassword() );

                // there may be a second remote revision for diffing between 2 remote revisions
                remote2 = null;
                if ( data.getRevision2() != null ) {
                    log( "Diff, fetching revision data..." );
                    remote2 = br.getFile( url.toString(), svn_path, data.getRevision2(), data.getUsername(), data.getPassword() );

                    // sort, oldest revision first
                    boolean lessThan = lessThan( data.getRevision2(), data.getRevision1() );
                    if ( lessThan ) {
                        File temp = remote1;
                        remote1 = remote2;
                        remote2 = temp;
                        temp = null;
                    }
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
                    JOptionPane.showMessageDialog( getView(), "Unable to fetch contents for comparison.", "Error", JOptionPane.ERROR_MESSAGE );
                    return ;
                }
                final File remote1 = files[ 0 ];
                final File remote2 = files[ 1 ];

                if ( remote1 == null && remote2 == null ) {
                    JOptionPane.showMessageDialog( getView(), "Unable to fetch contents for comparison.", "Error", JOptionPane.ERROR_MESSAGE );
                    return ;
                }
                if ( ( remote1 != null && remote1.isDirectory() ) || ( remote2 != null && remote2.isDirectory() ) ) {
                    JOptionPane.showMessageDialog( getView(), "Unable to compare directories.", "Error", JOptionPane.ERROR_MESSAGE );
                    return ;
                }

                // show JDiff
                getView().unsplit();
                DualDiff.toggleFor( getView() );

                Runnable r = new Runnable() {
                            public void run() {
                                // set the edit panes in the view
                                EditPane[] editPanes = getView().getEditPanes();

                                // always show the 1st remote revision in the left edit pane
                                editPanes[ 0 ].setBuffer( jEdit.openFile( getView(), remote1.getAbsolutePath() ) );

                                if ( remote2 == null ) {
                                    // or show the local working copy in the right edit pane
                                    editPanes[ 1 ].setBuffer( jEdit.openFile( getView(), path1 ) );
                                }
                                else {
                                    // show the 2nd remote revision in the right edit pane
                                    editPanes[ 1 ].setBuffer( jEdit.openFile( getView(), remote2.getAbsolutePath() ) );
                                }

                                // show the jdiff dockable
                                getView().getDockableWindowManager().showDockableWindow( "jdiff-lines" );

                                // do an explicit repaint of the view to clean up the display
                                getView().repaint();
                            }
                        };
                SwingUtilities.invokeLater( r );

            }
            catch ( Exception e ) {
                e.printStackTrace();
                log( "Error: " + e.getMessage() );
            }
        }
    }

    class SVNDiffRunner extends SwingWorker < String, Object > {

        @Override
        public String doInBackground() {
            try {
                log( "Preparing SVN diff..." );
                Diff diff = new Diff();
                return diff.diff( data );
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
                String filediff = get();
                if ( filediff != null ) {
                    jEdit.newFile( getView() ).insert( 0, filediff );
                    log( "SVN Diff created." );
                }
                else {
                    log( "Unable to create SVN diff." );
                }
            }
            catch ( Exception e ) {
                e.printStackTrace();
                log( "Error: " + e.getMessage() );
            }
        }
    }

}