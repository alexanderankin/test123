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

import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNCommitClient;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNWCUtil;


import ise.plugin.svn.data.CommitData;
import ise.plugin.svn.data.CommitInfo;
import ise.plugin.svn.SVNPlugin;

public class Commit {

    public CommitData commit( CommitData cd ) throws CommandInitializationException, SVNException {

        SVNKit.setupLibrary();

        // validate commit data values
        if (cd.getPaths() == null) {
            return null;     // nothing to do
        }
        if (cd.getOut() == null) {
            throw new CommandInitializationException("Invalid output stream.");
        }
        if (cd.getErr() == null) {
            cd.setErr(cd.getOut());
        }
        if (cd.getCommitMessage() == null) {
            cd.setCommitMessage("");
        }

        // convert paths to Files
        final List<String> paths = cd.getPaths();
        File[] localPaths = new File[ paths.size() ];
        for ( int i = 0; i < paths.size(); i++ ) {
            localPaths[ i ] = new File( paths.get( i ) );
            // check for file existence?
        }

        // use default svn config options
        ISVNOptions options = SVNWCUtil.createDefaultOptions( true );

        // use the svnkit client manager
        SVNClientManager clientManager = SVNClientManager.newInstance( options, SVNWCUtil.createDefaultAuthenticationManager(SVNPlugin.getSvnStorageDir(), cd.getUsername(), cd.getDecryptedPassword()) );

        // get a commit client
        SVNCommitClient client = clientManager.getCommitClient();

        // set an event handler so that messages go to the commit data streams for display
        // and gather the paths actually committed
        final List<String> result_paths = new ArrayList<String>();
        client.setEventHandler( new SVNCommandEventProcessor( cd.getOut(), cd.getErr(), false ) {
            @Override
            public void handleEvent(SVNEvent event, double progress) {
                super.handleEvent(event, progress);
                if (event.getFile() != null) {
                    result_paths.add(event.getFile().toString());
                }
            }
        });

        // actually do the commit
        /* doCommit(
                  File[] paths,
                  boolean keepLocks,
                  String commitMessage,
                  SVNProperties revisionProperties,
                  String[] changelists,
                  boolean keepChangelist,
                  boolean force,
                  SVNDepth depth) */

        SVNCommitInfo info = client.doCommit(
            localPaths,
            cd.getKeepLocks(),
            cd.getCommitMessage(),
            null,
            null,
            false,
            false,
            cd.getRecursive() ? SVNDepth.INFINITY : SVNDepth.EMPTY);

        // handle the results
        PrintStream out = cd.getOut();
        if ( info != SVNCommitInfo.NULL ) {
            out.println();
            out.println( "Committed revision " + info.getNewRevision() + "." );
            out.flush();
        }
        else {
            out.println();
            if ( info.getErrorMessage() != null ) {
                out.println("Commit failed:");
                out.println( info.getErrorMessage() );
            }
            else {
                out.println("Commit failed.");
            }
            out.flush();
        }
        out.close();

        CommitInfo ci = new CommitInfo();
        ci.setAuthor(info.getAuthor());
        ci.setDate(info.getDate());
        ci.setRevision(info.getNewRevision());
        ci.setException(info.getErrorMessage() == null ? "" : info.getErrorMessage().toString());

        cd.setPaths(result_paths);
        cd.setInfo(ci);
        return cd;
    }
}
