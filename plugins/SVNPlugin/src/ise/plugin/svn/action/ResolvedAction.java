package ise.plugin.svn.action;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import projectviewer.vpt.VPTNode;
import ise.plugin.svn.gui.OutputPanel;

import ise.plugin.svn.SVNPlugin;
import ise.plugin.svn.command.Resolved;
import ise.plugin.svn.data.SVNData;
import ise.plugin.svn.data.AddResults;
import ise.plugin.svn.library.GUIUtils;
import ise.plugin.svn.library.swingworker.*;
import ise.plugin.svn.gui.AddResultsPanel;
import ise.plugin.svn.io.ConsolePrintStream;

import org.tmatesoft.svn.core.wc.SVNInfo;

public class ResolvedAction extends NodeActor {

    public void actionPerformed( ActionEvent ae ) {
        if ( nodes != null && nodes.size() > 0 ) {
            final SVNData data = new SVNData();

            // get the paths
            boolean recursive = false;
            List<String> paths = new ArrayList<String>();
            for ( VPTNode node : nodes ) {
                if ( node != null && node.getNodePath() != null ) {
                    paths.add( node.getNodePath() );
                    if ( node.isDirectory() ) {
                        recursive = true;
                    }
                }
            }

            // user confirmations
            if ( recursive ) {
                // have the user verify they want a recursive resolve
                int response = JOptionPane.showConfirmDialog( getView(), "Recursively resolve all files in selected directories?", "Recursive Resolved?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE );
                if ( response == JOptionPane.CANCEL_OPTION ) {
                    return ;
                }
                recursive = response == JOptionPane.YES_OPTION;
            }
            else {
                // have the user confirm they really want to resolve
                int response = JOptionPane.showConfirmDialog( getView(), "Resolve selected files?", "Confirm Resolve", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE );
                if ( response == JOptionPane.NO_OPTION ) {
                    return ;
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
            logger.log( Level.INFO, "Resolving ..." );
            for ( Handler handler : logger.getHandlers() ) {
                handler.flush();
            }

            class Runner extends SwingWorker<AddResults, Object> {

                @Override
                public AddResults doInBackground() {
                    try {
                        Resolved resolve = new Resolved();
                        return resolve.resolve( data );
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
                        JPanel results_panel = new AddResultsPanel( get(), true );
                        panel.setResultsPanel( results_panel );
                        panel.showTab( OutputPanel.RESULTS );
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
