/*
 * edit_url.java - edit the current URL in new jEdit buffer
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

package infoviewer.actions;

import infoviewer.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;


public class edit_url extends InfoViewerAction {
    
    public edit_url() {
        super("infoviewer.edit_url");
    }
    
    public void actionPerformed(ActionEvent evt) {
        URL url = getViewer(evt).getURL();
        if (url == null) {
            GUIUtilities.error(null, "infoviewer.error.nourl", null);
        } else {
            jEdit.openFile(null, null, url.toString(), true, false);
        }
    }
}

