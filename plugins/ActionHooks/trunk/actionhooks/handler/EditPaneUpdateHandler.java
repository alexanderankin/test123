/*
 * EditPaneUpdateHandler.java - ActionHooks Plugin
 *
 * Copyright 2004 Ollie Rutherfurd <oliver@rutherfurd.net>
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
 *
 * $Id: EditPaneUpdateHandler.java,v 1.2 2004/02/01 20:12:15 orutherfurd Exp $
 */
package actionhooks.handler;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.util.Log;

import actionhooks.*;

public class EditPaneUpdateHandler extends EBMessageHandler
{

	static String[] events = {"EditPaneUpdate.CREATED",
							  "EditPaneUpdate.DESTROYED",
							  "EditPaneUpdate.BUFFER_CHANGED"};

	public EditPaneUpdateHandler()
	{
		// XXX load events dynamically
	}

	public String getMessageName()
	{
		return "org.gjt.sp.jedit.msg.EditPaneUpdate";
	}

	public View getView(EBMessage msg)
	{
		EditPaneUpdate epu = (EditPaneUpdate)msg;
		return epu.getEditPane().getView();
	}

	public boolean skip(EBMessage msg)
	{
		EditPaneUpdate epu = (EditPaneUpdate)msg;
		if(epu.getWhat().equals(EditPaneUpdate.CREATED))
		{
			// view is being created.  yes, this is a hack, but
			// so far it's the only way I've found to prevent
			// an error executing a macro.
			if(epu.getEditPane().getView().getEditPane() == null)
				return true;
		}
		return false;
	}

	public String[] getEventNames()
	{
		return this.events;
	}

	public String getEventName(EBMessage msg)
	{
		return "EditPaneUpdate." + ((EditPaneUpdate)msg).getWhat();
	}

}

// :collapseFolds=1:noTabs=false:lineSeparator=\n:tabSize=4:indentSize=4:deepIndent=true:folding=explicit:
