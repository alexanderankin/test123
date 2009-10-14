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

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;

import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatusClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.wc.SVNStatus;

import ise.plugin.svn.data.SVNData;
import ise.plugin.svn.data.StatusData;

public class Status {
    
    /**
     * This is a very fast way of getting status. It only checks against the
     * working copy, never remote.  There is no output to the SVN Console, 
     * there is no validation of user credentials.
     * @param path the file to check status of
     */
    public SVNStatus getStatus(File path) {
        SVNKit.setupLibrary();
        SVNClientManager clientManager = SVNClientManager.newInstance();
        SVNStatusClient client = clientManager.getStatusClient();
        SVNStatus status = null;
        try {
            status = client.doStatus(path, false);
        }
        catch(Exception e) {        // NOPMD
            return null;    // unknown status
        }
        return status;
    }

    public StatusData getStatus( SVNData cd ) throws CommandInitializationException, SVNException {
        SVNKit.setupLibrary();

        // validate data values
        if ( cd.getPaths() == null ) {
            return null;     // nothing to do
        }
        
        if ( cd.getOut() == null ) {
            throw new CommandInitializationException( "Invalid output stream." );
        }
        if ( cd.getErr() == null ) {
            cd.setErr( cd.getOut() );
        }

        List<String> paths = cd.getPaths();

        // use default svn config options
        ISVNOptions options = SVNWCUtil.createDefaultOptions( true );

        // use the svnkit client manager
        SVNClientManager clientManager = SVNClientManager.newInstance( options, SVNWCUtil.createDefaultAuthenticationManager( cd.getUsername(), cd.getDecryptedPassword() ) );

        // get a client
        SVNStatusClient client = clientManager.getStatusClient();

        // set an event handler so that messages go to the streams for display
        if ( cd.getOut() != null ) {
            client.setEventHandler( new SVNCommandEventProcessor( cd.getOut(), cd.getErr(), false ) );
        }

        // actually fetch the info
        StatusHandler handler = new StatusHandler( cd.getOut(), true );
        long revision = -1;
        SVNDepth depth = cd.getRecursive() ? SVNDepth.INFINITY : SVNDepth.EMPTY;
        for ( String path : paths ) {
            File localPath = new File( path );
            // svnkit 1.1.8:
            // doStatus(path, recursive, remote, reportAll, includeIgnored, handler)
            // svnkit 1.2.x:
            // doStatus(File path, SVNRevision revision, SVNDepth depth, boolean remote, boolean reportAll, boolean includeIgnored, boolean collectParentExternals, ISVNStatusHandler handler, Collection changeLists)
            try {
                revision = client.doStatus( localPath, SVNRevision.HEAD, depth, cd.getRemote(), true, false, false, handler, null );
            }
            catch ( Exception e ) {
                if ( cd.getRemote() ) {
                    // if disconnected, an error will be thrown if remote is true,
                    // so set remote to false and try again
                    revision = client.doStatus( localPath, SVNRevision.HEAD, depth, false, true, false, false, handler, null );
                }
                else {
                    // if here, then most likely the file isn't under version control at all
                    handler.addErrorFile( path );
                }
            }
        }
        StatusData status_data = handler.getResults();
        status_data.setRevision( revision );
        return status_data;
    }
}