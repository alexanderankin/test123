/*
Copyright (C) 2007  Shlomy Reinstein

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package perl.breakpoints;

import perl.core.Debugger;
import perl.core.Parser.GdbResult;
import perl.core.Parser.ResultHandler;
import perl.options.GeneralOptionPane;

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
		if (jEdit.getBooleanProperty(GeneralOptionPane.SHOW_BREAKPOINT_ERROR_PROP))
			JOptionPane.showMessageDialog(jEdit.getActiveView(), errMsg);
		jEdit.getActiveView().getStatus().setMessage(errMsg);
	}
}