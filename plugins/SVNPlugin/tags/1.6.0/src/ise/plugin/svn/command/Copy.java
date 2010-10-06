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
import org.tmatesoft.svn.core.wc.SVNCopyClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import org.tmatesoft.svn.core.wc.SVNCopySource;

import ise.plugin.svn.data.CopyData;
import ise.plugin.svn.SVNPlugin;

/**
 * Used to copy or move either a working file to another working file or to the
 * repository, or to move a repository file or directory to a working file or to
 * another repository location.  To recap, this class can copy:
 * working copy -> working copy
 * working copy -> repository
 * repository -> working copy
 * repository -> repository
 */
public class Copy {

    public SVNCommitInfo copy( CopyData data ) throws CommandInitializationException, SVNException {
        SVNKit.setupLibrary();

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
        SVNClientManager clientManager = SVNClientManager.newInstance( options, SVNWCUtil.createDefaultAuthenticationManager(SVNPlugin.getSvnStorageDir(),  data.getUsername(), data.getDecryptedPassword() ) );

        // get a copy client
        SVNCopyClient client = clientManager.getCopyClient();

        // set an event handler so that messages go to the commit data streams for display
        client.setEventHandler( new SVNCommandEventProcessor( data.getOut(), data.getErr(), false ) );

        // actually do the copy
        PrintStream out = data.getOut();
        SVNCopySource[] sourceFiles = data.getSourceFiles();
        File destinationFile = data.getDestinationFile();
        SVNCopySource[] sourceURLs = data.getSourceURLs();
        SVNURL destinationURL = data.getDestinationURL();
        SVNCommitInfo results = SVNCommitInfo.NULL;

        try {
            if ( sourceFiles != null && destinationFile != null ) {
                // copy working copy to working copy, this is a local copy or move
                SVNRevision revision = data.getRevision();
                if ( revision == null ) {
                    revision = SVNRevision.WORKING;
                }
                // message on local copy
                out.println( "source file(s): " );
                for ( SVNCopySource source : sourceFiles ) {
                    out.println( "\t" + source.getFile().getAbsolutePath() );
                }
                out.println( "revision: " + revision );

                // TODO: put hard-coded parameters in copy data
                client.doCopy( sourceFiles, destinationFile, data.getIsMove(), true, false );
            }
            else if ( sourceFiles != null && destinationURL != null ) {
                // copy working copy to repository with immediate commit, this can
                // be used to make a branch or tag
                SVNRevision revision = data.getRevision();
                if ( revision == null ) {
                    revision = SVNRevision.WORKING;
                }
                // TODO: put hard-coded parameters in copy data
                results = client.doCopy( sourceFiles, destinationURL, data.getIsMove(), true, false, data.getMessage(), null );
            }
            else if ( sourceURLs != null && destinationURL != null ) {
                // copy a repository file or directory to another repository file or
                // directory with immediate commit, this could be used to make a
                // branch or tag
                SVNRevision revision = data.getRevision();
                if ( revision == null ) {
                    revision = SVNRevision.HEAD;
                }
                // TODO: put hard-coded parameters in copy data
                // SVNCommitInfo doCopy(SVNCopySource[] sources, SVNURL dst, boolean isMove, boolean makeParents, boolean failWhenDstExists, String commitMessage, SVNProperties revisionProperties)
                results = client.doCopy( sourceURLs, destinationURL, data.getIsMove(), true, false, data.getMessage(), null );
            }
            else if ( sourceURLs != null && destinationFile != null ) {
                // copy a file or directory from the repository to a local working
                // copy, this can be used for an undelete.
                SVNRevision revision = data.getRevision();
                if ( revision == null ) {
                    revision = SVNRevision.WORKING;
                }
                // no message on copy to local

                // TODO: put hard-coded parameters in copy data
                client.doCopy( sourceURLs, destinationFile, data.getIsMove(), true, false );
            }
            else {
                StringBuilder sb = new StringBuilder();
                sb.append( "Invalid file and/or URL parameters:\n" );
                sb.append( "sourceFile(s) = " ).append( "\n" );
                for ( SVNCopySource source : sourceFiles ) {
                    sb.append( "\t" ).append( source.getFile().getAbsolutePath() ).append( "\n" );
                }
                sb.append( "sourceURL(s) = " ).append( "\n" );
                for ( SVNCopySource source : sourceURLs ) {
                    sb.append( "\t" ).append( source.getURL() ).append( "\n" );
                }
                sb.append( "destinationFile = " ).append( destinationFile ).append( "\n" );
                sb.append( "destinationURL = " ).append( destinationURL ).append( "\n" );
                throw new CommandInitializationException( sb.toString() );
            }

            if ( results != null && results.equals( SVNCommitInfo.NULL ) ) {
                // the commit didn't work, let the user know
                out.println(results.getErrorMessage());
            }
        }
        catch(Exception e) {
            // just throw the exception, the copy action will handle it.
            throw new CommandInitializationException(e);
        }
        finally {
            out.flush();
            out.close();
        }
        
        return results;
    }
}