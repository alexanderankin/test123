package ise.plugin.svn.action;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingUtilities;
import projectviewer.config.ProjectOptions;
import projectviewer.vpt.VPTNode;
import ise.plugin.svn.gui.CheckoutDialog;
import ise.plugin.svn.gui.OutputPanel;
import ise.plugin.svn.library.GUIUtils;
import ise.plugin.svn.io.*;
import ise.plugin.svn.data.*;
import ise.plugin.svn.*;
import java.util.logging.*;
import ise.plugin.svn.command.*;

public class CheckoutAction extends NodeActor {

    public void actionPerformed( ActionEvent ae ) {
        CheckoutDialog dialog = new CheckoutDialog( view );
        GUIUtils.center( view, dialog );
        dialog.setVisible( true );
        final CheckoutData cd = dialog.getValues();
        if ( cd == null ) {
            return ;        // user cancelled
        }

        cd.setOut( new ConsolePrintStream( this ) );

        view.getDockableWindowManager().showDockableWindow( "subversion" );
        final OutputPanel panel = SVNPlugin.getOutputPanel( view );
        panel.showTab( OutputPanel.CONSOLE );
        Logger logger = panel.getLogger();
        logger.log( Level.INFO, "Check out ..." );
        for ( Handler handler : logger.getHandlers() ) {
            handler.flush();
        }

        SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        try {
                            Checkout checkout = new Checkout();
                            long revision = checkout.doCheckout(cd);
                            cd.getOut().print( "Checkout completed, revision " + revision );
                        }
                        catch ( Exception e ) {
                            cd.getOut().printError( e.getMessage() );
                        }
                    }
                }
                                  );
    }
}
