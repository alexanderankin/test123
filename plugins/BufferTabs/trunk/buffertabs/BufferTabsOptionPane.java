/*
 * BufferTabsOptionPane.java - Option pane for BufferTabs
 * Copyright (C) 1999, 2000 Jason Ginchereau, Andre Kaplan
 * Copyright (C) 2000, 2001, 2002, 2003 Andre Kaplan
 * Copyright (C) 2003 Chris Samuels
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */


package buffertabs;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.AbstractOptionPane;


public class BufferTabsOptionPane extends AbstractOptionPane implements ItemListener {
    private JCheckBox enableCB;
    private JCheckBox iconsCB;
    private JCheckBox popupCB;
    private JComboBox locationChoice;

    private JRadioButton colorTabRB;
    private JRadioButton colorTextRB;
    private JCheckBox enableColorsCB;
    private JCheckBox muteColorsCB;
    private JCheckBox variationColorsCB;
    private JCheckBox highlightColorsCB;


    public BufferTabsOptionPane() {
        super("buffertabs");
    }


    public void _init() {
        enableCB = new JCheckBox(jEdit.getProperty("options.buffertabs.enable.label"));
        addComponent(enableCB);

        Dimension space = new Dimension(0, 10);
        addComponent(new Box.Filler(space, space, space));

        iconsCB = new JCheckBox(jEdit.getProperty("options.buffertabs.icons.label"));
        addComponent(iconsCB);

        addComponent(new Box.Filler(space, space, space));

        popupCB = new JCheckBox(jEdit.getProperty("options.buffertabs.popup.label"));
        addComponent(popupCB);

        addComponent(new Box.Filler(space, space, space));

        JPanel locationPanel = new JPanel();
        locationPanel.setLayout(new FlowLayout());
        locationPanel.add(new JLabel(jEdit.getProperty("options.buffertabs.location.label")));
        locationChoice = new JComboBox(
            new String[] { "top", "bottom", "left", "right"});
        locationPanel.add(locationChoice);
        addComponent(locationPanel);


        //CES: Color tabs

        addComponent( new Box.Filler( space, space, space ) );
        addSeparator( "options.buffertabs.color-tabs.separator" );
        addComponent( new Box.Filler( space, space, space ) );

        enableColorsCB = new JCheckBox( jEdit.getProperty( "options.buffertabs.color-tabs.label" ) );
        enableColorsCB.addItemListener( this );
        addComponent( enableColorsCB );

        JPanel indent3 = new JPanel();
        highlightColorsCB = new JCheckBox( jEdit.getProperty( "options.buffertabs.color-selected.label" ) );
        highlightColorsCB.addItemListener( this );
        indent3.add( new Box.Filler( space, space, space ) );
        indent3.add( highlightColorsCB );
        addComponent( indent3 );

        JPanel indent = new JPanel();
        muteColorsCB = new JCheckBox( jEdit.getProperty( "options.buffertabs.color-mute.label" ) );
        muteColorsCB.addItemListener( this );
        indent.add( new Box.Filler( space, space, space ) );
        indent.add( muteColorsCB );
        addComponent( indent );

        JPanel indent2 = new JPanel();
        variationColorsCB = new JCheckBox( jEdit.getProperty( "options.buffertabs.color-variation.label" ) );
        variationColorsCB.addItemListener( this );
        indent2.add( new Box.Filler( space, space, space ) );
        indent2.add( variationColorsCB );
        addComponent( indent2 );

        colorTabRB = new JRadioButton( jEdit.getProperty( "options.buffertabs.color-background.label" ) );
        addComponent( colorTabRB );

        colorTextRB = new JRadioButton( jEdit.getProperty( "options.buffertabs.color-foreground.label" ) );
        addComponent( colorTextRB );

        ButtonGroup group = new ButtonGroup();
        group.add( colorTabRB );
        group.add( colorTextRB );

        load();
    }


    public void load() {
        enableCB.setSelected(
            jEdit.getBooleanProperty("buffertabs.enable", false)
        );
        iconsCB.setSelected(
            jEdit.getBooleanProperty("buffertabs.icons", true)
        );
        popupCB.setSelected(
            jEdit.getBooleanProperty("buffertabs.usePopup", true)
        );

        locationChoice.setSelectedItem(
            getLocationProperty("buffertabs.location", "bottom")
        );

        //CES: Color tabs
        enableColorsCB.setSelected(
            jEdit.getBooleanProperty( "buffertabs.color-tabs", false )
        );

        highlightColorsCB.setSelected(
            jEdit.getBooleanProperty( "buffertabs.color-selected", true )
        );

        muteColorsCB.setSelected(
            jEdit.getBooleanProperty( "buffertabs.color-mute", true )
        );

        variationColorsCB.setSelected(
            jEdit.getBooleanProperty( "buffertabs.color-variation", true )
        );

        colorTabRB.setSelected(
            !jEdit.getBooleanProperty( "buffertabs.color-foreground", false )
        );

        colorTextRB.setSelected(
            jEdit.getBooleanProperty( "buffertabs.color-foreground", false )
        );


        muteColorsCB.setEnabled( enableColorsCB.isSelected() );
        variationColorsCB.setEnabled( muteColorsCB.isSelected() && enableColorsCB.isSelected());
        highlightColorsCB.setEnabled( enableColorsCB.isSelected() );
        colorTabRB.setEnabled( enableColorsCB.isSelected() );
        colorTextRB.setEnabled( enableColorsCB.isSelected() );

    }


    /**
     * Called when the options dialog's `OK' button is pressed.
     * This should save any properties saved in this option pane.
     */
    public void _save() {
        jEdit.setBooleanProperty("buffertabs.enable", enableCB.isSelected());
        jEdit.setBooleanProperty("buffertabs.icons", iconsCB.isSelected());
        jEdit.setBooleanProperty("buffertabs.usePopup", popupCB.isSelected());
        jEdit.setProperty("buffertabs.location",
        locationChoice.getSelectedItem().toString());

         //CES: Color tabs
        jEdit.setBooleanProperty( "buffertabs.color-tabs", enableColorsCB.isSelected() );
        jEdit.setBooleanProperty( "buffertabs.color-selected", highlightColorsCB.isSelected() );
        jEdit.setBooleanProperty( "buffertabs.color-mute", muteColorsCB.isSelected() );
        jEdit.setBooleanProperty( "buffertabs.color-variation", variationColorsCB.isSelected() );
        jEdit.setBooleanProperty( "buffertabs.color-foreground", colorTextRB.isSelected() );
    }


    public static String getLocationProperty(String prop, String defaultVal) {
        String location = jEdit.getProperty(prop);
        if (location == null) {
            location = defaultVal;
        }
        location = location.toLowerCase();
        if (!(     location.equals("top")
                || location.equals("bottom")
                || location.equals("left")
                || location.equals("right")
             )
        ) {
            location = defaultVal;
        }
        return location;
    }


    /**
     * Update options pane to reflect option changes
     */
    public void itemStateChanged( ItemEvent e ) {
        muteColorsCB.setEnabled( enableColorsCB.isSelected() );
        variationColorsCB.setEnabled( muteColorsCB.isSelected() && enableColorsCB.isSelected());
        highlightColorsCB.setEnabled( enableColorsCB.isSelected() );
        colorTabRB.setEnabled( enableColorsCB.isSelected() );
        colorTextRB.setEnabled( enableColorsCB.isSelected() );
    }
}

