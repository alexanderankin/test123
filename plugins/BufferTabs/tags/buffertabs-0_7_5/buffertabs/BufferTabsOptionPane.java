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
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.AbstractOptionPane;


public class BufferTabsOptionPane extends AbstractOptionPane {
    private JCheckBox enableCB;
    private JCheckBox iconsCB;
    private JCheckBox popupCB;
    private JComboBox locationChoice;


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
}
