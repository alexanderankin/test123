package console.ssh;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
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
			VFSPathSelected vps = (VFSPathSelected) msg;
			if (!vps.getPath().startsWith("sftp://")) return;
			Console c = ConsolePlugin.getConsole(vps.getView());
			c.setShell("ssh");
			ConsoleState cs = ConnectionManager.getConsoleState(c);
			if (cs.path == vps.getPath()) return;
		
			if (!jEdit.getBooleanProperty("console.changedir.nodeselect")) return;
			cs.path = vps.getPath();			
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



