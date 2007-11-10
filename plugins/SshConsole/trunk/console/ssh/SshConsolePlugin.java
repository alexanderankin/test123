package console.ssh;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.VFSPathSelected;

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
			if (cs.path == path) return;
			cs.path = path;
			// print prompt
			console.Shell s = c.getShell();
			Output output = c.getShellState(s);
			s.printPrompt(c, output);
			
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



