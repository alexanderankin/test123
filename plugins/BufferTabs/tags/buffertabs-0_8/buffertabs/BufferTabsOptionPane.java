/*
 * BufferTabsOptionPane.java - Option pane for BufferTabs
 * Copyright (C) 1999, 2000 Jason Ginchereau, Andre Kaplan
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
    
    private JRadioButton colourTabRB;
    private JRadioButton colourTextRB;
    private JCheckBox enableColoursCB;  
    private JCheckBox muteColoursCB;    
    private JCheckBox variationColoursCB;
    private JCheckBox highlightColoursCB;

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
        
                    
        //CES: Colour tabs  
        
        addComponent( new Box.Filler( space, space, space ) );
        addSeparator( "options.buffertabs.colourtabs.sep" );
        addComponent( new Box.Filler( space, space, space ) );
         
        enableColoursCB = new JCheckBox( jEdit.getProperty( "options.buffertabs.colourtabs.label" ) );
        enableColoursCB.addItemListener( this );
        addComponent( enableColoursCB );
         
        JPanel indent3 = new JPanel();
        highlightColoursCB = new JCheckBox( jEdit.getProperty( "options.buffertabs.colourhighlight.label" ) );
        highlightColoursCB.addItemListener( this );
        indent3.add( new Box.Filler( space, space, space ) );
        indent3.add( highlightColoursCB );
        addComponent( indent3 );
                 
        JPanel indent = new JPanel();
        muteColoursCB = new JCheckBox( jEdit.getProperty( "options.buffertabs.colourmute.label" ) );
        muteColoursCB.addItemListener( this );
        indent.add( new Box.Filler( space, space, space ) );
        indent.add( muteColoursCB );
        addComponent( indent );
         
        JPanel indent2 = new JPanel();
        variationColoursCB = new JCheckBox( jEdit.getProperty( "options.buffertabs.colourvariation.label" ) );
        variationColoursCB.addItemListener( this );
        indent2.add( new Box.Filler( space, space, space ) );
        indent2.add( variationColoursCB );
        addComponent( indent2 );
                      
        colourTabRB = new JRadioButton( jEdit.getProperty( "options.buffertabs.colourtab.label" ) );
        addComponent( colourTabRB );
         
        colourTextRB = new JRadioButton( jEdit.getProperty( "options.buffertabs.colourtext.label" ) );
        addComponent( colourTextRB );
         
        ButtonGroup group = new ButtonGroup();
        group.add( colourTabRB );
        group.add( colourTextRB );
     
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
        
        //CES: Colour tabs  
        enableColoursCB.setSelected(
            jEdit.getBooleanProperty( "buffertabs.colourtabs", true )
        );

        muteColoursCB.setSelected(
            jEdit.getBooleanProperty( "buffertabs.colourmute", true )
        );

        variationColoursCB.setSelected(
            jEdit.getBooleanProperty( "buffertabs.colourvariation", true )
        );
        
        
        highlightColoursCB.setSelected(
            jEdit.getBooleanProperty( "buffertabs.colourhighlight", true )
        );
                 
        colourTabRB.setSelected(
            !jEdit.getBooleanProperty( "buffertabs.colourizetext", false )
        );

        colourTextRB.setSelected(
            jEdit.getBooleanProperty( "buffertabs.colourizetext", false )
        );


        muteColoursCB.setEnabled( enableColoursCB.isSelected() );
        variationColoursCB.setEnabled( muteColoursCB.isSelected() && enableColoursCB.isSelected());
        highlightColoursCB.setEnabled( enableColoursCB.isSelected() );
        colourTabRB.setEnabled( enableColoursCB.isSelected() );
        colourTextRB.setEnabled( enableColoursCB.isSelected() );
        
    }


    /**
     * Called when the options dialog's `OK' button is pressed.
     * This should save any properties saved in this option pane.
    **/
    public void _save() {
        jEdit.setBooleanProperty("buffertabs.enable", enableCB.isSelected());
        jEdit.setBooleanProperty("buffertabs.icons", iconsCB.isSelected());
        jEdit.setBooleanProperty("buffertabs.usePopup", popupCB.isSelected());
        jEdit.setProperty("buffertabs.location",
        locationChoice.getSelectedItem().toString());
        
         //CES: Colour tabs  
        jEdit.setBooleanProperty( "buffertabs.colourtabs", enableColoursCB.isSelected() );
        jEdit.setBooleanProperty( "buffertabs.colourmute", muteColoursCB.isSelected() );
        jEdit.setBooleanProperty( "buffertabs.colourhighlight", highlightColoursCB.isSelected() );        
        jEdit.setBooleanProperty( "buffertabs.colourvariation", variationColoursCB.isSelected() );
        jEdit.setBooleanProperty( "buffertabs.colourizetext", colourTextRB.isSelected() );  
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
   *   Update options pane to reflect option changes
   *  @author Chris Samuels  
  */  
   public void itemStateChanged( ItemEvent e )
   {
      muteColoursCB.setEnabled( enableColoursCB.isSelected() );
      variationColoursCB.setEnabled( muteColoursCB.isSelected() && enableColoursCB.isSelected());
      highlightColoursCB.setEnabled( enableColoursCB.isSelected() );
      colourTabRB.setEnabled( enableColoursCB.isSelected() );
      colourTextRB.setEnabled( enableColoursCB.isSelected() );
   }
}
