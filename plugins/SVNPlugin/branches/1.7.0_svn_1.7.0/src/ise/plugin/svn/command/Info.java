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
import java.text.BreakIterator;
import java.text.SimpleDateFormat;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLock;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.util.SVNPathUtil;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;


import ise.plugin.svn.data.SVNData;
import ise.plugin.svn.SVNPlugin;

public class Info {


    public List<SVNInfo> info( SVNData data ) throws CommandInitializationException, SVNException {
        List<SVNInfo> results = getInfo( data );

        // print the results
        for ( SVNInfo info : results ) {
            handleInfo( info, data.getOut() );
        }
        data.getOut().close();
        return results;
    }

    public List<SVNInfo> getInfo( SVNData data ) throws CommandInitializationException, SVNException {
        SVNKit.setupLibrary();

        // validate commit data values
        if ( data.getPaths() == null ) {
            return null;     // nothing to do
        }
        if ( data.getOut() == null ) {
            throw new CommandInitializationException( "Invalid output stream." );
        }
        if ( data.getErr() == null ) {
            data.setErr( data.getOut() );
        }

        // convert first path to File
        List<String> paths = data.getPaths();


        // use the svnkit client manager
        SVNClientManager clientManager;
        if ( data.pathsAreURLs() ) {
            // use default svn config options
            ISVNOptions options = SVNWCUtil.createDefaultOptions( true );

            // need to log in to remote repository for urls
            clientManager = SVNClientManager.newInstance( options, SVNWCUtil.createDefaultAuthenticationManager(SVNPlugin.getSvnStorageDir(),  data.getUsername(), data.getDecryptedPassword() ) );
        }
        else {
            // get info from local working directory
            clientManager = SVNClientManager.newInstance();
        }

        // get a commit client
        SVNWCClient client = clientManager.getWCClient();

        // set an event handler so that messages go to the commit data streams for display
        client.setEventHandler( new SVNCommandEventProcessor( data.getOut(), data.getErr(), false ) );

        // printout the SVNData to the console
        data.getOut().println(SVNData.toString(data));
        
        // actually fetch the info
        List<SVNInfo> results = new ArrayList<SVNInfo>();
        if ( data.pathsAreURLs() ) {
            for ( String path : data.getPaths() ) {
                SVNURL svnurl = SVNURL.parseURIDecoded( path );
                SVNInfo result = client.doInfo( svnurl, SVNRevision.HEAD, SVNRevision.HEAD );
                results.add( result );
            }
        }
        else {
            for ( String path : paths ) {
                File localPath = new File( path );
                SVNInfo result = client.doInfo( localPath, SVNRevision.WORKING );
                results.add( result );
            }
        }

        return results;
    }

