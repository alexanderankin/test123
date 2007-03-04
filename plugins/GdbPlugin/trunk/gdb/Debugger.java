package gdb;

import gdb.Parser.GdbResult;
import gdb.Parser.ResultHandler;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
	// Local variables
	private static JPanel localsPanel = null;
	private static JTree localsTree = null;
	// Stack trace
	private static JPanel stackTracePanel = null;
	private static JTree stackTraceTree = null;
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
		stackTraceTree.setModel(emptyTreeModel);
		localsTree.setModel(emptyTreeModel);
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
		if (localsPanel == null)
			showLocals(jEdit.getActiveView());
		getLocals();
		if (stackTracePanel == null)
			showStackTrace(jEdit.getActiveView());
		getStackTrace();
		frontEnd.setCurrentLocation(file, line);
		
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
			String file = null;//res.getStringValue("frame/fullname");
			int line = (file != null) ?
				Integer.parseInt(res.getStringValue("frame/line")) : 0;
			if (reason.equals("breakpoint-hit")) {
				//System.err.println("Parser recognized bkpt hit message");
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

	private class LocalsResultHandler implements ResultHandler {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Locals");
		public void handle(String msg, GdbResult res) {
			//System.err.println("LocalsResultHandler called with " + msg);
			if (msg.equals("done")) {
				Object locals = res.getValue("locals");
				if (locals == null)
					return;
				if (locals instanceof Vector) {
					Vector<Object> localsVec = (Vector<Object>)locals;
					for (int i = 0; i < localsVec.size(); i++) {
						Object local = localsVec.get(i);
						if (local instanceof Hashtable) {
							Hashtable<String, Object> localHash =
								(Hashtable<String, Object>)local;
							String name = localHash.get("name").toString();
							String value = "<missing>";
							Object valueObj = localHash.get("value");
							if (valueObj != null)
								value = valueObj.toString();
							root.add(new DefaultMutableTreeNode(name + "=" + value));
						}
					}
				}
			}
			if (localsTree != null)
				localsTree.setModel(new DefaultTreeModel(root));
		}
	}
	public JEditFrontEnd getFrontEnd() {
		return frontEnd;
	}
	public void getLocals() {
		//System.err.println("getLocals()");
		LocalsResultHandler handler = new LocalsResultHandler();
		parser.addResultHandler(handler);
		commandManager.add("-stack-list-locals 2");
	}
	private class StackTraceNode {
		String file;
		int line = 0;
		int level = 0;
		String func;
		String from;
		Vector<String> args = null;
		StackTraceNode(String level, String func, String file, String line, String from)
		{
			if (level != null)
				this.level = Integer.parseInt(level);
			this.func = func;
			this.file = file;
			if (line != null)
				this.line = Integer.parseInt(line);
			this.from = from;
		}
		public void setArguments(Vector<String> arguments) {
			args = arguments;
		}
		public String toString() {
			String location;
			if (file != null)
				location = "at " + file + ":" + line;
			else
				location = "from " + from;
			StringBuffer arguments = new StringBuffer();
			if (args != null) {
				arguments.append("(");
				for (int i = 0; i < args.size(); i++) {
					if (i > 0)
						arguments.append(", ");
					arguments.append(args.get(i));
				}
				arguments.append(")");
			}
			return level + " " + func + arguments + " " + location;
		}
		public void selected() {
			Debugger.getInstance().getFrontEnd().goTo(file, line);
		}
	}
	private class StackArgumentsResultHandler implements ResultHandler {
		public void handle(String msg, GdbResult res) {
			//System.err.println("StackTraceResultHandler called with " + msg);
			if (stackTraceTree == null)
				return;
			DefaultMutableTreeNode root =
				(DefaultMutableTreeNode) stackTraceTree.getModel().getRoot();
			if (msg.equals("done")) {
				Object stack = res.getValue("stack-args");
				if (stack == null)
					return;
				if (stack instanceof Vector) {
					Vector<Object> frames = (Vector<Object>)stack;
					for (int i = 0; i < frames.size(); i++) {
						Object frame = frames.get(i);
						if (frame instanceof Hashtable) {
							Hashtable<String, Object> frameHash =
								(Hashtable<String, Object>)
								((Hashtable<String, Object>)frame).get("frame");
							Object frameArgs = frameHash.get("args");
							Vector<String> names = new Vector<String>();
							if (frameArgs instanceof Vector) {
								Vector<Object> frameArgsVec =
									(Vector<Object>)frameArgs;
								for (int j = 0; j < frameArgsVec.size(); j++) {
									Hashtable<String, Object> argsHash =
										(Hashtable<String, Object>)frameArgsVec.get(j);
									String name = argsHash.get("name").toString();
									names.add(name);
								}
								DefaultMutableTreeNode node =
									(DefaultMutableTreeNode) root.getChildAt(i);
								StackTraceNode frameNode = (StackTraceNode) node.getUserObject();
								frameNode.setArguments(names);
							}
						}
					}
				}
			}
			stackTraceTree.setModel(new DefaultTreeModel(root));
		}
	}
	public void getStackArguments() {
		StackArgumentsResultHandler handler = new StackArgumentsResultHandler();
		parser.addResultHandler(handler);
		commandManager.add("-stack-list-arguments 0");
	}
	private class StackTraceResultHandler implements ResultHandler {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Stack trace:");
		public void handle(String msg, GdbResult res) {
			//System.err.println("StackTraceResultHandler called with " + msg);
			if (msg.equals("done")) {
				Object stack = res.getValue("stack");
				if (stack == null)
					return;
				if (stack instanceof Vector) {
					Vector<Object> frames = (Vector<Object>)stack;
					for (int i = 0; i < frames.size(); i++) {
						Object frame = frames.get(i);
						if (frame instanceof Hashtable) {
							Hashtable<String, Object> frameHash =
								(Hashtable<String, Object>)
								((Hashtable<String, Object>)frame).get("frame");
							String level = frameHash.get("level").toString();
							String func = frameHash.get("func").toString();
							String file = frameHash.containsKey("file") ?
								frameHash.get("file").toString() : null;
							String line = frameHash.containsKey("line") ?
								frameHash.get("line").toString() : null;
							String from = frameHash.containsKey("from") ?
								frameHash.get("from").toString() : null;
							StackTraceNode frameNode = new StackTraceNode(
									level,
									func,
									file,
									line,
									from);
							root.add(new DefaultMutableTreeNode(frameNode));
						}
					}
				}
			}
			if (stackTraceTree != null)
				stackTraceTree.setModel(new DefaultTreeModel(root));
			getStackArguments();
		}
	}
	public void getStackTrace() {
		StackTraceResultHandler handler = new StackTraceResultHandler();
		parser.addResultHandler(handler);
		commandManager.add("-stack-list-frames");
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
	static public JPanel showLocals(View view) {
		if (localsPanel == null) {
			localsPanel = new JPanel(new BorderLayout());
			localsTree = new JTree();
			localsTree.setModel(emptyTreeModel);
			localsTree.setRootVisible(false);
			localsPanel.add(new JScrollPane(localsTree));
		}
		return localsPanel;
	}
	static public JPanel showStackTrace(View view) {
		if (stackTracePanel == null) {
			stackTracePanel = new JPanel(new BorderLayout());
			stackTraceTree = new JTree();
			stackTraceTree.setModel(emptyTreeModel);
			stackTraceTree.setRootVisible(false);
			stackTraceTree.addMouseListener(new StackTraceListener(stackTraceTree));
			stackTracePanel.add(new JScrollPane(stackTraceTree));
		}
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
	static class StackTraceListener extends MouseAdapter {
		JTree tree;
		Debugger debugger;
		StackTraceListener(JTree t) {
			tree = t;
		}
		public void mouseClicked(MouseEvent e) {
			TreePath path = tree.getSelectionPath();
			Object obj = ((DefaultMutableTreeNode) path
					.getLastPathComponent()).getUserObject();
			if (obj instanceof StackTraceNode) {
				StackTraceNode node = (StackTraceNode)obj;
				node.selected();
			}
		}
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
