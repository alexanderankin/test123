/**
 * 
 */
package gdb.breakpoints;

import gdb.core.Parser.GdbResult;
import gdb.core.Parser.ResultHandler;

public class BreakpointResultHandler implements ResultHandler {
	private Breakpoint bp;
	public BreakpointResultHandler(Breakpoint bp) {
		this.bp = bp;
	}
	public void handle(String msg, GdbResult res) {
		if (! msg.equals("done"))
			return;
		String type = (bp.isBreakpoint() ? "bkpt" : "wpt");
		String num = res.getStringValue(type + "/number");
		if (num != null)
			bp.setNumber(Integer.parseInt(num));
	}
}