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

import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import org.tmatesoft.svn.core.SVNException;

import ise.plugin.svn.data.LogData;
import ise.plugin.svn.data.LogResults;
import ise.plugin.svn.SVNPlugin;


public class Log {

    private LogResults results = new LogResults();

    // <file path, list of log entries for the file>
    private TreeMap < String, List < SVNLogEntry >> entries = new TreeMap < String, List < SVNLogEntry >> ();

    private PrintStream out = null;

    /**
     * Fills a LogResults based on the given LogData.
     * @param data LogData containing the information necessary to fetch an svn log.
     */
    public void doLog( LogData data ) throws CommandInitializationException, SVNException {
        SVNKit.setupLibrary();


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
        SVNClientManager clientManager = SVNClientManager.newInstance( options, SVNWCUtil.createDefaultAuthenticationManager(SVNPlugin.getSvnStorageDir(),  data.getUsername(), data.getDecryptedPassword() ) );


        // get log client
        SVNLogClient client = clientManager.getLogClient();


        // set an event handler so that messages go to the streams for display
        client.setEventHandler( new SVNCommandEventProcessor( data.getOut(), data.getErr(), false ) );


        out = data.getOut();
        LogHandler handler = new LogHandler();

        if ( data.pathsAreURLs() ) {

            // get the repository url, then trim the paths to be relative to
            // the repository url so they can be passed all at once to the svn
            // server.
            SVNURL repositoryUrl = getRepositoryURL( data );
            if ( repositoryUrl == null ) {
                out.println( "ERROR: repository URL is null" );
                out.flush();
                out.close();
                return ;
            }
            int length = repositoryUrl.toString().length();
            Set<String> pathsToCheck = new HashSet<String>();
            for ( String path : data.getPaths() ) {
                String toCheck = path.substring( length );
                pathsToCheck.add( toCheck );
            }

            for ( String path : pathsToCheck ) {
                out.println( "Log:" );
                out.println( "\trepository url: " + repositoryUrl );
                out.println( "\t          path: " + path );
                out.println( "\t  peg revision: " + data.getPegRevision() );
                out.println( "\tstart revision: " + data.getStartRevision() );
                out.println( "\t  end revision: " + data.getEndRevision() );
                out.println( "\t  stop on copy: " + data.getStopOnCopy() );
                out.println( "\t    show paths: " + data.getShowPaths() );
                out.println( "\t      max logs: " + data.getMaxLogs() );

                handler.setPath( path );
                String[] pathToCheck = { path };

                // Get log message for each path one at a time.  While it is possible
                // to get the log messages for several paths at once, it is impossible
                // to tell which message goes with which file.
                // doLog(SVNURL url, String[] paths, SVNRevision pegRevision, SVNRevision startRevision,
                //      SVNRevision endRevision, boolean stopOnCopy, boolean discoverChangedPaths, long limit, ISVNLogEntryHandler handler)
                client.doLog( repositoryUrl, pathToCheck, data.getPegRevision(), data.getStartRevision(),
                        data.getEndRevision(), data.getStopOnCopy(), data.getShowPaths(), data.getMaxLogs(), handler );
            }
        }
        else {
            for ( File path : localPaths ) {
                out.println( "Log:" );
                out.println( "\t          file: " + path.getAbsolutePath() );
                out.println( "\t  peg revision: " + data.getPegRevision() );
                out.println( "\tstart revision: " + data.getStartRevision() );
                out.println( "\t  end revision: " + data.getEndRevision() );
                out.println( "\t  stop on copy: " + data.getStopOnCopy() );
                out.println( "\t    show paths: " + data.getShowPaths() );
                out.println( "\t      max logs: " + data.getMaxLogs() );

                handler.setPath( path.getAbsolutePath() );
                File[] pathToCheck = new File[] { path };

                // svnkit method signature:
                // doLog(File[] paths, SVNRevision pegRevision, SVNRevision startRevision,
                //      SVNRevision endRevision, boolean stopOnCopy, boolean discoverChangedPaths, long limit, ISVNLogEntryHandler handler)
                client.doLog( pathToCheck, data.getPegRevision(), data.getStartRevision(),
                        data.getEndRevision(), data.getStopOnCopy(), data.getShowPaths(), data.getMaxLogs(), handler );
            }
        }
        results.setEntries( entries );
        out.flush();
        out.close();
    }

    public class LogHandler implements ISVNLogEntryHandler {

        private String path = null;

        public void setPath( String path ) {
            this.path = path;
        }

        public void handleLogEntry( SVNLogEntry logEntry ) {
            List<SVNLogEntry> messages = entries.get(path);
            if (messages == null) {
                messages = new ArrayList<SVNLogEntry>();
                entries.put(path, messages);
            }
            messages.add( logEntry );
        }
    }

    /**
     * @return a log results object
     */
    public LogResults getLogEntries() {
        return results;
    }

    public void printLogEntry( String path, SVNLogEntry logEntry ) {
        if ( out == null ) {
            return ;
        }

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

    private SVNURL getRepositoryURL( LogData data ) {
        try {
            Info info = new Info( );
            List<SVNInfo> infos = info.getInfo( data );
            if ( infos.size() == 0 ) {
                return null;
            }
            SVNInfo svn_info = infos.get( 0 );
            return svn_info.getRepositoryRootURL();
        }
        catch ( Exception e ) {
            return null;
        }
    }

    public static void main ( String[] args ) {
        // for testing
        LogData data = new LogData();
        data.setUsername( "daleanson" );
        data.setPassword( "" );
        List<String> paths = new ArrayList<String>();
        paths.add( "/home/danson/src/plugins/SVNPlugin/src/ise/plugin/svn/command/Log.java" );
        data.setPaths( paths );
        data.setOut( new ise.plugin.svn.io.ConsolePrintStream( new ise.plugin.svn.io.LogOutputStream( null ) ) );
        //long start_rev = 9795L;
        //long end_rev = 9810L;
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set( 2007, 5, 1 );
        //SVNRevision start = SVNRevision.parse(String.valueOf(start_rev));
        SVNRevision start = SVNRevision.create( cal.getTime() );
        cal.set( 2007, 6, 1 );
        //SVNRevision end = SVNRevision.parse(String.valueOf(end_rev));
        SVNRevision end = SVNRevision.create( cal.getTime() );
        data.setStartRevision( start );
        data.setEndRevision( end );
        Log log = new Log();
        try {
            org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory.setup();
            org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl.setup();
            org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory.setup();
            log.doLog( data );
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}