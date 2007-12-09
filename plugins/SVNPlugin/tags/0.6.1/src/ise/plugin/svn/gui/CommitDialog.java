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
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
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
import ise.java.awt.LambdaLayout;
import ise.plugin.svn.data.CommitData;
import ise.plugin.svn.library.GUIUtils;
import ise.plugin.svn.library.PasswordHandler;
import ise.plugin.svn.library.PasswordHandlerException;
import ise.plugin.svn.library.PropertyComboBox;


/**
 * Dialog for obtaining a comment for a commit.
 */
public class CommitDialog extends JDialog {
    // instance fields
    private View view = null;
    private Map<String, String> nodes = null;

    private JTextArea comment = null;
    private PropertyComboBox commentList = null;

    private boolean cancelled = false;

    private CommitData commitData = null;


    public CommitDialog( View view, Map<String, String> nodes ) {
        super( ( JFrame ) view, "Commit", true );
        if ( nodes == null ) {
            throw new IllegalArgumentException( "nodes may not be null" );
        }
        this.view = view;
        this.nodes = nodes;
        init();
    }

    protected void init() {

        commitData = new CommitData();

        JPanel panel = new JPanel( new LambdaLayout() );
        panel.setBorder( new EmptyBorder( 6, 6, 6, 6 ) );

        JLabel file_label = new JLabel( "Committing these files:" );
        JTable file_table = new JTable();
        //file_table.setFillsViewportHeight(true);  // java 1.6
        final DefaultTableModel file_table_model = new DefaultTableModel(
                    new String[] {
                        "", "File", "Status"
                    }, nodes.size() ) {
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

        // load the table model, determine if recursive, accumulate file paths
        boolean recursive = false;
        List<String> paths = new ArrayList<String>();
        int i = 0;
        Set < Map.Entry<String, String >> set = nodes.entrySet();
        for ( Map.Entry<String, String> me : set ) {
            String path = me.getKey();
            String status = me.getValue() == null ? "" : me.getValue();
            if (path != null) {
                File file = new File(path);
                if (file.isDirectory()) {
                    recursive = true;
                }
                paths.add(path);
                file_table_model.setValueAt( new Boolean( true ), i, 0 );
                file_table_model.setValueAt( path, i, 1 );
                file_table_model.setValueAt( status, i, 2 );
                ++i;
            }
        }
        commitData.setPaths( paths );
        commitData.setRecursive( recursive );

        file_table.getColumnModel().getColumn(0).setMaxWidth(25);
        file_table.getColumnModel().getColumn(1).setPreferredWidth(450);
        file_table.getColumnModel().getColumn(2).setPreferredWidth(50);

        final JCheckBox recursive_cb = new JCheckBox( "Recursively commit?" );
        recursive_cb.setSelected( recursive );
        recursive_cb.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        commitData.setRecursive( recursive_cb.isSelected() );
                    }
                }
                                      );

        JLabel label = new JLabel( "Enter comment for this commit:" );
        comment = new JTextArea( 5, 50 );
        comment.setLineWrap( true );
        comment.setWrapStyleWord( true );

        // list for previous comments
        final PropertyComboBox commentList = new PropertyComboBox( "ise.plugin.svn.comment." );
        commentList.setEditable( false );
        commentList.addItemListener( new ItemListener() {
                    public void itemStateChanged( ItemEvent e ) {
                        if ( PropertyComboBox.SELECT.equals( commentList.getSelectedItem().toString() ) ) {
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
                        for (int row = 0; row < file_table_model.getRowCount(); row++) {
                            Boolean selected = (Boolean)file_table_model.getValueAt(row, 0);
                            if (selected) {
                                paths.add((String)file_table_model.getValueAt(row, 1));
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
                                if ( commentList != null ) {
                                    commentList.addValue( msg );
                                }
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

        // field for bug number
        JLabel bug_label = new JLabel( "Issue #:" );
        JTextField bug_field = new JTextField( 10 );

        // add the components to the option panel
        /* TODO: make this work for bugtraq
        panel.add( "0, 0, 1, 1, W,  , 3", bug_label );
        panel.add( "1, 0, 1, 1, W, w, 3", bug_field );
        panel.add( "0, 1, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 11, true ) );
        */

        panel.add( "0, 2, 6, 1, W,  , 3", label );
        panel.add( "0, 3, 6, 1, W, wh, 3", new JScrollPane( comment ) );

        if ( commentList != null && commentList.getModel().getSize() > 0 ) {
            commentList.setPreferredSize(new Dimension(600, commentList.getPreferredSize().height));
            panel.add( "0, 4, 6, 1, W,  , 3", new JLabel( "Select a previous comment:" ) );
            panel.add( "0, 5, 6, 1, W, w, 3", commentList );
        }
        panel.add( "0, 6, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 11, true ) );

        panel.add( "0, 7, 6, 1, W,  , 3", file_label );
        JScrollPane file_scroller = new JScrollPane( file_table );
        file_scroller.getViewport().setPreferredSize(new Dimension(600, 200));
        panel.add( "0, 8, 6, 1, W, w, 3", file_scroller );
        panel.add( "0, 9, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 6, true ) );

        panel.add( "0, 10, 6, 1, W,  , 3", recursive_cb );
        panel.add( "0, 11, 1, 1, 0,  , 3", KappaLayout.createVerticalStrut( 11, true ) );

        panel.add( "0, 12, 6, 1, E,  , 0", btn_panel );

        setContentPane( panel );
        pack();

    }

    protected void _save() {
        if ( commentList != null ) {
            commentList.save();
        }
    }

    public CommitData getCommitData() {
        return commitData;
    }

    public static void main ( String[] args ) {
        TreeMap<String, String> files = new TreeMap<String, String>();
        files.put( "/home/danson/src/plugins/SVNPlugin/src/ise/plugin/svn/gui/CommitDialog.java", "modified");
        files.put( "/home/danson/src/plugins/SVNPlugin/src/ise/plugin/svn/gui/AddDialog.java", "modified");
        files.put( "/home/danson/src/plugins/SVNPlugin/src/ise/plugin/svn/gui/DeleteDialog.java", "modified");
        files.put( "/home/danson/src/plugins/SVNPlugin/src/ise/plugin/svn/gui/LogDialog.java", "modified");
        CommitDialog d = new CommitDialog( null, files );
        d.setVisible( true );
    }
}
