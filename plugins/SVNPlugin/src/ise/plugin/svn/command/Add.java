package ise.plugin.svn.command;

import java.io.*;
import java.util.*;

import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.cli.command.SVNCommandEventProcessor;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import ise.plugin.svn.data.SVNData;
import ise.plugin.svn.data.AddResults;

/**
 * Performs a subversion "add" command.
 */
public class Add {

    /**
     * @param cd SVNData containing a list of paths to be added
     * @return a list of paths that were scheduled to be added.
     */
    public AddResults add( SVNData cd ) throws CommandInitializationException, SVNException {

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
        List<String> paths = cd.getPaths();
        File[] localPaths = new File[ paths.size() ];
        for ( int i = 0; i < paths.size(); i++ ) {
            localPaths[ i ] = new File( paths.get( i ) );
            // check for file existence?
        }

        // use default svn config options
        ISVNOptions options = SVNWCUtil.createDefaultOptions( true );

        // use the svnkit client manager
        SVNClientManager clientManager = SVNClientManager.newInstance( options, cd.getUsername(), cd.getPassword() );

        // get a commit client
        SVNWCClient client = clientManager.getWCClient();

        // put the add output here
        final AddResults results = new AddResults();

        // set an event handler so that messages go to the commit data streams for display
        client.setEventHandler( new SVNCommandEventProcessor( cd.getOut(), cd.getErr(), false ) {
            @Override
            public void handleEvent(SVNEvent event, double progress) {
                super.handleEvent(event, progress);
                if (event.getFile() != null) {
                    results.addPath(event.getFile().toString());
                }
            }
        });

        // actually do the add(s)
        PrintStream out = cd.getOut();
        for ( String path : paths ) {
            // path, force, mkdir, add parents, recursive
            try {
                File file = new File(path);
                client.doAdd( file, false, false, true, cd.getRecursive() );
            }
            catch ( Exception e ) {
                out.println( e.getMessage() );
                results.addErrorPath(path, e.getMessage());
            }
        }

        out.flush();
        out.close();
        return results;
    }
}
