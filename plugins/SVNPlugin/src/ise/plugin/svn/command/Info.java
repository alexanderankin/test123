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

    public List<SVNInfo> info( CommitData cd ) throws CommandInitializationException, SVNException {
        List<SVNInfo> results = getInfo( cd );

        // print the results
        for ( SVNInfo info : results ) {
            handleInfo( info, cd.getOut() );
        }
        cd.getOut().close();
        return results;
    }

    public List<SVNInfo> getInfo( CommitData cd ) throws CommandInitializationException, SVNException {

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

        // convert first path to File
        List<String> paths = cd.getPaths();

        // use default svn config options
        ISVNOptions options = SVNWCUtil.createDefaultOptions( true );

        // use the svnkit client manager
        SVNClientManager clientManager = SVNClientManager.newInstance( options, cd.getUsername(), cd.getPassword() );

        // get a commit client
        SVNWCClient client = clientManager.getWCClient();

        // set an event handler so that messages go to the commit data streams for display
        client.setEventHandler( new SVNCommandEventProcessor( cd.getOut(), cd.getErr(), false ) );

        // actually fetch the info
        List<SVNInfo> results = new ArrayList<SVNInfo>();
        for ( String path : paths ) {
            File localPath = new File( path );
            SVNInfo result = client.doInfo( localPath, SVNRevision.HEAD );
            results.add( result );
        }
        return results;
    }

    public void handleInfo( SVNInfo info, PrintStream out ) {
        StringBuffer sb = new StringBuffer();
        sb.append( "\n" );
        if ( !info.isRemote() ) {
            sb.append( "Path: " + SVNFormatUtil.formatPath( info.getFile() )  + "\n" );
        }
        else if ( info.getPath() != null ) {
            String path = info.getPath();
            path = path.replace( '/', File.separatorChar );
            sb.append( "Path: " + path  + "\n" );
        }
        if ( info.getKind() != SVNNodeKind.DIR ) {
            if ( info.isRemote() ) {
                sb.append( "Name: " + SVNPathUtil.tail( info.getPath() )  + "\n" );
            }
            else {
                sb.append( "Name: " + info.getFile().getName()  + "\n" );
            }
        }
        sb.append( "URL: " + info.getURL()  + "\n" );
        if ( info.getRepositoryRootURL() != null ) {
            sb.append( "Repository Root: " + info.getRepositoryRootURL()  + "\n" );
        }
        if ( info.isRemote() && info.getRepositoryUUID() != null ) {
            sb.append( "Repository UUID: " + info.getRepositoryUUID()  + "\n" );
        }
        if ( info.getRevision() != null && info.getRevision().isValid() ) {
            sb.append( "Revision: " + info.getRevision()  + "\n" );
        }
        if ( info.getKind() == SVNNodeKind.DIR ) {
            sb.append( "Node Kind: directory"  + "\n" );
        }
        else if ( info.getKind() == SVNNodeKind.FILE ) {
            sb.append( "Node Kind: file"  + "\n" );
        }
        else if ( info.getKind() == SVNNodeKind.NONE ) {
            sb.append( "Node Kind: none"  + "\n" );
        }
        else {
            sb.append( "Node Kind: unknown"  + "\n" );
        }
        if ( info.getSchedule() == null && !info.isRemote() ) {
            sb.append( "Schedule: normal"  + "\n" );
        }
        else if ( !info.isRemote() ) {
            sb.append( "Schedule: " + info.getSchedule()  + "\n" );
        }
        if ( info.getAuthor() != null ) {
            sb.append( "Last Changed Author: " + info.getAuthor()  + "\n" );
        }
        if ( info.getCommittedRevision() != null && info.getCommittedRevision().getNumber() >= 0 ) {
            sb.append( "Last Changed Rev: " + info.getCommittedRevision() + "\n"  );
        }
        if ( info.getCommittedDate() != null ) {
            sb.append( "Last Changed Date: " + formatDate( info.getCommittedDate() ) + "\n"  );
        }
        if ( !info.isRemote() ) {
            if ( info.getTextTime() != null ) {
                sb.append( "Text Last Updated: " + formatDate( info.getTextTime() )  + "\n" );
            }
            if ( info.getPropTime() != null ) {
                sb.append( "Properties Last Updated: " + formatDate( info.getPropTime() ) + "\n"  );
            }
            if ( info.getChecksum() != null ) {
                sb.append( "Checksum: " + info.getChecksum()  + "\n" );
            }
            if ( info.getCopyFromURL() != null ) {
                sb.append( "Copied From URL: " + info.getCopyFromURL()  + "\n" );
            }
            if ( info.getCopyFromRevision() != null && info.getCopyFromRevision().getNumber() >= 0 ) {
                sb.append( "Copied From Rev: " + info.getCopyFromRevision()  + "\n" );
            }
            if ( info.getConflictOldFile() != null ) {
                sb.append( "Conflict Previous Base File: " + info.getConflictOldFile().getName()  + "\n" );
            }
            if ( info.getConflictWrkFile() != null ) {
                sb.append( "Conflict Previous Working File: " + info.getConflictWrkFile().getName() + "\n"  );
            }
            if ( info.getConflictNewFile() != null ) {
                sb.append( "Conflict Current Base File: " + info.getConflictNewFile().getName()  + "\n" );
            }
            if ( info.getPropConflictFile() != null ) {
                sb.append( "Conflict Properties File: " + info.getPropConflictFile().getName()  + "\n" );
            }
        }
        if ( info.getLock() != null ) {
            SVNLock lock = info.getLock();
            sb.append( "Lock Token: " + lock.getID() + "\n"  );
            sb.append( "Lock Owner: " + lock.getOwner() + "\n"  );
            sb.append( "Lock Created: " + formatDate( lock.getCreationDate() )  + "\n" );
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
        out.println(sb.toString());
        out.flush();
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
