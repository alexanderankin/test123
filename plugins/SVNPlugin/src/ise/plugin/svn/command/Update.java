package ise.plugin.svn.command;

import java.io.*;
import java.util.*;

import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.cli.command.SVNCommandEventProcessor;

import org.tmatesoft.svn.core.SVNException;

import ise.plugin.svn.data.SVNData;
import ise.plugin.svn.data.UpdateData;


public class Update {


    /**
     * Performs an update on the paths provided by the SVNData object.
     * @param cd the data needed by svn to perform an update, must have paths
     * and output stream set at minimum.
     * @return an UpdateData containing a list of the updated files
     */
    public UpdateData doUpdate( SVNData cd ) throws CommandInitializationException, SVNException {

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

        // convert paths to Files
        boolean recursive = false;
        List<String> paths = cd.getPaths();
        File[] localPaths = new File[ paths.size() ];
        for ( int i = 0; i < paths.size(); i++ ) {
            localPaths[ i ] = new File( paths.get( i ) );
            // check for file existence?
            if (localPaths[i].isDirectory()) {
                recursive = true;
            }
        }

        // use default svn config options
        ISVNOptions options = SVNWCUtil.createDefaultOptions( true );

        // use the svnkit client manager
        SVNClientManager clientManager = SVNClientManager.newInstance( options, cd.getUsername(), cd.getPassword() );

        // get a commit client
        SVNUpdateClient client = clientManager.getUpdateClient();

        // set an event handler so that messages go to the streams for display
        UpdateEventHandler handler = new UpdateEventHandler(cd.getOut(), cd.getErr());
        client.setEventHandler( handler );

        PrintStream out = cd.getOut();
        long revision = -1;

        for ( File file : localPaths ) {
            revision = client.doUpdate(file, SVNRevision.HEAD, recursive);
        }

        out.flush();
        out.close();

        // fetch the accumulated data from the handler
        UpdateData data = handler.getData();
        data.setRevision(revision);

        return data;
    }
}
