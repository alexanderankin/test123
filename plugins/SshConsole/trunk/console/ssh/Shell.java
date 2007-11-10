package console.ssh;

import java.io.IOException;
import java.io.OutputStream;

import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.util.Log;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import console.Console;
import console.ConsolePane;
import console.ErrorOutput;
import console.Output;
import ftp.ConnectionInfo;
import ftp.FtpVFS;

/**
 * Secure shell interface for jEdit console. A singleton exists for the whole jedit process.
 * @author ezust
 *
 */
public class Shell extends console.Shell {

	
	public void closeConsole(Console console)
	{
		// TODO Auto-generated method stub
		super.closeConsole(console);
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
			if (command != null && command.length() > 0) {
				ConsoleState ss = ConnectionManager.getConsoleState(console);
				// Expected to be non-null if you connected via FSB already
				ConnectionInfo info = ConnectionManager.getConnectionInfo(ss.path);
				Session session=ConnectionManager.client.getSession(info.user, info.host, 22);
				Connection c = ConnectionManager.getShellConnection(console, info);
				session.setUserInfo(c);
				OutputStream os = c.ostr;
				Log.log (Log.MESSAGE, this, "Command: " + command + "  input: " + input);
				os.write((command + "\n").getBytes() );
				os.flush();
			}
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
			promptString = "[" + s.path + "]> ";
		}
		output.writeAttrs(ConsolePane.colorAttributes(console.getPlainColor()), 
			"\n" + promptString);
	}    
	
};

