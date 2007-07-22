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
import org.tmatesoft.svn.core.SVNURL;

import ise.plugin.svn.data.CheckoutData;



public class Checkout {

    public long doCheckout( CheckoutData cd ) throws CommandInitializationException, SVNException {

        // validate data values
        if ( cd.getPaths() == null || cd.getURL() == null ) {
            return -1;     // nothing to do
        }
        if ( cd.getOut() == null ) {
            throw new CommandInitializationException( "Invalid output stream." );
        }
        if ( cd.getErr() == null ) {
            cd.setErr( cd.getOut() );
        }

        File localPath = new File(cd.getPaths().get(0));

        // use default svn config options
        ISVNOptions options = SVNWCUtil.createDefaultOptions( true );

        // use the svnkit client manager
        SVNClientManager clientManager = SVNClientManager.newInstance( options, cd.getUsername(), cd.getPassword() );

        // get a commit client
        SVNUpdateClient client = clientManager.getUpdateClient();

        // set an event handler so that messages go to the commit data streams for display
        client.setEventHandler( new SVNCommandEventProcessor( cd.getOut(), cd.getErr(), false ) );

        PrintStream out = cd.getOut();

        long revision = client.doCheckout(SVNURL.parseURIDecoded(cd.getURL()), localPath, SVNRevision.HEAD, SVNRevision.HEAD, true);

        out.flush();
        out.close();

        return revision;
    }
}
