package ise.plugin.svn.action;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingUtilities;
import projectviewer.config.ProjectOptions;
import projectviewer.vpt.VPTNode;
import ise.plugin.svn.command.CheckoutCommand;
import ise.plugin.svn.gui.CheckoutDialog;
import ise.plugin.svn.library.GUIUtils;

public class CheckoutAction extends NodeActor {

    public void actionPerformed( ActionEvent ae ) {
        CheckoutDialog dialog = new CheckoutDialog( view );
        GUIUtils.center( view, dialog );
        dialog.setVisible( true );
        final String[] params = dialog.getValues();
        if ( params == null ) {
            return ;
        }

        SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        view.getDockableWindowManager().showDockableWindow( "console" );
                        CheckoutCommand command = new CheckoutCommand();
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
