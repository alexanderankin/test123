package ise.plugin.svn.command;

import java.io.*;
import java.util.*;
import java.text.BreakIterator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.tmatesoft.svn.cli.command.SVNCommandEventProcessor;
import org.tmatesoft.svn.cli.SVNArgument;
import org.tmatesoft.svn.cli.SVNCommand;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLock;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.util.SVNFormatUtil;
import org.tmatesoft.svn.core.internal.util.SVNPathUtil;
import org.tmatesoft.svn.core.wc.ISVNInfoHandler;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.wc.xml.SVNXMLInfoHandler;
import org.tmatesoft.svn.core.wc.xml.SVNXMLSerializer;

public class Info {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss Z (EE, d MMM yyyy)", Locale.getDefault() );

    public void info( CommitData cd ) throws CommandInitializationException, SVNException {
        SVNInfo info = getInfo( cd );

        // print the results
        handleInfo( info, cd.getOut() );
    }

    public SVNInfo getInfo( CommitData cd ) throws CommandInitializationException, SVNException {

        // validate commit data values
        if ( cd.getPaths() == null ) {
            return null;     // nothing to do
        }
        if ( cd.getOut() == null ) {
            throw new CommandInitializationException( "Invalid output stream." );
        }
        if ( cd.getErr() == null ) {
            cd.setErr( cd.getOut() );
        }

        // convert first path to File -- add support for multiple files?
        List<String> paths = cd.getPaths();
        File localPath = new File( paths.get( 0 ) );

        // use default svn config options
        ISVNOptions options = SVNWCUtil.createDefaultOptions( true );

        // use the svnkit client manager
        SVNClientManager clientManager = SVNClientManager.newInstance( options, cd.getUsername(), cd.getPassword() );

        // get a commit client
        SVNWCClient client = clientManager.getWCClient();

        // set an event handler so that messages go to the commit data streams for display
        client.setEventHandler( new SVNCommandEventProcessor( cd.getOut(), cd.getErr(), false ) );

        // actually fetch the info
        SVNInfo result = client.doInfo( localPath, SVNRevision.HEAD );

        return result;
    }

    public void handleInfo( SVNInfo info, PrintStream out ) {
        if ( !info.isRemote() ) {
            out.println( "Path: " + SVNFormatUtil.formatPath( info.getFile() ) );
        }
        else if ( info.getPath() != null ) {
            String path = info.getPath();
            path = path.replace( '/', File.separatorChar );
            out.println( "Path: " + path );
        }
        if ( info.getKind() != SVNNodeKind.DIR ) {
            if ( info.isRemote() ) {
                out.println( "Name: " + SVNPathUtil.tail( info.getPath() ) );
            }
            else {
                out.println( "Name: " + info.getFile().getName() );
            }
        }
        out.println( "URL: " + info.getURL() );
        if ( info.getRepositoryRootURL() != null ) {
            out.println( "Repository Root: " + info.getRepositoryRootURL() );
        }
        if ( info.isRemote() && info.getRepositoryUUID() != null ) {
            out.println( "Repository UUID: " + info.getRepositoryUUID() );
        }
        if ( info.getRevision() != null && info.getRevision().isValid() ) {
            out.println( "Revision: " + info.getRevision() );
        }
        if ( info.getKind() == SVNNodeKind.DIR ) {
            out.println( "Node Kind: directory" );
        }
        else if ( info.getKind() == SVNNodeKind.FILE ) {
            out.println( "Node Kind: file" );
        }
        else if ( info.getKind() == SVNNodeKind.NONE ) {
            out.println( "Node Kind: none" );
        }
        else {
            out.println( "Node Kind: unknown" );
        }
        if ( info.getSchedule() == null && !info.isRemote() ) {
            out.println( "Schedule: normal" );
        }
        else if ( !info.isRemote() ) {
            out.println( "Schedule: " + info.getSchedule() );
        }
        if ( info.getAuthor() != null ) {
            out.println( "Last Changed Author: " + info.getAuthor() );
        }
        if ( info.getCommittedRevision() != null && info.getCommittedRevision().getNumber() >= 0 ) {
            out.println( "Last Changed Rev: " + info.getCommittedRevision() );
        }
        if ( info.getCommittedDate() != null ) {
            out.println( "Last Changed Date: " + formatDate( info.getCommittedDate() ) );
        }
        if ( !info.isRemote() ) {
            if ( info.getTextTime() != null ) {
                out.println( "Text Last Updated: " + formatDate( info.getTextTime() ) );
            }
            if ( info.getPropTime() != null ) {
                out.println( "Properties Last Updated: " + formatDate( info.getPropTime() ) );
            }
            if ( info.getChecksum() != null ) {
                out.println( "Checksum: " + info.getChecksum() );
            }
            if ( info.getCopyFromURL() != null ) {
                out.println( "Copied From URL: " + info.getCopyFromURL() );
            }
            if ( info.getCopyFromRevision() != null && info.getCopyFromRevision().getNumber() >= 0 ) {
                out.println( "Copied From Rev: " + info.getCopyFromRevision() );
            }
            if ( info.getConflictOldFile() != null ) {
                out.println( "Conflict Previous Base File: " + info.getConflictOldFile().getName() );
            }
            if ( info.getConflictWrkFile() != null ) {
                out.println( "Conflict Previous Working File: " + info.getConflictWrkFile().getName() );
            }
            if ( info.getConflictNewFile() != null ) {
                out.println( "Conflict Current Base File: " + info.getConflictNewFile().getName() );
            }
            if ( info.getPropConflictFile() != null ) {
                out.println( "Conflict Properties File: " + info.getPropConflictFile().getName() );
            }
        }
        if ( info.getLock() != null ) {
            SVNLock lock = info.getLock();
            out.println( "Lock Token: " + lock.getID() );
            out.println( "Lock Owner: " + lock.getOwner() );
            out.println( "Lock Created: " + formatDate( lock.getCreationDate() ) );
            if ( lock.getComment() != null ) {
                out.println( "Lock Comment " );
                int lineCount = getLineCount( lock.getComment() );
                if ( lineCount == 1 ) {
                    out.println( "(1 line)" );
                }
                else {
                    out.println( "(" + lineCount + " lines)" );
                }
                out.println( ":\n" + lock.getComment() + "\n" );
            }
        }
        out.flush();
        out.close();
    }

    private static String formatDate( Date date ) {
        return DATE_FORMAT.format( date );
    }

    private int getLineCount( String s ) {
        int count = 0;
        BreakIterator boundary = BreakIterator.getLineInstance();
        int start = boundary.first();
        for ( int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary.next() ) {
            ++count;
        }
        return count;
    }

}
