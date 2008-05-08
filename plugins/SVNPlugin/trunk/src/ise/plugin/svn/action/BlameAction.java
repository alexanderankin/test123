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
import ise.plugin.svn.command.Blame;
import ise.plugin.svn.data.LogData;
import ise.plugin.svn.io.ConsolePrintStream;
import ise.plugin.svn.library.swingworker.*;
import ise.plugin.svn.gui.component.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.JComponent;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.textarea.JEditTextArea;

/**
 * ActionListener to perform an svn blame.
 * This is not dependent on ProjectViewer.
 */
public class BlameAction extends SVNAction {

    private List<String> paths = null;
    private boolean pathsAreUrls = false;
    private LogData data = null;

    /**
     * @param view the View in which to display results
     * @param paths a list of paths to be processed (note that the Blame.java class only handles one file at a time.)
     * @param username the username for the svn repository
     * @param password the password for the username
     */
    public BlameAction( View view, List<String> paths, String username, String password ) {
        super( view, jEdit.getProperty("ips.Blame", "Blame") );
        if ( paths == null )
            throw new IllegalArgumentException( "paths may not be null" );
        this.paths = paths;
        setUsername( username );
        setPassword( password );
    }

    public BlameAction( View view, LogData data ) {
        super( view, jEdit.getProperty("ips.Blame", "Blame") );
        if ( data == null )
            throw new IllegalArgumentException( "data may not be null" );
        if ( data.getPaths() == null )
            throw new IllegalArgumentException( "paths may not be null" );
        this.paths = data.getPaths();
        this.pathsAreUrls = data.pathsAreURLs();
        setUsername( data.getUsername() );
        setPassword( data.getPassword() );
    }

    public void actionPerformed( ActionEvent ae ) {
        if ( paths != null && paths.size() > 0 ) {
            data = new LogData();
            data.setPaths( paths );
            data.setPathsAreURLs( pathsAreUrls );

            if ( getUsername() == null ) {
                verifyLogin( paths.get( 0 ) );
                if ( isCanceled() ) {
                    return ;
                }
                data.setUsername( getUsername() );
                data.setPassword( getPassword() );
            }

            data.setOut( new ConsolePrintStream( getView() ) );

            getView().getDockableWindowManager().showDockableWindow( "subversion" );
            final OutputPanel panel = SVNPlugin.getOutputPanel( getView() );
            panel.showConsole();
            final Logger logger = panel.getLogger();
            logger.log( Level.INFO, jEdit.getProperty("ips.Fetching_annotation_info_...", "Fetching annotation info ...") );
            for ( Handler handler : logger.getHandlers() ) {
                handler.flush();
            }

            class Runner extends SwingWorker < ArrayList<String> , Object > {

                @Override
                public ArrayList<String> doInBackground() {
                    try {
                        Blame blame = new Blame();
                        return blame.getBlame( data );
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
                        logger.log( Level.INFO, jEdit.getProperty("ips.Formatting_annotation_info_...", "Formatting annotation info ...") );
                        BlameModel model = new BlameModel();
                        model.setBlame( get() );
                        model.setTextArea( getView().getEditPane().getTextArea() );
                        BlamePane pane = new BlamePane();
                        pane.setModel( model );
                        JEditTextArea textArea = getView().getEditPane().getTextArea();
                        JEditBuffer buffer = textArea.getBuffer();
                        Object old_blame = buffer.getProperty( "_old_blame_" );
                        if ( old_blame != null ) {
                            textArea.removeLeftOfScrollBar( ( JComponent ) old_blame );
                            Object old_closer = buffer.getProperty( "_old_closer_" );
                            textArea.removeTopComponent( ( JComponent ) old_closer );
                        }
                        textArea.addLeftOfScrollBar( pane );
                        JComponent closer = pane.getCloser( getView() );
                        textArea.addTopComponent( closer );
                        textArea.addCaretListener( pane );
                        buffer.setProperty( "_old_blame_", pane );
                        buffer.setProperty( "_old_closer_", closer );
                        getView().invalidate();
                        getView().validate();
                        logger.log( Level.INFO, jEdit.getProperty("ips.Done.", "Done.") );
                    }
                    catch ( Exception e ) {
                        // ignored
                        e.printStackTrace();
                    }
                }
            }
            ( new Runner() ).execute();

        }
    }
}