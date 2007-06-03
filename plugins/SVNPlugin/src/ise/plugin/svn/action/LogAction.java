package ise.plugin.svn.action;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingUtilities;
import projectviewer.vpt.VPTNode;
import ise.plugin.svn.command.LogCommand;

public class LogAction extends NodeActor {

    public void actionPerformed( ActionEvent ae ) {
        if ( node != null ) {
            SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            view.getDockableWindowManager().showDockableWindow( "console" );
                            LogCommand command = new LogCommand();
                            String[] params = new String[] {node.getNodePath() };
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

    public void prepareForNode( VPTNode n ) {
        node = n;
    }
}
