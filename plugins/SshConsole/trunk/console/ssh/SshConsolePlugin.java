package console.ssh;

import java.util.regex.Matcher;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.VFSPathSelected;
import org.gjt.sp.util.Log;

import console.Console;
import console.ConsolePlugin;
import console.Output;

public class SshConsolePlugin extends EBPlugin {


	@Override
	public void handleMessage(EBMessage msg)
	{
		if (msg instanceof VFSPathSelected) {
			if (!jEdit.getBooleanProperty("console.changedir.nodeselect")) return;
			VFSPathSelected vps = (VFSPathSelected) msg;
			String path = vps.getPath();
			if (!path.startsWith("sftp://")) return;
			EditAction ea = jEdit.getAction("console.shell.ssh-show");
			ea.invoke(vps.getView());
			if (!vps.isDirectory())
				path = path.substring(0, path.lastIndexOf('/'));
			Console c = ConsolePlugin.getConsole(vps.getView());
			ConsoleState cs = ConnectionManager.getConsoleState(c);
			cs.setPath(path);
			// change directory
			Matcher m = ConnectionManager.sftpPath.matcher(path);
			if (m.matches()) {
				path = "/" + m.group(4);
				if (cs.dir.equals(path)) return;
				cs.dir = path;
				String command = "cd " + path; 
				console.Shell s = c.getShell();
				Output output = c.getShellState(s);
				output.print(c.getWarningColor(), command);
				s.execute(c, null, output, output, command);
			}
			else Log.log (Log.ERROR, this, "ERROR:" + path + " does not parse");
		}
	}

	public void start()
	{
		ConnectionManager.setup();
	}

	public void stop()
	{
		ConnectionManager.cleanup();
	}

	
}



