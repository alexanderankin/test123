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

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import com.jcraft.jsch.Session;

import console.Console;
import console.ConsolePane;
import console.Output;

import ftp.ConnectionInfo;

// {{{ class Shell
/**
 * Secure shell interface for jEdit console. A singleton exists for the whole jedit process.
 * State information for individual Console instances is handled by the ConsoleState class.
 * @author ezust
 * @version $Id$
 */
public class Shell extends console.Shell {
	static final byte[] EOF = new byte[] {4};
	static final byte[] SUSPEND = new byte[] {26};
	static final byte[] STOP = new byte[] {3};

	@Override
	public void endOfFile(Console console)
	{
		new ShellAction(console, EOF).actionPerformed(null);;
		console.stopAnimation();
	}

	public boolean handlesVFS(String vfsPath) {
		return (vfsPath.startsWith("sftp://"));
	}
	
	public boolean chDir(Console console, String path) {
		if (!handlesVFS(path)) return false;
		if (!jEdit.isStartupDone()) return false;
		ConsoleState cs = ConnectionManager.getConsoleState(console);
		cs.setPath(path, true);
		return true;
	}

	static final byte[] newline = new byte[] { '\n' };
	/** Send a ctrl-c down the pipe, to end the process, rather than the session. */
	public void stop(Console console)
	{
		new ShellAction(console, STOP).actionPerformed(null);
	}

	/** Sends ctrl-d (EOF) down the pipe, to signal end of file. */
	public void closeConsole(Console console)
	{
		endOfFile(console);
	}

	public void openConsole(Console console)
	{
	}

	public Shell() {
		super("ssh");
	}

    // {{{ execute() method
	/**
	 * @param console the instance that is running this command
	 * @param input is always null
	 * @param output a ShellState instance
	 * @param error another writable thing for errors (not used)
	 * @param command the command to execute
	 */
	public void execute(Console console, String input, Output output, Output error, String command)
	{
		
		ConsoleState cs = ConnectionManager.getConsoleState(console);

		if (cs.conn == null)  try {
			ConnectionInfo info = ConnectionManager.getConnectionInfo(cs.getPath());
			if (info == null || cs.getPath().equals("")) {
				Buffer b = console.getView().getEditPane().getBuffer( );				
				String p = b.getPath();			
				// check current buffer
				if (p.startsWith("sftp:"))
					cs.setPath(MiscUtilities.getParentOfPath(p), true);
			}
			info = ConnectionManager.getConnectionInfo(cs.getPath());
			if (info == null) {
				Log.log(Log.WARNING, this, "Unable to get connectioninfo for: " + cs.getPath());
				console.stopAnimation();
				printPrompt(console, output);
				return;
			}
			
			cs.info = info;
			Session session=ConnectionManager.client.getSession(info.user, info.host, info.port);
			Connection c = ConnectionManager.getShellConnection(console, info);
			session.setUserInfo(c);
			cs.os = c.ostr;
			cs.conn = c;
			cs.setPath("");
		}
		catch (Exception e) {
			Log.log (Log.WARNING, this, "getShellConnection failed:", e);
			console.stopAnimation();
		}
		boolean consumed = cs.preprocess(command);
		if (consumed) {
			printPrompt(console, output);
		}
		else if (cs.os != null) try {
			cs.os.write((command + "\n").getBytes() );
			cs.os.flush();
		}
		catch (IOException ioe ) {
			Log.log(Log.WARNING, this, "IOException writing to ssh pipe: " + ioe.toString());
			cs.close();
			console.stopAnimation();
		}

	} // }}}


	public void printPrompt(Console console, Output output)
	{
		ConsoleState s = ConnectionManager.getConsoleState(console);
		String promptString = "[no sftp:// connections?] >";
		if (s.info != null) {
			promptString = "[ssh:" + s.info.user + "@" + s.info.host + "]> ";
		}		
		if (s.conn == null || s.conn.inUse != true) {
		        output.writeAttrs(ConsolePane.colorAttributes(console.getPlainColor()),
			"\n" + promptString);
		}
		else try {
			s.os.write(newline);
			s.os.flush();
		}
		catch (IOException ioe) {
			endOfFile(console);
		}
	}

	/** sends a ctrl-Z down the pipe, to suspend the current job */
	public void detach(Console console)
	{
		new ShellAction(console, SUSPEND).actionPerformed(null);

	}


	/* Actions performed via keyboard when the ConsolePane has focus */
	@SuppressWarnings("serial")
	public static class ShellAction extends AbstractAction {

		final Console con;
		final byte[] cmd;
		public ShellAction(Console console, final byte[] str) {
			con = console;
			cmd = str;
		}

		public void actionPerformed(ActionEvent e)
		{
			if (!con.getShell().getName().equals("ssh")) return;
			ConsoleState cs = ConnectionManager.getConsoleState(con);
			if (cs != null && cs.os != null) try {
				cs.os.write(cmd);
				cs.os.flush();
			}
			catch (IOException ioe) {
				Log.log(Log.WARNING, this, "ioexception when writing - connection closed");
				cs.close();
			}
		}
	}

}; // }}}

// :folding=explicit: