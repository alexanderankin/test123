/**
 * 
 */
package gdb.breakpoints;

import gdb.core.Debugger;
import gdb.core.Parser.GdbResult;
import gdb.core.Parser.ResultHandler;

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.jEdit;

import debugger.jedit.Plugin;

public class BreakpointResultHandler implements ResultHandler {
	private Breakpoint bp;
	public BreakpointResultHandler(Breakpoint bp) {
		this.bp = bp;
	}
	public void handle(String msg, GdbResult res) {
		if (! msg.equals("done"))
			return;
		if (res == null) {
			reportError();
			return;
		}
		String type = (bp.isBreakpoint() ? "bkpt" : "wpt");
		String num = res.getStringValue(type + "/number");
		if (num != null)
			bp.setNumber(Integer.parseInt(num));
	}
	private void reportError() {
		String errMsg = jEdit.getProperty(Plugin.MESSAGE_PREFIX +
				"cannot_create_breakpoint");
		String gdbMsg = Debugger.getInstance().getGdbMessage();
		if (gdbMsg != null)
			errMsg = errMsg + gdbMsg;
		JOptionPane.showMessageDialog(jEdit.getActiveView(), errMsg);
	}
}