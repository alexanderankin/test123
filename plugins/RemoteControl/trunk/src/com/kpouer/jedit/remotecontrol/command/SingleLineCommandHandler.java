/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2011 Matthieu Casanova
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package com.kpouer.jedit.remotecontrol.command;

import com.kpouer.jedit.remotecontrol.MessageHandler;
import com.kpouer.jedit.remotecontrol.RemoteClient;
import com.kpouer.jedit.remotecontrol.executionengine.Engine;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.util.Log;

/**
 * An action Command is
 * S:::somebeanshell
 * S::bsh:somebeanshell
 * S::anotherexecutionengine:some code
 * @author Matthieu Casanova
 */
public class SingleLineCommandHandler implements MessageHandler
{
	private RemoteClient client;

	public SingleLineCommandHandler(RemoteClient remoteClient)
	{
		this.client = remoteClient;
	}

	@Override
	public boolean accept(String line)
	{
		return line.startsWith("S:");
	}

	@Override
	public void handleMessage(String line)
	{
		String command = line.substring(2);
		int i = command.indexOf(':');
		if (i == -1)
		{
			Log.log(Log.WARNING, this, "Wrong command " + line);
			return;
		}
		String transactionId;
		if (i == 0)
		{
			transactionId = null;
		}
		else
		{
			transactionId = command.substring(0, i);
		}
		command = command.substring(i + 1);
		int j = command.indexOf(':');
		if (j == -1)
		{
			Log.log(Log.WARNING, this, "Wrong command, missing engine " + line);
		}

		String engine;
		if (j == 0)
		{
			engine = "bsh";
		}
		else
		{
			engine = command.substring(0, j);
		}
		command = command.substring(j + 1);
		Engine service = ServiceManager.getService(Engine.class, engine);
		if (service == null)
		{
			Log.log(Log.WARNING, this, "Unknown engine service : " + Engine.class + " name=" + engine);
			return;
		}

		service.execute(client, command, transactionId);
	}
}
