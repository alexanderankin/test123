package ise.plugin.svn.action;

import ise.plugin.svn.gui.OutputPanel;
import ise.plugin.svn.SVNPlugin;
import ise.plugin.svn.command.Add;
import ise.plugin.svn.command.BrowseRepository;
import ise.plugin.svn.command.Info;
import ise.plugin.svn.data.DiffData;
import ise.plugin.svn.data.AddResults;
import ise.plugin.svn.gui.DiffDialog;
import ise.plugin.svn.gui.AddResultsPanel;
import ise.plugin.svn.gui.SVNInfoPanel;
import ise.plugin.svn.io.ConsolePrintStream;
import ise.plugin.svn.library.GUIUtils;
import ise.plugin.svn.library.swingworker.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EditPane;
import jdiff.DualDiff;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNInfo;

/**
 * ActionListener to perform sort of an svn diff.  While subversion can do a diff,
 * I'm delegating to the JDiff plugin to create and display the diff.
 * This is not dependent on ProjectViewer.
 */
public class DiffAction implements ActionListener {

    private DiffDialog dialog = null;

    private View view = null;
    private String path1 = null;    // local or remote file
    private String path2 = null;    // remote file
    private String username = null;
    private String password = null;

    /**
     * @param view the View in which to display results
     * @param path the name of a local file to be diffed.  A dialog will be shown
     * to let the user pick the revision to diff against.
     * @param username the username for the svn repository
     * @param password the password for the username
     */
    public DiffAction( View view, String path, String username, String password ) {
        if ( view == null )
            throw new IllegalArgumentException( "view may not be null" );
        if ( path == null || path.length() == 0 )
            throw new IllegalArgumentException( "path may not be null" );
        this.view = view;
        this.path1 = path;
        this.username = username;
        this.password = password;
    }

    public void actionPerformed( ActionEvent ae ) {
        if ( path1 != null && path1.length() > 0 ) {
            dialog = new DiffDialog( view, path1 );
            GUIUtils.center( view, dialog );
            dialog.setVisible( true );
            final DiffData data = dialog.getData();
            if ( data == null ) {
                return ;     // null means user cancelled
            }

            if ( username != null && password != null ) {
                data.setUsername( username );
                data.setPassword( password );
            }
            data.setOut( new ConsolePrintStream( view ) );

            view.getDockableWindowManager().showDockableWindow( "subversion" );
            final OutputPanel panel = SVNPlugin.getOutputPanel( view );
            panel.showConsole( );
            Logger logger = panel.getLogger();
            logger.log( Level.INFO, "Preparing to diff ..." );
            for ( Handler handler : logger.getHandlers() ) {
                handler.flush();
            }

            class Runner extends SwingWorker<SVNInfo, Object> {

                @Override
                public SVNInfo doInBackground() {
                    try {
                        Info info = new Info( );
                        List<SVNInfo> infos = info.getInfo(data);
                        if (infos.size() > 0) {
                            return infos.get(0);
                        }
                        return null;
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
                        SVNInfo info = get();
                        SVNURL url = info.getRepositoryRootURL();
                        String path = info.getPath();
                        if (info.getRevision().equals(data.getRevision())) {
                            JOptionPane.showMessageDialog(view, "There is no difference between the local copy and the repository copy", "No Difference", JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }

                        BrowseRepository br = new BrowseRepository();
                        File remote = br.getFile(url.toString(), path, data.getRevision().getNumber(), data.getUsername(), data.getPassword());
                        DualDiff.toggleFor(view);
                        EditPane[] editPanes = view.getEditPanes();
                        editPanes[0].setBuffer(jEdit.openFile(view, path1));
                        editPanes[1].setBuffer(jEdit.openFile(view, remote.getAbsolutePath()));
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
