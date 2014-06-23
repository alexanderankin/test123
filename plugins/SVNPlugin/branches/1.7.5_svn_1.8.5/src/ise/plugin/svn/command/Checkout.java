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

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.internal.wc2.SvnWcGeneration;
import org.tmatesoft.svn.core.internal.wc.admin.SVNAdminAreaFactory;
import org.tmatesoft.svn.core.internal.wc17.db.ISVNWCDb;
import org.tmatesoft.svn.core.wc2.SvnCheckout;
import org.tmatesoft.svn.core.wc2.SvnTarget;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;

import ise.plugin.svn.data.CheckoutData;
import ise.plugin.svn.SVNPlugin;

import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusClient;
import org.tmatesoft.svn.core.wc.SVNWCClient;

import org.gjt.sp.jedit.jEdit;

public class Checkout {

    @SuppressWarnings( "deprecation" )
    // SVNURL.parseURIEncoded
    public long doCheckout( CheckoutData cd ) throws CommandInitializationException, SVNException {
        if ( cd == null ) {
            throw new CommandInitializationException( "CheckoutData is null." );
        }

        // validate data values
        if ( cd.getPaths() == null || cd.getURL() == null ) {
            return -1;            // nothing to do
        }
        File localPath = new File( cd.getPaths().get( 0 ) );
        if ( localPath == null ) {
            return -1;            // nothing to do
        }
        if ( cd.getOut() == null ) {
            throw new CommandInitializationException( "Invalid output stream." );
        }
        if ( cd.getErr() == null ) {
            cd.setErr( cd.getOut() );
        }
        PrintStream out = cd.getOut();

        // ensure the svn library is ready
        SVNKit.setupLibrary();

        // use default svn config options
        ISVNOptions options = SVNWCUtil.createDefaultOptions( true );

        // use the svnkit client manager
        SVNClientManager clientManager = SVNClientManager.newInstance( options, SVNWCUtil.createDefaultAuthenticationManager( SVNPlugin.getSvnStorageDir(), cd.getUsername(), cd.getDecryptedPassword() ) );

        // get a client for checkout
        SVNUpdateClient client = clientManager.getUpdateClient();

        // set the working copy format for the checked out files
        int wc_format = cd.getWorkingCopyFormat();
        if ( wc_format == -1 ) {
            wc_format = jEdit.getIntegerProperty( "ise.plugin.svn.defaultWCVersion" );
        }

        // NOTE: better code from svnkit folks:
        /*
            final SvnCheckout checkout = svnOperationFactory.createCheckout();
            checkout.setSource(SvnTarget.fromURL(url));
            checkout.setSingleTarget(SvnTarget.fromFile(workingCopyDirectory));
            checkout.setTargetWorkingCopyFormat(targetWorkingCopyFormat);
            checkout.run();
            
            Possible values of targetWorkingCopyFormat are
            
            SVNAdminArea14Factory.WC_FORMAT
            SVNAdminArea15Factory.WC_FORMAT
            SVNAdminArea16Factory.WC_FORMAT
            ISVNWCDb.WC_FORMAT_17
            ISVNWCDb.WC_FORMAT_18            
        */

        final SvnCheckout checkout = client.getOperationsFactory().createCheckout();
        checkout.setSource(SvnTarget.fromURL(SVNURL.parseURIDecoded( cd.getURL() )));
        checkout.setSingleTarget(SvnTarget.fromFile(localPath));
        checkout.setTargetWorkingCopyFormat(wc_format);
        
        // set an event handler so that messages go to the data streams for display
        client.getOperationsFactory().setEventHandler( new SVNCommandEventProcessor( out, cd.getErr(), false ) );

        Long revision = checkout.run();
        

        out.flush();
        out.close();
        clientManager.dispose();

        // possibly change working copy format
        // TODO: Is this the right place to do this?
        /*
        int current_wc_format = getWCVersion( localPath );
        int default_wc_format = jEdit.getIntegerProperty( "ise.plugin.svn.defaultWCVersion", SVNAdminAreaFactory.WC_FORMAT_15 );
        if ( current_wc_format != default_wc_format ) {
            SVNWCClient wc_client = SVNClientManager.newInstance().getWCClient();
            wc_client.doSetWCFormat( localPath, default_wc_format );
        }
        */
        return revision;
    }

    private int getWCVersion( File path ) {
        try {
            SVNStatusClient st_client = SVNClientManager.newInstance().getStatusClient();
            SVNStatus status = st_client.doStatus( path, false );
            return status.getWorkingCopyFormat();
        } catch ( Exception e ) {
            return SVNAdminAreaFactory.WC_FORMAT_15;
        }
    }

}