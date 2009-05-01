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
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;

import org.tmatesoft.svn.core.SVNException;

import ise.plugin.svn.data.UpdateData;


public class Update {


    /**
     * Performs an update on the paths provided by the SVNData object.
     * @param data the data needed by svn to perform an update, must have paths
     * and output stream set at minimum.
     * @return an UpdateData containing a list of the updated files
     */
    public UpdateData doUpdate( UpdateData data ) throws CommandInitializationException, SVNException {
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

        // convert paths to Files
        boolean recursive = false;
        List<String> paths = data.getPaths();
        File[] localPaths = new File[ paths.size() ];
        for ( int i = 0; i < paths.size(); i++ ) {
            localPaths[ i ] = new File( paths.get( i ) );
            // check for file existence?
            if ( localPaths[ i ].isDirectory() ) {
                recursive = true;
            }
        }

        // use default svn config options
        ISVNOptions options = SVNWCUtil.createDefaultOptions( true );

        // use the svnkit client manager
        SVNClientManager clientManager = SVNClientManager.newInstance( options, new BasicAuthenticationManager(data.getUsername(), data.getDecryptedPassword()) );

        // get a commit client
        SVNUpdateClient client = clientManager.getUpdateClient();

        // set an event handler so that messages go to the streams for display
        UpdateEventHandler handler = new UpdateEventHandler( data );
        client.setEventHandler( handler );

        PrintStream out = data.getOut();
        long revision = -1;

        SVNDepth depth = data.getRecursive() ? SVNDepth.INFINITY : SVNDepth.EMPTY;
        for ( File file : localPaths ) {
            revision = client.doUpdate( file, data.getSVNRevision(), depth, false, false );
        }

        out.flush();
        out.close();

        // fetch the accumulated data from the handler
        data = handler.getData();
        data.setRevision( revision );

        return data;
    }
}
