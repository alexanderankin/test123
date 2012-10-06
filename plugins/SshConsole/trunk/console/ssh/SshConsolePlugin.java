/*          DO WHAT THE FRAK YOU WANT TO PUBLIC LICENSE (WTFPL)
                    Version 4, October 2012
	    Based on the wtfpl: http://sam.zoy.org/wtfpl/

 Copyright (C) 2012 Alan Ezust

 Everyone is permitted to copy and distribute verbatim or modified
 copies of this license document, and changing it is allowed as long
 as the name is changed.

            DO WHAT THE FRAK YOU WANT TO PUBLIC LICENSE
   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION

  0. You just DO WHAT THE FRAK YOU WANT TO.
  1. It is provided "as is" without any warranty whatsoever.
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
