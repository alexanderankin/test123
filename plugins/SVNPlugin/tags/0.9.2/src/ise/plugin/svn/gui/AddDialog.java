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
import java.awt.Dimension;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

import ise.java.awt.KappaLayout;
import ise.java.awt.LambdaLayout;
import ise.plugin.svn.PVHelper;
import ise.plugin.svn.data.SVNData;
import ise.plugin.svn.library.PasswordHandler;
import ise.plugin.svn.library.PasswordHandlerException;


/**
 * Dialog for adding files and directories.
 */
public class AddDialog extends JDialog {

    public final static String PREFIX = "ise.plugin.svn.pv.";

    private View view = null;
    private List<String> nodes = null;

    private boolean canceled = false;

    private SVNData addData = null;

    public AddDialog( View view, List<String> nodes ) {
        this( view, nodes, false );
    }

    public AddDialog( View view, List<String> nodes, boolean showLogin ) {
        super( ( JFrame ) view, "Add", true );
        if ( nodes == null ) {
            throw new IllegalArgumentException( "nodes may not be null" );
        }
        this.view = view;
        this.nodes = nodes;
        _init( showLogin );
    }

    /** Initialises the option pane. */
    protected void _init( boolean showLogin ) {

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
                File file = new File( node );
                if ( file.isDirectory() ) {
                    recursive = true;
                }
                paths.add( node );
            }
        }
        addData.setPaths( paths );
        addData.setRecursive( recursive );

        JLabel file_label = new JLabel( "Adding " + ( paths.size() == 1 ? "this file:" : "these files:" ) );
        BestRowTable file_table = new BestRowTable();
        final DefaultTableModel file_table_model = new DefaultTableModel(
                    new String[] {
                        "", "File"
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
        file_table.setModel( file_table_model );

        // load the table model
        int i = 0;
        for ( String path : paths ) {
            if ( path != null ) {
                file_table_model.setValueAt( true, i, 0 );
                file_table_model.setValueAt( path, i, 1 );
                ++i;
            }
        }
        file_table.getColumnModel().getColumn( 0 ).setMaxWidth( 25 );
        file_table.getColumnModel().getColumn( 1 ).setPreferredWidth( 625 );
        file_table.packRows();

        final JCheckBox recursive_cb = new JCheckBox( "Recursively add?" );
        recursive_cb.setSelected( recursive );
        recursive_cb.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        addData.setRecursive( recursive_cb.isSelected() );
                    }
                }
                                      );

        // possible login
        final LoginPanel login = new LoginPanel(nodes.get(0));
        login.setVisible(showLogin);

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
                        for ( int row = 0; row < file_table_model.getRowCount(); row++ ) {
                            Boolean selected = ( Boolean ) file_table_model.getValueAt( row, 0 );
                            if ( selected ) {
                                paths.add( ( String ) file_table_model.getValueAt( row, 1 ) );
                            }
                        }

                        if ( paths.size() == 0 ) {
                            // nothing to add, bail out
                            addData = null;
                        }
                        else {
                            addData.setPaths( paths );
                        }

                        addData.setUsername( login.getUsername() );
                        addData.setPassword( login.getPassword() );

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
        JScrollPane file_scroller = new JScrollPane( file_table );
        file_scroller.getViewport().setPreferredSize( new Dimension( 600, Math.min( file_table.getBestHeight(), 250 ) ) );

        panel.add( "0, 0, 1, 1, W,  , 3", file_label );
        panel.add( "0, 1, 1, 1, W, wh, 3", file_scroller );

        if ( recursive ) {
            panel.add( "0, 2, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 11, true ) );
            panel.add( "0, 3, 1, 1, W,  , 3", recursive_cb );
        }

        if ( showLogin ) {
            panel.add( "0, 5, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 11, true ) );
            panel.add( "0, 6, 1, 1, 0, w, 3", login);
        }

        panel.add( "0, 8, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 11, true ) );
        panel.add( "0, 9, 1, 1, E,  , 0", btn_panel );

        setContentPane( panel );
        pack();

    }

    public SVNData getSVNData() {
        return addData;
    }

    public static void main ( String[] args ) {
        List<String> paths = new ArrayList<String>();
        paths.add( "/home/danson/path/filename.txt" );
        paths.add( "/home/danson/path/filename2.txt" );
        AddDialog dialog = new AddDialog( null, paths, true );
        dialog.setVisible( true );

    }
}
