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

import com.kpouer.jedit.remotecontrol.command.ActionCommandHandler;
import com.kpouer.jedit.remotecontrol.command.OptionCommandHandler;
import com.kpouer.jedit.remotecontrol.command.RegisterEBMessageCommandHandler;
import com.kpouer.jedit.remotecontrol.command.SingleLineCommandHandler;
import com.kpouer.jedit.remotecontrol.command.UnregisterEBMessageCommandHandler;
import com.kpouer.jedit.remotecontrol.serializer.Serializer;
import com.kpouer.jedit.remotecontrol.welcome.WelcomeService;
import org.gjt.sp.jedit.EBMessage;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Set;

/**
 * @author Matthieu Casanova
 */
public class RemoteClient
{
	private final SocketChannel channel;

	private final LinkedList<String> messages;
	private final StringBuilder builder;

	private final Collection<MessageHandler> handlers;

	private final Serializer serializer;

	private final Properties props;

	/**
	 * The messages that the client want to receive
	 */
	private final Set<Class<? extends EBMessage>> registeredMessages;

	public RemoteClient(SocketChannel sChannel)
	{
		channel = sChannel;
		props = new Properties();
		registeredMessages = Collections.synchronizedSet(new HashSet<Class<? extends EBMessage>>());
		messages = new LinkedList<String>();
		builder = new StringBuilder();
		handlers = new ArrayList<MessageHandler>();
		String welcomeService = jEdit.getProperty("remotecontrol.welcomeservice", "");
		if (welcomeService.length() == 0)
		{
			handshaked();
		} else
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

	public void registerMessage(Class<? extends EBMessage> clz)
	{
		registeredMessages.add(clz);
	}

	public void unregisterMessage(Class<? extends EBMessage> clz)
	{
		registeredMessages.remove(clz);
	}

	public Serializer getSerializer()
	{
		String serializerName = props.getProperty("serializer", "xsjson");
		Serializer serializer = ServiceManager.getService(Serializer.class, serializerName);
		if (serializer == null)
		{
			Log.log(Log.ERROR, this, "Wrong serializer " + serializerName);
			serializer = ServiceManager.getService(Serializer.class, "xsjson");
		}
		if (serializer == null)
		{
			throw new InternalError("No xsjson serializer !!!");
		}
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
		} catch (CharacterCodingException e)
		{
			Log.log(Log.ERROR, this, e, e);
		}
		handleMessage();
	}

	public void handshaked()
	{
		handlers.clear();
		handlers.add(new SingleLineCommandHandler(this));
		handlers.add(new ActionCommandHandler());
		handlers.add(new OptionCommandHandler(this));
		handlers.add(new RegisterEBMessageCommandHandler(this));
		handlers.add(new UnregisterEBMessageCommandHandler(this));
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
				} catch (Exception e)
				{
					Log.log(Log.ERROR, this, "Error while processing message " + line, e);
				}
			}
		}
	}

	public void sendObject(Object o)
	{
		String message = serializer.serialize(o) + "\n";
		byte[] bytes = message.getBytes(RemoteServer.CHARSET);
		sendMessage(bytes);
	}

	void sendMessage(EBMessage message)
	{
		if (registeredMessages.contains(message.getClass()))
		{
			Serializer serializer = getSerializer();
			String s = serializer.serialize(message) + "\n";
			if (RemoteServer.DEBUG)
			{
				Log.log(Log.MESSAGE, this, s);
			}
			sendMessage(s.getBytes(RemoteServer.CHARSET));
		}
	}

	void sendMessage(byte[] message)
	{
		try
		{
			channel.write(ByteBuffer.wrap(message));
		} catch (IOException e)
		{
			Log.log(Log.WARNING, this, e, e);
		}
	}

	public void setProperty(String key, String value)
	{
		props.setProperty(key, value);
	}

	@Override
	public String toString()
	{
		return "RemoteClient[" + channel.socket().getInetAddress() + ":" + channel.socket().getPort() + "]";
	}
}
