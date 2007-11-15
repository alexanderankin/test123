package console.ssh;

import java.io.IOException;
import java.io.OutputStream;

import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.msg.VFSUpdate;

import console.CommandOutputParser;

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
	
	CommandOutputParser dirChangeListener = null;
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
		if (path.equals(newPath)) return;
		ConnectionInfo newInfo = ConnectionManager.getConnectionInfo(newPath);
		path = newPath;
		if (dirChangeListener != null)
			dirChangeListener.setDirectory(path);

		if (info == null || !newInfo.equals(info)) { 
			info = newInfo;
			if (conn != null) try 
			{
				conn.logout();
				conn.inUse = false;
			}
			catch (IOException e) {}
			os = null;
			conn = null;
		}

	} // }}}

	public String getPath() {
		return path;
	}
	
	public void preprocess(String command) {
		if (command.startsWith("cd ")) {
			String base = ConnectionManager.extractBase(path);
			String direct = ConnectionManager.extractDirectory(path);
			String argument = command.substring(3);
			String newPath = null;
			if (argument.startsWith("/")) {
				newPath = base + argument;
			}
			else {
				newPath = base + MiscUtilities.constructPath(dir, argument);
			}
			setPath(newPath);
			
		}
		
	}
	
	public void close() {
		if (conn != null) try {
			conn.logout();
			conn.inUse = false;
			conn = null;
		}
		catch (IOException ioe) {}
	}
	public void setDirectoryChangeListener(CommandOutputParser cop) {
		dirChangeListener = cop;
	}
	
} // }}}
