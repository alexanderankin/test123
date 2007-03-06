package gdb;

import gdb.Parser.GdbResult;
import gdb.Parser.ResultHandler;
import gdb.views.LocalVariables;
import gdb.views.StackTrace;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

import debugger.core.DebuggerDB;
import debugger.itf.DebuggerTool;
import debugger.itf.IBreakpoint;
import debugger.itf.IData;
import debugger.itf.JEditFrontEnd;
import debugger.jedit.Breakpoint;

public class Debugger implements DebuggerTool {

	private static Debugger debugger = null;
	
	private JEditFrontEnd frontEnd = null;
	private Parser parser;

	private boolean running = false;

	// Program output
	private static JPanel programOutputPanel = null;
	private static JTextArea programOutputText = null;
	// Gdb output
	private static JPanel gdbOutputPanel = null;
	private static JTextArea gdbOutputText = null;
	// Views
	private LocalVariables localsPanel = null;
	private StackTrace stackTracePanel = null;
	// Command manager
	private CommandManager commandManager = null;

	private class BreakpointResultHandler implements ResultHandler {
		private GdbBreakpoint bp;
		public BreakpointResultHandler(GdbBreakpoint bp) {
			this.bp = bp;
		}
		public void handle(String msg, GdbResult res) {
			if (! msg.equals("done"))
				return;
			String num = res.getStringValue("bkpt/number");
			if (num != null)
				bp.setNumber(Integer.parseInt(num));
		}
	}
	private class GdbBreakpoint implements IBreakpoint	{
		String file;
		int line;
		int number;
		GdbBreakpoint(String file, int line) {
			this.file = file;
			this.line = line;
		}
		public void setNumber(int num) {
			System.err.println("bp at " + file + ":" + line + " -> " + num);
			number = num;
		}
		public String getFile() {
			return file;
		}

		public int getLine() {
			return line;
		}
		public boolean canSetEnabled() {
			return true;
		}
		public void setEnabled(boolean enabled) {
			if (commandManager != null)
				if (enabled)
					commandManager.add("-break-enable " + number);
				else
					commandManager.add("-break-disable " + number);
		}
		public void remove() {
			if (commandManager != null)
				commandManager.add("-break-delete " + number);
		}
	}

	public IBreakpoint addBreakpoint(String file, int line) {
		GdbBreakpoint bp = new GdbBreakpoint(file, line);
		if (commandManager != null) {
			commandManager.add("-break-insert " + file + ":" + line,
					new BreakpointResultHandler(bp));
		}
		return bp; 
	}

