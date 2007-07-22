package ise.plugin.svn.gui;

// imports
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.browser.VFSBrowser;

import projectviewer.ProjectViewer;
import projectviewer.config.ProjectOptions;
import ise.java.awt.KappaLayout;
import ise.plugin.svn.data.SVNData;
import ise.plugin.svn.library.PasswordHandler;
import ise.plugin.svn.library.PasswordHandlerException;


/**
 * Dialog for adding files and directories.
 */
public class AddDialog extends JDialog {
    // instance fields
    private View view = null;
    private List<String> nodes = null;

    private boolean cancelled = false;

    private SVNData addData = null;

    public AddDialog( View view, List<String> nodes ) {
        super( ( JFrame ) view, "Add", true );
        if ( nodes == null ) {
            throw new IllegalArgumentException( "nodes may not be null" );
        }
        this.view = view;
        this.nodes = nodes;
        _init();
    }

    /** Initialises the option pane. */
    protected void _init() {

        addData = new SVNData();

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
                paths.add(node);
            }
        }
        addData.setPaths(paths);
        addData.setRecursive(recursive);

        JLabel file_label = new JLabel("Adding these files:");
        final JPanel file_panel = new JPanel(new GridLayout(0, 1, 2, 3));
        file_panel.setBackground(Color.WHITE);
        file_panel.setBorder(new EmptyBorder(3, 3, 3, 3));
        for(String path : paths) {
            JCheckBox cb = new JCheckBox(path);
            cb.setSelected(true);
            cb.setBackground(Color.WHITE);
            file_panel.add(cb);
        }

        final JCheckBox recursive_cb = new JCheckBox("Recursively add?");
        recursive_cb.setSelected(recursive);
        recursive_cb.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae) {
                    addData.setRecursive(recursive_cb.isSelected());
                }
        });

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
                        for (Component file : files) {
                            JCheckBox cb = (JCheckBox)file;
                            if (cb.isSelected()) {
                                paths.add(cb.getText());
                            }
                        }
                        if (paths.size() == 0) {
                            // nothing to commit, bail out
                            addData = null;
                        }
                        else {
                            addData.setPaths(paths);
                        }
                        AddDialog.this.setVisible( false );
                        AddDialog.this.dispose();
                    }
                }
                                );

        cancel_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        addData = null;
                        AddDialog.this.setVisible( false );
                        AddDialog.this.dispose();
                    }
                }
                                    );

        // add the components to the option panel
        panel.add( "0, 0, 1, 1, W,  , 3", file_label );
        panel.add( "0, 1, 1, 1, W, wh, 3", new JScrollPane( file_panel ) );
        panel.add( "1, 1, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 120, true));
        panel.add( "0, 2, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 6, true ) );

        panel.add( "0, 3, 1, 1, W,  , 3", recursive_cb );
        panel.add( "0, 4, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 10, true ) );

        panel.add( "0, 5, 1, 1, E,  , 0", btn_panel );

        setContentPane( panel );
        pack();

    }

    public SVNData getSVNData() {
        return addData;
    }
}
