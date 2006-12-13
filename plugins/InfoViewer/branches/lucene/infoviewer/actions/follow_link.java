/*
 * follow_link.java - follow link action for InfoViewer
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

package infoviewer.actions;

import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.accessibility.AccessibleHyperlink;
import javax.accessibility.AccessibleHypertext;
import javax.accessibility.AccessibleText;
import javax.swing.JEditorPane;


public class follow_link extends InfoViewerAction {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 2574264546733855775L;
	private Point clickPoint = null;
    
    public follow_link() {
        super("infoviewer.follow_link");
    }
    
    public void actionPerformed(ActionEvent evt) {
        if (clickPoint == null) return;
        JEditorPane pane = getViewer(evt).getViewer();
        AccessibleText txt = pane.getAccessibleContext().getAccessibleText();
        if (txt != null && txt instanceof AccessibleHypertext) {
            AccessibleHypertext hyp = (AccessibleHypertext) txt;
            int charIndex = hyp.getIndexAtPoint(clickPoint);
            int linkIndex = hyp.getLinkIndex(charIndex);
            if (linkIndex >= 0) {
                AccessibleHyperlink lnk = hyp.getLink(linkIndex);
                lnk.doAccessibleAction(0);
            }
        }
    }
    
    public void setClickPoint(Point p) {
        clickPoint = p;
    }
}

