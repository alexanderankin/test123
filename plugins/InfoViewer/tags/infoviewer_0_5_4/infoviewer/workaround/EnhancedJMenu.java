/*
 * EnhancedJMenu.java
 * Copyright (C) 2000 Dirk Moebius
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

package infoviewer.workaround;

import infoviewer.actions.*;
import javax.swing.*;

    
/**
 * this is a workaround class for <code>JMenu</code>
 * for JDK versions prior to 1.3. The method <code>add(Action)</code>
 * is overridden to set the properties for MNEMONIC and ACCELERATOR
 * correctly.
 */
public class EnhancedJMenu extends JMenu {
    public EnhancedJMenu() { super(); }
    public EnhancedJMenu(String s) { super(s); }
    public EnhancedJMenu(String s, boolean b) { super(s, b); }
    public JMenuItem add(Action a) {
        JMenuItem mi = super.add(a);
        if (a instanceof InfoViewerAction) {
            // set mnemonic
            Object obj_mnem = a.getValue(InfoViewerAction.MNEMONIC);
            if (obj_mnem != null) {
                String mnem = obj_mnem.toString();
                if (mnem.length() > 0) {
                    mi.setMnemonic(mnem.charAt(0));
                }
            }
            // set keystroke
            Object obj_key = a.getValue(InfoViewerAction.ACCELERATOR);
            if (obj_key != null) {
                mi.setAccelerator((KeyStroke) obj_key);
            }
        }
        return mi;
    }
    
}
