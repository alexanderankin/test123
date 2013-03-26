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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.browser.VFSBrowser;

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
	public void setPath(String newPath ) {
		setPath(newPath, true);
	}
	/**
	 * Has the side-effect of closing the connection if it is currently open to a path that is not
	 * on the same remote server as newPath, as well as changing directories on the remote
	 * host, and in the command output parser.
	 * TODO: perhaps return the connection to a pool instead of logout and kill connection?
	 * @param newPath a sftp:// VFS path assumed used as the base of all relative paths
	 * encountered during error parsing.
	 *
	 */
	public void setPath(String newPath, boolean chDirAfter)
	{
		/* Workaround because sometimes windows spits out paths
		 *  with the wrong separator char */
		if (File.separatorChar == '\\') {
			newPath = newPath.replace('\\', '/');
		}
		
		if (path.equals(newPath)) return;
		path = newPath;
		ConnectionInfo newInfo = ConnectionManager.getConnectionInfo(newPath);
		if (newInfo == null) return;		
		// update current directory in the CommandOutputParser
		
		if (info == null || !newInfo.equals(info)) {
			info = newInfo;
			if (conn != null) try
			{
				os.close();
				conn.logout();
			}
			catch (IOException e) {}
			finally
			{
				dir = "";
				os = null;
				conn = null;
			}
		}
		// update current directory
		if (dirChangeListener != null)
			dirChangeListener.setDirectory(path);
		newPath = ConnectionManager.extractDirectory(newPath);
		if (newPath == null || dir.equals(newPath)) return;
		dir = newPath;
		// update current directory on remote host
		console.Shell s = console.getShell();
		if (!s.getName().equals("ssh")) return;
		if (chDirAfter) {
			String command = "cd " + dir;
			Output output = console.getShellState(s);
			s.execute(console, null, output, output, command);
		}
	} // }}}

	// {{{ getPath() method
	public String getPath() {
		return path;
	} // }}}

	// {{{ preprocess method
	/**
	 * Extracts information from user-enter commands such as where they are "chdiring".
	 * Also processes built-in commands.
	 * @return true if the command was consumed (i.e. a built-in)
	 */
	protected boolean preprocess(String command) {
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
			setPath(newPath, false);
			return false;
		}
		if (command.startsWith("%browse")) {
			String base = ConnectionManager.extractBase(path);
			String direct = ConnectionManager.extractDirectory(path);
			String arg = command.substring(7);
			String newPath = path;
			if (arg.length() > 1) {
				arg = arg.substring(1);
				if (arg.length() > 0) {
					if (arg.startsWith("/")) {
						newPath = base + arg;
					}
					else {
						newPath = base + MiscUtilities.constructPath(direct, arg);
					}
				}
			}
			VFSBrowser.browseDirectory(console.getView(), newPath);
			return true;
		}
		if (command.startsWith("%edit ")) {
			String arg = command.substring(6);
			jEdit.openFile(console.getView(), path, arg, false, null);
			return true;
		}


		return false;
	} // }}}

	// {{{ close() method
	public void close() {
		dir = "";
		if (conn != null) try {
			conn.logout();
			ConnectionManager.closeConnection(conn);
			conn = null;
		}
		catch (IOException ioe) {}
	} // }}}

	// {{{ setDirectoryChangeListener method
	public void setDirectoryChangeListener(CommandOutputParser cop) {
		dirChangeListener = cop;
	} // }}}

} // }}}
