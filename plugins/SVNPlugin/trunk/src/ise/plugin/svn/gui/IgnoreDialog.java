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

import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

import ise.java.awt.KappaLayout;
import ise.java.awt.LambdaLayout;


public class IgnoreDialog extends JDialog {

    private String filename = null;
    private boolean recursive = false;

    public IgnoreDialog( View view, String name ) {
        super( ( JFrame ) view, jEdit.getProperty( "ips.Ignore_title", "Ignore" ), true );
        this.filename = new String( name );

        JPanel panel = new JPanel( new LambdaLayout() );
        panel.setBorder( new EmptyBorder( 6, 6, 6, 6 ) );

        // choices
        ButtonGroup bg = new ButtonGroup();
        final JRadioButton this_file_btn = new JRadioButton( jEdit.getProperty( "ips.This_file", "This file" ) );
        this_file_btn.setSelected( true );
        final JRadioButton this_dir_btn = new JRadioButton( jEdit.getProperty( "ips.This_directory", "This directory" ) );
        final JRadioButton this_dir_pattern_btn = new JRadioButton( jEdit.getProperty( "ips.Files_in_this_directory_with_this_pattern>", "Files in this directory with this pattern:" ) );
        bg.add( this_file_btn );
        bg.add( this_dir_btn );
        bg.add( this_dir_pattern_btn );

        final JTextField pattern_field = new JTextField();
        final JCheckBox recursive_cb = new JCheckBox( jEdit.getProperty( "ips.Recursive", "Recursive" ), false );

        // buttons
        KappaLayout kl = new KappaLayout();
        JPanel btn_panel = new JPanel( kl );
        JButton ok_btn = new JButton( jEdit.getProperty( "ips.Ok", "Ok" ) );
        JButton cancel_btn = new JButton( jEdit.getProperty( "ips.Cancel", "Cancel" ) );
        btn_panel.add( "0, 0, 1, 1, 0, w, 3", ok_btn );
        btn_panel.add( "1, 0, 1, 1, 0, w, 3", cancel_btn );
        kl.makeColumnsSameWidth( 0, 1 );

        // actions
        ok_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        File file = new File( filename );
                        if ( this_dir_btn.isSelected() ) {
                            filename = file.getParent();
                            if ( pattern_field.getText() != null ) {
                                filename += File.separator + pattern_field.getText();
                            }
                        }
                        recursive = recursive_cb.isSelected();
                        IgnoreDialog.this.setVisible( false );
                        IgnoreDialog.this.dispose();
                    }
                }
                                );

        cancel_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        filename = null;
                        IgnoreDialog.this.setVisible( false );
                        IgnoreDialog.this.dispose();
                    }
                }
                                    );

        // layout the panel
        panel.add( "0, 0, 8, 1, W, wh, 3", new JLabel("Ignore:"));
        panel.add( "0, 1, 8, 1, W, wh, 3", this_file_btn );
        panel.add( "0, 2, 8, 1, W, wh, 3", this_dir_btn );
        panel.add( "0, 3, 8, 1, W, wh, 3", this_dir_pattern_btn );
        panel.add( "1, 4, 7, 1, W, wh, 3", pattern_field );
        panel.add( "1, 5, 7, 1, W, wh, 3", recursive_cb );
        panel.add( "0, 6, 1, 1, 0,   , 0", KappaLayout.createVerticalStrut( 11, true ) );
        panel.add( "0, 7, 8, 1, E,   , 0", btn_panel );

        setContentPane( panel );
        pack();
    }

    public String getFilename() {
        return filename;
    }

    public boolean getRecursive() {
        return recursive;
    }

    public static void main ( String[] args ) {
        IgnoreDialog dialog = new IgnoreDialog( null, "/home/danson/tmp/test/FormatTest.java" );
        dialog.setVisible( true );

    }
}