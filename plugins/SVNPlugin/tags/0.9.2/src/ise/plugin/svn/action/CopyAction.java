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
import ise.plugin.svn.data.CommitData;
import ise.plugin.svn.data.CopyData;
import ise.plugin.svn.gui.AddResultsPanel;
import ise.plugin.svn.gui.CopyResultsPanel;
import ise.plugin.svn.gui.ErrorPanel;
import ise.plugin.svn.io.ConsolePrintStream;
import ise.plugin.svn.library.GUIUtils;
import ise.plugin.svn.library.swingworker.*;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.msg.BufferUpdate;

import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNURL;


/**
 * ActionListener to perform an svn copy.
 * This is not dependent on ProjectViewer.
 */
public class CopyAction implements ActionListener {

    private View view = null;
    private CopyData data = null;
    private String title = "Copy";

    private static final int W2W = 1;
    private static final int W2U = 2;
    private static final int U2W = 3;
    private static final int U2U = 4;

    /**
     * @param view the View in which to display results
     * @param data CopyData object containing the info for a copy of some sort
     */
    public CopyAction( View view, CopyData data ) {
        if ( view == null )
            throw new IllegalArgumentException( "view may not be null" );
        if ( data == null )
            throw new IllegalArgumentException( "data may not be null" );
        this.view = view;
        this.data = data;
        this.title = data.getTitle();
    }


    public void actionPerformed( ActionEvent ae ) {
        if ( data != null ) {
            data.setOut( new ConsolePrintStream( view ) );

            view.getDockableWindowManager().showDockableWindow( "subversion" );
            final OutputPanel panel = SVNPlugin.getOutputPanel( view );
            panel.showConsole();
            final Logger logger = panel.getLogger();
            String log_msg = title.equals("Tag") ? "Tagging" : title + "ing";
            logger.log( Level.INFO, log_msg );
            for ( Handler handler : logger.getHandlers() ) {
                handler.flush();
            }

            /**
             * Used to copy or move either a working file to another working file or to the
             * repository, or to move a repository file or directory to a working file or to
             * another repository location.  To recap, this class can copy:
             * working copy -> working copy
             * working copy -> repository
             * repository -> working copy
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
                                else if ( data.getDestinationURL() != null ) {
                                    // working copy -> repository
                                    where2where = W2U;
                                    if ( data.getSourceFiles().size() > 1 ) {
                                        // must be copying to a directory -- TODO: how to check
                                        // destination is actually a remote directory?
                                        // For now, assume directory and append filename.
                                        destination = data.getDestinationURL().toString() + "/" + file.getName();
                                    }
                                    else {
                                        destination = data.getDestinationURL().toString();
                                    }
                                    cd.setDestinationURL( SVNURL.parseURIDecoded(destination) );
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
                                if ( data.getDestinationFile() != null ) {
                                    // repository -> working file
                                    where2where = U2W;
                                    if ( data.getSourceURLs().size() > 1 ) {
                                        checkDestination( data.getDestinationFile() );
                                    }

                                    // if destination is a directory, figure out
                                    // the new file names of the copies
                                    if ( data.getDestinationFile().isDirectory() ) {
                                        String path = url.getPath();
                                        String name = path.substring( path.lastIndexOf( "/" ) );
                                        File f = new File( data.getDestinationFile(), name );
                                        destination = f.getAbsolutePath();
                                    }
                                    else {
                                        destination = data.getDestinationFile().getAbsolutePath();
                                    }
                                    destination = data.getDestinationFile().getAbsolutePath();
                                    cd.setDestinationFile( data.getDestinationFile() );
                                }
                                else if ( data.getDestinationURL() != null ) {
                                    // repository -> repository
                                    where2where = U2U;
                                    destination = data.getDestinationURL().toString();
                                    cd.setDestinationURL( data.getDestinationURL() );
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
                            panel.addTab( "Copy Error", error_panel );
                            return ;
                        }
                        TreeMap<String, SVNCommitInfo> results = get();
                        //System.out.println( "+++++ results = " + results );
                        //System.out.println( "+++++ where2where = " + where2where );
                        switch ( where2where ) {
                            case W2W:
                            case U2W: {
                                    // SVNCommitInfo in results will be null in these
                                    // cases since there is no actual commit.  These
                                    // files will be scheduled for svn add.
                                    AddResults ar = new AddResults();
                                    for ( String path : results.keySet() ) {
                                        ar.addPath( path );
                                    }
                                    JPanel results_panel = new AddResultsPanel( ar, AddResultsPanel.ADD, view, data.getUsername(), data.getPassword() );
                                    panel.addTab( title, results_panel );

                                    // open the file(s) and signal ProjectViewer to possibly add the file
                                    for ( String path : results.keySet() ) {
                                        File f = new File( path );
                                        if ( !f.isDirectory() ) {
                                            Buffer buffer = jEdit.openFile( view, path );
                                            BufferUpdate bu = new BufferUpdate( buffer, view, BufferUpdate.SAVED );
                                            EditBus.send( bu );
                                        }
                                    }
                                }
                                break;
                            case W2U:
                            case U2U: {
                                    // these cases result in an immediate commit, so
                                    // the SVNCommitInfo objects in the map are valid
                                    JPanel results_panel = new CopyResultsPanel( results, data.getDestinationURL().toString(), false );
                                    panel.addTab( title, results_panel );
                                }
                                break;
                            default:
                                // this shouldn't happen, so I'll just quietly
                                // ignore this case
                                //System.out.println( "+++++ the thing that can't happen did" );
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
                        throw new Exception( "Invalid destination: " + destination.getAbsolutePath() + "\n" + title + " destination must be an existing directory under version control." );
                    }
                }
            }
            ( new Runner() ).execute();

        }
    }
}
