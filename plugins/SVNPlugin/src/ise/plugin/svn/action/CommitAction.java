package ise.plugin.svn.action;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;
import javax.swing.SwingUtilities;
import projectviewer.vpt.VPTNode;
import ise.plugin.svn.command.Commit;
import ise.plugin.svn.data.CommitData;
import ise.plugin.svn.library.GUIUtils;
import ise.plugin.svn.io.ConsolePrintStream;

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
            cd.setOut( new ConsolePrintStream(this) );

            SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            view.getDockableWindowManager().showDockableWindow( "console" );

                            try {
                                Commit commit = new Commit( );
                                commit.commit( cd );
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
