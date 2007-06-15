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


public class Update {

    private TreeMap<String, String> entries = new TreeMap<String, String>();

    private PrintStream out = null;

    /**
     * Performs an update on the paths provided by the SVNData object.
     * @param cd the data needed by svn to perform an update, must have paths
     * and output stream set at minimum.
     * @return TreeMap<String, String> containing path and revision for updated files
     */
    public TreeMap<String, String> doUpdate( SVNData cd ) throws CommandInitializationException, SVNException {

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

        // set an event handler so that messages go to the commit data streams for display
        client.setEventHandler( new SVNCommandEventProcessor( cd.getOut(), cd.getErr(), false ) );

        out = cd.getOut();

        for ( File file : localPaths ) {
            long revision = client.doUpdate(file, SVNRevision.HEAD, recursive);
            entries.put(file.toString(), String.valueOf(revision));
        }

        out.flush();
        out.close();

        return entries;
    }
}
