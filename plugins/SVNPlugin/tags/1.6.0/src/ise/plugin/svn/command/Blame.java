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

import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.ISVNAnnotateHandler;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNStatusClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.wc.SVNStatusType;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNStatus;

import ise.plugin.svn.SVNPlugin;
import ise.plugin.svn.data.LogData;
import ise.plugin.svn.gui.component.BlameModel;

public class Blame {

    private ArrayList<String> results = new ArrayList<String>();
    private PrintStream out = null;

    /**
     * @return a list of revision and author, one entry per line of the file.
     */
    public BlameModel getBlame( LogData data ) throws CommandInitializationException, SVNException {
        SVNKit.setupLibrary();

        // validate data values
        if ( data.getPaths() == null ) {
            return null;     // nothing to do
        }
        if ( data.getOut() == null ) {
            throw new CommandInitializationException( "Invalid output stream." );
        }
        if ( data.getErr() == null ) {
            data.setErr( data.getOut() );
        }

        // convert first path to a file.  This method only works on one file at
        // a time.
        String path = data.getPaths().get( 0 );
        File file = null;
        if ( !data.pathsAreURLs() ) {
            file = new File( path );
        }

        // use default svn config options
        ISVNOptions options = SVNWCUtil.createDefaultOptions( true );

        // use the svnkit client manager
        SVNClientManager clientManager = SVNClientManager.newInstance( options, SVNWCUtil.createDefaultAuthenticationManager(SVNPlugin.getSvnStorageDir(), data.getUsername(), data.getDecryptedPassword()) );

        // get a client
        SVNLogClient client = clientManager.getLogClient();

        // set an event handler so that messages go to the commit data streams for display
        client.setEventHandler( new SVNCommandEventProcessor( data.getOut(), data.getErr(), false ) );

        out = data.getOut();

        ISVNAnnotateHandler handler = new ISVNAnnotateHandler() {
                    @Deprecated
                    public void handleLine( Date date, long revision,
                            String author, String line ) {
                        results.add( revision + " " + author );
                    }
                    public void handleEOF() {
                        
                    }
                    
                    public boolean handleRevision(Date date, long revision, String author, File contents) {
                        return false;
                    }
                    
                    public void handleLine(Date data, long revision, String author, 
                        String line, Date mergedDate, long mergedRevision, 
                        String mergedAuthor, String mergedPath, int lineNumber) {
                        results.add( revision + " " + author );
                    }
                };

        BlameModel model = new BlameModel();
        if ( data.pathsAreURLs() ) {
            SVNURL svnurl = SVNURL.parseURIDecoded( path );
            client.doAnnotate( svnurl, SVNRevision.HEAD, data.getStartRevision(), data.getEndRevision(), handler );
        }
        else {
            // collect the "blame" lines
            client.doAnnotate( file, SVNRevision.HEAD, data.getStartRevision(), data.getEndRevision(), handler );
            SVNStatusClient status_client = clientManager.getStatusClient();

            // check if the local file has been modified and set out of date if it has
            SVNStatus status = status_client.doStatus( file, true );
            model.setOutOfDate( SVNStatusType.STATUS_MODIFIED.equals( status.getContentsStatus() ) );
        }
        model.setBlame( results );

        out.flush();
        out.close();

        return model;
    }
    public static void main ( String[] args ) {
        // for testing
        LogData data = new LogData();
        data.setUsername( "danson" );
        data.setPassword( "" );
        List<String> paths = new ArrayList<String>();
        paths.add( "/home/danson/tmp/anothertest/test3/test/BigIntModTest2.java" );
        data.setPaths( paths );
        data.setOut( new ise.plugin.svn.io.ConsolePrintStream( new ise.plugin.svn.io.LogOutputStream( null ) ) );
        Blame blame = new Blame();
        try {
            org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory.setup();
            org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl.setup();
            org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory.setup();
            BlameModel results = blame.getBlame( data );
            System.out.println( results );
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}