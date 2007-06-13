package ise.plugin.svn.action;

import ise.plugin.svn.OutputPanel;
import ise.plugin.svn.SVNPlugin;
import ise.plugin.svn.command.Add;
import ise.plugin.svn.command.Info;
import ise.plugin.svn.data.AddData;
import ise.plugin.svn.data.AddResults;
import ise.plugin.svn.gui.AddDialog;
import ise.plugin.svn.gui.AddResultsPanel;
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

public class AddAction extends NodeActor {

    private AddDialog dialog = null;

    public void actionPerformed( ActionEvent ae ) {
        if ( nodes != null && nodes.size() > 0 ) {
            dialog = new AddDialog( view, nodes );
            GUIUtils.center( view, dialog );
            dialog.setVisible( true );
            final AddData cd = dialog.getAddData();
            if ( cd == null ) {
                return ;     // null means user cancelled
            }

            if ( username != null && password != null ) {
                cd.setUsername( username );
                cd.setPassword( password );
            }
            cd.setOut( new ConsolePrintStream(this) );

            view.getDockableWindowManager().showDockableWindow( "subversion" );
            final OutputPanel panel = SVNPlugin.getOutputPanel(view);
            panel.showTab(OutputPanel.CONSOLE);
            Logger logger = panel.getLogger();
            logger.log(Level.INFO, "Preparing to add ...");
            for(Handler handler : logger.getHandlers()) {
                handler.flush();
            }

            SwingUtilities.invokeLater( new Runnable() {
                        public void run() {

                            try {
                                Add add = new Add( );
                                final AddResults results = add.add( cd );
                                SwingUtilities.invokeLater(new Runnable(){
                                        public void run() {
                                            JPanel results_panel = new AddResultsPanel(results);
                                            panel.setResultsPanel(results_panel);
                                            panel.showTab(OutputPanel.RESULTS);
                                        }
                                });
                            }
                            catch ( Exception e ) {
                                cd.getOut().printError( e.getMessage() );
                            }
                        }
                    }
                                      );
        }
    }
}
