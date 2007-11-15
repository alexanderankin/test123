package console.ssh;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gjt.sp.util.Log;
import console.Console;
import ftp.ConnectionInfo;


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
	static HashMap<ConnectionInfo, Connection> connectionInfos = null;
	static HashMap<Console, ConsoleState> consoleStates = null;
	// Yes, I'm hiding the base class connections on purpose!
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
			if(connect.inUse)
				return;

			Log.log(Log.DEBUG,ConnectionManager.class,
				"Closing connection to "
				+ connect.info);
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
		connectionInfos = new HashMap<ConnectionInfo, Connection>();
		consoleStates = new HashMap<Console, ConsoleState> ();
	}
	static void cleanup() {

		for (Connection c : connections) {
			closeConnection(c);
		}
		connections.clear();
		connectionInfos.clear();
		consoleStates.clear();
	}

	public static ConsoleState getConsoleState(Console c) {
		if (consoleStates.containsKey(c)) return consoleStates.get(c);
		ConsoleState ss = new ConsoleState();
		consoleStates.put(c, ss);
		return ss;
	}

	public static synchronized Connection getShellConnection(Console console, ConnectionInfo info) 
	throws IOException {
		Connection connect = null;
		for (Connection c: connections)  {
			if (c.info.equals(info) &&  !c.inUse())  {
				connect = c;
				if(!connect.checkIfOpen())
				{
					Log.log(Log.DEBUG,ConnectionManager.class,
						"Connection "
						+ connect + " expired");
					try
					{
						connect.logout();
					}
					catch(IOException io)
					{
					}

					connections.remove(connect);
					connect = null;
				}
			}	
		}
		if(connect == null )
		{
			Log.log(Log.DEBUG,ConnectionManager.class,
				Thread.currentThread() +
				": Connecting to " + info);
			connect = new Connection(console, info);
			connections.add(connect);
		}
		else {
			connect.setConsole(console);
		}
		connect.inUse=true;
		return connect;
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
