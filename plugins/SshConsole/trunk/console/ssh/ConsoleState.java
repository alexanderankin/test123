package console.ssh;

import java.io.IOException;
import java.io.OutputStream;
import org.gjt.sp.jedit.MiscUtilities;
import console.CommandOutputParser;
import console.Console;
import console.Output;
import ftp.ConnectionInfo;

// {{{ ConsoleState class
/** 
 * This is the state information for each instance of the Console ssh shell.
 * @author ezust
 *
 */
public class ConsoleState
{
    // {{{ members
    
	public ConsoleState(Console c) {
		console = c;
	}
	
        // full sftp:// path
	private String path = "";
	
	Console console;
	// last directory changed to via FSB
	String dir = "";
	OutputStream os = null;
	// reference to ssh connection (may be reused by other consoles later) 
	Connection conn = null;
	// login information extracted from the ftp plugin
	ConnectionInfo info = null;
	// reference to other object interested in changes to working directory
	CommandOutputParser dirChangeListener = null;
    // }}}

    // {{{ setPath()
	/**
	 * Has the side-effect of closing the connection if it is currently open to a path that is not 
	 * on the same remote server as newPath
	 * TODO: perhaps return the connection to a pool instead of logout and kill connection?
	 * @param newPath a sftp:// VFS path assumed used as the base of all relative paths
	 * encountered during error parsing.
	 *  
	 */
	void setPath(String newPath) 
	{
		if (path.equals(newPath)) return;
		ConnectionInfo newInfo = ConnectionManager.getConnectionInfo(newPath);
		path = newPath;
		if (dirChangeListener != null)
			dirChangeListener.setDirectory(path);

		if (info == null || !newInfo.equals(info)) { 
			info = newInfo;
			if (conn != null) try 
			{
				os.close();
				conn.logout();
				conn.inUse = false;
				
			}
			catch (IOException e) {}
			finally 
			{
				os = null;
				conn = null;
			}
		}
		// update current directory
		newPath = ConnectionManager.extractDirectory(newPath);
		if (newPath == null || dir.equals(newPath)) return;
		dir = newPath;
		String command = "cd " + dir; 
		console.Shell s = console.getShell();
		Output output = console.getShellState(s);
		output.print(console.getWarningColor(), command);
		s.execute(console, null, output, output, command);
	} // }}}

	// {{{ getPath() method
	public String getPath() {
		return path;
	} // }}}
	
	// {{{ preprocess method
	protected void preprocess(String command) {
		if (command.startsWith("cd ")) {
			String base = ConnectionManager.extractBase(path);
			String direct = ConnectionManager.extractDirectory(path);
			String argument = command.substring(3);
			String newPath = null;
			if (argument.startsWith("/")) {
				newPath = base + argument;
			}
			else {
				newPath = base + MiscUtilities.constructPath(direct, argument);
			}
			setPath(newPath);
		}
	} // }}}
	
	// {{{ close() method
	public void close() {
		if (conn != null) try {
			conn.logout();
			ConnectionManager.closeConnection(conn);
			conn.inUse = false;
			conn = null;
		}
		catch (IOException ioe) {}
	} // }}}
	
	// {{{ setDirectoryChangeListener method
	public void setDirectoryChangeListener(CommandOutputParser cop) {
		dirChangeListener = cop;
	} // }}}
	
} // }}}
