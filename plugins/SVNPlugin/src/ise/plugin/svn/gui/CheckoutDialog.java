package ise.plugin.svn.gui;

// imports
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.*;
import org.gjt.sp.jedit.browser.VFSBrowser;

import projectviewer.ProjectViewer;
import projectviewer.config.ProjectOptions;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;
import ise.java.awt.KappaLayout;
import ise.plugin.svn.action.SVNAction;
import ise.plugin.svn.data.*;
import ise.plugin.svn.command.*;
import ise.plugin.svn.library.PasswordHandler;
import ise.plugin.svn.library.PasswordHandlerException;
import org.tmatesoft.svn.core.wc.SVNInfo;

/**
 * Dialog for obtaining the url and local directory for a checkout from a
 * subversion repository, and optionally a username, and password.
 */
public class CheckoutDialog extends JDialog {
    // instance fields
    private View view = null;

    private JTextField url = null;
    private JTextField path = null;
    private JTextField username = null;
    private JPasswordField password = null;

    private boolean cancelled = false;

    public CheckoutDialog( View view ) {
        super( ( JFrame ) view, "Checkout", true );
        this.view = view;
        _init();
    }

    /** Initialises the option pane. */
    protected void _init() {
        JPanel panel = new JPanel( new KappaLayout() );
        panel.setBorder( new EmptyBorder( 6, 6, 6, 6 ) );
        String project_name = getProjectName();

        // subversion repository url field
        JLabel url_label = new JLabel( jEdit.getProperty( SVNAction.PREFIX + "url.label" ) );
        url = new JTextField( jEdit.getProperty( SVNAction.PREFIX + project_name + ".url" ), 30 );

        // populate url field from existing svn info, if available
        List<String> info_path = new ArrayList<String>();
        info_path.add(getProjectRoot());
        SVNData info_data = new SVNData();
        info_data.setPaths(info_path);
        String url_text = null;
        List<SVNInfo> info_results = null;
        try {
            info_results = new Info().getInfo(info_data);
        }
        catch(Exception e) {
            info_results = null;
        }
        if (info_results != null && info_results.size() > 0) {
            SVNInfo svn_info = info_results.get(0);
            if (svn_info != null && svn_info.getURL() != null) {
                url_text = svn_info.getURL().toString();
            }
        }
        if ( url_text != null ) {
            url.setText( url_text );
        }

        // local destination directory
        JLabel path_label = new JLabel( jEdit.getProperty( SVNAction.PREFIX + "path.label" ) );
        path = new JTextField( getProjectRoot(), 30 );
        JButton browse_btn = new JButton( "Browse" );
        browse_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        String[] dirs = GUIUtilities.showVFSFileDialog( view, getProjectRoot(), VFSBrowser.CHOOSE_DIRECTORY_DIALOG, false );
                        if (dirs != null && dirs.length > 0) {
                            path.setText(dirs[0]);
                        }
                    }
                }
                                    );

        // username field
        JLabel username_label = new JLabel( jEdit.getProperty( SVNAction.PREFIX + "username.label" ) );
        username = new JTextField( jEdit.getProperty( SVNAction.PREFIX + project_name + ".username" ), 30 );

        // password field
        JLabel password_label = new JLabel( jEdit.getProperty( SVNAction.PREFIX + "password.label" ) );
        String pwd = jEdit.getProperty( SVNAction.PREFIX + project_name + ".password" );
        if ( pwd != null && pwd.length() > 0 ) {
            try {
                PasswordHandler ph = new PasswordHandler();
                pwd = ph.decrypt( pwd );
            }
            catch ( Exception e ) {
                pwd = "";
            }
        }
        password = new JPasswordField( pwd, 30 );

        // buttons
        KappaLayout kl = new KappaLayout();
        JPanel btn_panel = new JPanel( kl );
        JButton ok_btn = new JButton( "Ok" );
        JButton cancel_btn = new JButton( "Cancel" );
        btn_panel.add( "0, 0, 1, 1, 0, w, 3", ok_btn );
        btn_panel.add( "1, 0, 1, 1, 0, w, 3", cancel_btn );
        kl.makeColumnsSameWidth( 0, 1 );

        ok_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        if ( url == null || url.getText().length() == 0 ) {
                            JOptionPane.showMessageDialog( CheckoutDialog.this, "URL is required.", "Error", JOptionPane.ERROR_MESSAGE );
                            return ;
                        }
                        if ( path == null || path.getText().length() == 0 ) {
                            JOptionPane.showMessageDialog( CheckoutDialog.this, "Directory is required.", "Error", JOptionPane.ERROR_MESSAGE );
                            return ;
                        }
                        cancelled = false;
                        CheckoutDialog.this.setVisible( false );
                        CheckoutDialog.this.dispose();
                    }
                }
                                );

        cancel_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        cancelled = true;
                        CheckoutDialog.this.setVisible( false );
                        CheckoutDialog.this.dispose();
                    }
                }
                                    );


        // add the components to the option panel
        panel.add( "0, 0, 1, 1, E,  , 3", url_label );
        panel.add( "1, 0, 2, 1, 0, w, 3", url );

        panel.add( "0, 1, 1, 1, E,  , 3", path_label );
        panel.add( "1, 1, 2, 1, 0, w, 3", path );
        panel.add( "3, 1, 1, 1, 0, w, 3", browse_btn );

        panel.add( "0, 2, 1, 1, E,  , 3", username_label );
        panel.add( "1, 2, 2, 1, 0, w, 3", username );

        panel.add( "0, 3, 1, 1, E,  , 3", password_label );
        panel.add( "1, 3, 2, 1, 0, w, 3", password );

        panel.add( "0, 4, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 10, true ) );
        panel.add( "0, 5, 4, 1, E,  , 0", btn_panel );

        setContentPane( panel );
        pack();

    }

    public CheckoutData getValues() {
        if ( cancelled ) {
            return null;
        }
        CheckoutData cd = new CheckoutData();
        cd.setURL(url.getText());
        cd.setUsername(username.getText());
        cd.setPassword(new String(password.getPassword()));
        List<String> paths = new ArrayList<String>();
        paths.add(path.getText());
        cd.setPaths(paths);
        return cd;
    }

    private String getProjectName() {
        VPTProject project = ProjectViewer.getActiveProject( view );
        return project == null ? "" : project.getName();
    }

    private String getProjectRoot() {
        VPTProject project = ProjectViewer.getActiveProject( view );
        return project == null ? "" : project.getRootPath();
    }

}
