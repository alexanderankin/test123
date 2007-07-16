package ise.plugin.svn.action;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
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


public class CheckoutAction implements ActionListener, PropertyChangeListener {

    private View view = null;
    private String url = null;
    private String username = null;
    private String password = null;
    private CheckoutData cd = null;

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

    public void propertyChange( PropertyChangeEvent pce ) {
        // check for done
        if ( "done".equals( pce.getPropertyName() ) ) {
            createProject( pce.getNewValue().toString() );
        }
    }

    public void actionPerformed( ActionEvent ae ) {
        NodeActor.setupLibrary();
        CheckoutDialog dialog = new CheckoutDialog( view, url );
        GUIUtils.center( view, dialog );
        dialog.setVisible( true );
        cd = dialog.getValues();
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
                    firePropertyChange( "done", revision.toString(), "false" );
                }
                catch ( Exception e ) {
                    cd.getOut().printError( e.getMessage() );
                }
            }

        }
        Runner runner = new Runner();
        runner.addPropertyChangeListener( this );
        runner.execute();
    }

    private void createProject( String revision ) {
        int make_project = JOptionPane.showConfirmDialog( view,
                "Checkout complete at revision " + revision + ".\n" +
                "Would you like to create a project from these files?",
                "Create Project?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE );
        if ( make_project != JOptionPane.YES_OPTION ) {
            return ;
        }

        // use the directory name that the user entered for the location of the
        // checkout as the default project name.  The UI will let the user
        // change it if they want.
        String path = cd.getPaths().get(0);
        int index = path.lastIndexOf( "/" );
        index = index == -1 ? 0 : index + 1;
        String project_name = path.substring( index );
        final VPTProject project = new VPTProject( project_name );

        project.setRootPath( path );

        // dialog for the project properties
        final ProjectPropertiesPane pp_pane = new ProjectPropertiesPane( project, true, path );
        final JDialog dialog = new JDialog( view, "Create Project", true );
        JPanel panel = new JPanel( new BorderLayout() );
        panel.setBorder( BorderFactory.createEmptyBorder( 6, 6, 6, 6 ) );
        panel.add( pp_pane, BorderLayout.CENTER );
        JPanel btn_panel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 3, 3 ) );
        JButton ok_btn = new JButton( " OK " );
        ok_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        dialog.setVisible( false );
                        dialog.dispose();

                        boolean ok = false;
                        try {
                            // save the panel and check the 'isOK' method result
                            pp_pane.save();
                            Boolean b = ( Boolean ) PrivilegedAccessor.invokeMethod( pp_pane, "isOK", null );
                            ok = b.booleanValue();
                        }
                        catch ( Exception e ) {
                            e.printStackTrace();
                            return ;
                        }
                        if ( ok ) {
                            final ProjectManager pm = ProjectManager.getInstance();
                            if ( pm != null ) {
                                try {
                                    // add the project to ProjectViewer, but first
                                    // get the group. If the user picked one, it will be
                                    // a project property, otherwise, default to the root project group
                                    VPTGroup group = ( VPTGroup ) project.getObjectProperty( "projectviewer.new-parent" );
                                    if (group == null) {
                                        group = VPTRoot.getInstance();
                                    }
                                    pm.addProject( project, group );

                                    // offer to import the files just checked out into ProjectViewer
                                    // note that Importers are runnable, so add the importer to the
                                    // event queue for handling.  If Importer.doImport is called here,
                                    // there will be a deadlock.
                                    RootImporter importer = new RootImporter( project, ProjectViewer.getViewer( view ), true );
                                    SwingUtilities.invokeLater(importer);

                                    // I wonder about this -- if the import happens later
                                    // (because of the invokeLater), will the save happen correctly?
                                    pm.save();

                                    // set ProjectViewer to show the new node
                                    ProjectViewer.setActiveNode( view, project );
                                }
                                catch ( Exception e ) {
                                    e.printStackTrace( System.err );
                                }
                            }
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
