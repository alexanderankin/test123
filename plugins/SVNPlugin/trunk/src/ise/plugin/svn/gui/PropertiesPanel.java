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

import ise.plugin.svn.library.*;
import ise.java.awt.KappaLayout;
import ise.java.awt.LambdaLayout;

import java.awt.*;
import java.awt.event.*;

import java.io.*;

import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

import org.gjt.sp.jedit.View;


/**
 * A panel to display SVN properties.
 *
 * @author    Dale Anson
 */
public class PropertiesPanel extends JPanel {

    /**
     * Description of the Field
     */
    private HashMap _property_tfs;

    private String lSep = System.getProperty( "line.separator" );

    /**
     * Description of the Field
     */
    private JButton new_btn = null;

    public PropertiesPanel( String filename, Properties props ) {
        if ( filename == null ) {
            filename = "";
        }
        if ( props == null ) {
            props = new Properties();
            props.setProperty( "Error:", "No properties available." );
        }

        // set up the table to show the properties
        // bugger.  java.util.Properties should declare <String, Object>
        TreeMap<Object, Object> map = new TreeMap<Object, Object>( ( Map<Object, Object> ) props );   // TreeMap sorts
        JTable props_table = new JTable();
        final DefaultTableModel model = new DefaultTableModel(
                    new String[] {
                        "Name", "Value"
                    }, map.size() );
        props_table.setModel( model );

        Set < Map.Entry < Object, Object >> entrySet = map.entrySet();
        int i = 0;
        for ( Map.Entry<Object, Object> me : entrySet ) {
            Object key = me.getKey();
            Object value = me.getValue();
            if ( key == null || value == null ) {
                continue;
            }
            model.setValueAt( key.toString(), i, 0 );
            model.setValueAt( value.toString(), i, 1 );
            ++i;
        }
        props_table.addMouseListener( new TableCellViewer( props_table ) );

        // set up a Save and New button
        KappaLayout kl = new KappaLayout();
        JPanel btn_panel = new JPanel( kl );
        btn_panel.setBorder( new javax.swing.border.EmptyBorder( 11, 0, 0, 0 ) );
        JButton save_btn = new JButton( "Save" );
        save_btn.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    for ( int row = 0; row < model.getRowCount(); row++ ) {
                        String key = ( String ) model.getValueAt( row, 0 );
                        String value = ( String ) model.getValueAt( row, 1 );
                        if ( value == null || value.equals( "" ) ) {
                            // do svn property delete   user_prefs.remove( key );
                        }
                        else {
                            // do svn property add      user_prefs.put( key, value );
                        }
                    }
                    return ;
                }
            }
        );

        new_btn = new JButton( "New" );
        new_btn.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    model.addRow( new String[] {"", ""} );
                }
            }
        );

        btn_panel.add( save_btn, "0, 0, 1, 1, 0, w, 3" );
        btn_panel.add( new_btn, "1, 0, 1, 1, 0, w, 3" );
        kl.makeColumnsSameWidth( new int[] {0, 1} );
        JPanel bottom_panel = new JPanel();
        bottom_panel.add( btn_panel );

        setLayout( new LambdaLayout() );
        setBorder( new javax.swing.border.EmptyBorder( 6, 6, 6, 6 ) );
        JLabel label = new JLabel( "Properties for: " + filename, JLabel.LEFT );
        add( label, "0, 0, 1, 1, W, w, 3" );
        JScrollPane js = new JScrollPane(props_table);
        js.setPreferredSize(new Dimension(js.getPreferredSize().width, 100));
        add( js, "0, 1, 1, 1, 0, w, 3" );
        add( bottom_panel, "0, 2, 1, 1, 0, w, 6" );

    }

    public static void main ( String[] args ) {
        Properties p = new Properties();
        p.setProperty( "svn:externals", "/some/dir/ectory" );
        p.setProperty( "svn:keywords", "word, word2" );
        JFrame frame = new JFrame();
        frame.setContentPane( new PropertiesPanel( "filename", p ) );
        frame.pack();
        frame.setVisible( true );
    }
}
