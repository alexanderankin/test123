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
import javax.swing.event.*;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

import ise.java.awt.KappaLayout;
import ise.java.awt.LambdaLayout;


public class IgnoreDialog extends JDialog {

    private String pathfilename = null;
    private String path = null;
    private String filename = null;
    private String pattern = null;
    private boolean recursive = false;
    private boolean cancelled = false;

    /**
     * @param view parent frame
     * @param pathfilename absolute path of a file or directory
     */
    public IgnoreDialog( View view, String pathfilename ) {
        super( ( JFrame ) view, jEdit.getProperty( "ips.Ignore_title", "Ignore" ), true );
        this.pathfilename = pathfilename;
        File f = new File( pathfilename );
        path = f.getParent();
        filename = f.getName();

        JPanel panel = new JPanel( new LambdaLayout() );
        panel.setBorder( new EmptyBorder( 6, 6, 6, 6 ) );

        // choices
        ButtonGroup bg = new ButtonGroup();
        final JRadioButton this_file_btn = new JRadioButton( jEdit.getProperty( "ips.This_file", "This file" ) );
        final JRadioButton this_dir_btn = new JRadioButton( jEdit.getProperty( "ips.This_directory", "This directory" ) );
        final JRadioButton this_dir_pattern_btn = new JRadioButton( jEdit.getProperty( "ips.Files_in_this_directory_with_this_pattern>", "Files in this directory with this pattern:" ) );
        if ( f.isFile() ) {
            bg.add( this_file_btn );
            this_file_btn.setSelected( true );
        }
        else {
            bg.add( this_dir_btn );
            this_dir_btn.setSelected( true );
        }
        bg.add( this_dir_pattern_btn );

        final JTextField pattern_field = new JTextField();
        pattern_field.setEnabled( false );
        int extension_index = pathfilename.lastIndexOf( "." );
        if ( extension_index > 0 ) {
            pattern_field.setText( "*" + pathfilename.substring( extension_index ) );
        }

        final JCheckBox recursive_cb = new JCheckBox( jEdit.getProperty( "ips.Recursive", "Recursive" ), false );
        recursive_cb.setEnabled( false );

        // buttons
        KappaLayout kl = new KappaLayout();
        JPanel btn_panel = new JPanel( kl );
        JButton ok_btn = new JButton( jEdit.getProperty( "ips.Ok", "Ok" ) );
        ok_btn.setMnemonic(KeyEvent.VK_O);
        JButton cancel_btn = new JButton( jEdit.getProperty( "ips.Cancel", "Cancel" ) );
        cancel_btn.setMnemonic(KeyEvent.VK_C);
        btn_panel.add( "0, 0, 1, 1, 0, w, 3", ok_btn );
        btn_panel.add( "1, 0, 1, 1, 0, w, 3", cancel_btn );
        kl.makeColumnsSameWidth( 0, 1 );

        // actions
        this_dir_pattern_btn.addChangeListener(
            new ChangeListener() {
                public void stateChanged( ChangeEvent ae ) {
                    AbstractButton btn = ( AbstractButton ) ae.getSource();
                    pattern_field.setEnabled( btn.isSelected() );
                    recursive_cb.setEnabled( btn.isSelected() );
                }
            }
        );

        ok_btn.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    path = IgnoreDialog.this.pathfilename;
                    if ( this_dir_pattern_btn.isSelected() ) {
                        pattern = pattern_field.getText();
                        if ( pattern == null || pattern.length() == 0 ) {
                            JOptionPane.showMessageDialog( IgnoreDialog.this, jEdit.getProperty("ips.No_pattern_entered.", "No pattern entered."), jEdit.getProperty("ips.Error", "Error"), JOptionPane.ERROR_MESSAGE );
                            return;
                        }
                        // patterns can't be added to a file, they need to be added to
                        // the parent directory
                        File f = new File( path );
                        if ( !f.isDirectory() ) {
                            path = f.getParent();
                            if ( path.endsWith( "/" ) ) {
                                path = path.substring( 0, path.length() - 1 );
                            }
                        }
                        else {
                            path = f.getAbsolutePath();
                        }
                    }
                    recursive = recursive_cb.isSelected();
                    IgnoreDialog.this.setVisible( false );
                    IgnoreDialog.this.dispose();
                }
            }
        );

        cancel_btn.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    cancelled = true;
                    IgnoreDialog.this.setVisible( false );
                    IgnoreDialog.this.dispose();
                }
            }
        );

        // layout the panel
        panel.add( "0, 0, 8, 1, W, wh, 3", new JLabel( "Ignore:" ) );
        panel.add( "0, 1, 8, 1, W, wh, 3", f.isFile() ? this_file_btn : this_dir_btn );
        panel.add( "0, 2, 8, 1, W, wh, 3", this_dir_pattern_btn );
        panel.add( "1, 3, 7, 1, W, wh, 3", pattern_field );
        panel.add( "1, 4, 7, 1, W, wh, 3", recursive_cb );
        panel.add( "0, 5, 1, 1, 0,   , 0", KappaLayout.createVerticalStrut( 11, true ) );
        panel.add( "0, 6, 8, 1, E,   , 0", btn_panel );

        setContentPane( panel );
        pack();
        getRootPane().setDefaultButton(ok_btn);
        ok_btn.requestFocus();
    }

    public String getPath() {
        return path;
    }

    public String getFilename() {
        return filename;
    }

    public String getPattern() {
        return pattern;
    }

    public boolean getRecursive() {
        return recursive;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public static void main ( String[] args ) {
        IgnoreDialog dialog = new IgnoreDialog( null, "/home/danson/tmp/test/FormatTest.java" );
        dialog.setVisible( true );

    }
}