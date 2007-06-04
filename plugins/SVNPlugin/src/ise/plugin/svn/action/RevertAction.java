package ise.plugin.svn.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import ise.plugin.svn.command.RevertCommand;
import projectviewer.vpt.VPTNode;

public class RevertAction extends NodeActor {

    public void actionPerformed( ActionEvent ae ) {
        if ( node != null ) {

            // if user has selected a directory, ask if the revert should be
            // applied recursively, and give the user the opportunity to
            // cancel the revert.
            boolean is_directory = node.isDirectory();
            boolean recursive = false;
            if ( is_directory ) {
                int choice = JOptionPane.showConfirmDialog( null, "Revert all files in this directory and subdirectories?", "Apply Recursively?", JOptionPane.YES_NO_CANCEL_OPTION );
                if ( choice == JOptionPane.CANCEL_OPTION ) {
                    return ;
                }
                recursive = choice == JOptionPane.YES_OPTION;
            }

            final String recurse = recursive ? "--recursive" : "";
            SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            view.getDockableWindowManager().showDockableWindow( "console" );
                            RevertCommand command = new RevertCommand();
                            List<String> args = new ArrayList<String>();
                            args.add(node.getNodePath());
                            if (recurse.length() > 0) {
                                args.add("--recursive");
                            }

                            String[] params = new String[args.size()];
                            params = args.toArray(params);

                            try {
                                String result = command.execute( params );
                                print( result );
                            }
                            catch ( Exception e ) {
                                printError( e.getMessage() );
                                e.printStackTrace();
                            }
                        }
                    }
                                      );
        }
    }
}
