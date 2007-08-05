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

package ise.plugin.svn.action;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.JPanel;

import projectviewer.vpt.VPTNode;

import ise.plugin.svn.gui.OutputPanel;
import ise.plugin.svn.SVNPlugin;

import ise.plugin.svn.command.Status;
import ise.plugin.svn.data.SVNData;
import ise.plugin.svn.data.StatusData;
import ise.plugin.svn.gui.StatusResultsPanel;
import ise.plugin.svn.library.GUIUtils;
import ise.plugin.svn.library.swingworker.*;
import ise.plugin.svn.io.ConsolePrintStream;

import org.tmatesoft.svn.core.wc.SVNStatus;


public class StatusAction extends NodeActor {


    public void actionPerformed( ActionEvent ae ) {
        if ( nodes != null && nodes.size() > 0 ) {
            final SVNData cd = new SVNData();
            List<String> paths = new ArrayList<String>();
            for ( VPTNode node : nodes ) {
                if ( node != null && node.getNodePath() != null ) {
                    paths.add( node.getNodePath() );
                }
            }
            cd.setPaths( paths );
            if ( username != null && password != null ) {
                cd.setUsername( username );
                cd.setPassword( password );
            }

            cd.setOut( new ConsolePrintStream( view ) );

            view.getDockableWindowManager().showDockableWindow( "subversion" );
            final OutputPanel output_panel = SVNPlugin.getOutputPanel( view );
            output_panel.showConsole();
            Logger logger = output_panel.getLogger();
            logger.log( Level.INFO, "Gathering status ..." );
            for ( Handler handler : logger.getHandlers() ) {
                handler.flush();
            }

            class Runner extends SwingWorker<StatusData, Object> {

                @Override
                public StatusData doInBackground() {
                    try {
                        Status status = new Status();
                        return status.getStatus( cd );
                    }
                    catch ( Exception e ) {
                        cd.getOut().printError( e.getMessage() );
                    }
                    finally {
                        cd.getOut().close();
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        JPanel panel = new StatusResultsPanel( get(), view, username, password );
                        output_panel.addTab("Status", panel);
                    }
                    catch ( Exception e ) {
                        // ignored
                    }
                }
            }
            ( new Runner() ).execute();

        }
    }

}
