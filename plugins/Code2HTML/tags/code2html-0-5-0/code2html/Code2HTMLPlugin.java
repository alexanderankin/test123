/*
 * Code2HTMLPlugin.java
 * Copyright (c) 2000, 2001, 2002 Andre Kaplan
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


package code2html;

import java.util.Vector;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;

import org.gjt.sp.jedit.gui.OptionsDialog;

import org.gjt.sp.util.Log;


/**
 * Code2HTML plugin
 *
 * @author Andr&eacute; Kaplan
 */
public class Code2HTMLPlugin
    extends EditPlugin
{
    public Code2HTMLPlugin() {
        super();
    }


    public void start() {}


    public void stop() {}


    /*
    public void createMenuItems(Vector menuItems) {
        menuItems.addElement(GUIUtilities.loadMenu("code2html"));
    }


    public void createOptionPanes(OptionsDialog dialog) {
        dialog.addOptionPane(new Code2HTMLOptionPane());
    }
    */
}

