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

package debugger.jedit;

import java.util.Enumeration;
import java.util.Vector;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFSManager;

import perl.breakpoints.Breakpoint;
import perl.breakpoints.BreakpointList;
import perl.core.Debugger;
import perl.launch.LaunchConfigurationManager;
import perl.variables.TypeMacroMap;

import debugger.itf.JEditFrontEnd;

public class Plugin extends EditPlugin implements JEditFrontEnd {
	static public final String NAME = "GdbPlugin";
	static public final String OPTION_PREFIX = "options.debugger.";
	static public final String MESSAGE_PREFIX = "messages.debugger.";
	static private Debugger debugger = Debugger.getInstance();
	
	public void start()	{
		debugger.setFrontEnd(this);
		// Ensure launch configurations are loaded, to set label of Go! menu
		LaunchConfigurationManager.getInstance();
		TypeMacroMap.getInstance();
	}

	public void stop() {
		// Remove all debugger painters
		Vector<Breakpoint> breakpoints = BreakpointList.getInstance().getBreakpoints();
		Enumeration<Breakpoint> bpEnum = breakpoints.elements();
		while (bpEnum.hasMoreElements()) {
			Breakpoint bp = (Breakpoint) bpEnum.nextElement();
			bp.remove();
		}
		removeCurrentPositionPainter();
		debugger.destroy();
	}
	
	static CurrentPositionPainter dp = null;
	static View dpview = null;
	private static void jumpTo(final String file, final int line, final boolean isCurrent) {
		final View view = jEdit.getActiveView();
		if (isCurrent && (dp != null)) {
			dpview.getTextArea().getGutter().removeExtension(dp);
		}
		if (file == null)
			return;
		final int defLine = line - 1;
		VFSManager.runInAWTThread(new Runnable()
		{
			public void run()
			{
				Buffer buffer = jEdit.openFile(view, file);
				if(buffer == null)
				{
					view.getStatus().setMessage("Unable to open: " + file);
					return;
				}

				view.getTextArea().setCaretPosition(
						view.getTextArea().getLineStartOffset(defLine));
				if (isCurrent) {
					dp = new CurrentPositionPainter(view.getEditPane(), buffer, line);
					dpview = view;
					view.getTextArea().getGutter().addExtension(dp);
				}
			}
		});
	}
	public void goTo(String file, int line) {
		goTo(file, line, false);
	}
	public void goTo(String file, int line, boolean isCurrent) {
		jumpTo(file, line, isCurrent);
	}
	public void setCurrentLocation(String file, int line) {
		goTo(file, line, true);
	}

	public void programExited() {
		removeCurrentPositionPainter();
	}

	private void removeCurrentPositionPainter() {
		if (dpview != null && dp != null)
			dpview.getTextArea().getGutter().removeExtension(dp);
	}
}
