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
import ise.plugin.svn.gui.PropertiesPanel;
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

/**
 * ActionListener to show SVN properties.
 * This is not dependent on ProjectViewer.
 */
public class PropertyAction implements ActionListener {

    private View view = null;
    private String filename = null;
    private Properties properties = null;
    private PropertyData data = null;

    /**
     * @param view the View in which to display results
     * @param props the properties to show
     */
    public PropertyAction( View view, String name, Properties props ) {
        if ( view == null )
            throw new IllegalArgumentException( "view may not be null" );
        if ( props == null )
            throw new IllegalArgumentException( "props may not be null" );
        this.view = view;
        this.filename = name == null ? "" : name;
        this.properties = props;
    }

    public PropertyAction( View view, PropertyData data ) {
        if ( view == null )
            throw new IllegalArgumentException( "view may not be null" );
        if ( data == null )
            throw new IllegalArgumentException( "data may not be null" );
        this.view = view;
        this.data = data;
    }

    public void actionPerformed( ActionEvent ae ) {
        if ( properties != null ) {
            view.getDockableWindowManager().showDockableWindow( "subversion" );
            final OutputPanel panel = SVNPlugin.getOutputPanel( view );
            panel.showConsole();
            panel.addTab( "Properties", new PropertiesPanel( filename, properties ) );
        }
        else if ( data != null ) {

            data.setOut( new ConsolePrintStream( view ) );

            view.getDockableWindowManager().showDockableWindow( "subversion" );
            final OutputPanel panel = SVNPlugin.getOutputPanel( view );
            panel.showConsole();
            Logger logger = panel.getLogger();
            logger.log( Level.INFO, "Fetching properties ..." );
            for ( Handler handler : logger.getHandlers() ) {
                handler.flush();
            }

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
                        String filename = "";
                        Properties properties = null;
                        TreeMap<String, Properties> results = get();
                        if ( results != null ) {
                            Set < Map.Entry < String, Properties >> set = results.entrySet();
                            for ( Map.Entry<String, Properties> me : set ) {
                                filename = me.getKey();
                                properties = me.getValue();
                                break;
                            }
                            panel.addTab( "Properties", new PropertiesPanel( filename, properties ) );
                        }
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
