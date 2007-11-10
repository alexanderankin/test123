package console.ssh;

import java.io.OutputStream;

/** 
 * This is the state information for each instance of the Console shell.
 * @author ezust
 *
 */
public class ConsoleState
{
	String path = "";
	OutputStream os = null;
	Connection conn = null;
}
