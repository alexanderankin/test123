/*
 * jEdit editor settings:
 * :mode=java:tabSize=4:indentSize=4:noTabs=true:maxLineLen=0:
 *
 * HelpfulJList.java - a JList that displays tooltips on obscured list items
 * Copyright (C) 1999 Jason Ginchereau
 * small modifications by Dirk Moebius
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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


/**
 * Adds tool-tip helpers for obscured list items.
 *
 * @author Jason Ginchereau
 * @author Dirk Moebius (small modifications)
 */

public class HelpfulJList extends JList implements MouseListener {

    public HelpfulJList(ListModel model) {
        super(model);
        ToolTipManager.sharedInstance().registerComponent(this);
        addMouseListener(this);
    }


    public HelpfulJList() {
        super();
        ToolTipManager.sharedInstance().registerComponent(this);
        addMouseListener(this);
    }


    public final String getToolTipText(MouseEvent evt) {
        int index = locationToIndex(evt.getPoint());

        if (index >= 0) {
            Object item = getModel().getElementAt(index);
            Component renderer = getCellRenderer().getListCellRendererComponent(
                this, item, index, isSelectedIndex(index), false);
            Dimension cellSize = renderer.getPreferredSize();
            Rectangle cellBounds = getCellBounds(index, index);

            if (cellBounds != null) {
                Rectangle cellRect = new Rectangle(0, cellBounds.y, cellSize.width, cellBounds.height);
                if(!cellRectIsVisible(cellRect))
                    return item.toString();
            }
        }

        return null;
    }


    public final Point getToolTipLocation(MouseEvent evt) {
        int index = locationToIndex(evt.getPoint());

        if (index >= 0) {
            Object item = getModel().getElementAt(index);
            Component renderer = getCellRenderer().getListCellRendererComponent(
                this, item, index, isSelectedIndex(index), false);
            Dimension cellSize = renderer.getPreferredSize();
            Rectangle cellBounds = getCellBounds(index, index);

            if (cellBounds != null) {
                Rectangle cellRect = new Rectangle(cellBounds.x, cellBounds.y, cellSize.width, cellBounds.height);
                if (!cellRectIsVisible(cellRect)) {
                    int offs = 0;
                    if (renderer instanceof JLabel) {
                        Icon icon = ((JLabel)renderer).getIcon();
                        if (icon != null)
                            offs += icon.getIconWidth();
                    }
                    return new Point(cellRect.x + offs, cellRect.y);
                }
            }
        }

        return null;
    }


    private final boolean cellRectIsVisible(Rectangle cellRect) {
        Rectangle vr = getVisibleRect();
        return vr.contains(cellRect.x + 22, cellRect.y + 2)
            && vr.contains(cellRect.x + 22 + cellRect.width - 31, cellRect.y + 2 + cellRect.height - 4);
    }


    public void mouseClicked(MouseEvent evt)  { }
    public void mousePressed(MouseEvent evt)  { }
    public void mouseReleased(MouseEvent evt) { }


    public void mouseEntered(MouseEvent evt)  {
        ToolTipManager ttm = ToolTipManager.sharedInstance();
        toolTipInitialDelay = ttm.getInitialDelay();
        ttm.setInitialDelay(0);
    }


    public void mouseExited(MouseEvent evt)   {
        ToolTipManager ttm = ToolTipManager.sharedInstance();
        if (toolTipInitialDelay >= 0)
            ttm.setInitialDelay(toolTipInitialDelay);
    }


    private int toolTipInitialDelay = -1;

}
