/*
 * ConnectionManager.java - Manages persistent connections
 * Copyright (C) 2002 Slava Pestov
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
 */

package ftp;

import com.fooware.net.*;
import javax.swing.Timer;
import java.awt.event.*;
import java.awt.Component;
import java.io.IOException;
import java.util.*;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.util.Log;

public class ConnectionManager
{
	public static void forgetPasswords()
	{
		logins.clear();
	}

	public static void closeUnusedConnections()
	{
		synchronized(lock)
		{
			for(int i = 0; i < connections.size(); i++)
			{
				Connection _connect = (Connection)connections.get(i);
				if(!_connect.inUse())
				{
					closeConnection(_connect);
					i--;
				}
			}
		}
	}

	public static ConnectionInfo getConnectionInfo(Component comp,
		FtpAddress address, boolean secure)
	{
		String host, user;

		if(address != null)
		{
			host = address.host;
			user = address.user;

			ConnectionInfo info = (ConnectionInfo)logins.get(host);

			if(info != null && (info.user.equals(user) || user == null))
			{
				return info;
			}
		}
		else
			host = user = null;

		/* since this can be called at startup time,
		 * we need to hide the splash screen. */
		GUIUtilities.hideSplashScreen();

		LoginDialog dialog = new LoginDialog(comp,secure,host,user,null);
		if(!dialog.isOK())
			return null;

		host = dialog.getHost();
		int port = 21;
		int index = host.indexOf(':');
		if(index != -1)
		{
			try
			{
				port = Integer.parseInt(host.substring(index + 1));
				host = host.substring(0,index);
			}
			catch(NumberFormatException e)
			{
			}
		}

		ConnectionInfo info = new ConnectionInfo(secure,host,port,
			dialog.getUser(),dialog.getPassword());

		// hash by host name
		logins.put(host,info);

		return info;
	}

	public static Connection getConnection(ConnectionInfo info)
		throws IOException
	{
		synchronized(lock)
		{
			Connection connect = null;

			for(int i = 0; i < connections.size(); i++)
			{
				Connection _connect = (Connection)connections.get(i);
				if(_connect.info.equals(info) && !_connect.inUse())
				{
					connect = _connect;
					if(!connect.checkIfOpen())
					{
						Log.log(Log.DEBUG,ConnectionManager.class,
							"Connection "
							+ connect + " expired");
						try
						{
							connect.client.logout();
						}
						catch(IOException io)
						{
						}

						connections.remove(connect);
						connect = null;
					}
					else
						break;
				}
			}

			if(connect == null)
			{
				connect = new Connection(info);

				FtpClient client = new FtpClient();

				Log.log(Log.DEBUG,ConnectionManager.class,
					Thread.currentThread() +
					": Connecting to " + info);
				client.connect(info.host,info.port);

				if(!client.getResponse().isPositiveCompletion())
				{
					throw new FtpException(
						client.getResponse());
				}

				client.userName(info.user);

				if(client.getResponse().isPositiveIntermediary())
				{
					client.password(info.password);

					FtpResponse response = client.getResponse();
					if(!response.isPositiveCompletion())
					{
						client.logout();
						throw new FtpLoginException(response);
					}
				}
				else if(client.getResponse().isPositiveCompletion())
				{
					// do nothing, server let us in without
					// a password
				}
				else
				{
					FtpResponse response = client.getResponse();
					client.logout();
					throw new FtpLoginException(response);
				}

				connect.client = client;

				client.printWorkingDirectory();
				FtpResponse response = client.getResponse();
				if(response != null
					&& response.getReturnCode() != null
					&& response.getReturnCode().charAt(0) == '2')
				{
					String msg = response.getMessage().substring(4);
					if(msg.startsWith("\""))
					{
						int index = msg.indexOf('"',1);
						if(index != -1)
							connect.home = msg.substring(1,index);
					}
				}

				connections.add(connect);
			}

			connect.lock();

			return connect;
		}
	}

	public static void releaseConnection(Connection connect)
	{
		synchronized(lock)
		{
			connect.unlock();
		}
	}

	public static class ConnectionInfo
	{
		public boolean secure;
		public String host;
		public int port = 21;
		public String user;
		public String password;

		public ConnectionInfo()
		{
		}

		public ConnectionInfo(boolean secure, String host, int port,
			String user, String password)
		{
			this.secure = secure;
			this.host = host;
			this.port = port;
			this.user = user;
			this.password = password;
		}

		public boolean equals(Object o)
		{
			if(!(o instanceof ConnectionInfo))
				return false;

			ConnectionInfo c = (ConnectionInfo)o;
			return c.secure == secure
				&& c.host.equals(host)
				&& c.port == port
				&& c.user.equals(user)
				&& c.password.equals(password);
		}

		public String toString()
		{
			return (secure ? SFtpVFS.PROTOCOL : FtpVFS.PROTOCOL)
				+ "://" + host + ":" + port;
		}

		public int hashCode()
		{
			return host.hashCode();
		}
	}

	public static class Connection
	{
		static int COUNTER;

		Connection(ConnectionInfo info)
		{
			id = COUNTER++;
			this.info = info;

			closeTimer = new Timer(0,new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					ConnectionManager.closeConnection(Connection.this);
				}
			});
		}

		public int id;
		public ConnectionInfo info;
		public FtpClient client;
		public String home;

		boolean inUse()
		{
			return inUse;
		}

		void lock()
		{
			if(inUse)
			{
				throw new InternalError("Trying to lock "
					+ "connection twice!");
			}
			else
			{
				Log.log(Log.DEBUG,ConnectionManager.class,
					Thread.currentThread() +
					": Connection " + this + " locked");
				inUse = true;
				closeTimer.stop();
			}
		}

		void unlock()
		{
			if(!inUse)
			{
				Log.log(Log.ERROR,ConnectionManager.class,
					new Exception(Thread.currentThread() +
					": Trying to release connection twice!"));
			}
			else
			{
				Log.log(Log.DEBUG,ConnectionManager.class,
					Thread.currentThread() +
					": Connection " + this + " released");
			}

			inUse = false;
			closeTimer.stop();
			closeTimer.setInitialDelay(connectionTimeout);
			closeTimer.setRepeats(false);
			closeTimer.start();
		}

		boolean checkIfOpen()
		{
			try
			{
				// to ensure that the server didn't disconnect
				// before the keep-alive timeout expires
				client.noOp();
				client.getResponse();
				return true;
			}
			catch(Exception e)
			{
				return false;
			}
		}

		public String toString()
		{
			return id + ":" + info.host;
		}

		private boolean inUse;
		private Timer closeTimer;
	}

	// package-private members
	static void closeConnection(Connection connect)
	{
		synchronized(lock)
		{
			if(connect.inUse)
				return;

			Log.log(Log.DEBUG,ConnectionManager.class,
				"Closing connection to "
				+ connect.info);
			try
			{
				connect.client.logout();
			}
			catch(IOException io)
			{
				Log.log(Log.ERROR,ConnectionManager.class,io);
			}

			connections.remove(connect);
		}
	}

	// private members
	private static Object lock;
	private static ArrayList connections;
	private static HashMap logins;
	private static int connectionTimeout = 120000;

	static
	{
		lock = new Object();
		connections = new ArrayList();
		logins = new HashMap();
	}
}
