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

import org.tmatesoft.svn.cli.command.SVNCommandEventProcessor;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCopyClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import ise.plugin.svn.data.CopyData;

/**
 * Used to copy or move either a working file to another working file or to the
 * repository, or to move a repository file or directory to a working file or to
 * another repository location.
 */
public class Copy {

    public SVNCommitInfo copy( CopyData data ) throws CommandInitializationException, SVNException {

        // validate data values
        if ( data.getOut() == null ) {
            throw new CommandInitializationException( "Invalid output stream." );
        }
        if ( data.getErr() == null ) {
            data.setErr( data.getOut() );
        }

        // use default svn config options
        ISVNOptions options = SVNWCUtil.createDefaultOptions( true );

        // use the svnkit client manager
        SVNClientManager clientManager = SVNClientManager.newInstance( options, data.getUsername(), data.getPassword() );

        // get a copy client
        SVNCopyClient client = clientManager.getCopyClient();

        // set an event handler so that messages go to the commit data streams for display
        client.setEventHandler( new SVNCommandEventProcessor( data.getOut(), data.getErr(), false ) );

        // actually do the copy
        PrintStream out = data.getOut();
        File sourceFile = data.getSourceFile();
        File destinationFile = data.getDestinationFile();
        SVNURL sourceURL = data.getSourceURL();
        SVNURL destinationURL = data.getDestinationURL();
        SVNCommitInfo results = null;

        if (sourceFile != null && destinationFile != null) {
            // copy working copy to working copy, this is a local move
            SVNRevision revision = data.getRevision();
            if (revision == null) {
                revision = SVNRevision.WORKING;
            }
            client.doCopy(sourceFile, revision, destinationFile, data.getForce(), data.getIsMove());
        }
        else if (sourceFile != null && destinationURL != null) {
            // copy working copy to repository with immediate commit, this can
            // be used to make a branch or tag
            SVNRevision revision = data.getRevision();
            if (revision == null) {
                revision = SVNRevision.WORKING;
            }
            results = client.doCopy(sourceFile, revision, destinationURL, !data.getForce(), data.getMessage());
        }
        else if (sourceURL != null && destinationURL != null) {
            // copy a repository file or directory to another repository file or
            // directory with immediate commit, this can also be used to make a
            // branch or tag
            SVNRevision revision = data.getRevision();
            if (revision == null) {
                revision = SVNRevision.WORKING;
            }
            results = client.doCopy(sourceURL, revision, destinationURL, data.getIsMove(), !data.getForce(), data.getMessage());
        }
        else if (sourceURL != null && destinationFile != null) {
            // copy a file or directory from the repository to a local working
            // copy, this can be used for an undelete.
            SVNRevision revision = data.getRevision();
            if (revision == null) {
                revision = SVNRevision.WORKING;
            }
            client.doCopy(sourceURL, revision, destinationFile);
        }
        else {
            throw new CommandInitializationException("invalid file and/or URL parameters");
        }

        out.flush();
        out.close();
        return results;
    }
}
