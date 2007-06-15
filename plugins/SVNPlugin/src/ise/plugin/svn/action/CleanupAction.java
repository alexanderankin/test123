package ise.plugin.svn.action;

import ise.plugin.svn.gui.OutputPanel;

import ise.plugin.svn.SVNPlugin;
import ise.plugin.svn.command.Cleanup;
import ise.plugin.svn.data.SVNData;
import ise.plugin.svn.io.ConsolePrintStream;
import ise.plugin.svn.library.GUIUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;
import projectviewer.vpt.VPTNode;

public class CleanupAction extends NodeActor {


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
            final Logger logger = panel.getLogger();
            logger.log( Level.INFO, "Cleaning up ..." );
            for ( Handler handler : logger.getHandlers() ) {
                handler.flush();
            }

            SwingUtilities.invokeLater( new Runnable() {
                        public void run() {

                            try {
                                Cleanup c = new Cleanup( );
                                c.cleanup( data );
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
