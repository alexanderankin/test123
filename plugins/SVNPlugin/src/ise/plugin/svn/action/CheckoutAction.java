package ise.plugin.svn.action;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Stack;
import javax.swing.*;
import projectviewer.ProjectManager;
import projectviewer.ProjectViewer;
import projectviewer.config.ProjectPropertiesPane;
import projectviewer.importer.RootImporter;
import projectviewer.vpt.*;
import ise.plugin.svn.gui.CheckoutDialog;
import ise.plugin.svn.gui.OutputPanel;
import ise.plugin.svn.library.GUIUtils;
import ise.plugin.svn.library.PrivilegedAccessor;
import ise.plugin.svn.io.*;
import ise.plugin.svn.data.*;
import ise.plugin.svn.*;
import java.util.logging.*;
import ise.plugin.svn.command.*;
import ise.plugin.svn.library.swingworker.SwingWorker;

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

    public CheckoutAction( View view, CheckoutData data ) {
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
        NodeActor.setupLibrary();
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
                    Long revision = get();
                    if ( revision == null ) {
                        throw new Exception( "Checkout failed." );
                    }
                    cd.getOut().print( "Checkout completed, revision " + revision );
                    int make_project = JOptionPane.showConfirmDialog( view,
                            "Checkout complete at revision " + revision + ".\n" +
                            "Would you like to create a project from these files?",
                            "Create Project?",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE );
                    if ( make_project == JOptionPane.YES_OPTION ) {
                        createProject();
                    }
                }
                catch ( Exception e ) {
                    cd.getOut().printError( e.getMessage() );
                }
            }

            private void createProject() {
                int index = cd.getURL().lastIndexOf( "/" );
                index = index == -1 ? 0 : index + 1;
                String project_name = cd.getURL().substring( index );
                final VPTProject project = new VPTProject( project_name );
                project.setRootPath( cd.getPaths().get( 0 ) );
                final ProjectPropertiesPane pp_pane = new ProjectPropertiesPane( project, true, cd.getPaths().get( 0 ) );
                final JDialog dialog = new JDialog( view, "Create Project", true );
                JPanel panel = new JPanel( new BorderLayout() );
                panel.setBorder( BorderFactory.createEmptyBorder( 6, 6, 6, 6 ) );
                panel.add( pp_pane, BorderLayout.CENTER );
                JPanel btn_panel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 3, 3 ) );
                JButton ok_btn = new JButton( " OK " );
                ok_btn.addActionListener( new ActionListener() {
                            public void actionPerformed( ActionEvent ae ) {
                                boolean ok = false;
                                try {
                                    System.out.println("1");
                                    pp_pane.save();
                                    System.out.println("2");
                                    Boolean b = ( Boolean ) PrivilegedAccessor.invokeMethod( pp_pane, "isOK", null );
                                    System.out.println("3");
                                    ok = b.booleanValue();
                                    System.out.println("4");
                                }
                                catch ( Exception e ) {
                                    e.printStackTrace();
                                    return ;
                                }
                                System.out.println("5");
                                if ( ok ) {
                                    ProjectManager.getInstance().addProject( project, ( VPTGroup ) project.getObjectProperty( "projectviewer.new-parent" ) );
                                    dialog.setVisible( false );
                                    dialog.dispose();
                                    RootImporter importer = new RootImporter( project, ProjectViewer.getViewer( view ), true );
                                    importer.doImport();
                                    ProjectViewer.setActiveNode( view, project );
                                }
                            }
                        }
                                        );
                JButton cancel_btn = new JButton( "Cancel" );
                cancel_btn.addActionListener( new ActionListener() {
                            public void actionPerformed( ActionEvent ae ) {
                                dialog.setVisible( false );
                                dialog.dispose();
                            }
                        }
                                            );
                btn_panel.add( ok_btn );
                btn_panel.add( cancel_btn );

                panel.add( btn_panel, BorderLayout.SOUTH );
                dialog.setContentPane( panel );
                pp_pane.init();
                dialog.pack();
                GUIUtils.center( view, dialog );
                dialog.setVisible( true );
            }
        }
        ( new Runner() ).execute();
    }
}
