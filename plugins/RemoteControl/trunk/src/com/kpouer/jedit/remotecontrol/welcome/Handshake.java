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

package com.kpouer.jedit.remotecontrol.welcome;

import com.kpouer.jedit.remotecontrol.MessageHandler;
import com.kpouer.jedit.remotecontrol.RemoteClient;
import org.gjt.sp.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author Matthieu Casanova
 */
public class Handshake implements MessageHandler, WelcomeService
{
	private RemoteClient client;
	private SocketChannel sChannel;

	@Override
	public void setClient(RemoteClient client)
	{
		this.client = client;
	}

	@Override
	public void setChannel(SocketChannel sChannel)
	{
		this.sChannel = sChannel;
	}

	@Override
	public boolean accept(String line)
	{
		return true;
	}

	@Override
	public void handleMessage(String line)
	{
		if (line.equals(HELLO))
		{
			Log.log(Log.MESSAGE, this, "Handshake received");
			try
			{
				sChannel.write(ByteBuffer.wrap(HANDSHAKE_WELCOME));
				client.handshaked();
			}
			catch (IOException e)
			{
				Log.log(Log.WARNING, this, e, e);
			}
			Log.log(Log.MESSAGE, this, "Client handshaked " + this);
		}
	}
}
