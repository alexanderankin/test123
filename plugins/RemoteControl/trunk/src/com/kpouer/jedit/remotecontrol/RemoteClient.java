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

import com.kpouer.jedit.remotecontrol.serializer.Serializer;
import com.kpouer.jedit.remotecontrol.welcome.WelcomeService;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/**
 * @author Matthieu Casanova
 */
public class RemoteClient
{
	private SocketChannel channel;

	private final LinkedList<String> messages;
	private final StringBuilder builder;

	private Collection<MessageHandler> handlers;

	private Serializer serializer;

	public RemoteClient(SocketChannel sChannel)
	{
		this.channel = sChannel;
		messages = new LinkedList<String>();
		builder = new StringBuilder();
		handlers = new ArrayList<MessageHandler>();
		String welcomeService = jEdit.getProperty("remotecontrol.welcomeservice", "");
		if (welcomeService.length() == 0)
		{
			handshaked();
		}
		else
		{
			WelcomeService welcome = ServiceManager.getService(WelcomeService.class, welcomeService);
			if (welcome == null)
				throw new InternalError("No welcome service by that name " + welcomeService);
			welcome.setClient(this);
			welcome.setChannel(sChannel);
			handlers.add(welcome);
		}
		serializer = ServiceManager.getService(Serializer.class, "xsjson");
	}

	public Serializer getSerializer()
	{
		return serializer;
	}

	public void read(ByteBuffer buf)
	{
		buf.flip();
		CharsetDecoder decoder = RemoteServer.CHARSET.newDecoder();
		try
		{
			CharBuffer charBuffer = decoder.decode(buf);
			for (int i = 0; i < charBuffer.length(); i++)
			{
				char c = charBuffer.get(i);
				switch (c)
				{
					case '\n':
						if (builder.length() != 0)
						{
							String line = builder.toString().trim();
							builder.setLength(0);
							if (!line.isEmpty())
							{
								Log.log(Log.DEBUG, this, "Received " + line);
								messages.add(line);
							}
						}
						break;
					case '\r':
						break;
					default:
						builder.append(c);
				}
			}
		}
		catch (CharacterCodingException e)
		{
			Log.log(Log.ERROR, this, e, e);
		}
		handleMessage();
	}

	public void handshaked()
	{
		handlers.clear();
		handlers.add(new SingleLineCommandHandler(this));
	}

	private void handleMessage()
	{
		while (!messages.isEmpty())
		{
			String line = messages.removeFirst();
			for (MessageHandler handler : handlers)
			{
				try
				{
					if (handler.accept(line))
					{
						handler.handleMessage(line);
						break;
					}
				}
				catch (Exception e)
				{
					Log.log(Log.ERROR, this, "Error while processing message " + line, e);
				}

			}
		}
	}

	public void sendObject(Object o)
	{
		String message = serializer.serialize(o);
		byte[] bytes = message.getBytes(RemoteServer.CHARSET);
		sendMessage(bytes);
	}

	void sendMessage(byte[] message)
	{
		try
		{
			channel.write(ByteBuffer.wrap(message));
		}
		catch (IOException e)
		{
			Log.log(Log.WARNING, this, e, e);
		}
	}

	@Override
	public String toString()
	{
		return "RemoteClient[" + channel.socket().getInetAddress() + ":" + channel.socket().getPort() + "]";
	}
}
