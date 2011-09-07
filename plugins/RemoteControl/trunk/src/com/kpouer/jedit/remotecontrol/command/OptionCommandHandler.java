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
import org.gjt.sp.util.Log;

/**
 * An action Command is S::somebeanshell S:bsh:somebeanshell S:anotherexecutionengine:some code
 *
 * @author Matthieu Casanova
 */
public class OptionCommandHandler implements MessageHandler
{
	private final RemoteClient client;

	public OptionCommandHandler(RemoteClient remoteClient)
	{
		client = remoteClient;
	}

	@Override
	public boolean accept(String line)
	{
		return line.startsWith("O:");
	}

	@Override
	public void handleMessage(String line)
	{
		String command = line.substring(2);
		String[] split = command.split("=");
		Log.log(Log.DEBUG, this, client + " set option " + command);
		client.setProperty(split[0], split[1]);
	}
}
