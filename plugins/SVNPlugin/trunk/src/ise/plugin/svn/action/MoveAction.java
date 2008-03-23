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
import ise.plugin.svn.command.Copy;
import ise.plugin.svn.data.AddResults;
import ise.plugin.svn.data.CopyData;
import ise.plugin.svn.gui.AddResultsPanel;
import ise.plugin.svn.gui.CopyResultsPanel;
import ise.plugin.svn.gui.ErrorPanel;
import ise.plugin.svn.io.ConsolePrintStream;
import ise.plugin.svn.library.swingworker.*;

import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.JPanel;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.msg.BufferUpdate;

import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNURL;


/**
 * ActionListener to perform an svn move, based on CopyAction
 * This is not dependent on ProjectViewer.
 */
public class MoveAction extends SVNAction {

    private CopyData data = null;

    private static final int W2W = 1;
    //private static final int W2U = 2;     // can't do a move like this
    //private static final int U2W = 3;     // can't do a move like this
    private static final int U2U = 4;

    /**
     * @param view the View in which to display results
     * @param data CopyData object containing the info for a copy of some sort
     */
    public MoveAction( View view, CopyData data ) {
        super(view, "Move");
        if ( data == null )
            throw new IllegalArgumentException( "data may not be null" );
        this.data = data;
    }


    public void actionPerformed( ActionEvent ae ) {
        if ( data != null ) {
            data.setOut( new ConsolePrintStream( getView() ) );

            verifyLogin();
            data.setUsername( getUsername());
            data.setPassword( getPassword());

            getView().getDockableWindowManager().showDockableWindow( "subversion" );

            final OutputPanel panel = SVNPlugin.getOutputPanel( getView() );
            panel.showConsole();
            final Logger logger = panel.getLogger();
            logger.log( Level.INFO, "Moving ..." );
            for ( Handler handler : logger.getHandlers() ) {
                handler.flush();
            }

            /**
             * working copy -> working copy
             * repository -> repository
             */
            class Runner extends SwingWorker<TreeMap<String, SVNCommitInfo>, Object> {

                private int where2where;
                private String errorMessage = null;

                @Override
                public TreeMap<String, SVNCommitInfo> doInBackground() {
                    TreeMap<String, SVNCommitInfo> results = new TreeMap<String, SVNCommitInfo>();
                    try {
                        if ( data.getSourceFiles() != null ) {
                            for ( File file : data.getSourceFiles() ) {
                                if ( file == null ) {
                                    continue;
                                }
                                CopyData cd = new CopyData();
                                cd.setSourceFile( file );
                                cd.setRevision( data.getRevision() );
                                cd.setIsMove(true);
                                String destination = "";
                                if ( data.getDestinationFile() != null ) {
                                    // working copy -> working copy
                                    where2where = W2W;
                                    if ( data.getSourceFiles().size() > 1 ) {
                                        checkDestination( data.getDestinationFile() );
                                    }

                                    // if destination is a directory, figure out
                                    // the new file names of the copies
                                    if ( data.getDestinationFile().isDirectory() ) {
                                        File f = new File( data.getDestinationFile(), file.getName() );
                                        destination = f.getAbsolutePath();
                                    }
                                    else {
                                        destination = data.getDestinationFile().getAbsolutePath();
                                    }
                                    cd.setDestinationFile( data.getDestinationFile() );
                                }
                                cd.setOut( data.getOut() );
                                cd.setMessage( data.getMessage() );
                                Copy copy = new Copy();
                                SVNCommitInfo result = copy.copy( cd );
                                if ( result != null ) {
                                    results.put( destination, result );
                                }
                            }
                        }
                        else if ( data.getSourceURLs() != null ) {
                            for ( SVNURL url : data.getSourceURLs() ) {
                                if ( url == null ) {
                                    continue;
                                }
                                CopyData cd = new CopyData();
                                String destination = "";
                                cd.setSourceURL( url );
                                cd.setRevision( data.getRevision() );
                                cd.setIsMove(true);
                                if ( data.getDestinationURL() != null ) {
                                    // repository -> repository
                                    where2where = U2U;
                                    String segment = url.toString().substring(url.toString().lastIndexOf("/"));
                                    destination = data.getDestinationURL().toString() + segment;
                                    cd.setDestinationURL( data.getDestinationURL().appendPath(segment, true) );
                                }
                                cd.setOut( data.getOut() );
                                Copy copy = new Copy();
                                SVNCommitInfo result = copy.copy( cd );
                                if ( result != null ) {
                                    results.put( destination, result );
                                }
                            }
                        }
                    }
                    catch ( Exception e ) {
                        errorMessage = e.getMessage();
                        data.getOut().printError( errorMessage );
                        e.printStackTrace();
                    }
                    finally {
                        data.getOut().close();
                    }
                    return results;
                }

                @Override
                protected void done() {
                    try {
                        if ( errorMessage != null ) {
                            JPanel error_panel = new ErrorPanel( errorMessage );
                            panel.addTab( "Move Error", error_panel );
                            return ;
                        }
                        TreeMap<String, SVNCommitInfo> results = get();
                        switch ( where2where ) {
                            case W2W: {
                                    // SVNCommitInfo in results will be null in these
                                    // cases since there is no actual commit.  These
                                    // files will be scheduled for svn add.
                                    AddResults ar = new AddResults();
                                    for ( String path : results.keySet() ) {
                                        ar.addPath( path );
                                    }
                                    JPanel results_panel = new AddResultsPanel( ar, AddResultsPanel.ADD, getView(), getUsername(), getPassword() );
                                    panel.addTab( "Move", results_panel );

                                    // open the file(s) and signal ProjectViewer to possibly add the file
                                    for ( String path : results.keySet() ) {
                                        File f = new File( path );
                                        if ( !f.isDirectory() ) {
                                            Buffer buffer = jEdit.openFile( getView(), path );
                                            BufferUpdate bu = new BufferUpdate( buffer, getView(), BufferUpdate.SAVED );
                                            EditBus.send( bu );
                                        }
                                    }
                                }
                                break;
                            case U2U: {
                                    // these cases result in an immediate commit, so
                                    // the SVNCommitInfo objects in the map are valid
                                    JPanel results_panel = new CopyResultsPanel( results, data.getDestinationURL().toString(), true );
                                    panel.addTab( "Move", results_panel );
                                }
                                break;
                            default:
                                // this shouldn't happen, so I'll just quietly
                                // ignore this case
                        }
                    }
                    catch ( Exception e ) {
                        // ignored
                        e.printStackTrace();
                    }
                }

                private void checkDestination( File destination ) throws Exception {
                    // destination must be a directory and must exist
                    if ( !destination.exists() || !destination.isDirectory() ) {
                        throw new Exception( "Invalid destination: " + destination.getAbsolutePath() + "\nMove destination must be an existing directory under version control." );
                    }
                }
            }
            ( new Runner() ).execute();

        }
    }
}
