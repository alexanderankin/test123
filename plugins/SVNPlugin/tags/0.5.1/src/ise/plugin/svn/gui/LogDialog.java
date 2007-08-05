/*
Copyright (c) 2007, Dale Anson
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.
* Neither the name of the author nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package ise.plugin.svn.gui;

// imports
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.browser.VFSBrowser;

import projectviewer.ProjectViewer;
import projectviewer.config.ProjectOptions;
import ise.java.awt.KappaLayout;
import ise.plugin.svn.data.LogData;
import ise.plugin.svn.library.PasswordHandler;
import ise.plugin.svn.library.PasswordHandlerException;

import org.tmatesoft.svn.core.wc.SVNRevision;

/**
 * Dialog for getting revision date ranges to use when calling the Log command.
 */
public class LogDialog extends JDialog {
    // instance fields
    private View view = null;
    private LogData data = null;

    private boolean recursive = false;

    private SVNRevision startRevision = SVNRevision.create( 0L );
    private SVNRevision endRevision = SVNRevision.HEAD;

    private boolean cancelled = false;

    public LogDialog( View view, LogData data ) {
        super( ( JFrame ) view, "Log Settings", true );
        if ( data == null ) {
            throw new IllegalArgumentException( "data may not be null" );
        }
        this.view = view;
        this.data = data;
        _init();
    }

    /** Initialises the option pane. */
    protected void _init() {

        JPanel panel = new JPanel( new KappaLayout() );
        panel.setBorder( new EmptyBorder( 6, 6, 6, 6 ) );

        // list the selected files
        JLabel file_label = new JLabel( "Show log for these files:" );
        final JPanel file_panel = new JPanel( new GridLayout( 0, 1, 2, 3 ) );
        file_panel.setBackground( Color.WHITE );
        file_panel.setBorder( new EmptyBorder( 3, 3, 3, 3 ) );
        for ( String path : data.getPaths() ) {
            JCheckBox cb = new JCheckBox( path );
            cb.setSelected( true );
            cb.setBackground( Color.WHITE );
            file_panel.add( cb );
        }

        // ask if directories should be recursed
        final JCheckBox recursive_cb = new JCheckBox( "Recurse subdirectories?" );
        recursive_cb.setSelected( recursive );
        recursive_cb.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        data.setRecursive( recursive_cb.isSelected() );
                    }
                }
                                      );

        // radio buttons to choose revision range, all, by number, or by date
        final JRadioButton show_all = new JRadioButton( "All" );
        show_all.setSelected( true );
        show_all.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        startRevision = SVNRevision.create( 0L );
                        endRevision = SVNRevision.HEAD;
                    }
                }
                                  );

        JRadioButton revision_range = new JRadioButton( "By range:" );

        ButtonGroup revision_group = new ButtonGroup();
        revision_group.add( show_all );
        revision_group.add( revision_range );

        // revision chooser panels
        final RevisionSelectionPanel start_revision_panel = new RevisionSelectionPanel( "Start Revision" );
        final RevisionSelectionPanel end_revision_panel = new RevisionSelectionPanel( "End Revision" );
        start_revision_panel.setEnabled( false );
        end_revision_panel.setEnabled( false );

        revision_range.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        AbstractButton btn = ( AbstractButton ) ae.getSource();
                        start_revision_panel.setEnabled( btn.isSelected() );
                        end_revision_panel.setEnabled( btn.isSelected() );
                    }
                }
                                        );

        show_all.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        AbstractButton btn = ( AbstractButton ) ae.getSource();
                        start_revision_panel.setEnabled( !btn.isSelected() );
                        end_revision_panel.setEnabled( !btn.isSelected() );
                    }
                }
                                  );

        final JSpinner max_logs = new JSpinner();
        ((JSpinner.NumberEditor)max_logs.getEditor()).getModel().setMinimum(new Integer(1));
        ((JSpinner.NumberEditor)max_logs.getEditor()).getModel().setValue(new Integer(100));

        final JCheckBox stopOnCopy = new JCheckBox("Stop on copy");
        final JCheckBox showPaths = new JCheckBox("Show paths");
        showPaths.setSelected(true);

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
                        // fill in the log data object -- get the paths
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
                            data = null;
                        }
                        else {
                            data.setPaths( paths );
                        }

                        // set revision range
                        if ( show_all.isSelected() ) {
                            data.setStartRevision( SVNRevision.create( 0L ) );
                            data.setEndRevision( SVNRevision.HEAD );
                        }
                        else {
                            data.setStartRevision( start_revision_panel.getRevision() );
                            data.setEndRevision( end_revision_panel.getRevision() );
                        }

                        // set number of logs to show
                        data.setMaxLogs(((Integer)max_logs.getValue()).intValue());

                        // set whether or not to recurse past copy points in the
                        // revision history
                        data.setStopOnCopy(stopOnCopy.isSelected());

                        // set whether or not to show the other files that were part
                        // of the revision history
                        data.setShowPaths(showPaths.isSelected());

                        LogDialog.this.setVisible( false );
                        LogDialog.this.dispose();
                    }
                }
                                );

        cancel_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        data = null;
                        LogDialog.this.setVisible( false );
                        LogDialog.this.dispose();
                    }
                }
                                    );

        // add the components to the option panel
        panel.add( "0, 0, 2, 1, W,  , 3", file_label );
        panel.add( "0, 1, 2, 1, W, wh, 3", new JScrollPane( file_panel ) );
        panel.add( "4, 1, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 120, true ) );

        panel.add( "0, 2, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 6, true ) );

        panel.add( "0, 3, 2, 1, W,  , 3", recursive_cb );

        panel.add( "0, 4, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 6, true ) );

        panel.add( "0, 5, 2, 1, W,  , 3", new JLabel( "Revision Range:" ) );
        panel.add( "0, 6, 2, 1, W,  , 3", show_all );
        panel.add( "0, 7, 2, 1, W,  , 3", revision_range );
        panel.add( "0, 8, 1, 1, W,  , 3", start_revision_panel );
        panel.add( "1, 8, 1, 1, E   , 3", end_revision_panel );
        panel.add( "0, 9, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 6, true ) );

        panel.add( "0, 10, 1, 1, W, , 3", stopOnCopy);
        panel.add( "0, 11, 1, 1, W, , 3", showPaths);

        JPanel max_logs_panel = new JPanel(new FlowLayout());
        max_logs_panel.add(new JLabel("Maximum log entries to show:"));
        max_logs_panel.add(max_logs);
        panel.add( "0, 12, 2, 1, W,  , 3", max_logs_panel );

        panel.add( "0, 13, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 11, true ) );

        panel.add( "0, 14, 2, 1, E,  , 0", btn_panel );

        setContentPane( panel );
        pack();

    }

    public LogData getData() {
        return data;
    }
}
