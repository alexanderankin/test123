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

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;


import ise.plugin.svn.data.*;
import ise.plugin.svn.SVNPlugin;

/**
 * Lock and unlock commands.
 */
public class Lock {

    public LockResults lock( CommitData data ) throws CommandInitializationException, SVNException {
        return doLock( data, true );
    }
    public LockResults unlock( CommitData data ) throws CommandInitializationException, SVNException {
        return doLock( data, false );
    }
    private LockResults doLock( CommitData data, boolean lock ) throws CommandInitializationException, SVNException {
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
        SVNClientManager clientManager = SVNClientManager.newInstance( options, SVNWCUtil.createDefaultAuthenticationManager(SVNPlugin.getSvnStorageDir(), data.getUsername(), data.getDecryptedPassword() ));

        // get a working copy client
        SVNWCClient client = clientManager.getWCClient();

        // set an event handler so that messages go to the output streams for display
        client.setEventHandler( new SVNCommandEventProcessor( data.getOut(), data.getErr(), false ) );

        // actually do the lock
        PrintStream out = data.getOut();
        LockResults results = new LockResults();
        try {
            if ( data.pathsAreURLs() ) {
                SVNURL[] urls = new SVNURL[ data.getPaths().size() ];
                for ( int i = 0; i < data.getPaths().size(); i++ ) {
                    urls[ i ] = SVNURL.parseURIDecoded( data.getPaths().get( i ) );
                }
                if ( lock ) {
                    client.doLock( urls, data.getForce(), data.getCommitMessage() );
                }
                else {
                    client.doUnlock( urls, data.getForce() );
                }
            }
            else {
                File[] files = new File[ data.getPaths().size() ];
                for ( int i = 0; i < data.getPaths().size(); i++ ) {
                    files[ i ] = new File( data.getPaths().get( i ) );
                }
                if ( lock ) {
                    client.doLock( files, data.getForce(), data.getCommitMessage() );
                }
                else {
                    client.doUnlock( files, data.getForce() );
                }
            }
            results.addPaths( data.getPaths() );
        }
        catch ( Exception e ) {
            out.println( e.getMessage() );
            results.addErrorPath( "?", e.getMessage() );
        }

        out.flush();
        out.close();
        return results;
    }
}
