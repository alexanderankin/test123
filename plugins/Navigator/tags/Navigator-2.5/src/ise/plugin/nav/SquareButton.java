/*
* Copyright (c) 2008, Dale Anson
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

package ise.plugin.nav;

import java.awt.Dimension;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * A 30 x 30 JButton.
 */
public class SquareButton extends JButton {
    private Dimension size = new Dimension(30, 30);

    public SquareButton(Icon icon) {
        this(30, icon);
    }

    public SquareButton(int side, Icon icon) {
        super(icon);
        if (side > 1) {
            size = new Dimension(side, side);
        }
    }

    public Dimension getPreferredSize() {
        return size;
    }
    public Dimension getMinimumSize() {
        return size;
    }
    public Dimension getMaximumSize() {
        return size;
    }
}
