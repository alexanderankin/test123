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

// imports
import java.awt.BorderLayout;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.*;

import ise.java.awt.*;
import ise.plugin.svn.data.*;
import ise.plugin.svn.command.*;
import ise.plugin.svn.library.*;
import static ise.plugin.svn.gui.HistoryModelNames.*;
import ise.plugin.svn.gui.component.*;


/**
 * Dialog for merge command
 * Merge this (local or remote file or directory)
 * Into this (local or remote file or directory)
 * Put the results here (local directory)
 * cb Dry run?
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
        super( ( JFrame ) view, jEdit.getProperty( "ips.Merge", "Merge" ), true );
        this.view = view;
        this.fromPath = fromPath;
        init();
    }

    protected void init() {
        KappaLayout layout = new KappaLayout();
        JPanel panel = new JPanel( layout );
        panel.setBorder( new EmptyBorder( 6, 6, 6, 6 ) );

        // merge from location
        final BrowseLocalRemotePanel merge_from_panel = new BrowseLocalRemotePanel( view, jEdit.getProperty( "ips.Merge_this_path>", "Merge this path:" ), fromPath, "" );

        // revision selection panel
        JPanel revision_selection_panel = new JPanel( new BorderLayout() );
        final RevisionSelectionPanel start_revision_panel = new RevisionSelectionPanel( jEdit.getProperty( "ips.From_this_revision>", "From this revision:" ) );
        final RevisionSelectionPanel end_revision_panel = new RevisionSelectionPanel( jEdit.getProperty( "ips.To_this_revision>", "To this revision:" ) );
        revision_selection_panel.add( start_revision_panel, BorderLayout.WEST );
        revision_selection_panel.add( end_revision_panel, BorderLayout.EAST );

        // merge into location
        final BrowseLocalRemotePanel merge_into_panel = new BrowseLocalRemotePanel( view, jEdit.getProperty( "ips.Into_this_path>", "Into this path:" ), fromPath, "" );

        // merge destination location
        String destination = null;
        if ( fromPath != null ) {
            File f = new File( fromPath );
            destination = f.isDirectory() ? f.getAbsolutePath() : f.getParent();
        }
        final BrowseLocalRemotePanel merge_destination_panel = new BrowseLocalRemotePanel( view, jEdit.getProperty( "ips.Place_merged_files_here>", "Place merged files here:" ), destination, "", false );

        // force and recursive checkboxes
        JPanel merge_options_panel = new JPanel( new KappaLayout() );
        merge_options_panel.setBorder( BorderFactory.createTitledBorder( "Merge Options" ) );
        final JCheckBox force_cb = new JCheckBox( jEdit.getProperty( "ips.Force", "Force" ) );
        final JCheckBox recursive_cb = new JCheckBox( jEdit.getProperty( "ips.Recursive", "Recursive" ) );
        merge_options_panel.add( "0, 0, 1, 1, 0, , 6", recursive_cb );
        merge_options_panel.add( "1, 0, 1, 1, 0, , 6", force_cb );

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
                        if ( merge_from_panel.isDestinationLocal() && merge_from_panel.getPath() != null ) {
                            data.setFromFile( new File( merge_from_panel.getPath() ) );
                        }
                        else {
                            data.setFromPath( merge_from_panel.getPath() );
                        }
                        if ( merge_into_panel.isDestinationLocal() && merge_into_panel.getPath() != null ) {
                            data.setIntoFile( new File( merge_into_panel.getPath() ) );
                        }
                        else {
                            data.setIntoPath( merge_into_panel.getPath() );
                        }
                        if ( merge_destination_panel.isDestinationLocal() && merge_destination_panel.getPath() != null ) {
                            data.setDestinationFile( new File( merge_destination_panel.getPath() ) );
                        }
                        else {
                            data.setDestinationPath( merge_destination_panel.getPath() );
                        }
                        data.setStartRevision( start_revision_panel.getRevision() );
                        data.setEndRevision( end_revision_panel.getRevision() );
                        data.setRecursive( recursive_cb.isSelected() );
                        data.setForce( force_cb.isSelected() );

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
        panel.add( "0, 0, 1, 1, 0, w , 3", merge_from_panel );
        panel.add( "0, 1, 1, 1, 0,   , 0", KappaLayout.createVerticalStrut( 11, true ) );
        panel.add( "0, 2, 1, 1, 0, w , 3", revision_selection_panel );
        panel.add( "0, 3, 1, 1, 0,   , 0", KappaLayout.createVerticalStrut( 11, true ) );
        panel.add( "0, 4, 1, 1, 0, w , 3", merge_into_panel );
        panel.add( "0, 5, 1, 1, 0,   , 0", KappaLayout.createVerticalStrut( 11, true ) );
        panel.add( "0, 6, 1, 1, 0, w , 3", merge_destination_panel );
        panel.add( "0, 7, 1, 1, 0,   , 0", KappaLayout.createVerticalStrut( 11, true ) );
        panel.add( "0, 8, 1, 1, 0, w , 3", merge_options_panel );
        panel.add( "0, 9, 1, 1, 0,   , 0", KappaLayout.createVerticalStrut( 16, true ) );
        panel.add( "0, 10, 1, 1, E,   , 3", btn_panel );

        setContentPane( panel );
        pack();

    }

    public MergeData getData() {
        return canceled ? null : data;
    }
}