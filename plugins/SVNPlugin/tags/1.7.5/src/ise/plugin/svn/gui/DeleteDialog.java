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

import java.awt.Dimension;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

import org.gjt.sp.jedit.View;

import ise.plugin.svn.data.DeleteData;
import ise.java.awt.*;
import org.gjt.sp.jedit.jEdit;


public class DeleteDialog extends JDialog {

    private List<String> paths = null;
    private DeleteData data = null;
    private boolean showLogin = false;

    private JCheckBox forceCheckbox;
    private JCheckBox dryRunCheckbox;
    private JCheckBox deleteFilesCheckbox;
    private DefaultTableModel fileTableModel;
    private JButton okButton;
    private JButton cancelButton;

    /**
     * @param view the parent frame
     * @param path the local working file to diff against.
     */
    public DeleteDialog( View view, DeleteData data ) {
        this( view, data, false );
    }

    public DeleteDialog( View view, DeleteData data, boolean showLogin ) {
        super( ( JFrame ) view, jEdit.getProperty( "ips.Delete", "Delete" ), true );
        if ( data == null ) {
            throw new IllegalArgumentException( "data may not be null" );
        }
        this.data = data;
        paths = data.getPaths();
        this.showLogin = showLogin;

        installComponents();
        installListeners();
    }

    private void installComponents() {

        JPanel panel = new JPanel( new KappaLayout() );
        panel.setBorder( new EmptyBorder( 12, 11, 11, 12 ) );

        JLabel file_label = new JLabel( jEdit.getProperty( "ips.Delete", "Delete" ) + " " + ( paths.size() == 1 ? jEdit.getProperty( "ips.this_file", "this file" ) : jEdit.getProperty( "ips.these_files", "these files" ) ) + ":" );
        BestRowTable file_table = new BestRowTable();
        fileTableModel = new DefaultTableModel(
                    new String[] {
                        "", jEdit.getProperty( "ips.File", "File" )
                    }, paths.size() ) {
                    public Class getColumnClass( int index ) {
                        if ( index == 0 ) {
                            return Boolean.class;
                        }
                        else {
                            return super.getColumnClass( index );
                        }

                    }
                };
        file_table.setModel( fileTableModel );

        // load the table model
        int i = 0;
        for ( String path : paths ) {
            if ( path != null ) {
                fileTableModel.setValueAt( true, i, 0 );
                fileTableModel.setValueAt( path, i, 1 );
                ++i;
            }
        }
        file_table.getColumnModel().getColumn( 0 ).setMaxWidth( 25 );
        file_table.getColumnModel().getColumn( 1 ).setPreferredWidth( 625 );
        file_table.packRows();

        forceCheckbox = new JCheckBox( jEdit.getProperty( "ips.Force", "Force" ) );
        forceCheckbox.setSelected( data.getForce() );

        dryRunCheckbox = new JCheckBox( jEdit.getProperty( "ips.Dry_run", "Dry run" ) );
        dryRunCheckbox.setSelected( data.getDryRun() );

        deleteFilesCheckbox = new JCheckBox( jEdit.getProperty( "ips.Delete_files_from_file_system", "Delete files from file system" ) );
        deleteFilesCheckbox.setSelected( data.getDeleteFiles() );

        // possible login
        final LoginPanel login = new LoginPanel( data.getPaths().get( 0 ) );
        login.setVisible( showLogin );


        KappaLayout kl = new KappaLayout();
        JPanel btn_panel = new JPanel( kl );
        okButton = new JButton( jEdit.getProperty( "ips.Ok", "Ok" ) );
        okButton.setMnemonic(KeyEvent.VK_O);
        cancelButton = new JButton( jEdit.getProperty( "ips.Cancel", "Cancel" ) );
        cancelButton.setMnemonic(KeyEvent.VK_C);
        btn_panel.add( "0, 0, 1, 1, 0, w, 3", okButton );
        btn_panel.add( "1, 0, 1, 1, 0, w, 3", cancelButton );
        kl.makeColumnsSameWidth( 0, 1 );

        JScrollPane file_scroller = new JScrollPane( file_table );
        file_scroller.getViewport().setPreferredSize( new Dimension( 600, Math.min( file_table.getBestHeight(), 250 ) ) );

        panel.add( "0, 0, 1, 1, W, w , 3", file_label );
        panel.add( "0, 1, 1, 1, W, wh, 3", file_scroller );

        panel.add( "0, 2, 1, 1", KappaLayout.createVerticalStrut(11, true));

        panel.add( "0, 3, 1, 1, W, w, 3", forceCheckbox );
        panel.add( "0, 4, 1, 1, W, w, 3", dryRunCheckbox );
        panel.add( "0, 5, 1, 1, W, w, 3", deleteFilesCheckbox );
        if ( showLogin ) {
            panel.add( KappaLayout.createVerticalStrut( 11 ), "0, 6, 1, 1" );
            panel.add( login, "0, 7, 1, 1, 0, w, 0" );
        }
        panel.add( KappaLayout.createVerticalStrut( 11 ), "0, 8, 1, 1" );
        panel.add( btn_panel, "0, 9, 1, 1, E" );
        setContentPane( panel );
        pack();
        getRootPane().setDefaultButton(okButton);
        okButton.requestFocus();
    }

    private void installListeners() {
        okButton.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        // get the paths
                        List<String> paths = new ArrayList<String>();
                        for ( int row = 0; row < fileTableModel.getRowCount(); row++ ) {
                            Boolean selected = ( Boolean ) fileTableModel.getValueAt( row, 0 );
                            if ( selected ) {
                                paths.add( ( String ) fileTableModel.getValueAt( row, 1 ) );
                            }
                        }

                        if ( paths.size() == 0 ) {
                            // nothing to add, bail out
                            data = null;
                        }
                        else {
                            data.setPaths( paths );
                        }

                        getData().setForce( forceCheckbox.isSelected() );
                        getData().setDryRun( dryRunCheckbox.isSelected() );
                        getData().setDeleteFiles( deleteFilesCheckbox.isSelected() );
                        DeleteDialog.this.setVisible( false );
                        DeleteDialog.this.dispose();
                    }
                }
                                  );

        cancelButton.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        DeleteDialog.this.data = null;
                        DeleteDialog.this.setVisible( false );
                        DeleteDialog.this.dispose();
                    }
                }
                                      );

    }

    public DeleteData getData() {
        return data;
    }
}