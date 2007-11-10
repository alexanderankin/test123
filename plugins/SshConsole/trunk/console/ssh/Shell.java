package console.ssh;

import org.gjt.sp.util.Log;

import com.jcraft.jsch.Session;

import console.Console;
import console.ConsolePane;
import console.Output;
import ftp.ConnectionInfo;

/**
 * Secure shell interface for jEdit console. A singleton exists for the whole jedit process.
 * @author ezust
 *
 */
public class Shell extends console.Shell {

	
	public void closeConsole(Console console)
	{
		
	}

	@Override
	public void openConsole(Console console)
	{
	
	}

	public Shell() {
		super("ssh");
	}
	
	/**
	 * @param console the instance that is running this command
	 * @param input is always null
	 * @param output a ShellState instance 
	 * @param error another writable thing for errors (not used)
	 * @param command the command to execute
	 */
	public void execute(Console console, String input, Output output, Output error, String command)
	{
		 try {
			ConsoleState ss = ConnectionManager.getConsoleState(console);
			if (ss.conn == null) {
				ConnectionInfo info = ConnectionManager.getConnectionInfo(ss.path);
				if (info == null) {
					Log.log(Log.ERROR, this, "Unable to get connectioninfo for: " + ss.path);
					return;
				}
				ss.info = info;
				Session session=ConnectionManager.client.getSession(info.user, info.host, info.port);
				Connection c = ConnectionManager.getShellConnection(console, info);
				session.setUserInfo(c);
				ss.os = c.ostr;
				ss.conn = c;
			}
			Log.log (Log.MESSAGE, this, "Command: " + command + "  input: " + input);
			ss.os.write((command + "\n").getBytes() );
			ss.os.flush();
		}
		catch (Exception e) {
			Log.log (Log.WARNING, this, "execute failed:", e);
		}
		finally {
			printPrompt(console, output);
		}
	}

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
	}    
	
};

