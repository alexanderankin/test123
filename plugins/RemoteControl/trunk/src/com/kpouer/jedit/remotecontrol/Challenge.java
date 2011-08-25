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

package com.kpouer.jedit.remotecontrol;

import org.gjt.sp.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author Matthieu Casanova
 */
public class Challenge implements MessageHandler
{
	private static final String HANDSHAKE = "jEdit-RemoteServer-Hello";
	private static final byte[] HANDSHAKE_ANSWER = "jEdit-RemoteServer-Welcome".getBytes(RemoteServer.CHARSET);
	private final RemoteClient client;
	private SocketChannel sChannel;

	public Challenge(RemoteClient client, SocketChannel sChannel)
	{
		this.client = client;
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
		if (line.equals(HANDSHAKE))
		{
			Log.log(Log.MESSAGE, this, "Handshake received");
			try
			{
				sChannel.write(ByteBuffer.wrap(HANDSHAKE_ANSWER));
			}
			catch (IOException e)
			{
				Log.log(Log.WARNING, this, e, e);
			}
			client.handshaked();
			Log.log(Log.MESSAGE, this, "Client handshaked " + this);
		}
		else
		{
			Log.log(Log.WARNING, this, "Wrong Handshake " + line);
		}
	}
}
