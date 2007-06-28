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

import org.gjt.sp.jedit.View;

import org.tmatesoft.svn.core.SVNCommitInfo;


/**
 * ActionListener to perform an svn commit.
 * This is not dependent on ProjectViewer.
 */
public class CommitAction implements ActionListener {

    private CommitDialog dialog = null;
    private View view = null;
    private List<String> paths = null;
    private String username = null;
    private String password = null;

    /**
     * @param view the View in which to display results
     * @param paths a list of paths to be added
     * @param username the username for the svn repository
     * @param password the password for the username
     */
    public CommitAction( View view, List<String> paths, String username, String password ) {
        if ( view == null )
            throw new IllegalArgumentException( "view may not be null" );
        if ( paths == null )
            throw new IllegalArgumentException( "paths may not be null" );
        this.view = view;
        this.paths = paths;
        this.username = username;
        this.password = password;
    }


    public void actionPerformed( ActionEvent ae ) {
        if ( paths != null && paths.size() > 0 ) {
            dialog = new CommitDialog( view, paths );
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
            cd.setOut( new ConsolePrintStream( view ) );

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
