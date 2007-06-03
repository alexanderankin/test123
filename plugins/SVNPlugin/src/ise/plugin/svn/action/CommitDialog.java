package ise.plugin.svn.action;

// imports
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.browser.VFSBrowser;

import projectviewer.ProjectViewer;
import projectviewer.config.ProjectOptions;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;
import ise.java.awt.KappaLayout;
import ise.plugin.svn.SVN2;
import ise.plugin.svn.library.PasswordHandler;
import ise.plugin.svn.library.PasswordHandlerException;


/**
 * Dialog for obtaining a comment for a commit.
 */
public class CommitDialog extends JDialog {
    // instance fields
    private View view = null;

    private JTextArea comment = null;

    private boolean cancelled = false;

    public CommitDialog( View view ) {
        super( ( JFrame ) view, "Commit", true );
        this.view = view;
        _init();
    }

    /** Initialises the option pane. */
    protected void _init() {
        JPanel panel = new JPanel( new KappaLayout() );
        panel.setBorder( new EmptyBorder( 6, 6, 6, 6 ) );

        JLabel label = new JLabel( "Enter comment for this commit:" );
        comment = new JTextArea( 10, 50 );
        comment.setLineWrap( true );
        comment.setWrapStyleWord( true );

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
                        cancelled = false;
                        CommitDialog.this.setVisible( false );
                        // don't dispose of the dialog, by keeping it, we keep the last
                        // entered comment available for the user so they don't have to
                        // re-enter it in case there is some problem with the commit.
                    }
                }
                                );

        cancel_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        cancelled = true;
                        CommitDialog.this.setVisible( false );
                        // don't dispose of the dialog, by keeping it, we keep the last
                        // entered comment available for the user so they don't have to
                        // re-enter it in case there is some problem with the commit.
                    }
                }
                                    );

        // add the components to the option panel
        panel.add( "0, 0, 1, 1, W", label );
        panel.add( "0, 1, 1, 1, W", new JScrollPane( comment ) );

        panel.add( "0, 2, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 10, true ) );
        panel.add( "0, 3, 1, 1, E,  , 0", btn_panel );

        setContentPane( panel );
        pack();

    }

    public String getComment() {
        if ( cancelled ) {
            return null;
        }
        return comment.getText();
    }
}
