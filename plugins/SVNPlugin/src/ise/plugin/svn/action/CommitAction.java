package ise.plugin.svn.action;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;
import javax.swing.SwingUtilities;
import projectviewer.vpt.VPTNode;
import ise.plugin.svn.command.Commit;
import ise.plugin.svn.command.CommitData;
import ise.plugin.svn.library.GUIUtils;

public class CommitAction extends NodeActor {

    static CommitDialog dialog = null;

    public void actionPerformed( ActionEvent ae ) {
        if ( node != null ) {
            if ( dialog == null ) {
                dialog = new CommitDialog( view );
                GUIUtils.center( view, dialog );
            }
            dialog.setVisible( true );
            // don't dispose of the dialog, by keeping it, we keep the last
            // entered comment available for the user so they don't have to
            // re-enter it in case there is some problem with the commit.

            final String comment = dialog.getComment();
            if ( comment == null ) {
                return ;     // null comments means user cancelled
            }

            final CommitData cd = new CommitData();
            List<String> paths = new ArrayList<String>();
            String path = node.getNodePath();
            paths.add( path );
            cd.setPaths( paths );
            cd.setRecursive( node.isDirectory() );
            cd.setCommitMessage( comment );
            if ( username != null && password != null ) {
                cd.setUsername( username );
                cd.setPassword( password );
            }
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            cd.setOut( new PrintStream(new BufferedOutputStream(baos)) );

            SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            view.getDockableWindowManager().showDockableWindow( "console" );

                            try {
                                Commit commit = new Commit( );
                                commit.commit(cd);
                                print(baos.toString());
                            }
                            catch ( Exception e ) {
                                printError( e.getMessage() );
                            }
                        }
                    }
                                      );
        }
    }
}
