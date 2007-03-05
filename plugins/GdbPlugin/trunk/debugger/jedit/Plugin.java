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
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JOptionPane;

import gdb.Debugger;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.textarea.Gutter;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaExtension;

import debugger.core.DebuggerDB;
import debugger.itf.DebuggerTool;
import debugger.itf.IBreakpoint;
import debugger.itf.JEditFrontEnd;
import debugger.jedit.launch.LaunchConfiguration;
import debugger.jedit.launch.LaunchConfigurationManager;

public class Plugin extends EditPlugin implements JEditFrontEnd {
	public static String OPTION_PREFIX = "options.debugger.";
	static private DebuggerTool debugger = Debugger.getInstance();
	static private Hashtable env = null;
	static private LaunchConfiguration currentConfig;
	static private Hashtable<DebuggerPainter, View> painters =
		new Hashtable<DebuggerPainter, View>();
	
	public void start()	{
		debugger.setFrontEnd(this);
	}

	public void stop() {
		// Remove all debugger painters
		Enumeration<DebuggerPainter> paintersEnum = painters.keys();
		while (paintersEnum.hasMoreElements()) {
			DebuggerPainter dp = paintersEnum.nextElement();
			View v = painters.get(dp);
			v.getTextArea().getGutter().removeExtension(dp);
		}
		if (dpview != null && dp != null)
			dpview.getTextArea().getGutter().removeExtension(dp);
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
					currentConfig.getEnvironmentHash());
		} else 
			debugger.go();
	}
	
	static private class Breakpoint implements IBreakpoint {
		IBreakpoint b;
		BreakpointPainter p;
		View v;
		Buffer buf;
		Breakpoint(IBreakpoint brkpt, Buffer buf) {
			b = brkpt;
			this.buf = buf;
		}
		public String getFile() {
			return b.getFile();
		}
		public int getLine() {
			// TODO Auto-generated method stub
			return b.getLine();
		}
		public Buffer getBuffer() {
			return buf;
		}
		public void addPainter(View view) {
			v = view;
			p = new BreakpointPainter(view.getEditPane(), buf, b.getLine());
			view.getTextArea().getGutter().addExtension(p);
			painters.put(p, v);
		}
		public void removePainter() {
			v.getTextArea().getGutter().removeExtension(p);
			painters.remove(p);
		}
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
		IBreakpoint b = debugger.addBreakpoint(buffer.getPath(), line);
		if (b != null) {
			Breakpoint bp = new Breakpoint(b, buffer);
			DebuggerDB.getInstance().addBreakpoint(bp);
			bp.addPainter(view);
		}
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
			debugger.removeBreakpoint(bp);
			DebuggerDB.getInstance().removeBreakpoint(bp);
			bp.removePainter();
		}
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
}
