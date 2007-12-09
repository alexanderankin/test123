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
import java.awt.event.*;
import java.io.*;
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
import ise.java.awt.LambdaLayout;
import ise.plugin.svn.data.PropertyData;
import ise.plugin.svn.PVHelper;
import ise.plugin.svn.library.FileUtilities;


/**
 * Dialog for adding files and directories.
 */
public class PropertyEditor extends JDialog {
    // instance fields
    private View view = null;
    private String name = null;
    private String value = null;
    private boolean isDirectory = false;

    private boolean cancelled = false;

    private PropertyData propertyData = null;

    private String[] default_file_prop_names = new String[] {
                "",
                "svn:executable",
                "svn:mime-type",
                "svn:ignore",
                "svn:keywords",
                "svn:eol-style",              // native, CRLF, CR, LF
                "svn:externals",
                "svn:special",
                "bugtraq:url",
                "bugtraq:warnifnoissue",      // boolean
                "bugtraq:label",
                "bugtraq:message",
                "bugtraq:number",             // boolean
                "bugtraq:append"};           // boolean

    private String[] default_dir_prop_names = new String[] {
                "",
                "svn:mime-type",
                "svn:ignore",
                "svn:keywords",
                "svn:eol-style",              // native, CRLF, CR, LF
                "svn:externals",
                "svn:special",
                "bugtraq:url",
                "bugtraq:warnifnoissue",      // boolean
                "bugtraq:label",
                "bugtraq:message",
                "bugtraq:number",             // boolean
                "bugtraq:append"};           // boolean


    public PropertyEditor( View view, String name, String value, boolean isDirectory ) {
        super( ( JFrame ) view, "Property Editor", true );
        this.view = view;
        this.name = name;
        this.value = value;
        this.isDirectory = isDirectory;
        _init();
    }

    /** Initialises the option pane. */
    protected void _init() {
        propertyData = new PropertyData();

        JPanel panel = new JPanel( new LambdaLayout() );
        panel.setBorder( new EmptyBorder( 6, 6, 6, 6 ) );

        JLabel prop_name_label = new JLabel( "Property name:" );
        final JComboBox prop_chooser = new JComboBox( isDirectory ? default_dir_prop_names : default_file_prop_names );
        prop_chooser.setEditable( true );
        prop_chooser.setSelectedItem( name == null ? "" : name );

        JPanel content_panel = new JPanel( new LambdaLayout() );
        content_panel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Property value" ) );
        final JRadioButton text_btn = new JRadioButton( "Enter a text value:" );
        text_btn.setSelected( true );
        final JRadioButton file_btn = new JRadioButton( "Or load value from file:" );
        ButtonGroup bg = new ButtonGroup();
        bg.add( text_btn );
        bg.add( file_btn );
        final JTextArea text_value = new JTextArea( 8, 30 );
        if ( value != null ) {
            text_value.setText( value );
        }
        final JTextField file_value = new JTextField( 30 );
        file_value.setEnabled( false );
        final JButton browse_btn = new JButton( "Browse..." );
        browse_btn.setEnabled( false );
        browse_btn.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    String[] filename = GUIUtilities.showVFSFileDialog( view, PVHelper.getProjectRoot( view ), VFSBrowser.OPEN_DIALOG, false );
                    if ( filename != null && filename.length > 0 ) {
                        file_value.setText( filename[ 0 ] );
                    }
                }
            }
        );
        ActionListener al = new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        file_value.setEnabled( file_btn.isSelected() );
                        browse_btn.setEnabled( file_btn.isSelected() );
                        text_value.setEnabled( text_btn.isSelected() );
                    }
                };
        text_btn.addActionListener( al );
        file_btn.addActionListener( al );
        content_panel.add( "0, 0, 7, 1, W, w, 3", text_btn );
        content_panel.add( "0, 1, 1, 1", KappaLayout.createHorizontalStrut( 11, true ) );
        content_panel.add( "1, 1, 6, 1, 0, wh, 3", new JScrollPane( text_value ) );
        content_panel.add( "0, 2, 7, 1, W, w, 3", file_btn );
        content_panel.add( "0, 3, 1, 1", KappaLayout.createHorizontalStrut( 11, true ) );
        content_panel.add( "1, 3, 5, 1, W, w, 3", file_value );
        content_panel.add( "6, 3, 1, 1, E,  , 3", browse_btn );

        final JCheckBox recursive_cb = new JCheckBox( "Apply recursively?" );
        recursive_cb.setSelected( false );
        recursive_cb.setEnabled( isDirectory );
        recursive_cb.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        propertyData.setRecursive( recursive_cb.isSelected() );
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
                        Object item = prop_chooser.getSelectedItem();
                        if ( item != null && !item.toString().isEmpty() ) {
                            propertyData.setName( item.toString() );
                            if ( text_btn.isSelected() ) {
                                propertyData.setValue( text_value.getText() == null ? "" : text_value.getText() );
                            }
                            else {
                                String filename = file_value.getText();
                                if (filename == null || filename.isEmpty()) {
                                    JOptionPane.showMessageDialog( view, "No filename entered for property value.", "Error", JOptionPane.ERROR_MESSAGE );
                                    file_value.requestFocusInWindow();
                                    return ;
                                }
                                try {
                                    Reader reader = new BufferedReader( new FileReader( filename ) );
                                    StringWriter writer = new StringWriter();
                                    FileUtilities.copy( reader, writer );
                                    propertyData.setValue( writer.toString() );
                                }
                                catch ( Exception e ) {
                                    JOptionPane.showMessageDialog( view, "Unable to read property value from file:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
                                    return ;
                                }
                            }
                        }

                        PropertyEditor.this.setVisible( false );
                        PropertyEditor.this.dispose();
                    }
                }
                                );

        cancel_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        propertyData = null;
                        PropertyEditor.this.setVisible( false );
                        PropertyEditor.this.dispose();
                    }
                }
                                    );

        // add the components to the option panel
        panel.add( "0, 0, 1, 1, W,  , 6", prop_name_label );
        panel.add( "1, 0, 5, 1, W, w, 4", prop_chooser );

        panel.add( "0, 1, 6, 1, W, wh, 3", content_panel );
        panel.add( "0, 2, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 6, true ) );

        panel.add( "0, 3, 6, 1, W,  , 6", recursive_cb );
        panel.add( "0, 4, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 10, true ) );

        panel.add( "0, 5, 6, 1, E,  , 6", btn_panel );

        setContentPane( panel );
        pack();

    }

    /**
     * @return null indicates user cancelled
     */
    public PropertyData getPropertyData() {
        return propertyData;
    }

    public static void main ( String[] args ) {
        PropertyEditor pe = new PropertyEditor( null, "svn:externals", "*", true );
        pe.setVisible( true );
    }
}
