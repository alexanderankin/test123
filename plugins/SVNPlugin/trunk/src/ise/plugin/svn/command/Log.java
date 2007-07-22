package ise.plugin.svn.command;

import java.io.*;
import java.util.*;

import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.cli.command.SVNCommandEventProcessor;
import org.tmatesoft.svn.core.wc.SVNInfo;

import org.tmatesoft.svn.core.SVNException;

import ise.plugin.svn.data.LogData;


public class Log {

    private TreeMap < String, List < SVNLogEntry >> entries = new TreeMap < String, List < SVNLogEntry >> ();

    private PrintStream out = null;

    /**
     * @return a list of paths that were scheduled to be added.
     */
    public void doLog( LogData data ) throws CommandInitializationException, SVNException {

        // validate data values
        if ( data.getPaths() == null ) {
            return ;     // nothing to do
        }
        if ( data.getOut() == null ) {
            throw new CommandInitializationException( "Invalid output stream." );
        }
        if ( data.getErr() == null ) {
            data.setErr( data.getOut() );
        }

        // convert paths to Files
        List<String> paths = data.getPaths();
        File[] localPaths = null;
        if ( !data.pathsAreURLs() ) {
            localPaths = new File[ paths.size() ];
            for ( int i = 0; i < paths.size(); i++ ) {
                localPaths[ i ] = new File( paths.get( i ) );
                // check for file existence?
            }
        }

        // use default svn config options
        ISVNOptions options = SVNWCUtil.createDefaultOptions( true );

        // use the svnkit client manager
        SVNClientManager clientManager = SVNClientManager.newInstance( options, data.getUsername(), data.getPassword() );

        // get a commit client
        SVNLogClient client = clientManager.getLogClient();
        SVNWCClient wc_client = clientManager.getWCClient();

        // set an event handler so that messages go to the commit data streams for display
        client.setEventHandler( new SVNCommandEventProcessor( data.getOut(), data.getErr(), false ) );

        out = data.getOut();

        if ( data.pathsAreURLs() ) {
            for (String path : data.getPaths()) {
                SVNURL svnurl = SVNURL.parseURIDecoded(path);
                LogHandler handler = new LogHandler( path );
                // files for logs, paths, peg revision, start revision,
                // end revision, stop on copy, report paths, number of entries, handler
                client.doLog( svnurl, null, SVNRevision.create( 0L ), data.getStartRevision(),
                    data.getEndRevision(), data.getStopOnCopy(), data.getShowPaths(), data.getMaxLogs(), handler );
                entries.put( handler.getPath(), handler.getEntries() );
            }
        }
        else {
            for ( File file : localPaths ) {
                // this feels like a kludge, I shouldn't have to do a substring
                // call to figure out the path of the file
                LogHandler handler = new LogHandler( file );
                SVNInfo info = wc_client.doInfo(file, SVNRevision.WORKING);
                //System.out.println("+++++ repository url = " + info.getRepositoryRootURL());
                //System.out.println("+++++ url = " + info.getURL());
                //System.out.println("+++++ url.getPath = " + info.getURL().getPath());
                String rep_url_string = info.getRepositoryRootURL().toString();
                String file_url_string = info.getURL().toString();
                String path = file_url_string.substring(rep_url_string.length());
                SVNURL rep_url = SVNURL.parseURIDecoded(rep_url_string);
                String[] rep_paths = new String[]{path};
                // I should also be able to set the peg revision, but it seems that
                // using anything beside 0 fails.
                client.doLog( rep_url, rep_paths, SVNRevision.create(0L), data.getStartRevision(),
                    data.getEndRevision(), data.getStopOnCopy(), data.getShowPaths(), data.getMaxLogs(), handler );
                entries.put( handler.getPath(), handler.getEntries() );
            }
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

        public LogHandler( String p ) {
            path = p;
        }

        public void handleLogEntry( SVNLogEntry logEntry ) {
            if ( logEntries == null ) {
                logEntries = new ArrayList<SVNLogEntry>();
            }
            logEntries.add( logEntry );
            Log.this.printLogEntry( path, logEntry );
        }

        public String getPath() {
            return path;
        }

        public List<SVNLogEntry> getEntries() {
            return logEntries;
        }
    }

    /**
     * @return a map with the path of a file as the key and a list of associated
     * log entries as the value.
     */
    public TreeMap < String, List < SVNLogEntry >> getLogEntries() {
        return entries;
    }

    public void printLogEntry( String path, SVNLogEntry logEntry ) {
        if ( out == null )
            return ;

        out.println( "path: " + path );
        out.println( "revision: " + logEntry.getRevision() );
        out.println( "author: " + logEntry.getAuthor() );
        out.println( "date: " + logEntry.getDate() );
        out.println( "log message: " + logEntry.getMessage() );

        // displaying all paths that were changed in that revision, changed
        // path information is represented by SVNLogEntryPath.
        if ( logEntry.getChangedPaths().size() > 0 ) {
            out.println();
            out.println( "changed paths:" );

            // keys are changed paths
            Set changedPathsSet = logEntry.getChangedPaths().keySet();

            for ( Iterator changedPaths = changedPathsSet.iterator(); changedPaths.hasNext(); ) {
                SVNLogEntryPath entryPath = ( SVNLogEntryPath ) logEntry.getChangedPaths().get( changedPaths.next() );

                /*
                 SVNLogEntryPath.getPath returns the changed path itself

                 SVNLogEntryPath.getType returns a character describing
                 how the path was changed ('A' - added, 'D' - deleted or
                 'M' - modified);

                 If the path was copied from another one (branched) then
                 SVNLogEntryPath.getCopyPath and
                 SVNLogEntryPath.getCopyRevision tells where it was copied
                 from and what revision the origin path was at.
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

    public static void main (String[] args) {
        // for testing
        LogData data = new LogData();
        data.setUsername("daleanson");
        data.setPassword("");
        List<String> paths = new ArrayList<String>();
        paths.add("/home/danson/src/plugins/SVNPlugin/src/ise/plugin/svn/command/Log.java");
        data.setPaths(paths);
        data.setOut(new ise.plugin.svn.io.ConsolePrintStream(new ise.plugin.svn.io.LogOutputStream(null)));
        long start_rev = 9795L;
        long end_rev = 9810L;
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(2007, 5, 1);
        //SVNRevision start = SVNRevision.parse(String.valueOf(start_rev));
        SVNRevision start = SVNRevision.create(cal.getTime());
        cal.set(2007, 6, 1);
        //SVNRevision end = SVNRevision.parse(String.valueOf(end_rev));
        SVNRevision end = SVNRevision.create(cal.getTime());
        data.setStartRevision(start);
        data.setEndRevision(end);
        Log log = new Log();
        try {
            org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory.setup();
            org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl.setup();
            org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory.setup();
            log.doLog(data);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
