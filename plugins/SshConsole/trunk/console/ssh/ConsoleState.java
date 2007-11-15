package console.ssh;

import java.io.IOException;
import java.io.OutputStream;

import ftp.ConnectionInfo;

// {{{ consolestate
/** 
 * This is the state information for each instance of the Console shell.
 * @author ezust
 *
 */
public class ConsoleState
{
    // {{{ members
    
    // full sftp:// path
	private String path = "";
	// last directory changed to via FSB
	String dir = "";
	OutputStream os = null;
	// reference to ssh connection (may be reused by other consoles later) 
	Connection conn = null;
	// login information extracted from the ftp plugin
	ConnectionInfo info = null;
    // }}}

    // {{{ setPath()
	/**
	 * Has the side-effect of closing the connection if it is currently open to a path that is not 
	 * on the same remote server as newPath
	 * TODO: perhaps return the connection to a pool?
	 * @param newPath a sftp:// VFS path assumed used as the base of all relative paths
	 * encountered during error parsing.
	 *  
	 */
	void setPath(String newPath) {
		ConnectionInfo newInfo = ConnectionManager.getConnectionInfo(newPath);
		path = newPath;
		
		if (newInfo.equals(info)) return;
		info = newInfo;
		try {
			conn.logout();
			conn.inUse = false;
		}
		catch (Exception e) {}
		os = null;
		conn = null;

	} // }}}

	public String getPath() {
		return path;
	}
} // }}}
