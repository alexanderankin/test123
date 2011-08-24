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

import com.kpouer.jedit.remotecontrol.xstream.BufferConverter;
import com.kpouer.jedit.remotecontrol.xstream.EditPaneConverter;
import com.kpouer.jedit.remotecontrol.xstream.GlobalConverter;
import com.kpouer.jedit.remotecontrol.xstream.ViewConverter;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Matthieu Casanova
 */
public class RemoteServer implements Runnable
{
	static final Charset CHARSET = Charset.forName("UTF-8");
	private final int port;
	private ServerSocketChannel socketChannel;

	private final Object LOCK;
	private volatile boolean running = true;
	private Selector selector;

	private final Map<SocketChannel, RemoteClient> clients;
	private final XStream xstream;

	private static boolean DEBUG = true;

	private final jEditListener jEditListener;

	public RemoteServer(int port)
	{
		this.port = port;
		LOCK = new Object();
		clients = new HashMap<SocketChannel, RemoteClient>();
		jEditListener = new jEditListener(this);
		xstream = new XStream(new JettisonMappedXmlDriver());
		xstream.registerConverter(new GlobalConverter());
		xstream.registerConverter(new BufferConverter());
		xstream.registerConverter(new ViewConverter(jEditListener));
		xstream.registerConverter(new EditPaneConverter(jEditListener));
//		xstream.registerConverter(new EBMessageConverter(), XStream.PRIORITY_NORMAL - 1);
//		xstream.registerConverter(new BufferChangingConverter());
//		xstream.registerConverter(new BufferUpdateConverter());
//		xstream.registerConverter(new BufferSetMessageConverter());


	}

	public jEditListener getjEditListener()
	{
		return jEditListener;
	}

	void start()
	{
		synchronized (LOCK)
		{
			Thread thread = new Thread(this);
			thread.setName("RemoteControl");
			thread.setDaemon(true);
			thread.start();
		}
	}

	void stop()
	{
		synchronized (LOCK)
		{
			running = false;
		}
	}

	public void dispatchMessage(EBMessage message)
	{
		if (clients.isEmpty())
			return;
		String s = xstream.toXML(message);
		for (RemoteClient remoteClient : clients.values())
		{
			if (DEBUG)
			{
				Log.log(Log.MESSAGE, this, s);
			}
			remoteClient.sendMessage(s.getBytes(CHARSET));
		}
	}

	@Override
	public void run()
	{
		Log.log(Log.MESSAGE, this, "RemoteServer started on port " + port);
		try
		{
			jEditListener.start();
			selector = Selector.open();

			socketChannel = ServerSocketChannel.open();
			socketChannel.configureBlocking(false);
			socketChannel.socket().bind(new InetSocketAddress(port));

			socketChannel.register(selector, SelectionKey.OP_ACCEPT);
			while (running)
			{
				selector.select();

				Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
				while (iterator.hasNext())
				{
					SelectionKey selectionKey = iterator.next();
					iterator.remove();
					try
					{
						processSelectionKey(selectionKey);
					}
					catch (Exception e)
					{
						Log.log(Log.ERROR, this, e, e);
					}
				}
			}
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, "Unable to start RemoteServer on port " + port, e);
		}
		finally
		{
			if (socketChannel != null)
			{
				try
				{
					socketChannel.close();
				}
				catch (IOException e)
				{
				}
			}
			jEditListener.stop();
			for (Map.Entry<SocketChannel, RemoteClient> socketChannelRemoteClientEntry : clients.entrySet())
			{
				try
				{
					socketChannelRemoteClientEntry.getValue().sendMessage("BYE".getBytes(CHARSET));
					socketChannelRemoteClientEntry.getKey().close();
				}
				catch (IOException e)
				{
				}
			}
			clients.clear();
		}
		Log.log(Log.MESSAGE, this, "RemoteServer closed");
	}

	private void processSelectionKey(SelectionKey selectionKey) throws IOException
	{
		if (selectionKey.isAcceptable())
		{
			ServerSocketChannel ssChannel = (ServerSocketChannel) selectionKey.channel();
			SocketChannel sChannel = ssChannel.accept();
			if (sChannel != null)
			{
				sChannel.configureBlocking(false);
				sChannel.register(selector, sChannel.validOps());
				RemoteClient client = new RemoteClient(sChannel, xstream);
				clients.put(sChannel, client);
				Log.log(Log.MESSAGE, this, "CLient connected : " + client);
			}
		}
		if (selectionKey.isReadable())
		{
			SocketChannel channel = (SocketChannel) selectionKey.channel();
			ByteBuffer buf = ByteBuffer.allocateDirect(1024);
			int nbRead = channel.read(buf);
			if (nbRead == -1)
			{
				RemoteClient removed = clients.remove(channel);
				Log.log(Log.MESSAGE, this, "CLient disconnected : " + removed);
				channel.close();
			}
			else
			{
				RemoteClient remoteClient = clients.get(channel);
				remoteClient.read(buf);
			}
		}
	}
}
