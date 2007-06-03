package ise.plugin.svn.action;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingUtilities;
import projectviewer.vpt.VPTNode;
import ise.plugin.svn.command.CommitCommand;
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

            SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            view.getDockableWindowManager().showDockableWindow( "console" );
                            CommitCommand command = new CommitCommand();
                            String[] params = new String[] {node.getNodePath(), comment };
                            try {
                                String result = command.execute( params );
                                print( result );
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
