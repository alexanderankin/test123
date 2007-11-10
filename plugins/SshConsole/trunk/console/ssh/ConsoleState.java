package console.ssh;

import java.io.IOException;
import java.io.OutputStream;

import ftp.ConnectionInfo;

/** 
 * This is the state information for each instance of the Console shell.
 * @author ezust
 *
 */
public class ConsoleState
{
	void updatePath(String newPath) {
		ConnectionInfo newInfo = ConnectionManager.getConnectionInfo(newPath);
		if (newInfo.equals(info)) return;
		info = newInfo;
		try {
			conn.logout();
		}
		catch (IOException ioe) {}
		conn.inUse = false;
		os = null;
		conn = null;
	}
	// full sftp:// path
	String path = "";
	// directory on that host
	String dir = "";
	OutputStream os = null;
	Connection conn = null;
	ConnectionInfo info = null;
}
