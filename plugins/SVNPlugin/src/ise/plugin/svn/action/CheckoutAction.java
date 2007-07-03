package ise.plugin.svn.action;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
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
import ise.plugin.svn.library.swingworker.*;

import org.gjt.sp.jedit.View;


public class CheckoutAction implements ActionListener {

    private View view = null;
    private String url = null;
    private String username = null;
    private String password = null;

    /**
     * @param view the View in which to display results
     * @param paths a list of paths to be added
     * @param username the username for the svn repository
     * @param password the password for the username
     */
    public CheckoutAction( View view, String username, String password ) {
        if ( view == null )
            throw new IllegalArgumentException( "view may not be null" );
        this.view = view;
        this.username = username;
        this.password = password;
    }

    public CheckoutAction(View view, CheckoutData data) {
        if ( view == null )
            throw new IllegalArgumentException( "view may not be null" );
        if ( data == null )
            throw new IllegalArgumentException( "data may not be null" );
        this.view = view;
        this.url = data.getURL();
        this.username = data.getUsername();
        this.password = data.getPassword();
    }

    public void actionPerformed( ActionEvent ae ) {
        CheckoutDialog dialog = new CheckoutDialog( view, url );
        GUIUtils.center( view, dialog );
        dialog.setVisible( true );
        final CheckoutData cd = dialog.getValues();
        if ( cd == null ) {
            return ;        // user cancelled
        }

        cd.setOut( new ConsolePrintStream( view ) );

        view.getDockableWindowManager().showDockableWindow( "subversion" );
        final OutputPanel panel = SVNPlugin.getOutputPanel( view );
        panel.showConsole();
        Logger logger = panel.getLogger();
        logger.log( Level.INFO, "Check out ..." );
        for ( Handler handler : logger.getHandlers() ) {
            handler.flush();
        }

        class Runner extends SwingWorker<Long, Object> {

            @Override
            public Long doInBackground() {
                try {
                    Checkout checkout = new Checkout();
                    return checkout.doCheckout( cd );
                }
                catch ( Exception e ) {
                    cd.getOut().printError( e.getMessage() );
                }
                finally {
                    cd.getOut().close();
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    cd.getOut().print( "Checkout completed, revision " + get() );
                }
                catch ( Exception e ) {
                    cd.getOut().printError( e.getMessage() );
                }
            }
        }
        ( new Runner() ).execute();
    }
}
