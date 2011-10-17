/*
 * BufferUpdateHandler.java - ActionHooks Plugin
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
 * $Id: BufferUpdateHandler.java,v 1.1 2004/02/01 19:40:39 orutherfurd Exp $
 */
package actionhooks.handler;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.util.Log;

import actionhooks.*;

public class BufferUpdateHandler extends EBMessageHandler
{

	static String[] events = {"BufferUpdate.LOADED",
							  "BufferUpdate.CLOSED",
							  "BufferUpdate.SAVING",
							  "BufferUpdate.SAVED"};

	public BufferUpdateHandler()
	{
		// XXX load events dynamically
	}

	public String getMessageName()
	{
		return "org.gjt.sp.jedit.msg.BufferUpdate";
	}

	public View getView(EBMessage msg)
	{
		Log.log(Log.DEBUG, this, "getting view for " + msg); // ##
		return ((BufferUpdate)msg).getView();
	}

	public String[] getEventNames()
	{
		return events;
	}

	public String getEventName(EBMessage msg)
	{
		return "BufferUpdate." + ((BufferUpdate)msg).getWhat();
	}

}

// :collapseFolds=1:noTabs=false:lineSeparator=\n:tabSize=4:indentSize=4:deepIndent=true:folding=explicit:
