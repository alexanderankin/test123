package ise.plugin.svn.action;

import ise.plugin.svn.gui.OutputPanel;

import ise.plugin.svn.SVNPlugin;
import ise.plugin.svn.command.Delete;
import ise.plugin.svn.data.AddResults;
import ise.plugin.svn.data.DeleteData;
import ise.plugin.svn.data.DeleteResults;
import ise.plugin.svn.gui.AddResultsPanel;
import ise.plugin.svn.gui.DeleteDialog;
import ise.plugin.svn.gui.SVNInfoPanel;
import ise.plugin.svn.io.ConsolePrintStream;
import ise.plugin.svn.library.GUIUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;

/**
 * ActionListener to perform an svn delete.
 * This is not dependent on ProjectViewer.
 */
public class DeleteAction implements ActionListener {

    private View view = null;
    private List<String> paths = null;
    private DeleteData data = null;
    private String username = null;
    private String password = null;

    /**
     * @param view the View in which to display results
     * @param paths a list of paths to be added
     * @param username the username for the svn repository
     * @param password the password for the username
     */
    public DeleteAction( View view, List<String> paths, String username, String password ) {
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
            data = new DeleteData();

            data.setPaths( paths );

            if ( username != null && password != null ) {
                data.setUsername( username );
                data.setPassword( password );
            }

            data.setOut( new ConsolePrintStream( view ) );

            // show dialog
            DeleteDialog dialog = new DeleteDialog( view, data );
            GUIUtils.center( view, dialog );
            dialog.setVisible( true );
            data = dialog.getData();
            if ( data == null ) {
                return ;     // null data signals user cancelled
            }


            view.getDockableWindowManager().showDockableWindow( "subversion" );
            final OutputPanel panel = SVNPlugin.getOutputPanel( view );
            panel.showConsole();
            Logger logger = panel.getLogger();
            logger.log( Level.INFO, "Deleting ..." );
            for ( Handler handler : logger.getHandlers() ) {
                handler.flush();
            }

            class Runner extends SwingWorker<DeleteResults, Object> {

                @Override
                public DeleteResults doInBackground() {
                    try {
                        Delete delete = new Delete( );
                        return delete.delete( data );
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
                        AddResults results = (AddResults)get();
                        JPanel results_panel = new AddResultsPanel( results, AddResultsPanel.DELETE );
                        panel.addTab("Delete", results_panel);
                    }
                    catch ( Exception e ) {
                        e.printStackTrace();
                    }
                }
            }
            ( new Runner() ).execute();

        }
    }

    public DeleteData getData() {
        return data;
    }
}
