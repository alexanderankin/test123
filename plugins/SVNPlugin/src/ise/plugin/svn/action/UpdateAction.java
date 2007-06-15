package ise.plugin.svn.action;

import ise.plugin.svn.gui.OutputPanel;

import ise.plugin.svn.SVNPlugin;
import ise.plugin.svn.command.Update;
import ise.plugin.svn.data.SVNData;
import ise.plugin.svn.gui.UpdateResultsPanel;
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

public class UpdateAction extends NodeActor {


    public void actionPerformed( ActionEvent ae ) {
        if ( nodes != null && nodes.size() > 0 ) {
            final SVNData data = new SVNData();

            boolean recursive = false;
            List<String> paths = new ArrayList<String>();
            for ( VPTNode node : nodes ) {
                if ( node != null ) {
                    paths.add( node.getNodePath() );
                    if (node.isDirectory()) {
                        recursive = true;
                    }
                }
            }
            data.setPaths( paths );

            // user confirmations
            if (recursive) {
                // have the user verify they want a recursive update
                int response = JOptionPane.showConfirmDialog(getView(), "Recursively update all files in selected directories?", "Recursive Update?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (response == JOptionPane.CANCEL_OPTION) {
                    return;
                }
                recursive = response == JOptionPane.YES_OPTION;
            }

            if ( username != null && password != null ) {
                data.setUsername( username );
                data.setPassword( password );
            }

            data.setOut( new ConsolePrintStream( this ) );

            view.getDockableWindowManager().showDockableWindow( "subversion" );
            final OutputPanel panel = SVNPlugin.getOutputPanel( view );
            panel.showTab( OutputPanel.CONSOLE );
            Logger logger = panel.getLogger();
            logger.log( Level.INFO, "Updating ..." );
            for ( Handler handler : logger.getHandlers() ) {
                handler.flush();
            }

            SwingUtilities.invokeLater( new Runnable() {
                        public void run() {

                            try {
                                Update update = new Update( );
                                final TreeMap<String, String> results = update.doUpdate(data);
                                SwingUtilities.invokeLater( new Runnable() {
                                            public void run() {
                                                JPanel results_panel = new UpdateResultsPanel( results );
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
