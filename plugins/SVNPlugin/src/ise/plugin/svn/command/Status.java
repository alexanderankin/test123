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
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusClient;

import ise.plugin.svn.data.CommitData;

public class Status {

    public List<SVNStatus> getStatus( CommitData cd ) throws CommandInitializationException, SVNException {

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
        SVNClientManager clientManager = SVNClientManager.newInstance( options, cd.getUsername(), cd.getPassword() );

        // get a commit client
        SVNStatusClient client = clientManager.getStatusClient();

        // set an event handler so that messages go to the commit data streams for display
        client.setEventHandler( new SVNCommandEventProcessor( cd.getOut(), cd.getErr(), false ) );

        // actually fetch the info
        List<SVNStatus> results = new ArrayList<SVNStatus>();
        for ( String path : paths ) {
            File localPath = new File( path );
            SVNStatus result = client.doStatus( localPath, true );
            results.add( result );
        }
        return results;
    }
}