	public IData getData(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public void go() {
		commandManager.add("-exec-continue");
	}

	public void pause() {
		// TODO Auto-generated method stub

	}

	public void quit() {
		// TODO Auto-generated method stub

	}

	private void sessionEnded() {
		running = false;
		if (stackTracePanel != null)
			stackTracePanel.sessionEnded();
		if (localsPanel != null)
			localsPanel.sessionEnded();
	}
	public void start(String prog, String args, String cwd, Hashtable<String, String> env) {
		String command = "gdb --interpreter=mi " + prog;
		//File dir = new File(getBufferDirectory());
		if (cwd == null || cwd.length() == 0)
			cwd = ".";
		File dir = new File(cwd);
		Process p;
		try {
			p = Runtime.getRuntime().exec(command, null, dir);
			running = true;
	        parser = new Parser(this, p);
	        parser.addOutOfBandHandler(new OutOfBandHandler());
			parser.start();
			commandManager = new CommandManager(this, p, parser);
			commandManager.start();
			// First set up the arguments
			commandManager.add("-exec-arguments " + args);
			// Now set up the breakpoints
			Vector<Breakpoint> bps = DebuggerDB.getInstance().getBreakpoints();
			for (int i = 0; i < bps.size(); i++) {
				Breakpoint b = bps.get(i);
				GdbBreakpoint gbp = (GdbBreakpoint)b.getBreakpoint();
				commandManager.add(
						"-break-insert " + gbp.getFile() + ":" + gbp.getLine(),
						new BreakpointResultHandler(gbp));
				if (! b.isEnabled())
					gbp.setEnabled(false);
			}
			commandManager.add("-exec-run");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void stopped(String file, int line) {
		updateStackTrace();
		updateLocals(0);
		frontEnd.setCurrentLocation(file, line);
		
	}

	private void updateStackTrace() {
		if (stackTracePanel != null) {
			stackTracePanel.setCommandManager(commandManager);
			stackTracePanel.setParser(parser);
			stackTracePanel.update();
		}
	}
	public void breakpointHit(int bkptno, String file, int line) {
		frontEnd.breakpointHit(bkptno, file, line);
	}

	public void setFrontEnd(JEditFrontEnd frontEnd) {
		this.frontEnd = frontEnd;
	}

	private class BreakpointHitHandler implements ResultHandler {
		int bkptno;
		BreakpointHitHandler(int bkptno) {
			this.bkptno = bkptno;
		}
		public void handle(String msg, GdbResult res) {
			String file = res.getStringValue("fullname");
			int line = 0;
			if (file != null) {
				line = Integer.parseInt(res.getStringValue("line"));
			}
			breakpointHit(bkptno, file, line);
			stopped(file, line);
		}
	}
	private class StoppedHandler implements ResultHandler {
		public void handle(String msg, GdbResult res) {
			String file = res.getStringValue("fullname");
			int line = 0;
			if (file != null) {
				line = Integer.parseInt(res.getStringValue("line"));
			}
			stopped(file, line);
		}
	}
	private class OutOfBandHandler implements ResultHandler {
		public void handle(String msg, GdbResult res) {
			final String getCurrentPosition = new String("-file-list-exec-source-file");
			String reason = res.getStringValue("reason");
			String file = null;
			int line = (file != null) ?
				Integer.parseInt(res.getStringValue("frame/line")) : 0;
			if (reason.equals("breakpoint-hit")) {
				int bkptno = Integer.parseInt(res.getStringValue("bkptno"));
				if (file != null) {
					breakpointHit(bkptno, file, line);
					stopped(file, line);
				} else {
					commandManager.add(getCurrentPosition, new BreakpointHitHandler(bkptno));
				}
			} else if (reason.startsWith("exited")) {
				System.err.println("Exited");
				sessionEnded();
			} else {
				if (file != null) {
					stopped(file, line);
				} else {
					commandManager.add(getCurrentPosition, new StoppedHandler());
				}
			}
		}
	}

	public JEditFrontEnd getFrontEnd() {
		return frontEnd;
	}
	public void updateLocals(int frame) {
		if (localsPanel != null) {
			localsPanel.setCommandManager(commandManager);
			localsPanel.update(frame);
		}
	}
	public void frameSelected(int level) {
		updateLocals(level);
	}

	static public JPanel showProgramOutput(View view) {
		if (programOutputPanel == null)	{
			programOutputPanel = new JPanel(new BorderLayout());
			programOutputText = new JTextArea();
			programOutputPanel.add(new JScrollPane(programOutputText));
		}
		return programOutputPanel;
	}
	static public JPanel showGdbOutput(View view) {
		if (gdbOutputPanel == null)	{
			gdbOutputPanel = new JPanel(new BorderLayout());
			gdbOutputText = new JTextArea();
			gdbOutputPanel.add(new JScrollPane(gdbOutputText));
		}
		return gdbOutputPanel;
	}
	public JPanel showLocals(View view) {
		if (localsPanel == null)
			localsPanel = new LocalVariables();
		return localsPanel;
	}
	public JPanel showStackTrace(View view) {
		if (stackTracePanel == null)
			stackTracePanel = new StackTrace();
		return stackTracePanel;
	}
	public void gdbRecord(String line)
	{
		if (gdbOutputText == null)
			showGdbOutput(jEdit.getActiveView());
		gdbOutputText.append(line);
	}
	public void programRecord(String line)
	{
		if (programOutputText == null)
			showProgramOutput(jEdit.getActiveView());
		programOutputText.append(line);
	}
	public static Debugger getInstance() {
		if (debugger  == null)
			debugger = new Debugger();
		return debugger;
	}

	public boolean isRunning() {
		return running;
	}

	public void next() {
		commandManager.add("-exec-next");
	}

	public void step() {
		commandManager.add("-exec-step");
	}
}
