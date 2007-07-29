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

import ise.plugin.svn.gui.OutputPanel;

import ise.plugin.svn.SVNPlugin;
import ise.plugin.svn.command.Copy;
import ise.plugin.svn.data.CopyData;
import ise.plugin.svn.io.ConsolePrintStream;
import ise.plugin.svn.library.GUIUtils;
import ise.plugin.svn.library.swingworker.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.JPanel;

import org.gjt.sp.jedit.View;

import org.tmatesoft.svn.core.SVNCommitInfo;


/**
 * ActionListener to perform an svn copy.
 * This is not dependent on ProjectViewer.
 */
public class CopyAction implements ActionListener {

    private View view = null;
    private CopyData data = null;

    /**
     * @param view the View in which to display results
     * @param data CopyData object containing the info for a copy of some sort
     */
    public CopyAction( View view, CopyData data ) {
        if ( view == null )
            throw new IllegalArgumentException( "view may not be null" );
        if ( data == null )
            throw new IllegalArgumentException( "data may not be null" );
        this.view = view;
        this.data = data;
    }


    public void actionPerformed( ActionEvent ae ) {
        if ( data != null ) {
            data.setOut( new ConsolePrintStream( view ) );

            view.getDockableWindowManager().showDockableWindow( "subversion" );
            final OutputPanel panel = SVNPlugin.getOutputPanel( view );
            panel.showConsole();
            final Logger logger = panel.getLogger();
            logger.log( Level.INFO, "Copying ..." );
            for ( Handler handler : logger.getHandlers() ) {
                handler.flush();
            }

            class Runner extends SwingWorker<SVNCommitInfo, Object> {

                @Override
                public SVNCommitInfo doInBackground() {
                    try {
                        Copy copy = new Copy();
                        return copy.copy(data);
                    }
                    catch ( Exception e ) {
                        data.getOut().printError( e.getMessage() );
                    }
                    finally {
                        data.getOut().close();
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        SVNCommitInfo results = get();
                        if ( results != null ) {
                            //JPanel results_panel = new CommitResultsPanel( results );
                            //panel.addTab( "Copy", results_panel );
                        }
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
