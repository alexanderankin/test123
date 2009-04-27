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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.table.*;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

import ise.java.awt.KappaLayout;
import ise.java.awt.LambdaLayout;
import ise.plugin.svn.data.LogData;
import ise.plugin.svn.gui.component.*;

import org.tmatesoft.svn.core.wc.SVNRevision;

/**
 * Dialog for getting revision date ranges to use when calling the Log command.
 */
public class LogDialog extends JDialog {
    // instance fields
    private LogData data = null;

    private boolean recursive = false;
    private SVNRevision startRevision = SVNRevision.create( 0L );
    private SVNRevision endRevision = SVNRevision.HEAD;

    private static Color background = jEdit.getColorProperty( "view.bgColor", Color.WHITE );
    private static Color foreground = jEdit.getColorProperty( "view.fgColor", Color.LIGHT_GRAY );

    public LogDialog( View view, LogData data ) {
        super( ( JFrame ) view, jEdit.getProperty( "ips.Log_Settings", "Log Settings" ), true );
        if ( data == null ) {
            throw new IllegalArgumentException( "data may not be null" );
        }
        this.data = data;
        _init();
    }

    /** Initialises the option pane. */
    protected void _init() {
        JPanel panel = new JPanel( new LambdaLayout() );
        panel.setBorder( new EmptyBorder( 6, 6, 6, 6 ) );

        // list the selected files
        JLabel file_label = new JLabel( jEdit.getProperty( "ips.Show_log_for_these_files>", "Show log for these files:" ) );
        BestRowTable file_table = new BestRowTable();
        final DefaultTableModel file_table_model = new DefaultTableModel(
                    new String[] {
                        "", jEdit.getProperty( "ips.File", "File" )
                    }, data.getPaths().size() ) {
                    public Class getColumnClass( int index ) {
                        if ( index == 0 ) {
                            return Boolean.class;
                        }
                        else {
                            return super.getColumnClass( index );
                        }

                    }
                };
        file_table.setModel( file_table_model );

        // load the table model
        int i = 0;
        for ( String path : data.getPaths() ) {
            if ( path != null ) {
                file_table_model.setValueAt( true, i, 0 );
                file_table_model.setValueAt( path, i, 1 );
                ++i;
            }
        }
        file_table.getColumnModel().getColumn( 0 ).setMaxWidth( 25 );
        file_table.getColumnModel().getColumn( 1 ).setPreferredWidth( 575 );
        file_table.packRows();

        // ask if directories should be recursed
        final JCheckBox recursive_cb = new JCheckBox( jEdit.getProperty( "ips.Recurse_subdirectories?", "Recurse subdirectories?" ) );
        recursive_cb.setSelected( recursive );
        recursive_cb.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        data.setRecursive( recursive_cb.isSelected() );
                    }
                }
                                      );

        // radio buttons to choose revision range, all, by number, or by date
        final JRadioButton show_all = new JRadioButton( jEdit.getProperty( "ips.All", "All" ) );
        show_all.setSelected( true );
        show_all.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        AbstractButton btn = ( AbstractButton ) ae.getSource();
                        if ( btn.isSelected() ) {
                            startRevision = SVNRevision.create( 0L );
                            endRevision = SVNRevision.HEAD;
                        }
                    }
                }
                                  );

        JRadioButton revision_range = new JRadioButton( jEdit.getProperty( "ips.By_range>", "By range:" ) );

        ButtonGroup revision_group = new ButtonGroup();
        revision_group.add( show_all );
        revision_group.add( revision_range );

        // revision chooser panels
        final RevisionSelectionPanel start_revision_panel = new RevisionSelectionPanel( jEdit.getProperty( "ips.Start_Revision", "Start Revision" ) );
        final RevisionSelectionPanel end_revision_panel = new RevisionSelectionPanel( jEdit.getProperty( "ips.End_Revision", "End Revision" ) );
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
        ( ( JSpinner.NumberEditor ) max_logs.getEditor() ).getModel().setMinimum( Integer.valueOf( 1 ) );
        ( ( JSpinner.NumberEditor ) max_logs.getEditor() ).getModel().setValue( Integer.valueOf( 1000 ) );
        max_logs.setForeground( foreground );
        max_logs.setBackground( background );

        final JCheckBox stopOnCopy = new JCheckBox( jEdit.getProperty( "ips.Stop_on_copy", "Stop on copy" ) );
        final JCheckBox showPaths = new JCheckBox( jEdit.getProperty( "ips.Show_paths", "Show paths" ) );
        showPaths.setSelected( true );

        // buttons
        KappaLayout kl = new KappaLayout();
        JPanel btn_panel = new JPanel( kl );
        JButton ok_btn = new JButton( jEdit.getProperty( "ips.Ok", "Ok" ) );
        JButton cancel_btn = new JButton( jEdit.getProperty( "ips.Cancel", "Cancel" ) );
        btn_panel.add( "0, 0, 1, 1, 0, w, 3", ok_btn );
        btn_panel.add( "1, 0, 1, 1, 0, w, 3", cancel_btn );
        kl.makeColumnsSameWidth( 0, 1 );

        ok_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        // fill in the log data object -- get the paths
                        List<String> paths = new ArrayList<String>();
                        for ( int row = 0; row < file_table_model.getRowCount(); row++ ) {
                            Boolean selected = ( Boolean ) file_table_model.getValueAt( row, 0 );
                            if ( selected ) {
                                paths.add( ( String ) file_table_model.getValueAt( row, 1 ) );
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
                        data.setMaxLogs( ( ( Integer ) max_logs.getValue() ).intValue() );

                        // set whether or not to recurse past copy points in the
                        // revision history
                        data.setStopOnCopy( stopOnCopy.isSelected() );

                        // set whether or not to show the other files that were part
                        // of the revision history
                        data.setShowPaths( showPaths.isSelected() );

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
        JScrollPane file_scroller = new JScrollPane( file_table );
        file_scroller.getViewport().setPreferredSize( new Dimension( 600, Math.min( file_table.getBestHeight() + 50, 250 ) ) );
        panel.add( "0, 0, 2, 1, W,  , 3", file_label );
        panel.add( "0, 1, 2, 1, W, wh, 3", file_scroller );

        panel.add( "0, 2, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 6, true ) );

        panel.add( "0, 3, 2, 1, W,  , 3", recursive_cb );

        panel.add( "0, 4, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 6, true ) );

        panel.add( "0, 5, 2, 1, W,  , 3", new JLabel( jEdit.getProperty( "ips.Revision_Range>", "Revision Range:" ) ) );
        panel.add( "0, 6, 2, 1, W,  , 3", show_all );
        panel.add( "0, 7, 2, 1, W,  , 3", revision_range );
        panel.add( "0, 8, 1, 1, W,  , 3", start_revision_panel );
        panel.add( "1, 8, 1, 1, E,  , 3", end_revision_panel );
        panel.add( "0, 9, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 6, true ) );

        panel.add( "0, 10, 1, 1, W, , 3", stopOnCopy );
        panel.add( "0, 11, 1, 1, W, , 3", showPaths );

        JPanel max_logs_panel = new JPanel( new FlowLayout() );
        max_logs_panel.add( new JLabel( jEdit.getProperty( "ips.Maximum_log_entries_to_show>", "Maximum log entries to show:" ) ) );
        max_logs_panel.add( max_logs );
        panel.add( "0, 12, 2, 1, W,  , 3", max_logs_panel );

        panel.add( "0, 13, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 11, true ) );

        panel.add( "0, 14, 2, 1, E,  , 0", btn_panel );

        setContentPane( panel );
        pack();

    }

    public LogData getData() {
        return data;
    }

    public static void main ( String[] args ) {
        LogData data = new LogData();
        List<String> paths = new ArrayList<String>();
        paths.add( "/home/danson/path/filename.txt" );
        data.setPaths( paths );
        LogDialog dialog = new LogDialog( null, data );
        dialog.setVisible( true );
    }
}