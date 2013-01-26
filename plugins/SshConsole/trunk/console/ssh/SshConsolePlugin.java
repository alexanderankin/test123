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

import org.gjt.sp.jedit.EditPlugin;


/**
  SshConsole - a jEdit plugin that offers a ssh shell to the Console that responds
  to VFSPathSelected events from the VFSBrowser.
  @author Alan Ezust
  @version $Id$
*/
public class SshConsolePlugin extends EditPlugin {

    public void start()
	{
		ConnectionManager.setup();
	}

	public void stop()
	{
		ConnectionManager.cleanup();
	}
}
