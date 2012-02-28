/*            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
                    Version 2, December 2004

 Copyright (C) 2004 Sam Hocevar <sam@hocevar.net>

 Everyone is permitted to copy and distribute verbatim or modified
 copies of this license document, and changing it is allowed as long
 as the name is changed.

            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION

  0. You just DO WHAT THE FUCK YOU WANT TO. 
*/
package console.ssh;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.msg.VFSPathSelected;

import console.Console;
import console.ConsolePlugin;

/**
  SshConsole - a jEdit plugin that offers a ssh shell to the Console that responds
  to VFSPathSelected events from the VFSBrowser. 
  @author Alan Ezust
  @version $Id$
*/  
public class SshConsolePlugin extends EBPlugin {

    public void start()
	{
		ConnectionManager.setup();
	}

	public void stop()
	{
		ConnectionManager.cleanup();
	}
    
	public void handleMessage(EBMessage msg)
	{
		if (msg instanceof VFSPathSelected) {
			VFSPathSelected vps = (VFSPathSelected) msg;
			String path = vps.getPath();
			if (!path.startsWith("sftp://")) return;
			if (!vps.isDirectory())
				path = path.substring(0, path.lastIndexOf('/'));
			Console c = ConsolePlugin.getConsole(vps.getView());
			if (!c.isVisible()) return;
			ConsoleState cs = ConnectionManager.getConsoleState(c);
			cs.setPath(path, true);
		}
	}
}
