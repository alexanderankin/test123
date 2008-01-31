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
import ise.plugin.svn.gui.RemoteDiffDialog;
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
 * This is not dependent on ProjectViewer.  This version does a diff of 2 repository
 * items.
 */
public class RemoteDiffAction implements ActionListener {

    private View view = null;
    private DiffData data;
    private Logger logger = null;

    public RemoteDiffAction( View view, DiffData data ) {
        if ( view == null )
            throw new IllegalArgumentException( "view may not be null" );
        if ( data == null )
            throw new IllegalArgumentException( "data may not be null" );
        this.view = view;
        this.data = data;
        if ( data.pathsAreURLs() == false ) {
            throw new IllegalArgumentException( "RemoteDiffAction is for remote diffs, the given paths must be repository URLs." );
        }
    }

    private void log( String msg ) {
        logger.log( Level.INFO, msg );
        for ( Handler handler : logger.getHandlers() ) {
            handler.flush();
        }
    }

    public void actionPerformed( ActionEvent ae ) {
        // RemoteDiffDialog validates that there are exactly 2 paths in the
        // diff data
        RemoteDiffDialog dialog = new RemoteDiffDialog( view, data );
        GUIUtils.center( view, dialog );
        dialog.setVisible( true );
        data = dialog.getData();
        if ( data == null ) {
            return ;     // null means user canceled
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
                    File remote1 = null;
                    File remote2 = null;

                    String path1 = data.getPaths().get(0);
                    if (path1.startsWith(data.getURL())) {
                        path1 = path1.substring(data.getURL().length());
                    }
                    String path2 = data.getPaths().get(1);
                    if (path2.startsWith(data.getURL())) {
                        path2 = path2.substring(data.getURL().length());
                    }

                    BrowseRepository br = new BrowseRepository();
                    remote1 = br.getFile( data.getURL(), path1, data.getRevision1(), data.getUsername(), data.getPassword() );
                    remote2 = br.getFile( data.getURL(), path2, data.getRevision2(), data.getUsername(), data.getPassword() );

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

                    if ( remote1 == null || remote2 == null ) {
                        JOptionPane.showMessageDialog( view, "Unable to fetch contents for comparison.", "Error", JOptionPane.ERROR_MESSAGE );
                        return ;
                    }
                    if ( remote1.isDirectory() || remote2.isDirectory() ) {
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

                    editPanes[ 1 ].setBuffer( jEdit.openFile( view, remote2.getAbsolutePath() ) );

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

    /**
     * @return true if rev1 is before rev2
     */
    private boolean lessThan( SVNRevision rev1, SVNRevision rev2 ) {
        if ( rev1.getDate() != null && rev2.getDate() != null ) {
            return rev1.getDate().getTime() < rev2.getDate().getTime();
        }
        return rev1.getNumber() < rev2.getNumber();
    }
}
