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



import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCommitClient;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;


import ise.plugin.svn.data.DeleteData;
import ise.plugin.svn.data.DeleteResults;
import ise.plugin.svn.SVNPlugin;


public class Delete {

    public DeleteResults delete( DeleteData data ) throws CommandInitializationException, SVNException {
        SVNKit.setupLibrary();

        // validate data values
        if ( data.getPaths() == null ) {
            return null;     // nothing to do
        }
        if ( data.getOut() == null ) {
            throw new CommandInitializationException( "Invalid output stream." );
        }
        if ( data.getErr() == null ) {
            data.setErr( data.getOut() );
        }

        // use default svn config options
        ISVNOptions options = SVNWCUtil.createDefaultOptions( true );

        // use the svnkit client manager
        SVNClientManager clientManager = SVNClientManager.newInstance( options, SVNWCUtil.createDefaultAuthenticationManager(SVNPlugin.getSvnStorageDir(), data.getUsername(), data.getDecryptedPassword()) );

        if ( !data.pathsAreURLs() ) {
            // working copies, convert paths to Files
            List<String> paths = data.getPaths();
            File[] localPaths = new File[ paths.size() ];
            for ( int i = 0; i < paths.size(); i++ ) {
                localPaths[ i ] = new File( paths.get( i ) );
                // check for file existence?
            }

            // get a commit client
            SVNWCClient client = clientManager.getWCClient();

            // set an event handler so that messages go to the commit data streams for display
            client.setEventHandler( new SVNCommandEventProcessor( data.getOut(), data.getErr(), false ) );

            // actually do the deletes(s)
            PrintStream out = data.getOut();
            DeleteResults results = new DeleteResults();
            for ( String path : paths ) {
                try {
                    File file = new File( path );
                    client.doDelete( file, data.getForce(), data.getDeleteFiles(), data.getDryRun() );
                    results.addPath( path );
                }
                catch ( Exception e ) {
                    out.println( e.getMessage() );
                    results.addErrorPath( path, e.getMessage() );
                }
            }
            out.flush();
            out.close();
            return results;
        }
        else {
            // remote urls, need to use a commit client
            SVNCommitClient client = clientManager.getCommitClient();

            DeleteResults results = new DeleteResults();
            List<String> paths = data.getPaths();
            SVNURL[] remotePaths = new SVNURL[ paths.size() ];
            for ( int i = 0; i < paths.size(); i++ ) {
                remotePaths[ i ] = SVNURL.parseURIDecoded( paths.get( i ) );
            }

            // set an event handler so that messages go to the commit data streams for display
            client.setEventHandler( new SVNCommandEventProcessor( data.getOut(), data.getErr(), false ) );

            // actually do the delete
            String commitMessage = data.getCommitMessage();
            if ( commitMessage == null || commitMessage.length() == 0 ) {
                commitMessage = "no message";
            }
            SVNCommitInfo info = client.doDelete( remotePaths, commitMessage );

            // handle the results
            PrintStream out = data.getOut();
            if ( info != SVNCommitInfo.NULL ) {
                out.println();
                out.println( "Deleted, revision " + info.getNewRevision() + "." );
                results.setRevision( info.getNewRevision() );
                out.flush();
            }
            else {
                out.println();
                String msg = "Delete failed";
                if ( info.getErrorMessage() != null ) {
                    out.println( "Delete failed:" );
                    out.println( info.getErrorMessage() );
                    msg = ": " + info.getErrorMessage();
                }
                else {
                    out.println( "Delete failed." );
                    msg += ".";
                }
                for ( String path : data.getPaths() ) {
                    results.addErrorPath( path, msg );
                }
                out.flush();
            }
            out.close();

            results.addPaths( data.getPaths() );
            return results;
        }
    }
}
