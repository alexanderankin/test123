/*
 * EnhancedJToolBar.java
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

import javax.swing.*;
import javax.swing.event.*;


/**
 * this is a workaround class for <code>JToolBar</code>
 * for JDK versions prior to 1.3. The method <code>add(Action)</code>
 * doesn't set the right properties.
 */
public class EnhancedJToolBar extends JToolBar {
    public EnhancedJToolBar() { super(); }
    public EnhancedJToolBar(int orientation) { super(orientation); }
    public JButton add(Action a) {
        JButton b = super.add(a);
        b.setText(null);
        b.setToolTipText(a.getValue(Action.SHORT_DESCRIPTION).toString());
        return b;
    }
}
