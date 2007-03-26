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

import gdb.Debugger;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.textarea.JEditTextArea;

import debugger.core.DebuggerDB;
import debugger.itf.DebuggerTool;
import debugger.itf.IBreakpoint;
import debugger.itf.JEditFrontEnd;
import debugger.jedit.launch.LaunchConfiguration;
import debugger.jedit.launch.LaunchConfigurationManager;
import debugger.jedit.views.BreakpointView;
import debugger.jedit.views.ControlView;

public class Plugin extends EditPlugin implements JEditFrontEnd {
	static public final String OPTION_PREFIX = "options.debugger.";
	static private DebuggerTool debugger = Debugger.getInstance();
	static private LaunchConfiguration currentConfig;
	
	public void start()	{
		debugger.setFrontEnd(this);
	}

	public void stop() {
		// Remove all debugger painters
		Vector<Breakpoint> breakpoints = DebuggerDB.getInstance().getBreakpoints();
		Enumeration<Breakpoint> bpEnum = breakpoints.elements();
		while (bpEnum.hasMoreElements()) {
			Breakpoint bp = (Breakpoint) bpEnum.nextElement();
			bp.remove();
		}
		removeCurrentPositionPainter();
	}
	
	public static void next(View view) {
		if (debugger.isRunning())
			debugger.next();
	}
	public static void step(View view) {
		if (debugger.isRunning())
			debugger.step();
	}
	public static void go(View view) {
		if (! debugger.isRunning()) {
			currentConfig =
				LaunchConfigurationManager.getInstance().getDefault();
			debugger.start(currentConfig.getProgram(),
					currentConfig.getArguments(),
					currentConfig.getDirectory(),
					currentConfig.getEnvironment().split(","));
		} else 
			debugger.go();
	}
	public static void finishCurrentFunction(View view) {
		if (debugger.isRunning())
			debugger.finishCurrentFunction();
	}
	public static void pause(View view) {
		if (debugger.isRunning())
			debugger.pause();
	}
	public static void quit(View view) {
		if (debugger.isRunning())
			debugger.quit();
	}
	public static void toggleBreakpoint(View view)
	{
		Buffer buffer = view.getBuffer();
		JEditTextArea ta = view.getTextArea();
		int line = ta.getCaretLine() + 1;
		Vector<IBreakpoint> breakpoints = DebuggerDB.getInstance().getBreakpoints(buffer.getPath(), line);
		if (breakpoints.isEmpty())
			setBreakpoint(view);
		else
			removeBreakpoint(view);
	}
	public static void setBreakpoint(View view) {
		Buffer buffer = view.getBuffer();
		JEditTextArea ta = view.getTextArea();
		int line = ta.getCaretLine() + 1;
		new Breakpoint(view, debugger, buffer, line);
	}
	public static void removeBreakpoint(View view) {
		Buffer buffer = view.getBuffer();
		JEditTextArea ta = view.getTextArea();
		int line = ta.getCaretLine() + 1;
		Vector<IBreakpoint> breakpoints = DebuggerDB.getInstance().getBreakpoints(buffer.getPath(), line);
		if (breakpoints.isEmpty())
			return;
		for (int i = 0; i < breakpoints.size(); i++) {
			IBreakpoint b = breakpoints.get(i);
			Breakpoint bp = (Breakpoint)b;
			bp.remove();
		}
	}
	static CurrentPositionPainter dp = null;
	static View dpview = null;
	private static ControlView controlView = null;
	private static BreakpointView brkView = null;
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
		DebuggerDB.getInstance().setCurrentLocation(file, line);
		goTo(file, line, true);
	}
	public void breakpointHit(int bkptno, String file, int line) {
		String msg = "Breakpoint " + bkptno + " hit";
		if (file != null)
			msg = msg + ", at " + file + ":" + line + ".";
		System.err.println(msg);
		JOptionPane.showMessageDialog(null, msg);
	}
	// Views
	static public JPanel showControlPanel(View view) {
		if (controlView == null)
			controlView = new ControlView();
		return controlView;
	}
	static public JPanel showBreakpoints(View view) {
		if (brkView == null)
			brkView = new BreakpointView();
		return brkView;
	}

	static public void toggleAllViews(View view) {
		view.getDockableWindowManager().toggleDockableWindow("debugger-program-output");
		view.getDockableWindowManager().toggleDockableWindow("debugger-gdb-output");
		view.getDockableWindowManager().toggleDockableWindow("debugger-show-variables");
		view.getDockableWindowManager().toggleDockableWindow("debugger-show-stack-trace");
		view.getDockableWindowManager().toggleDockableWindow("debugger-breakpoints");
	}
	
	public void programExited() {
		removeCurrentPositionPainter();
	}

	private void removeCurrentPositionPainter() {
		if (dpview != null && dp != null)
			dpview.getTextArea().getGutter().removeExtension(dp);
	}
}
