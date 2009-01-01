/*
Copyright (c) 2008, Dale Anson
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

import java.awt.Font;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.*;

import ise.java.awt.*;
import ise.plugin.svn.action.LogAction;
import ise.plugin.svn.data.*;
import ise.plugin.svn.command.*;
import ise.plugin.svn.library.*;
import static ise.plugin.svn.gui.HistoryModelNames.*;
import ise.plugin.svn.gui.component.*;

import org.tmatesoft.svn.core.wc.SVNRevision;

/**
 * Dialog for merge command
 * Merge this (local or remote file or directory)
 * Into this (local file or directory)
 */
public class MergeDialog extends JDialog {
    // instance fields
    private View view = null;       // parent frame

    private String fromPath = null; // merge this path (local or remote)

    private MergeData data = null;

    private boolean canceled = true;

    /**
     * @param view parent frame
     * @param fromPath path/file to merge
     */
    public MergeDialog( View view, String fromPath ) {
        super( ( JFrame ) view, jEdit.getProperty( "ips.Merge", "Merge" ), false );
        setResizable( false );
        this.view = view;
        this.fromPath = fromPath;
        init();
    }

    protected void init() {
        KappaLayout layout = new KappaLayout();
        JPanel panel = new JPanel( layout );
        panel.setBorder( new EmptyBorder( 6, 6, 6, 6 ) );

        // merge from location
        JPanel merge_from_panel = new JPanel( new KappaLayout() );
        final BrowseLocalRemotePanel from_url_panel = new BrowseLocalRemotePanel( view, jEdit.getProperty( "ips.Merge_from_this_path/revision>", "Merge from this path/revision:" ), fromPath, "" );
        final RevisionSelectionPanel start_revision_panel = new RevisionSelectionPanel( jEdit.getProperty( "ips.At_this_revision>", "At this revision:" ) );
        JButton from_log_btn = new JButton( jEdit.getProperty( "ips.Log", "Log" ) );
        start_revision_panel.setShowBase( false );
        start_revision_panel.setShowDate( false );
        merge_from_panel.add( "0, 0, 6, 1, E, w, 3", from_url_panel );
        merge_from_panel.add( "0, 1, 5, 1, E, w, 3", start_revision_panel );
        merge_from_panel.add( "5, 1, 1, 1, 0, w, 3", from_log_btn );
        from_log_btn.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    List<String> paths = new ArrayList<String>();
                    paths.add( from_url_panel.getPath() );
                    LogAction la = new LogAction( view, paths, null, null );
                    la.actionPerformed( null );
                }
            }
        );
        from_log_btn.setToolTipText( jEdit.getProperty( "ips.Show_log_for_this_file.", "Show log for this file." ) );

        // merge to location
        JPanel merge_to_panel = new JPanel( new KappaLayout() );
        final BrowseLocalRemotePanel to_url_panel = new BrowseLocalRemotePanel( view, jEdit.getProperty( "ips.To_this_path/revision>", "To this path/revision:" ), fromPath, "" );
        final RevisionSelectionPanel end_revision_panel = new RevisionSelectionPanel( jEdit.getProperty( "ips.At_this_revision>", "At this revision:" ) );
        JButton to_log_btn = new JButton( jEdit.getProperty( "ips.Log", "Log" ) );
        end_revision_panel.setShowBase( false );
        end_revision_panel.setShowDate( false );
        merge_to_panel.add( "0, 0, 6, 1, E, w, 3", to_url_panel );
        merge_to_panel.add( "0, 1, 5, 1, E, w, 3", end_revision_panel );
        merge_to_panel.add( "5, 1, 1, 1, 0, w, 3", to_log_btn );
        to_log_btn.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    List<String> paths = new ArrayList<String>();
                    paths.add( to_url_panel.getPath() );
                    LogAction la = new LogAction( view, paths, null, null );
                    la.actionPerformed( null );
                }
            }
        );
        to_log_btn.setToolTipText( jEdit.getProperty( "ips.Show_log_for_this_file.", "Show log for this file." ) );

        // sync "from" and "to", so that when the user makes a change in the
        // "from", the "to" changes to be the same. This makes it easier on the
        // user for "-c" style merges
        from_url_panel.addDocumentListener(
            new DocumentListener() {
                public void changedUpdate( DocumentEvent de ) {
                    changed( de );
                }

                public void insertUpdate( DocumentEvent de ) {
                    changed( de );
                }

                public void removeUpdate( DocumentEvent de ) {
                    changed( de );
                }

                private void changed( DocumentEvent de ) {
                    to_url_panel.setPath( from_url_panel.getPath() );
                }
            }
        );
        start_revision_panel.addPropertyChangeListener(
            new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent pce ) {
                    if ( pce != null && pce.getNewValue() != null && pce.getNewValue() instanceof SVNRevision ) {
                        end_revision_panel.setRevision( ( SVNRevision ) pce.getNewValue() );
                    }
                }
            }
        );

        // merge destination location
        String destination = null;
        if ( fromPath != null ) {
            File f = new File( fromPath );
            destination = f.isDirectory() ? f.getAbsolutePath() : f.getParent();
        }
        final BrowseLocalRemotePanel merge_destination_panel = new BrowseLocalRemotePanel( view, jEdit.getProperty( "ips.Place_merged_files_in_this_working_directory>", "Place merged files in this working directory:" ), destination, "", false );

        // merge option checkboxes
        JPanel merge_options_panel = new JPanel( new KappaLayout() );
        merge_options_panel.setBorder( BorderFactory.createTitledBorder( jEdit.getProperty("ips.Merge_Options>", "Merge Options:") ) );
        final JCheckBox dryrun_cb = new JCheckBox( jEdit.getProperty( "ips.Dry_run", "Dry run" ) );
        dryrun_cb.setSelected( true );
        final JCheckBox force_cb = new JCheckBox( jEdit.getProperty( "ips.Force", "Force" ) );
        final JCheckBox recursive_cb = new JCheckBox( jEdit.getProperty( "ips.Recursive", "Recursive" ) );
        recursive_cb.setSelected( true );
        final JCheckBox ignore_ancestry_cb = new JCheckBox( jEdit.getProperty( "ips.Ignore_Ancestry", "Ignore Ancestry" ) );
        merge_options_panel.add( "0, 0, 1, 1, 0, , 6", dryrun_cb );
        merge_options_panel.add( "1, 0, 1, 1, 0, , 6", recursive_cb );
        merge_options_panel.add( "2, 0, 1, 1, 0, , 6", force_cb );
        merge_options_panel.add( "3, 0, 1, 1, 0, , 6", ignore_ancestry_cb );

        // command line sample
        JPanel command_line_panel = new JPanel( new KappaLayout() );
        JLabel command_line_label = new JLabel( jEdit.getProperty( "ips.Command-line_Equivalent>", "Command-line Equivalent:" ) );
        final JTextArea command_line = new JTextArea();
        command_line.setRows( 5 );
        command_line.setColumns( 80 );
        command_line.setLineWrap( true );
        command_line.setWrapStyleWord( true );
        command_line.setFont( new Font("Monospaced", Font.PLAIN, 10) );
        command_line.setEditable( false );
        JButton show_command_line = new JButton( jEdit.getProperty("ips.Show", "Show") );
        show_command_line.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    // fill in the merge data object
                    data = new MergeData();
                    if ( from_url_panel.isDestinationLocal() && from_url_panel.getPath() != null ) {
                        data.setFromFile( new File( from_url_panel.getPath() ) );
                    }
                    else {
                        data.setFromPath( from_url_panel.getPath() );
                    }
                    if ( to_url_panel.isDestinationLocal() && to_url_panel.getPath() != null ) {
                        data.setToFile( new File( to_url_panel.getPath() ) );
                    }
                    else {
                        data.setToPath( to_url_panel.getPath() );
                    }
                    if ( merge_destination_panel.isDestinationLocal() && merge_destination_panel.getPath() != null ) {
                        data.setDestinationFile( new File( merge_destination_panel.getPath() ) );
                    }
                    data.setStartRevision( start_revision_panel.getRevision() );
                    data.setEndRevision( end_revision_panel.getRevision() );
                    data.setDryRun( dryrun_cb.isSelected() );
                    data.setRecursive( recursive_cb.isSelected() );
                    data.setForce( force_cb.isSelected() );
                    data.setIgnoreAncestry( ignore_ancestry_cb.isSelected() );

                    command_line.setText( data.commandLineEquivalent() );
                }
            }
        );
        command_line_panel.add( "0, 0, 6, 1, E, w, 3", command_line_label );
        command_line_panel.add( "0, 1, 5, 1, 0, w, 3", new JScrollPane( command_line ) );
        command_line_panel.add( "5, 1, 1, 1, 0, w, 3", show_command_line );

        // ok and cancel buttons
        KappaLayout kl = new KappaLayout();
        JPanel btn_panel = new JPanel( kl );
        JButton ok_btn = new JButton( jEdit.getProperty( "ips.Ok", "Ok" ) );
        JButton cancel_btn = new JButton( jEdit.getProperty( "ips.Cancel", "Cancel" ) );
        btn_panel.add( "0, 0, 1, 1, 0, w, 3", ok_btn );
        btn_panel.add( "1, 0, 1, 1, 0, w, 3", cancel_btn );
        kl.makeColumnsSameWidth( 0, 1 );

        ok_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {

                        // fill in the merge data object
                        data = new MergeData();
                        if ( from_url_panel.isDestinationLocal() && from_url_panel.getPath() != null ) {
                            data.setFromFile( new File( from_url_panel.getPath() ) );
                        }
                        else {
                            data.setFromPath( from_url_panel.getPath() );
                        }
                        if ( to_url_panel.isDestinationLocal() && to_url_panel.getPath() != null ) {
                            data.setToFile( new File( to_url_panel.getPath() ) );
                        }
                        else {
                            data.setToPath( to_url_panel.getPath() );
                        }
                        if ( merge_destination_panel.isDestinationLocal() && merge_destination_panel.getPath() != null ) {
                            data.setDestinationFile( new File( merge_destination_panel.getPath() ) );
                        }
                        data.setStartRevision( start_revision_panel.getRevision() );
                        data.setEndRevision( end_revision_panel.getRevision() );
                        data.setDryRun( dryrun_cb.isSelected() );
                        data.setRecursive( recursive_cb.isSelected() );
                        data.setForce( force_cb.isSelected() );
                        data.setIgnoreAncestry( ignore_ancestry_cb.isSelected() );

                        String check_valid = data.checkValid();
                        if ( check_valid != null ) {
                            JOptionPane.showMessageDialog( MergeDialog.this, check_valid, jEdit.getProperty( "ips.Error", "Error" ), JOptionPane.ERROR_MESSAGE );
                            data = null;
                            return ;
                        }

                        canceled = false;
                        MergeDialog.this.setVisible( false );
                        MergeDialog.this.dispose();
                    }
                }
                                );

        cancel_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        canceled = true;
                        MergeDialog.this.setVisible( false );
                        MergeDialog.this.dispose();
                    }
                }
                                    );

        // add the components to the option panel
        panel.add( "0,  0, 1, 1, 0, w, 3", merge_from_panel );
        panel.add( "0,  1, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 11, true ) );
        panel.add( "0,  2, 1, 1, 0, w, 3", merge_to_panel );
        panel.add( "0,  3, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 11, true ) );
        panel.add( "0,  6, 1, 1, 0, w, 3", merge_destination_panel );
        panel.add( "0,  7, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 11, true ) );
        panel.add( "0,  8, 1, 1, 0, w, 3", merge_options_panel );
        panel.add( "0,  9, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 11, true ) );
        panel.add( "0, 10, 1, 1, 0, w, 3", command_line_panel );
        panel.add( "0, 11, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 16, true ) );
        panel.add( "0, 12, 1, 1, E,  , 3", btn_panel );

        setContentPane( panel );
        pack();

    }

    public MergeData getData() {
        return canceled ? null : data;
    }
}