package ise.plugin.svn.action;

import ise.plugin.svn.gui.OutputPanel;

import ise.plugin.svn.SVNPlugin;
import ise.plugin.svn.command.Commit;
import ise.plugin.svn.data.CommitData;
import ise.plugin.svn.gui.CommitDialog;
import ise.plugin.svn.gui.CommitResultsPanel;
import ise.plugin.svn.io.ConsolePrintStream;
import ise.plugin.svn.library.GUIUtils;
import ise.plugin.svn.library.swingworker.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.JPanel;

import org.tmatesoft.svn.core.SVNCommitInfo;

import projectviewer.vpt.VPTNode;

public class CommitAction extends NodeActor {

    private CommitDialog dialog = null;

    public void actionPerformed( ActionEvent ae ) {
        if ( nodes != null && nodes.size() > 0 ) {
            dialog = new CommitDialog( view, nodes );
            GUIUtils.center( view, dialog );
            dialog.setVisible( true );
            final CommitData cd = dialog.getCommitData();
            if ( cd == null ) {
                return ;     // null means user cancelled
            }

            if ( username != null && password != null ) {
                cd.setUsername( username );
                cd.setPassword( password );
            }
            cd.setOut( new ConsolePrintStream( this ) );

            view.getDockableWindowManager().showDockableWindow( "subversion" );
            final OutputPanel panel = SVNPlugin.getOutputPanel( view );
            panel.showConsole();
            final Logger logger = panel.getLogger();
            logger.log( Level.INFO, "Committing ..." );
            for ( Handler handler : logger.getHandlers() ) {
                handler.flush();
            }

            class Runner extends SwingWorker<CommitData, Object> {

                @Override
                public CommitData doInBackground() {
                    try {
                        Commit commit = new Commit( );
                        return commit.commit( cd );
                    }
                    catch ( Exception e ) {
                        cd.getOut().printError( e.getMessage() );
                    }
                    finally {
                        cd.getOut().close();
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        JPanel results_panel = new CommitResultsPanel( get() );
                        panel.addTab("Commit", results_panel);
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
