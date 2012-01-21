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

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNRevision;


import ise.plugin.svn.SVNPlugin;
import ise.plugin.svn.data.ExportData;
import ise.plugin.svn.data.UpdateData;
import org.tmatesoft.svn.core.wc.SVNCopySource;

/**
 * File -> File or
 * URL -> File
 * RHS must be directory.
 */
public class Export {

    public UpdateData export( ExportData data ) throws CommandInitializationException, SVNException {
        SVNKit.setupLibrary();

        // validate destination, must be a local directory
        File destination = data.getDestinationFile();
        if ( destination == null ) {
            throw new CommandInitializationException( "No destination for export specified." );
        }
        if ( !destination.isDirectory() ) {
            throw new CommandInitializationException( "Cannot export to a file, must have a directory." );
        }
        if ( !destination.exists() ) {
            destination.mkdirs();
        }

        // check and set up output stream
        if ( data.getOut() == null ) {
            throw new CommandInitializationException( "Invalid output stream." );
        }
        if ( data.getErr() == null ) {
            data.setErr( data.getOut() );
        }
        PrintStream out = data.getOut();

        // get an svn update client using default svn config options
        ISVNOptions options = SVNWCUtil.createDefaultOptions( true );
        SVNClientManager clientManager = SVNClientManager.newInstance( options, SVNWCUtil.createDefaultAuthenticationManager(SVNPlugin.getSvnStorageDir(), data.getUsername(), data.getDecryptedPassword()) );
        SVNUpdateClient client = clientManager.getUpdateClient();

        // set an event handler so that messages go to the streams for display
        UpdateData update_data = new UpdateData();
        update_data.setOut( data.getOut() );
        update_data.setErr( data.getErr() );
        UpdateEventHandler handler = new UpdateEventHandler( update_data );
        client.setEventHandler( handler );

        // do the export
        long revision = 0L;
        SVNRevision peg_revision = data.getPegRevision();
        if ( peg_revision == null ) {
            peg_revision = SVNRevision.UNDEFINED;
        }
        if ( data.getSourceFiles() != null ) {
            SVNCopySource[] sources = data.getSourceFiles();
            for (SVNCopySource source : sources) {
                File file = source.getFile();
                // svnkit 1.2.x:
                // doExport(File srcPath, File dstPath, SVNRevision pegRevision, SVNRevision revision, String eolStyle, boolean overwrite, SVNDepth depth)
                SVNDepth depth = data.getRecursive() ? SVNDepth.INFINITY : SVNDepth.EMPTY;
                revision = client.doExport( file, new File( destination, file.getName() ), peg_revision, data.getRevision(), data.getEOLStyle(), data.getForce(), depth );
            }
        }
        else if ( data.getSourceURLs() != null ) {
            SVNCopySource[] sources = data.getSourceURLs();
            for (SVNCopySource source : sources) {
                SVNURL url = source.getURL();
                String filename = url.getPath().substring(url.getPath().lastIndexOf("/") + 1);
                // svnkit 1.2.x:
                // doExport(SVNURL url, File dstPath, SVNRevision pegRevision, SVNRevision revision, String eolStyle, boolean overwrite, SVNDepth depth)
                SVNDepth depth = data.getRecursive() ? SVNDepth.INFINITY : SVNDepth.EMPTY;
                revision = client.doExport( url, new File( destination, filename), peg_revision, data.getRevision(), data.getEOLStyle(), data.getForce(), depth );
            }
        }

        // clean up
        out.flush();
        out.close();

        // fetch the accumulated data from the handler
        update_data = handler.getData();
        update_data.setRevision( revision );
        return update_data;
    }
}
