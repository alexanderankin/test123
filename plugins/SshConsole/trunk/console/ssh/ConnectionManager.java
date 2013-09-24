/*          DO WHAT THE FRAK YOU WANT TO PUBLIC LICENSE (WTFPL)
                    Version 4, October 2012
	    Based on the wtfpl: http://sam.zoy.org/wtfpl/

 Copyright © 2012 Alan Ezust

 Everyone is permitted to copy and distribute verbatim or modified
 copies of this license document, and changing it is allowed as long
 as the name is changed.

            DO WHAT THE FRAK YOU WANT TO PUBLIC LICENSE
   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION

  0. You just DO WHAT THE FRAK YOU WANT TO.
  1. It is provided "as is" without any warranty whatsoever.
*/


package console.ssh;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gjt.sp.util.Log;
import console.Console;
import ftp.ConnectionInfo;

/** Manages a mapping of paths to ssh connections.
 *
 * Does a whole lot of regex-based parsing of paths to remove/insert
 * an sftp:// prefix to them. 
 * 
 * TODO: get rid of all the regexes and reuse common URL/URI parsing methods. 
 * 
 * @author ezust
 *
 */
public class ConnectionManager extends ftp.ConnectionManager
{
	// {{{ members
	/**
	 * group(1) - user (optional)
	 * group(2) - host
	 * group(3) - port (optional)
	 * group(4) - path
	 */
	static Pattern sftpPath = Pattern.compile("sftp://(?:([^@]+)@)?([^/:]+)(?::(\\d+))?/(.*)$");
	static HashMap<Console, ConsoleState> consoleStates = null;
	/** Yes, I'm hiding the base class connections on purpose! */
	static ArrayList<Connection> connections = new ArrayList<Connection>();
	// }}}


	/**
	 * @param vfsPath a path of the form sftp://(user@)?host(:port)?(/path)
	 * @return path part from vfsPath (on a remote host).
	 */
	static String extractDirectory(String vfsPath) {
		String path = null;
		// change directory
		Matcher m = sftpPath.matcher(vfsPath);
		if (m.matches()) {
			path = "/" + m.group(4);
		}
		return path;
	}

	/**
	 *
	 * @param vfsPath
	 * @return everything but the path part of the vfsPath
	 */
	static String extractBase(String vfsPath) {
		String path = null;
		// change directory
		Matcher m = sftpPath.matcher(vfsPath);
		if (m.matches()) {
			int end = Math.max(m.end(3), m.end(2));
			end = Math.max(end, m.end(1));
			path = vfsPath.substring(0, end);
		}
		return path;
	}

	static ConnectionInfo parseAddress(String vfsPath) {
		Matcher m = sftpPath.matcher(vfsPath);
		if (!m.matches()) return null;
		String user = m.group(1) != null? m.group(1): System.getProperty("user.name");
		String host = m.group(2);
		String portstr = m.group(3) != null? m.group(3) : "22";
		int port = Integer.parseInt(portstr);
		return new ConnectionInfo(true, host, port, user, null, null );
	}

	public static void closeUnusedConnections()
	{
		synchronized(lock)
		{
			for(int i = 0; i < connections.size(); i++)
			{
				Connection _connect = connections.get(i);
				if(!_connect.inUse())
				{
					closeConnection(_connect);
					i--;
				}
			}
		}
	} //}}}

	//{{{ closeConnection() method
	static void closeConnection(Connection connect)
	{
		synchronized(lock)
		{  
			if(connect.inUse) {
				Log.log(Log.DEBUG, ConnectionManager.class, "Can't close connection that still in use");
				return;
			}

			Log.log(Log.DEBUG,ConnectionManager.class, "Closing connection to " + connect.info);
			try
			{
				connect.logout();
			}
			catch(IOException io)
			{
				Log.log(Log.ERROR,ConnectionManager.class,io);
			}
			connections.remove(connect);
		}
	} //}}}


	static void setup()
	{
		lock = new Object();
		consoleStates = new HashMap<Console, ConsoleState> ();
	}
	public static void cleanup() {

		for (ConsoleState cs: consoleStates.values()) {
			cs.close();
		}
		connections.clear();
		consoleStates.clear();
	}

	public static ConsoleState getConsoleState(Console c) {
		if (consoleStates.containsKey(c)) return consoleStates.get(c);
		ConsoleState ss = new ConsoleState(c);
		consoleStates.put(c, ss);
		return ss;
	}

	public static synchronized Connection getShellConnection(Console console, ConnectionInfo info)
	throws IOException
	{
		Connection connect = null;

		synchronized (lock)
		{
			Iterator<Connection> iterator = connections.iterator();
			while (iterator.hasNext())
			{
				Connection c = iterator.next();
				if (c.info.equals(info) &&  !c.inUse())
				{
					connect = c;
					if(!connect.checkIfOpen())
					{
						Log.log(Log.DEBUG,ConnectionManager.class, "Connection " + connect + " expired");
						try
						{
							connect.logout();
						}
						catch(IOException io)
						{
						}

						iterator.remove();
						connect = null;
					}
				}
			}

			if(connect == null )
			{
				Log.log(Log.DEBUG, ConnectionManager.class, 
					Thread.currentThread() + ": Connecting to " + info);
				connect = new Connection(console, info);
				connections.add(connect);
			}
			else {
				connect.setConsole(console);
			}
			connect.inUse=true;
			return connect;
		}
	}

	/**
	 *
	 * @param vfsPath must be of the form sftp://user@host/home/user/path/to/resource
	 * @return
	 */
	public static ConnectionInfo getConnectionInfo(String vfsPath) {

		Matcher m = sftpPath.matcher(vfsPath);
		if (!m.matches()) return null;
		String user = m.group(1);
		if (user == null || user.length() < 1) {
			user = System.getProperty("user.name");
		}
		String host = m.group(2);
		String port = m.group(3);
		if (port == null) port = "22";
		return logins.get(host+":" + port);
	}

}
