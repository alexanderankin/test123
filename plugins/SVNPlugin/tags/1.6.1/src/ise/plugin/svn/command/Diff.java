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

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.wc.SVNRevision;


import ise.plugin.svn.data.DiffData;
import ise.plugin.svn.SVNPlugin;

/**
 * Does an SVN diff, not a JDiff diff.
 * TODO: Change the paths produced by this diff to be relative to the (project?)
 * root rather than absolute.
 */
public class Diff {

    public String diff( DiffData data ) throws CommandInitializationException, SVNException {
        SVNKit.setupLibrary();

        // validate diff data values
        if ( data.getPaths() == null ) {
            return null;     // nothing to do
        }
        if ( data.getOut() == null ) {
            throw new CommandInitializationException( "Invalid output stream." );
        }
        if ( data.getErr() == null ) {
            data.setErr( data.getOut() );
        }

        // convert paths to Files
        final List<String> paths = data.getPaths();
        File[] localPaths = new File[ paths.size() ];
        for ( int i = 0; i < paths.size(); i++ ) {
            localPaths[ i ] = new File( paths.get( i ) );
            // check for file existence?
        }

        // use default svn config options
        ISVNOptions options = SVNWCUtil.createDefaultOptions( true );

        // use the svnkit client manager
        SVNClientManager clientManager = SVNClientManager.newInstance( options, SVNWCUtil.createDefaultAuthenticationManager(SVNPlugin.getSvnStorageDir(),  data.getUsername(), data.getDecryptedPassword() ) );

        // get a diff client
        SVNDiffClient client = clientManager.getDiffClient();

        // set an event handler so that messages go to the diff data streams for display
        // and gather the paths actually diffted
        final List<String> result_paths = new ArrayList<String>();
        client.setEventHandler(
            new SVNCommandEventProcessor( data.getOut(), data.getErr(), false ) {
                @Override
                public void handleEvent( SVNEvent event, double progress ) {
                    super.handleEvent( event, progress );
                    if ( event.getFile() != null ) {
                        result_paths.add( event.getFile().toString() );
                    }
                }
            }
        );

        // actually do the diff
        // File, rev, file, rev -- TODO
        // File, rev, rev -- from PV, working file against revision
        // File, rev, URL, rev -- TODO
        // URL, rev, rev -- TODO from SVN Browser, remote file against revision
        // URL, rev, URL, rev -- TODO
        // for all, use ancestry = false

        // where to put the resulting output
        ByteArrayOutputStream diff_output = new ByteArrayOutputStream();
        if ( data.getRevision1() == null ) {
            data.setRevision1( SVNRevision.HEAD );
        }
        if ( data.getRevision2() == null ) {
            data.setRevision2( SVNRevision.WORKING );
        }
        if ( data.pathsAreURLs() ) {
            // URL, rev, rev -- from SVN Browser, remote file against revision
            /// TODO:
            //client.doDiff( SVNURL.parseURIDecoded( paths.get( 0 ) ), SVNRevision.create( 0 ), data.getRevision1(), data.getRevision2(), false, false, diff_output );
        }
        else {
            // File, rev, rev -- from PV, working file against revision
            for ( File file : localPaths ) {
                client.doDiff( file, SVNRevision.UNDEFINED, data.getRevision1(), data.getRevision2(), SVNDepth.INFINITY, false, diff_output, ( Collection ) null );
            }
        }
        return diff_output.toString();
    }
}