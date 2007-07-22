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
import ise.plugin.svn.command.Revert;
import ise.plugin.svn.data.SVNData;
import ise.plugin.svn.data.AddResults;
import ise.plugin.svn.library.GUIUtils;
import ise.plugin.svn.library.swingworker.*;
import ise.plugin.svn.gui.AddResultsPanel;
import ise.plugin.svn.io.ConsolePrintStream;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;

import org.tmatesoft.svn.core.wc.SVNInfo;

/**
 * ActionListener to perform an svn revert.
 * This is not dependent on ProjectViewer.
 */
public class RevertAction implements ActionListener {
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
    public RevertAction( View view, List<String> paths, String username, String password ) {
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
            final SVNData data = new SVNData();

            // get the paths
            boolean recursive = false;
            for ( String path : paths ) {
                if (path != null) {
                    File file = new File(path);
                    if (file.isDirectory()) {
                        recursive = true;
                    }
                }
            }

            // user confirmations
            if ( recursive ) {
                // have the user verify they want a recursive revert
                int response = JOptionPane.showConfirmDialog( view, "Recursively revert all files in selected directories?", "Recursive Revert?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE );
                if ( response == JOptionPane.CANCEL_OPTION ) {
                    return ;
                }
                recursive = response == JOptionPane.YES_OPTION;
            }
            else {
                // have the user confirm they really want to revert
                int response = JOptionPane.showConfirmDialog( view, "Revert selected files?", "Confirm Revert", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE );
                if ( response == JOptionPane.NO_OPTION ) {
                    return ;
                }
            }

            data.setPaths( paths );

            if ( username != null && password != null ) {
                data.setUsername( username );
                data.setPassword( password );
            }

            data.setOut( new ConsolePrintStream( view ) );

            view.getDockableWindowManager().showDockableWindow( "subversion" );
            final OutputPanel panel = SVNPlugin.getOutputPanel( view );
            panel.showConsole();
            Logger logger = panel.getLogger();
            logger.log( Level.INFO, "Reverting ..." );
            for ( Handler handler : logger.getHandlers() ) {
                handler.flush();
            }

            class Runner extends SwingWorker<AddResults, Object> {

                @Override
                public AddResults doInBackground() {
                    try {
                        Revert revert = new Revert();
                        return revert.revert( data );
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
                        AddResults results = get();
                        JPanel results_panel = new AddResultsPanel( results, AddResultsPanel.REVERT );
                        panel.addTab("Revert", results_panel);
                        for (String path : results.getPaths()) {
                            Buffer buffer = jEdit.getBuffer(path);
                            if (buffer != null) {
                                buffer.reload(RevertAction.this.view);
                            }
                        }
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
