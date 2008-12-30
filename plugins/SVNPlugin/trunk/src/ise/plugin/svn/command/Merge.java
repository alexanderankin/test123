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

package ise.plugin.svn.command;

import java.io.*;
import java.util.*;

import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.SVNException;

import org.tmatesoft.svn.cli.SVNCommand;
import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.internal.util.SVNFormatUtil;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNEventAction;
import org.tmatesoft.svn.core.wc.SVNStatusType;

import ise.plugin.svn.data.MergeData;
import ise.plugin.svn.data.MergeResults;


import org.gjt.sp.jedit.jEdit;


public class Merge {

    /**
     * Fills a MergeResults based on the given MergeData.
     * @param data MergeData containing the information necessary to do a merge.
     */
    public MergeResults doMerge( MergeData data ) throws CommandInitializationException, SVNException {
        SVNKit.setupLibrary();

        if ( data.getOut() == null ) {
            throw new CommandInitializationException( "Invalid output stream." );
        }
        if ( data.getErr() == null ) {
            data.setErr( data.getOut() );
        }

        // use default svn config options
        ISVNOptions options = SVNWCUtil.createDefaultOptions( true );

        // use the svnkit client manager
        SVNClientManager clientManager = SVNClientManager.newInstance( options, data.getUsername(), data.getDecryptedPassword() );

        // get a diff client
        SVNDiffClient client = clientManager.getDiffClient();

        // set an event handler so that messages go to the merge data streams for display
        MergeResults results = new MergeResults();
        results.setDryRun(data.getDryRun());
        results.setCommandLineEquivalent(data.commandLineEquivalent());
        MergeEventProcessor handler = new MergeEventProcessor( data.getOut(), results );
        client.setEventHandler( handler );

        try {
            System.out.println( data.toString() );
            if ( data.getFromFile() != null && data.getToFile() != null ) {
                //local, revision, local, revision, destination
                System.out.println( "+++++ method 1" );
                client.doMerge(
                    data.getFromFile(),
                    data.getStartRevision(),
                    data.getToFile(),
                    data.getEndRevision(),
                    data.getDestinationFile(),
                    data.getRecursive(),
                    !data.getIgnoreAncestry(),
                    data.getForce(),
                    data.getDryRun() );
            }
            else if ( data.getFromFile() != null && data.getToPath() != null ) {
                //local, revision, remote, revision, destination
                System.out.println( "+++++ method 2" );
                client.doMerge(
                    data.getFromFile(),
                    data.getStartRevision(),
                    SVNURL.parseURIDecoded( data.getToPath() ),
                    data.getEndRevision(),
                    data.getDestinationFile(),
                    data.getRecursive(),
                    !data.getIgnoreAncestry(),
                    data.getForce(),
                    data.getDryRun() );
            }
            else if ( data.getFromPath() != null && data.getToFile() != null ) {
                //remote, revision, local, revision, destination
                System.out.println( "+++++ method 3" );
                client.doMerge(
                    SVNURL.parseURIDecoded( data.getFromPath() ),
                    data.getStartRevision(),
                    data.getToFile(),
                    data.getEndRevision(),
                    data.getDestinationFile(),
                    data.getRecursive(),
                    !data.getIgnoreAncestry(),
                    data.getForce(),
                    data.getDryRun() );
            }
            else if ( data.getFromPath() != null && data.getToPath() != null ) {
                //remote, revision, remote, revision, destination
                System.out.println( "+++++ method 4" );
                client.doMerge(
                    SVNURL.parseURIDecoded( data.getFromPath() ),
                    data.getStartRevision(),
                    SVNURL.parseURIDecoded( data.getToPath() ),
                    data.getEndRevision(),
                    data.getDestinationFile(),
                    data.getRecursive(),
                    !data.getIgnoreAncestry(),
                    data.getForce(),
                    data.getDryRun() );
            }
            else {
                String msg = data.checkValid();
                handler.getResults().setErrorMessage( msg == null ? jEdit.getProperty( "ips.Merge_data_error.", "Merge data error." ) : msg );
            }

        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        return handler.getResults();
    }

    class MergeEventProcessor implements ISVNEventHandler {

        private final PrintStream out;
        private MergeResults results = null;


        public MergeEventProcessor( PrintStream out, MergeResults results ) {
            this.out = out;
            this.results = results;
        }

        public MergeResults getResults() {
            return results;
        }

        public void handleEvent( SVNEvent event, double progress ) {
            String filename = "";
            if ( event.getFile() != null ) {
                filename = SVNFormatUtil.formatPath( event.getFile() );
            }
            if ( event.getAction() == SVNEventAction.UPDATE_ADD ) {
                if ( event.getContentsStatus() == SVNStatusType.CONFLICTED ) {
                    SVNCommand.println( out, "C    " + filename );
                    results.addConflicted( filename );
                }
                else {
                    SVNCommand.println( out, "A    " + filename );
                    results.addAdded( filename );
                }
            }
            else if ( event.getAction() == SVNEventAction.UPDATE_DELETE ) {
                SVNCommand.println( out, "D    " + filename );
                results.addDeleted( filename );
            }
            else if ( event.getAction() == SVNEventAction.UPDATE_UPDATE ) {
                StringBuffer sb = new StringBuffer();
                if ( event.getNodeKind() != SVNNodeKind.DIR ) {
                    if ( event.getContentsStatus() == SVNStatusType.CHANGED ) {
                        sb.append( "U" );
                        results.addUpdated( filename );
                    }
                    else if ( event.getContentsStatus() == SVNStatusType.CONFLICTED ) {
                        sb.append( "C" );
                        results.addConflicted( filename );
                    }
                    else if ( event.getContentsStatus() == SVNStatusType.MERGED ) {
                        sb.append( "G" );
                        results.addMerged( filename );
                    }
                    else {
                        sb.append( " " );
                    }
                }
                else {
                    sb.append( ' ' );
                }
                if ( event.getPropertiesStatus() == SVNStatusType.CHANGED ) {
                    sb.append( "U" );
                    results.addUpdated( filename );
                }
                else if ( event.getPropertiesStatus() == SVNStatusType.CONFLICTED ) {
                    sb.append( "C" );
                    results.addConflicted( filename );
                }
                else if ( event.getPropertiesStatus() == SVNStatusType.MERGED ) {
                    sb.append( "G" );
                    results.addMerged( filename );
                }
                else {
                    sb.append( " " );
                }
                if ( sb.toString().trim().length() > 0 ) {
                    SVNCommand.println( out, sb.toString() + "  " + filename );
                }
            }
            else if ( event.getAction() == SVNEventAction.ADD ) {
                if ( SVNProperty.isBinaryMimeType( event.getMimeType() ) ) {
                    SVNCommand.println( out, "A  (bin)  " + filename );
                    results.addAdded( filename );
                }
                else {
                    SVNCommand.println( out, "A         " + filename );
                    results.addAdded( filename );
                }
            }
            else if ( event.getAction() == SVNEventAction.DELETE ) {
                SVNCommand.println( out, "D         " + filename );
                results.addDeleted( filename );
            }
            else if ( event.getAction() == SVNEventAction.SKIP ) {
                SVNCommand.println( out, "Skipped '" + filename + "'" );
                results.addSkipped( filename );
            }
        }

        public void checkCancelled() throws SVNCancelException {}


    }
}