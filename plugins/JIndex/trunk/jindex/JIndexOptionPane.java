/*
 * jEdit editor settings:
 * :mode=java:tabSize=4:indentSize=4:noTabs=true:maxLineLen=0:
 *
 * JIndexOptionPane.java - JIndex options panel
 * Copyright (C) 1999 Dirk Moebius
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

package jindex;

import org.gjt.sp.jedit.*;
import javax.swing.table.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.*;
import java.io.File;
import java.util.Vector;


public class JIndexOptionPane
        extends AbstractOptionPane
        implements ActionListener
{

    // private members
    private JCheckBox cbFastDisplay;
    private JButton bCreate;


    public JIndexOptionPane() {
        super("jindex");
    }


    public void _init() {
        setBorder(new EmptyBorder(5,5,5,5));

        Dimension space = new Dimension(0, 30);

        addComponent(cbFastDisplay = new JCheckBox(jEdit.getProperty(
            "options.jindex.fastDisplay")));

        if ("false".equals(jEdit.getProperty("jindex.fastDisplay")))
            cbFastDisplay.setSelected(false);
        else
            cbFastDisplay.setSelected(true);

        addComponent(new Box.Filler(space, space, space));

        bCreate = new JButton(jEdit.getProperty("options.jindex.createIndex"));
        bCreate.addActionListener(this);

        GridBagConstraints cons = new GridBagConstraints();
        cons.gridy = y++;
        cons.gridwidth = cons.REMAINDER;
        cons.fill = cons.NONE;
        cons.anchor = cons.CENTER;
        gridBag.setConstraints(bCreate, cons);
        add(bCreate);
    }


    /**
     * Called when the options dialog's `OK' button is pressed.
     * This should save any properties saved in this option pane.
     */
    public void _save() {
        jEdit.setProperty("jindex.fastDisplay",
            cbFastDisplay.isSelected() ? "true" : "false");
    }


    public void actionPerformed(ActionEvent evt) {
        new ConfigureDialog(jEdit.getFirstView());
    }

}
