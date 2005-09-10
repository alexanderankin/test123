/*
 * open_buffer.java - open the current jEdit buffer in InfoViewer
 * Copyright (C) 2001 Dirk Moebius
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

import java.awt.event.ActionEvent;


public class open_buffer extends InfoViewerAction
{

    /**
	 * 
	 */
	private static final long serialVersionUID = -4070973801912095786L;


	public open_buffer()
    {
        super("infoviewer.open_buffer");
    }


    public void actionPerformed(ActionEvent evt)
    {
        getViewer(evt).gotoBufferURL();
    }

}

