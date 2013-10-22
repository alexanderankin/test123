/*          DO WHAT THE FRAK YOU WANT TO PUBLIC LICENSE (WTFPL)
                    Version 4, October 2012
	    Based on the wtfpl: http://sam.zoy.org/wtfpl/

 Copyright © 2012 Alan Ezust, Artem Bryantsev

 Everyone is permitted to copy and distribute verbatim or modified
 copies of this license document, and changing it is allowed as long
 as the name is changed.

            DO WHAT THE FRAK YOU WANT TO PUBLIC LICENSE
   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION

  0. You just DO WHAT THE FRAK YOU WANT TO.
  1. It is provided "as is" without any warranty whatsoever.
*/
package console.ssh;


// {{{ Imports
import java.awt.Color;
import java.io.InputStream;

import console.CommandOutputParser;
import console.Console;
import console.Output;
import console.ParsingOutputStreamTask;

import errorlist.DefaultErrorSource;
// }}}

/**
 * Thread for handing output of running remote ssh commands
 *
 * @version $Id$
 */
class StreamThread extends ParsingOutputStreamTask 
{
	private String status;
	/**
	 * @param in - a stream to read things from, that we want to display.
	 */
	public StreamThread(Console console, InputStream in, Output output, Color defaultColor)
	{
		super(in, output, defaultColor, console.getConsolePane().getBackground(), null, null);
		
		setWaitingLoop(WLTypes.blockWL);
		
		ConsoleState cs = ConnectionManager.getConsoleState(console);
		String currentDirectory = cs.getPath();
		DefaultErrorSource es = new SecureErrorSource(cs, console.getView());
		CommandOutputParser copt = new CommandOutputParser(console.getView(), es, defaultColor);
		copt.setDirectory(currentDirectory);
		cs.setDirectoryChangeListener(copt);
		
		setErrorParser(copt);
		setAnsiParser(defaultColor, console.getConsolePane().getBackground());
		status = "sshconsole";
	}
	
	public void setStatus(String newStatus) {
		status = newStatus;
	}
	
	public String getStatus() {
		return status;
	}
	
	public String toString() {
		return status;
	}
}

// :folding=explicit: