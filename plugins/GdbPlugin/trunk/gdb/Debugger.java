package gdb;

import gdb.Parser.GdbResult;
import gdb.Parser.ResultHandler;
import gdb.views.LocalVariables;
import gdb.views.StackTrace;
import gdb.views.StackTrace.StackTraceNode;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

import debugger.core.DebuggerDB;
import debugger.itf.DebuggerTool;
import debugger.itf.IBreakpoint;
import debugger.itf.IData;
import debugger.itf.JEditFrontEnd;

public class Debugger implements DebuggerTool {

	private static Debugger debugger = null;
	
	private JEditFrontEnd frontEnd = null;
	private Parser parser;

	private boolean running = false;

	static private TreeModel emptyTreeModel = new DefaultTreeModel(null);

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

	private class Data implements IData {
		String name, value;
		Vector<IData> children = new Vector<IData>(); 
		Data(String name, String value) {
			this.name = name;
			this.value = value;
		}
		public void addChild(IData data) {
			children.add(data);
		}
		public Vector<IData> getChildren() {
			return children;
		}
		public String getName() {
			return name;
		}
		public String getValue() {
			return value;
		}
	}
	private class Breakpoint implements IBreakpoint	{
		String file;
		int line;
		Breakpoint(String file, int line) {
			this.file = file;
			this.line = line;
		}
		public String getFile() {
			return file;
		}

		public int getLine() {
			return line;
		}
	}

	public IBreakpoint addBreakpoint(String file, int line) {
		if (commandManager != null) {
			commandManager.add("-break-insert " + file + ":" + line);
		}
		return new Breakpoint(file, line);
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

	public void removeBreakpoint(IBreakpoint brkpt) {
		// TODO Auto-generated method stub

	}

	private void sessionEnded() {
		running = false;
		stackTracePanel.sessionEnded();
		localsPanel.sessionEnded();
	}
	public void start(String prog, String args, String cwd, Hashtable env) {
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
			Vector<IBreakpoint> bps = DebuggerDB.getInstance().getBreakpoints();
			for (int i = 0; i < bps.size(); i++) {
				IBreakpoint b = bps.get(i);
				commandManager.add("-break-insert " + b.getFile() + ":" +
						b.getLine());
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
