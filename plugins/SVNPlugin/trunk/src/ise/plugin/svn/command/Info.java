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

    private static SVNClientManager _clientManager = null;
    private static SVNWCClient _client = null;
    public static boolean isWorkingCopy( File path ) {
        if (_client == null) {
            SVNKit.setupLibrary();
            _clientManager = SVNClientManager.newInstance();
            _client = _clientManager.getWCClient();
        }
        try {
            SVNInfo info = _client.doInfo(path, SVNRevision.WORKING);
            return info != null;
        }
        catch(SVNException e) {        // NOPMD
            return false;
        }
        finally {
            _clientManager.dispose();   
        }
    }

    public List<SVNInfo> info( SVNData data ) throws CommandInitializationException, SVNException {
        List<SVNInfo> results = getInfo( data );

        // print the results
        for ( SVNInfo info : results ) {
            handleInfo( info, data.getOut() );
        }
        data.getOut().close();
        return results;
    }

    @SuppressWarnings("deprecation")    // SVNURL.parseURIEncoded
    public List<SVNInfo> getInfo( SVNData data ) throws CommandInitializationException, SVNException {
        SVNKit.setupLibrary();

        // validate commit data values
        if ( data.getPaths() == null ) {
            return null;            // nothing to do
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
            clientManager = SVNClientManager.newInstance( options, SVNWCUtil.createDefaultAuthenticationManager( SVNPlugin.getSvnStorageDir(), data.getUsername(), data.getDecryptedPassword() ) );
        } else {
            // get info from local working directory
            clientManager = SVNClientManager.newInstance();
        }

        // get a commit client
        SVNWCClient client = clientManager.getWCClient();

        // set an event handler so that messages go to the commit data streams for display
        client.setEventHandler( new SVNCommandEventProcessor( data.getOut(), data.getErr(), false ) );

        // printout the SVNData to the console
        data.getOut().println( SVNData.toString( data ) );

        // actually fetch the info
        List<SVNInfo> results = new ArrayList<SVNInfo>();
        if ( data.pathsAreURLs() ) {
            for ( String path : data.getPaths() ) {
                SVNURL svnurl = SVNURL.parseURIDecoded( path );
                SVNInfo result = client.doInfo( svnurl, SVNRevision.HEAD, SVNRevision.HEAD );
                results.add( result );
            }
        } else {
            for ( String path : paths ) {
                File localPath = new File( path );
                SVNInfo result = client.doInfo( localPath, SVNRevision.WORKING );
                results.add( result );
            }
        }
        
        clientManager.dispose();

        return results;
    }

    public void handleInfo( SVNInfo info, PrintStream out ) {
        StringBuffer sb = new StringBuffer(512 );
        sb.append( '\n' );
        if ( !info.isRemote() ) {
            sb.append( "Path: ").append(SVNFormatUtil.formatPath( info.getFile() )).append('\n' );
        } else if ( info.getPath() != null ) {
            String path = info.getPath();
            path = path.replace( '/', File.separatorChar );
            sb.append( "Path: ").append(path).append('\n');
        }
        if ( info.getKind() != SVNNodeKind.DIR ) {
            if ( info.isRemote() ) {
                sb.append( "Name: ").append(SVNPathUtil.tail( info.getPath() )).append('\n');
            } else {
                sb.append( "Name: ").append(info.getFile().getName()).append('\n');
            }
        }
        sb.append( "URL: ").append(info.getURL()).append('\n');
        if ( info.getRepositoryRootURL() != null ) {
            sb.append( "Repository Root: ").append(info.getRepositoryRootURL()).append('\n');
        }
        if ( info.isRemote() && info.getRepositoryUUID() != null ) {
            sb.append( "Repository UUID: ").append(info.getRepositoryUUID()).append('\n');
        }
        if ( info.getRevision() != null && info.getRevision().isValid() ) {
            sb.append( "Revision: ").append(info.getRevision()).append('\n');
        }
        if ( info.getKind() == SVNNodeKind.DIR ) {
            sb.append( "Node Kind: directory\n" );
        } else if ( info.getKind() == SVNNodeKind.FILE ) {
            sb.append( "Node Kind: file\n" );
        } else if ( info.getKind() == SVNNodeKind.NONE ) {
            sb.append( "Node Kind: none\n" );
        } else {
            sb.append( "Node Kind: unknown\n" );
        }
        if ( info.getSchedule() == null && !info.isRemote() ) {
            sb.append( "Schedule: normal\n" );
        } else if ( !info.isRemote() ) {
            sb.append( "Schedule: ").append(info.getSchedule()).append('\n');
        }
        if ( info.getAuthor() != null ) {
            sb.append( "Last Changed Author: ").append(info.getAuthor()).append('\n');
        }
        if ( info.getCommittedRevision() != null && info.getCommittedRevision().getNumber() >= 0 ) {
            sb.append( "Last Changed Rev: ").append(info.getCommittedRevision()).append('\n');
        }
        if ( info.getCommittedDate() != null ) {
            sb.append( "Last Changed Date: ").append(formatDate( info.getCommittedDate() )).append('\n');
        }
        if ( !info.isRemote() ) {
            if ( info.getTextTime() != null ) {
                sb.append( "Text Last Updated: ").append(formatDate( info.getTextTime() )).append('\n' );
            }
            if ( info.getPropTime() != null ) {
                sb.append( "Properties Last Updated: ").append(formatDate( info.getPropTime() )).append( '\n' );
            }
            if ( info.getChecksum() != null ) {
                sb.append( "Checksum: ").append(info.getChecksum()).append('\n');
            }
            if ( info.getCopyFromURL() != null ) {
                sb.append( "Copied From URL: ").append( info.getCopyFromURL() ).append( '\n' );
            }
            if ( info.getCopyFromRevision() != null && info.getCopyFromRevision().getNumber() >= 0 ) {
                sb.append( "Copied From Rev: " ).append( info.getCopyFromRevision() ).append( '\n' );
            }
            if ( info.getConflictOldFile() != null ) {
                sb.append( "Conflict Previous Base File: " ).append( info.getConflictOldFile().getName() ).append( '\n' );
            }
            if ( info.getConflictWrkFile() != null ) {
                sb.append( "Conflict Previous Working File: " ).append( info.getConflictWrkFile().getName() ).append( '\n' );
            }
            if ( info.getConflictNewFile() != null ) {
                sb.append( "Conflict Current Base File: " ).append( info.getConflictNewFile().getName() ).append( '\n' );
            }
            if ( info.getPropConflictFile() != null ) {
                sb.append( "Conflict Properties File: " ).append( info.getPropConflictFile().getName() ).append( '\n' );
            }
        }
        if ( info.getLock() != null ) {
            SVNLock lock = info.getLock();
            sb.append( "Lock Token: " ).append( lock.getID() );
            sb.append( "\nLock Owner: " ).append( lock.getOwner() );
            sb.append( "\nLock Created: " ).append( formatDate( lock.getCreationDate() ) ).append( '\n' );
            if ( lock.getComment() != null ) {
                sb.append( "Lock Comment " );
                int lineCount = getLineCount( lock.getComment() );
                if ( lineCount == 1 ) {
                    sb.append( "(1 line)" );
                } else {
                    sb.append( '(' ).append( lineCount ).append( " lines)" );
                }
                sb.append( ":\n" ).append( lock.getComment() ).append( '\n' );
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
        for ( int end = boundary.next(); end != BreakIterator.DONE; end = boundary.next() ) {
            ++count;
        }
        return count;
    }

}