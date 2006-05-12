/*
 * edit_url.java - edit the current URL in new jEdit buffer
 * Copyright (C) 1999-2001 Dirk Moebius
 *
 * :tabSize=4:indentSize=4:noTabs=true:maxLineLen=0:
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

import infoviewer.InfoViewer;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;


public class edit_url extends InfoViewerAction
{

    /**
	 * 
	 */
	private static final long serialVersionUID = -4493276148898998310L;


	public edit_url()
    {
        super("infoviewer.edit_url");
    }


    public void actionPerformed(ActionEvent evt)
    {
        View view = jEdit.getFirstView();
        InfoViewer infoviewer = getViewer(evt);
        String url = infoviewer.getCurrentURL().getURL();
        Frame frame = getFrame(evt);

        if (frame != null && frame instanceof View)
            view = (View)frame;

        if (url == null)
        {
            GUIUtilities.error(null, "infoviewer.error.nourl", null);
            return;
        }

        // cut off anchor:
        int anchorPos = url.indexOf('#');
        if (anchorPos >= 0)
            url = url.substring(0, anchorPos);

        // open url:
        view.toFront();
        jEdit.openFile(view, url);
    }
}

