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

import com.thoughtworks.xstream.XStream;
import org.gjt.sp.jedit.BeanShell;
import org.gjt.sp.jedit.bsh.NameSpace;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.util.LinkedList;

/**
 * @author Matthieu Casanova
 */
public class RemoteClient
{
	SocketChannel sChannel;
	private final XStream xstream;
	private static final String HANDSHAKE = "jEdit-RemoteServer-Hello";
	private static final byte[] HANDSHAKE_ANSWER = "jEdit-RemoteServer-Welcome".getBytes(RemoteServer.CHARSET);

	private boolean handshaked;


	private final LinkedList<String> messages;
	private final StringBuilder builder;

	public RemoteClient(SocketChannel sChannel, XStream xstream)
	{
		this.sChannel = sChannel;
		this.xstream = xstream;
		messages = new LinkedList<String>();
		builder = new StringBuilder();
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

	private void handleMessage()
	{
		if (!handshaked)
		{
			handleHandshake();
		}
		if (handshaked)
		{
			while (!messages.isEmpty())
			{
				String line = messages.removeFirst();
				if (line.startsWith("S:"))
				{
					handleSingleLineCommand(line);
				}
			}
		}
	}

	private void handleHandshake()
	{
		while (!messages.isEmpty())
		{
			String line = messages.removeFirst();
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
				handshaked = true;
				Log.log(Log.MESSAGE, this, "Client handshaked " + this);
				break;
			}
			else
			{
				Log.log(Log.WARNING, this, "Wrong Handshake " + line);
			}
		}
	}

	void sendMessage(byte[] message)
	{
		if (!handshaked)
			return;
		try
		{
			sChannel.write(ByteBuffer.wrap(message));
		}
		catch (IOException e)
		{
			Log.log(Log.WARNING, this, e, e);
		}
	}

	/**
	 * Handle a single line command
	 * S:transactionId:somebeanshell
	 *
	 * If transactionId is empty, there is no response.
	 * @param line the command received
	 */
	private void handleSingleLineCommand(String line)
	{
		String command = line.substring(2);
		int i = command.indexOf(':');
		if (i == -1)
		{
			Log.log(Log.WARNING, this, "Wrong command " + line);
			return;
		}
		String transaction;
		if (i == 0)
		{
			transaction = null;
		}
		else
		{
			transaction = command.substring(0, i);
		}
		command = command.substring(i + 1);
		execute(command, transaction);
	}

	private void execute(final String script, final String transactionId)
	{
		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				NameSpace ns = new NameSpace(
					BeanShell.getNameSpace(),
					"RemoteClient namespace");

				Object ret = null;
				try
				{
					ret = BeanShell.eval(jEdit.getActiveView(), ns, script);
				}
				catch (Throwable e)
				{
					Log.log(Log.ERROR, this, "Remote command failed:"+script, e);
					ret = e;
				}
				if (transactionId != null)
				{
					CommandResponse response = new CommandResponse(transactionId, ret);
					String s = xstream.toXML(response);
					sendMessage(s.getBytes(RemoteServer.CHARSET));
				}
			}
		});
	}

	@Override
	public String toString()
	{
		return "RemoteClient[" + sChannel.socket().getInetAddress() + ":" + sChannel.socket().getPort() + ",hanshaked=" + handshaked + "]";
	}
}
