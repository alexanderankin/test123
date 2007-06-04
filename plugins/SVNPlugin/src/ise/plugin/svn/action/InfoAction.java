package ise.plugin.svn.action;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingUtilities;
import projectviewer.vpt.VPTNode;
import ise.plugin.svn.command.InfoCommand;

public class InfoAction extends NodeActor {


    public void actionPerformed( ActionEvent ae ) {
        if ( node != null ) {
            SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            view.getDockableWindowManager().showDockableWindow( "console" );
                            InfoCommand command = new InfoCommand();
                            String[] params;
                            if (username != null && password != null) {
                                params = new String[]{node.getNodePath(), username, password};
                            }
                            else {
                                params = new String[] {node.getNodePath() };
                            }

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
