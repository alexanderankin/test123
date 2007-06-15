package ise.plugin.svn.action;

import ise.plugin.svn.gui.OutputPanel;

import ise.plugin.svn.SVNPlugin;
import ise.plugin.svn.command.Log;
import ise.plugin.svn.data.SVNData;
import ise.plugin.svn.gui.LogResultsPanel;
import ise.plugin.svn.gui.SVNInfoPanel;
import ise.plugin.svn.io.ConsolePrintStream;
import ise.plugin.svn.library.GUIUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;
import projectviewer.vpt.VPTNode;
import org.tmatesoft.svn.core.SVNLogEntry;

public class LogAction extends NodeActor {


    public void actionPerformed( ActionEvent ae ) {
        if ( nodes != null && nodes.size() > 0 ) {
            final SVNData data = new SVNData();


            List<String> paths = new ArrayList<String>();
            for ( VPTNode node : nodes ) {
                if ( node != null ) {
                    paths.add( node.getNodePath() );
                }
            }
            data.setPaths( paths );

            if ( username != null && password != null ) {
                data.setUsername( username );
                data.setPassword( password );
            }

            data.setOut( new ConsolePrintStream( this ) );

            view.getDockableWindowManager().showDockableWindow( "subversion" );
            final OutputPanel panel = SVNPlugin.getOutputPanel( view );
            panel.showTab( OutputPanel.CONSOLE );
            Logger logger = panel.getLogger();
            logger.log( Level.INFO, "Fetching log ..." );
            for ( Handler handler : logger.getHandlers() ) {
                handler.flush();
            }

            SwingUtilities.invokeLater( new Runnable() {
                        public void run() {

                            try {
                                Log log = new Log( );
                                log.doLog( data );
                                final TreeMap<String, List<SVNLogEntry>> results = log.getLogEntries();
                                SwingUtilities.invokeLater( new Runnable() {
                                            public void run() {
                                                JPanel results_panel = new LogResultsPanel( results );
                                                panel.setResultsPanel( results_panel );
                                                panel.showTab( OutputPanel.RESULTS );
                                            }
                                        }
                                                          );
                            }
                            catch ( Exception e ) {
                                data.getOut().printError( e.getMessage() );
                            }
                        }
                    }
                                      );
        }
    }
}
