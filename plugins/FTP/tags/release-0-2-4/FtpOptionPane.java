/*
 * FtpOptionPane.java - Option pane for FtpPlugin
 * Copyright (C) 2001 Joe Laffey
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


import javax.swing.JCheckBox;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.AbstractOptionPane;

/**
 * Creates the OptionPane that contains the options for the Ftp Plugin
 *
 * @author Joe Laffey
 * @version $Id$
**/
public class FtpOptionPane extends AbstractOptionPane {
    private JCheckBox useBin;
   
    public FtpOptionPane() {
        super("Ftp");
    }

    public void _init() {
        useBin = new JCheckBox("Transfer files using BINARY mode (preserves line endings)");
        addComponent(useBin);

        load();
    }

    /**
     * Loads saved properties.
    **/
    public void load() {
        useBin.setSelected(
            jEdit.getBooleanProperty("ftp.useBinary", true)
        );
    }


    /**
     * Called when the options dialog's `OK' button is pressed.
     * This should save any properties saved in this option pane.
    **/
    public void _save() {
        jEdit.setBooleanProperty("ftp.useBinary", useBin.isSelected());
    }
}
