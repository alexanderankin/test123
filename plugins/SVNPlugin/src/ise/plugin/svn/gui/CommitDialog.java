package ise.plugin.svn.gui;

// imports
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
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
import ise.plugin.svn.data.CommitData;
import ise.plugin.svn.library.PasswordHandler;
import ise.plugin.svn.library.PasswordHandlerException;


/**
 * Dialog for obtaining a comment for a commit.
 */
public class CommitDialog extends JDialog {
    // instance fields
    private View view = null;
    private List<String> nodes = null;

    private JTextArea comment = null;

    private boolean cancelled = false;

    private CommitData commitData = null;

    private static Vector<String> previousComments = null;
    private static final String SELECT = "-- Select --";

    public CommitDialog( View view, List<String> nodes ) {
        super( ( JFrame ) view, "Commit", true );
        if ( nodes == null ) {
            throw new IllegalArgumentException( "nodes may not be null" );
        }
        this.view = view;
        this.nodes = nodes;
        _init();
    }

    /** Initialises the option pane. */
    protected void _init() {

        // load previous comments
        if ( previousComments == null ) {
            previousComments = new Vector<String>();

            for ( int i = 1; i < 10; i++ ) {
                String comment = jEdit.getProperty( "ise.plugin.svn.comment." + i );
                if ( comment == null ) {
                    break;
                }
                previousComments.insertElementAt( comment, 0 );
            }
        }
        if ( previousComments.size() > 0 && !previousComments.contains(SELECT)) {
            previousComments.insertElementAt( SELECT, 0 );
        }

        commitData = new CommitData();

        JPanel panel = new JPanel( new KappaLayout() );
        panel.setBorder( new EmptyBorder( 6, 6, 6, 6 ) );

        // set recursive value, if any of the nodes are a directory, set
        // recursive to true.  While we're at it, make a list of strings of
        // the node paths.
        boolean recursive = false;
        List<String> paths = new ArrayList<String>();
        for ( String node : nodes ) {
            if ( node != null ) {
                File file = new File(node);
                if ( file.isDirectory() ) {
                    recursive = true;
                }
                paths.add( node );
            }
        }
        commitData.setPaths( paths );
        commitData.setRecursive( recursive );

        JLabel file_label = new JLabel( "Committing these files:" );
        final JPanel file_panel = new JPanel( new GridLayout( 0, 1, 2, 3 ) );
        file_panel.setBackground( Color.WHITE );
        file_panel.setBorder( new EmptyBorder( 3, 3, 3, 3 ) );
        for ( String path : paths ) {
            JCheckBox cb = new JCheckBox( path );
            cb.setSelected( true );
            cb.setBackground( Color.WHITE );
            file_panel.add( cb );
        }

        final JCheckBox recursive_cb = new JCheckBox( "Recursively commit?" );
        recursive_cb.setSelected( recursive );
        recursive_cb.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        commitData.setRecursive( recursive_cb.isSelected() );
                    }
                }
                                      );

        JLabel label = new JLabel( "Enter comment for this commit:" );
        comment = new JTextArea( 10, 50 );
        comment.setLineWrap( true );
        comment.setWrapStyleWord( true );

        // list for previous comments
        final JComboBox commentList = new JComboBox( previousComments );
        commentList.setEditable( false );
        commentList.addItemListener( new ItemListener() {
                    public void itemStateChanged( ItemEvent e ) {
                        if ( SELECT.equals( commentList.getSelectedItem().toString() ) ) {
                            return ;
                        }
                        comment.setText( commentList.getSelectedItem().toString() );
                    }
                }
                                   );

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
                        // get the paths
                        List<String> paths = new ArrayList<String>();
                        Component[] files = file_panel.getComponents();
                        for ( Component file : files ) {
                            JCheckBox cb = ( JCheckBox ) file;
                            if ( cb.isSelected() ) {
                                paths.add( cb.getText() );
                            }
                        }
                        if ( paths.size() == 0 ) {
                            // nothing to commit, bail out
                            commitData = null;
                        }
                        else {
                            commitData.setPaths( paths );
                            String msg = comment.getText();
                            if ( msg == null || msg.length() == 0 ) {
                                msg = "no comment";
                            }
                            else {
                                if (previousComments.size() == 0){
                                    previousComments.insertElementAt(SELECT, 0);
                                }
                                previousComments.insertElementAt( msg, 1 );
                            }
                            commitData.setCommitMessage( msg );
                        }
                        CommitDialog.this._save();
                        CommitDialog.this.setVisible( false );
                        CommitDialog.this.dispose();
                    }
                }
                                );

        cancel_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        commitData = null;
                        CommitDialog.this.setVisible( false );
                        CommitDialog.this.dispose();
                    }
                }
                                    );

        // add the components to the option panel
        panel.add( "0, 0, 1, 1, W,  , 3", file_label );
        panel.add( "0, 1, 1, 1, W, wh, 3", new JScrollPane( file_panel ) );
        panel.add( "1, 1, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 120, true ) );
        panel.add( "0, 2, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 6, true ) );

        panel.add( "0, 3, 1, 1, W,  , 3", recursive_cb );
        panel.add( "0, 4, 1, 1, 0,  , 3", KappaLayout.createVerticalStrut( 6, true ) );

        panel.add( "0, 5, 1, 1, W,  , 3", label );
        panel.add( "0, 6, 1, 1, W, wh, 3", new JScrollPane( comment ) );

        if ( previousComments != null && previousComments.size() > 0 ) {
            panel.add( "0, 7, 1, 1, W,  , 3", new JLabel( "Select a previous comment:" ) );
            panel.add( "0, 8, 1, 1, W, w, 3", commentList );
        }

        panel.add( "0, 9, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 10, true ) );
        panel.add( "0, 10, 1, 1, E,  , 0", btn_panel );

        setContentPane( panel );
        pack();

    }

    protected void _save() {
        if ( previousComments != null ) {
            for (int i = 1; i < Math.min(previousComments.size(), 10); i++) {
                String comment = previousComments.get( i );
                if ( SELECT.equals( comment ) ) {
                    continue;
                }
                if ( comment != null && comment.length() > 0 ) {
                    jEdit.setProperty( "ise.plugin.svn.comment." + i, comment );
                }
            }
        }
    }

    public CommitData getCommitData() {
        return commitData;
    }
}
