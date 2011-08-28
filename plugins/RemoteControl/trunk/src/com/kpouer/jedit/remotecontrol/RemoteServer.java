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

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.util.Log;

import javax.swing.event.EventListenerList;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Matthieu Casanova
 */
public class RemoteServer implements Runnable
{
	public static final Charset CHARSET = Charset.forName("UTF-8");
	private final int port;
	private ServerSocketChannel socketChannel;

	private final Object LOCK;
	private volatile boolean running = true;
	private Selector selector;

	private final Map<SocketChannel, RemoteClient> clients;
	private static boolean DEBUG = true;

	private final jEditListener jEditListener;
	private Thread thread;

	private final EventListenerList listeners;

	public RemoteServer(int port)
	{
		this.port = port;
		LOCK = new Object();
		clients = new HashMap<SocketChannel, RemoteClient>();
		jEditListener = new jEditListener(this);
		listeners = new EventListenerList();
	}

	public jEditListener getjEditListener()
	{
		return jEditListener;
	}

	void start()
	{
		synchronized (LOCK)
		{
			thread = new Thread(this);
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
			if (thread != null)
			{
				thread.interrupt();
			}
		}
	}

	public void dispatchMessage(EBMessage message)
	{
		if (clients.isEmpty())
			return;
		for (RemoteClient remoteClient : clients.values())
		{
			String s = remoteClient.getSerializer().serialize(message);
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
			fireServerClosed();
		}
		thread = null;
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
				sChannel.register(selector, SelectionKey.OP_READ);
				try
				{
					RemoteClient client = new RemoteClient(sChannel);
					clients.put(sChannel, client);
					fireClientConnected(client);
					Log.log(Log.MESSAGE, this, "CLient connected : " + client);
				}
				catch (Exception e)
				{
					sChannel.close();
					Log.log(Log.ERROR, this, e, e);
				}
			}
		}
		if (selectionKey.isReadable())
		{
			SocketChannel channel = (SocketChannel) selectionKey.channel();
			ByteBuffer buf = ByteBuffer.allocateDirect(1024);
			int nbRead = channel.read(buf);
			if (nbRead == -1)
			{
				removeClient(channel);
			}
			else
			{
				RemoteClient remoteClient = clients.get(channel);
				remoteClient.read(buf);
			}
		}
	}

	public void removeClient(SocketChannel channel)
	{
		RemoteClient removed = clients.remove(channel);
		fireClientDisconnected(removed);
		Log.log(Log.MESSAGE, this, "Client disconnected : " + removed);
		try
		{
			channel.close();
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, e, e);
		}
	}

	public void visitClients(ClientVisitor visitor)
	{
		synchronized (clients)
		{
			for (RemoteClient remoteClient : clients.values())
			{
				visitor.visit(remoteClient);
			}
		}
	}

	public void addServerListener(ServerListener listener)
	{
		listeners.add(ServerListener.class, listener);
	}

	public void removeServerListener(ServerListener listener)
	{
		listeners.remove(EventListener.class, listener);
	}

	public void fireClientConnected(RemoteClient client)
	{
		ServerListener[] list;
		synchronized (listeners)
		{
			list = listeners.getListeners(ServerListener.class);
		}
		for (ServerListener clientListener : list)
		{
			clientListener.clientConnected(client);
		}
	}

	public void fireClientDisconnected(RemoteClient client)
	{
		ServerListener[] list;
		synchronized (listeners)
		{
			list = listeners.getListeners(ServerListener.class);
		}
		for (ServerListener clientListener : list)
		{
			clientListener.clientDisconnected(client);
		}
	}

	public void fireServerClosed()
	{
		ServerListener[] list;
		synchronized (listeners)
		{
			list = listeners.getListeners(ServerListener.class);
		}
		for (ServerListener clientListener : list)
		{
			clientListener.serverClosed();
		}
	}
}
