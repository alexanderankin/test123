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
import ise.plugin.svn.command.Property;
import ise.plugin.svn.data.PropertyData;
import ise.plugin.svn.gui.PropertyPanel;
import ise.plugin.svn.io.ConsolePrintStream;
import ise.plugin.svn.library.swingworker.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.JOptionPane;
import org.gjt.sp.jedit.View;

/**
 * ActionListener to show SVN properties.
 * This is not dependent on ProjectViewer.
 */
public class PropertyAction extends SVNAction {

    private PropertyData data = null;

    /**
     * @param view the View in which to display results
     * @param data what to show
     */
    public PropertyAction( View view, PropertyData data ) {
        super(view, "Property");
        if ( data == null ) {
            throw new IllegalArgumentException( "data may not be null" );
        }
        this.data = data;
    }

    public void actionPerformed( ActionEvent ae ) {
        if ( data != null ) {
            // ask if properties should be found for children
            if ( data.hasDirectory() && data.askRecursive() ) {
                int answer = JOptionPane.showConfirmDialog( getView(), "One or more of the items selected is a directory.\nWould you like to see properties for subdirectories and files?", "Show Child Properties?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE );
                data.setRecursive( JOptionPane.YES_OPTION == answer );
            }
            verifyLogin(data.getPaths() == null ? null : data.getPaths().get(0));
            if (isCanceled()) {
                return;
            }
            data.setUsername( getUsername());
            data.setPassword( getPassword());

            // set up the svn console
            data.setOut( new ConsolePrintStream( getView() ) );
            getView().getDockableWindowManager().showDockableWindow( "subversion" );
            final OutputPanel panel = SVNPlugin.getOutputPanel( getView() );
            panel.showConsole();
            Logger logger = panel.getLogger();
            logger.log( Level.INFO, "Fetching properties ..." );
            for ( Handler handler : logger.getHandlers() ) {
                handler.flush();
            }

            // property fetcher
            class Runner extends SwingWorker < TreeMap<String, Properties> , Object > {

                @Override
                public TreeMap<String, Properties> doInBackground() {
                    try {
                        Property prop = new Property();
                        prop.doGetProperties( data );
                        return prop.getProperties();
                    }
                    catch ( Exception e ) {
                        e.printStackTrace();
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
                        TreeMap<String, Properties> results = get();
                        if ( results != null ) {
                            panel.addTab( "Properties", new PropertyPanel( getView(), results, data ) );
                        }
                        else {
                            JOptionPane.showMessageDialog( getView(), "No properties found.", "Error", JOptionPane.ERROR_MESSAGE );
                        }
                    }
                    catch ( Exception e ) {
                        // ignored
                        //e.printStackTrace();
                    }
                }
            }

            // fetch the properties
            ( new Runner() ).execute();

        }
    }
}
