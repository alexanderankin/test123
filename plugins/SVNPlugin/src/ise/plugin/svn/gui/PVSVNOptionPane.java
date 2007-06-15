package ise.plugin.svn.gui;

// imports
import java.awt.*;
import javax.swing.*;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.util.Log;

import projectviewer.config.ProjectOptions;
import ise.java.awt.KappaLayout;
import ise.plugin.svn.library.PasswordHandler;
import ise.plugin.svn.library.PasswordHandlerException;
import ise.plugin.svn.data.*;
import ise.plugin.svn.command.*;

import org.tmatesoft.svn.core.wc.SVNInfo;

/**
 * Option pane for setting the url, username, and password for subversion via
 * ProjectViewer.
 */
public class PVSVNOptionPane extends AbstractOptionPane {
    // instance fields
    public static String PREFIX = "ise.plugin.svn.pv.";

    private JTextField url;
    private JTextField username;
    private JPasswordField password;

    public PVSVNOptionPane() {
        super( "ise.plugin.svn" );
        setLayout(new KappaLayout());
    }

    /** Initialises the option pane. */
    protected void _init() {
        String project_name = getProjectName();

        // url field
        JLabel url_label = new JLabel( jEdit.getProperty( PREFIX + "url.label" ) );
        url = new JTextField( jEdit.getProperty( PREFIX + project_name + ".url" ), 30 );

        // populate url field from existing svn info, if available
        java.util.List<String> info_path = new java.util.ArrayList<String>();
        info_path.add(getProjectRoot());
        SVNData info_data = new SVNData();
        info_data.setPaths(info_path);
        String url_text = null;
        java.util.List<SVNInfo> info_results = null;
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

        // username field
        JLabel username_label = new JLabel( jEdit.getProperty( PREFIX + "username.label" ) );
        username = new JTextField( jEdit.getProperty( PREFIX + project_name + ".username" ), 30 );

        // password field
        JLabel password_label = new JLabel( jEdit.getProperty( PREFIX + "password.label" ) );
        String pwd = jEdit.getProperty( PREFIX + project_name + ".password" );
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

        // add the components to the option panel
        add( "0, 0, 3, 1, W,  , 3", new JLabel("<html><b>Subversion Settings</b>") );

        add( "0, 1, 1, 1, E,  , 3", url_label );
        add( "1, 1, 2, 1, 0, w, 3", url );

        add( "0, 2, 1, 1, E,  , 3", username_label );
        add( "1, 2, 2, 1, 0, w, 3", username );

        add( "0, 3, 1, 1, E,  , 3", password_label );
        add( "1, 3, 2, 1, 0, w, 3", password );

    }

    // #_save() : void
    /** Saves properties from the option pane. */
    protected void _save() {
        String name = getProjectName();
        jEdit.setProperty(
            PREFIX + name + ".url",
            ( url == null ? "" : url.getText() )
        );
        jEdit.setProperty(
            PREFIX + name + ".username",
            ( username == null ? "" : username.getText() )
        );

        char[] pwd_chars = password == null ? new char[0] : password.getPassword();
        String pwd = new String(pwd_chars);
        for (int i = 0; i < pwd_chars.length; i++) {
            pwd_chars[i] = '0';
        }
        try {
            if ( pwd.length() > 0 ) {
                PasswordHandler ph = new PasswordHandler();
                pwd = ph.encrypt( pwd );
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        jEdit.setProperty(
            PREFIX + name + ".password",
            pwd
        );
    }

    private String getProjectName() {
        String project_name = "";
        if ( ProjectOptions.getProject().getName() != null ) {
            project_name = ProjectOptions.getProject().getName();
        }
        return project_name;
    }

    private String getProjectRoot() {
        String project_root = "";
        if ( ProjectOptions.getProject().getRootPath() != null ) {
            project_root = ProjectOptions.getProject().getRootPath();
        }
        return project_root;
    }

}
