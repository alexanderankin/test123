/*
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

package infoviewer;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.border.*;
import org.gjt.sp.jedit.*;


public class InfoViewerOptionPane extends AbstractOptionPane {

    // private members
    private JTextField tHome;
    private JTextField tMaxGoMenu;
    

    public InfoViewerOptionPane() {
        super("infoviewer");
		setBorder(new EmptyBorder(5,5,5,5));

        Dimension space = new Dimension(0, 30);

        // create the dialog:

        // 1. homepage property
        String homepage = jEdit.getProperty("infoviewer.homepage");
        if (homepage == null) {
            String jEditHome = MiscUtilities.constructPath(
                                   jEdit.getJEditHome(), "doc");
            jEditHome = jEditHome.replace(File.separatorChar, '/');
            homepage = "file:" + jEditHome + "/jeditdocs/index.html";
            jEdit.setProperty("infoviewer.homepage", homepage);
        }
        addComponent(jEdit.getProperty("options.infoviewer.homepage"),
            tHome = new JTextField(homepage, 15));
            
        // 2. max. number of menu entries in "Go" menu
        String max_go_menu = jEdit.getProperty("infoviewer.max_go_menu");
        if (max_go_menu == null) {
            max_go_menu = "20";
            jEdit.setProperty("infoviewer.max_go_menu", max_go_menu);
        }
        addComponent(jEdit.getProperty("options.infoviewer.max_go_menu"),
            tMaxGoMenu = new JTextField(max_go_menu, 15));
        
    }


    /**
     * Called when the options dialog's `OK' button is pressed.
     * This should save any properties saved in this option pane.
     */
    public void save() {
        jEdit.setProperty("infoviewer.homepage", tHome.getText());
        jEdit.setProperty("infoviewer.max_go_menu", tMaxGoMenu.getText());
    }
    
}
