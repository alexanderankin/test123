package console.ssh;

import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Matcher;

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
	
	public void execute(Console console, String input, Output output, Output error, String command)
	{
		 try {
			ConsoleState ss = ConnectionManager.getConsoleState(console);
			if (ss.conn == null) {
				ConnectionInfo info = ConnectionManager.getConnectionInfo(ss.path);
				Session session=ConnectionManager.client.getSession(info.user, info.host, 22);
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
			Log.log (Log.WARNING, this, "no ssh session:", e);
		}
		finally {
			printPrompt(console, output);
		}
	}

	public void printPrompt(Console console, Output output)
	{
		ConsoleState s = ConnectionManager.getConsoleState(console);
		String promptString = "[no sftp:// connections?] >";
		if (s.path != null && s.path.length() > 0) {
			ConnectionInfo info = ConnectionManager.getConnectionInfo(s.path);
			promptString = "[ssh:" + info.user + "@" + info.host + "]> ";
		}
		if (s.conn == null || s.conn.inUse != true) { 
		        output.writeAttrs(ConsolePane.colorAttributes(console.getPlainColor()), 
			"\n" + promptString);
		}
	}    
	
};