    public void handleInfo( SVNInfo info, PrintStream out ) {
        StringBuffer sb = new StringBuffer(512);
        sb.append( "\n" );
        if ( !info.isRemote() ) {
            sb.append( "Path: " + SVNFormatUtil.formatPath( info.getFile() ) + "\n" );
        }
        else if ( info.getPath() != null ) {
            String path = info.getPath();
            path = path.replace( '/', File.separatorChar );
            sb.append( "Path: " + path + "\n" );
        }
        if ( info.getKind() != SVNNodeKind.DIR ) {
            if ( info.isRemote() ) {
                sb.append( "Name: " + SVNPathUtil.tail( info.getPath() ) + "\n" );
            }
            else {
                sb.append( "Name: " + info.getFile().getName() + "\n" );
            }
        }
        sb.append( "URL: " + info.getURL() + "\n" );
        if ( info.getRepositoryRootURL() != null ) {
            sb.append( "Repository Root: " + info.getRepositoryRootURL() + "\n" );
        }
        if ( info.isRemote() && info.getRepositoryUUID() != null ) {
            sb.append( "Repository UUID: " + info.getRepositoryUUID() + "\n" );
        }
        if ( info.getRevision() != null && info.getRevision().isValid() ) {
            sb.append( "Revision: " + info.getRevision() + "\n" );
        }
        if ( info.getKind() == SVNNodeKind.DIR ) {
            sb.append( "Node Kind: directory" + "\n" );
        }
        else if ( info.getKind() == SVNNodeKind.FILE ) {
            sb.append( "Node Kind: file" + "\n" );
        }
        else if ( info.getKind() == SVNNodeKind.NONE ) {
            sb.append( "Node Kind: none" + "\n" );
        }
        else {
            sb.append( "Node Kind: unknown" + "\n" );
        }
        if ( info.getSchedule() == null && !info.isRemote() ) {
            sb.append( "Schedule: normal" + "\n" );
        }
        else if ( !info.isRemote() ) {
            sb.append( "Schedule: " + info.getSchedule() + "\n" );
        }
        if ( info.getAuthor() != null ) {
            sb.append( "Last Changed Author: " + info.getAuthor() + "\n" );
        }
        if ( info.getCommittedRevision() != null && info.getCommittedRevision().getNumber() >= 0 ) {
            sb.append( "Last Changed Rev: " + info.getCommittedRevision() + "\n" );
        }
        if ( info.getCommittedDate() != null ) {
            sb.append( "Last Changed Date: " + formatDate( info.getCommittedDate() ) + "\n" );
        }
        if ( !info.isRemote() ) {
            if ( info.getTextTime() != null ) {
                sb.append( "Text Last Updated: " + formatDate( info.getTextTime() ) + "\n" );
            }
            if ( info.getPropTime() != null ) {
                sb.append( "Properties Last Updated: " + formatDate( info.getPropTime() ) + "\n" );
            }
            if ( info.getChecksum() != null ) {
                sb.append( "Checksum: " + info.getChecksum() + "\n" );
            }
            if ( info.getCopyFromURL() != null ) {
                sb.append( "Copied From URL: " + info.getCopyFromURL() + "\n" );
            }
            if ( info.getCopyFromRevision() != null && info.getCopyFromRevision().getNumber() >= 0 ) {
                sb.append( "Copied From Rev: " + info.getCopyFromRevision() + "\n" );
            }
            if ( info.getConflictOldFile() != null ) {
                sb.append( "Conflict Previous Base File: " + info.getConflictOldFile().getName() + "\n" );
            }
            if ( info.getConflictWrkFile() != null ) {
                sb.append( "Conflict Previous Working File: " + info.getConflictWrkFile().getName() + "\n" );
            }
            if ( info.getConflictNewFile() != null ) {
                sb.append( "Conflict Current Base File: " + info.getConflictNewFile().getName() + "\n" );
            }
            if ( info.getPropConflictFile() != null ) {
                sb.append( "Conflict Properties File: " + info.getPropConflictFile().getName() + "\n" );
            }
        }
        if ( info.getLock() != null ) {
            SVNLock lock = info.getLock();
            sb.append( "Lock Token: " + lock.getID() + "\n" );
            sb.append( "Lock Owner: " + lock.getOwner() + "\n" );
            sb.append( "Lock Created: " + formatDate( lock.getCreationDate() ) + "\n" );
            if ( lock.getComment() != null ) {
                sb.append( "Lock Comment " );
                int lineCount = getLineCount( lock.getComment() );
                if ( lineCount == 1 ) {
                    sb.append( "(1 line)" );
                }
                else {
                    sb.append( "(" + lineCount + " lines)" );
                }
                sb.append( ":\n" + lock.getComment() + "\n" );
            }
        }
        out.println( sb.toString() );
        out.flush();
    }

    private static String formatDate( Date date ) {
        return new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss Z (EE, d MMM yyyy)", Locale.getDefault() ).format( date );
    }

    private int getLineCount( String s ) {
        int count = 1;
        BreakIterator boundary = BreakIterator.getLineInstance();
        boundary.setText( s );
        int start = boundary.first();
        for ( int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary.next() ) {
            ++count;
        }
        return count;
    }

}