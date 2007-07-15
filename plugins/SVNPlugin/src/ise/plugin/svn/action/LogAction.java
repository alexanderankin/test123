package ise.plugin.svn.action;

import ise.plugin.svn.gui.OutputPanel;

import ise.plugin.svn.SVNPlugin;
import ise.plugin.svn.command.Log;
import ise.plugin.svn.data.LogData;
import ise.plugin.svn.gui.LogDialog;
import ise.plugin.svn.gui.LogResultsPanel;
import ise.plugin.svn.gui.SVNInfoPanel;
import ise.plugin.svn.io.ConsolePrintStream;
import ise.plugin.svn.library.GUIUtils;
import ise.plugin.svn.library.swingworker.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.JPanel;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.gjt.sp.jedit.View;

/**
 * ActionListener to perform an svn log.
 * This is not dependent on ProjectViewer.
 */
public class LogAction implements ActionListener {

    private View view = null;
    private List<String> paths = null;
    private boolean pathsAreUrls = false;
    private String username = null;
    private String password = null;
    private LogData data = null;

    /**
     * @param view the View in which to display results
     * @param paths a list of paths to be added
     * @param username the username for the svn repository
     * @param password the password for the username
     */
    public LogAction( View view, List<String> paths, String username, String password ) {
        if ( view == null )
            throw new IllegalArgumentException( "view may not be null" );
        if ( paths == null )
            throw new IllegalArgumentException( "paths may not be null" );
        this.view = view;
        this.paths = paths;
        this.username = username;
        this.password = password;
    }

    public LogAction(View view, LogData data) {
        if ( view == null )
            throw new IllegalArgumentException( "view may not be null" );
        if ( data == null )
            throw new IllegalArgumentException( "data may not be null" );
        if ( data.getPaths() == null )
            throw new IllegalArgumentException( "paths may not be null" );
        this.view = view;
        this.paths = data.getPaths();
        this.pathsAreUrls = data.pathsAreURLs();
        this.username = data.getUsername();
        this.password = data.getPassword();
    }

    public void actionPerformed( ActionEvent ae ) {
        if ( paths != null && paths.size() > 0 ) {
            data = new LogData();
            data.setPaths( paths );
            data.setPathsAreURLs(pathsAreUrls);

            LogDialog dialog = new LogDialog(view, data);
            GUIUtils.center( view, dialog );
            dialog.setVisible(true);
            data = dialog.getData();
            if (data == null) {
                return;     // null data signals user cancelled
            }

            if ( username != null && password != null ) {
                data.setUsername( username );
                data.setPassword( password );
            }

            data.setOut( new ConsolePrintStream( view ) );

            view.getDockableWindowManager().showDockableWindow( "subversion" );
            final OutputPanel panel = SVNPlugin.getOutputPanel( view );
            panel.showConsole();
            Logger logger = panel.getLogger();
            logger.log( Level.INFO, "Fetching log ..." );
            for ( Handler handler : logger.getHandlers() ) {
                handler.flush();
            }

            class Runner extends SwingWorker < TreeMap < String, List < SVNLogEntry >> , Object > {

                @Override
                public TreeMap < String, List < SVNLogEntry >> doInBackground() {
                    try {
                        Log log = new Log( );
                        log.doLog( data );
                        return log.getLogEntries();
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
                        JPanel results_panel = new LogResultsPanel( get(), data.getShowPaths() );
                        panel.addTab( "Log", results_panel );
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
