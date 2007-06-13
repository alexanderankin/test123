package ise.plugin.svn.command;

import java.io.*;
import java.util.*;

import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.cli.command.SVNCommandEventProcessor;

import org.tmatesoft.svn.core.SVNException;

import ise.plugin.svn.data.SVNData;


public class Log {

    private TreeMap<String, List<SVNLogEntry>> entries = new TreeMap();

    private PrintStream out = null;

    /**
     * @return a list of paths that were scheduled to be added.
     */
    public void doLog( SVNData cd ) throws CommandInitializationException, SVNException {

        // validate data values
        if ( cd.getPaths() == null ) {
            return ;     // nothing to do
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
        SVNLogClient client = clientManager.getLogClient();

        // set an event handler so that messages go to the commit data streams for display
        client.setEventHandler( new SVNCommandEventProcessor( cd.getOut(), cd.getErr(), false ) );

        out = cd.getOut();

        // files for logs, start revision, end revision, stop on copy, report paths, number of entries, handler
        for ( File file : localPaths ) {
            LogHandler handler = new LogHandler(file);
            client.doLog( new File[] {file}, SVNRevision.create( 0L ), SVNRevision.HEAD, true, false, 100, handler );
            entries.put(handler.getPath(), handler.getEntries());
        }

        out.flush();
        out.close();
    }

    public class LogHandler implements ISVNLogEntryHandler {

        private String path = "";
        private List<SVNLogEntry> logEntries = null;

        public LogHandler( File f ) {
            path = f.toString();
        }

        public void handleLogEntry( SVNLogEntry logEntry ) {
            if ( logEntries == null ) {
                logEntries = new ArrayList<SVNLogEntry>();
            }
            logEntries.add( logEntry );
            Log.this.printLogEntry( path, logEntry );
        }

        public String getPath(){
            return path;
        }

        public List<SVNLogEntry> getEntries() {
            return logEntries;
        }
    }

    public TreeMap<String, List<SVNLogEntry>> getLogEntries() {
        return entries;
    }

    public void printLogEntry( String path, SVNLogEntry logEntry ) {
        if ( out == null )
            return ;

        out.println("path: " + path);
        /*
         * gets the revision number
         */
        out.println( "revision: " + logEntry.getRevision() );
        /*
         * gets the author of the changes made in that revision
         */
        out.println( "author: " + logEntry.getAuthor() );
        /*
         * gets the time moment when the changes were committed
         */
        out.println( "date: " + logEntry.getDate() );
        /*
         * gets the commit log message
         */
        out.println( "log message: " + logEntry.getMessage() );
        /*
         * displaying all paths that were changed in that revision; cahnged
         * path information is represented by SVNLogEntryPath.
         */
        if ( logEntry.getChangedPaths().size() > 0 ) {
            out.println();
            out.println( "changed paths:" );
            /*
             * keys are changed paths
             */
            Set changedPathsSet = logEntry.getChangedPaths().keySet();

            for ( Iterator changedPaths = changedPathsSet.iterator(); changedPaths
                    .hasNext(); ) {
                /*
                 * obtains a next SVNLogEntryPath
                 */
                SVNLogEntryPath entryPath = ( SVNLogEntryPath ) logEntry
                        .getChangedPaths().get( changedPaths.next() );
                /*
                 * SVNLogEntryPath.getPath returns the changed path itself;
                 *
                 * SVNLogEntryPath.getType returns a charecter describing
                 * how the path was changed ('A' - added, 'D' - deleted or
                 * 'M' - modified);
                 *
                 * If the path was copied from another one (branched) then
                 * SVNLogEntryPath.getCopyPath &
                 * SVNLogEntryPath.getCopyRevision tells where it was copied
                 * from and what revision the origin path was at.
                 */
                out.println( " "
                        + entryPath.getType()
                        + " "
                        + entryPath.getPath()
                        + ( ( entryPath.getCopyPath() != null ) ? " (from "
                            + entryPath.getCopyPath() + " revision "
                            + entryPath.getCopyRevision() + ")" : "" ) );
            }
        }
    }
}
