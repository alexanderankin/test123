package ise.plugin.svn.action;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import projectviewer.vpt.VPTNode;
import ise.plugin.svn.gui.OutputPanel;

import ise.plugin.svn.SVNPlugin;
import ise.plugin.svn.command.Info;
import ise.plugin.svn.data.CommitData;
import ise.plugin.svn.library.GUIUtils;
import ise.plugin.svn.gui.SVNInfoPanel;
import ise.plugin.svn.io.ConsolePrintStream;

import org.tmatesoft.svn.core.wc.SVNInfo;

public class InfoAction extends NodeActor {

    public void actionPerformed( ActionEvent ae ) {
        if ( nodes != null && nodes.size() > 0 ) {
            final CommitData cd = new CommitData();
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

            cd.setOut( new ConsolePrintStream(this));

            view.getDockableWindowManager().showDockableWindow( "subversion" );
            final OutputPanel panel = SVNPlugin.getOutputPanel(view);
            panel.showTab(OutputPanel.CONSOLE);
            Logger logger = panel.getLogger();
            logger.log(Level.INFO, "Fetching info...");
            for(Handler handler : logger.getHandlers()) {
                handler.flush();
            }

            SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            try {
                                Info info = new Info();
                                final List<SVNInfo> results = info.info( cd );
                                SwingUtilities.invokeLater(new Runnable(){
                                        public void run() {
                                            JPanel info_panel = new SVNInfoPanel(results);
                                            panel.setResultsPanel(info_panel);
                                            panel.showTab(OutputPanel.RESULTS);
                                        }
                                });
                            }
                            catch ( Exception e ) {
                                cd.getOut().printError( e.getMessage() );
                            }
                            cd.getOut().close();
                        }
                    }
                                      );
        }
    }

}
