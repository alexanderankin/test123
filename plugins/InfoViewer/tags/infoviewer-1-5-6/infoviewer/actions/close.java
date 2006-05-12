/*
 * close.java - close action for InfoViewer
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

import java.awt.Frame;
import java.awt.event.ActionEvent;

import org.gjt.sp.jedit.View;


public class close extends InfoViewerAction
{

    /**
	 * 
	 */
	private static final long serialVersionUID = -4424449640799402279L;


	public close()
    {
        super("infoviewer.close");
    }


    public void actionPerformed(ActionEvent evt)
    {
        Frame frame = getFrame(evt);

        if (frame == null)
            return;

        if (frame instanceof View)
            ((View)frame).getDockableWindowManager().removeDockableWindow("infoviewer");
        else
            frame.dispose();
    }
}

