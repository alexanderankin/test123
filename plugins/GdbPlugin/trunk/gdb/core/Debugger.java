package gdb.core;

import gdb.breakpoints.Breakpoint;
import gdb.breakpoints.BreakpointList;
import gdb.breakpoints.BreakpointView;
import gdb.context.StackTrace;
import gdb.core.GdbState.State;
import gdb.core.Parser.GdbResult;
import gdb.core.Parser.ResultHandler;
import gdb.execution.ControlView;
import gdb.launch.LaunchConfiguration;
import gdb.launch.LaunchConfigurationManager;
import gdb.options.GeneralOptionPane;
import gdb.output.Console;
import gdb.output.Console.InputHandler;
import gdb.variables.LocalVariables;
import gdb.variables.Variables;
import gdb.variables.Watches;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.JEditTextArea;

import debugger.itf.DebuggerTool;
import debugger.itf.IData;
import debugger.itf.JEditFrontEnd;

public class Debugger implements DebuggerTool {

	private static Debugger debugger = null;

	private JEditFrontEnd frontEnd = null;

	// Views
	private Console programOutput = null;
	private Console gdbOutput = null;
	private ControlView controlView = null;
	private BreakpointView breakpointsPanel = null;
	private LocalVariables localsPanel = null;
	private StackTrace stackTracePanel = null;
	private Watches watchesPanel = null;
	private Variables variablesPanel = null;
	// Command manager
	private CommandManager commandManager = null;
	// Parser
	private Parser parser = null;

	public IData getData(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public void go() {
		if (! isRunning())
			start();
		else 
			commandManager.add("-exec-continue");
	}

	public void pause() {
		if (isRunning())
			commandManager.add("-exec-interrupt");
	}

	public void quit() {
		if (! isRunning())
			return;
		commandManager.add("-gdb-exit", new ResultHandler() {
			public void handle(String msg, GdbResult res) {
				if (msg.equals("exit")) {
					sessionEnded();
				}
			}
		});
	}

	public void next() {
		if (isRunning())
			commandManager.add("-exec-next");
	}

	public void step() {
		if (isRunning())
			commandManager.add("-exec-step");
	}

	public void finishCurrentFunction() {
		if (isRunning())
			commandManager.add("-exec-finish");
	}

	public void runToCursor() {
		View view = jEdit.getActiveView();
		Buffer buffer = view.getBuffer();
		JEditTextArea ta = view.getTextArea();
		int line = ta.getCaretLine() + 1;
		if (isRunning())
			commandManager.add("-exec-until " + buffer.getPath() + ":" + line);
	}
	
	private void sessionEnded() {
		parser = null;
		commandManager = null;
		GdbState.setState(State.IDLE);
		frontEnd.programExited();
	}
	public void start() {
		LaunchConfiguration currentConfig =
			LaunchConfigurationManager.getInstance().getDefault();
		if (programOutput != null)
			programOutput.clear();
		if (gdbOutput != null)
			gdbOutput.clear();
		debugger.start(currentConfig.getProgram(),
				currentConfig.getArguments(),
				currentConfig.getDirectory(),
				currentConfig.getEnvironment().split(","));
	}
	public void start(String prog, String args, String cwd, String [] env) {
		String command = jEdit.getProperty(GeneralOptionPane.GDB_PATH_PROP) +
			" --interpreter=mi " + prog;
		if (cwd == null || cwd.length() == 0)
			cwd = ".";
		File dir = new File(cwd);
		Process p;
		try {
			p = Runtime.getRuntime().exec(command, env, dir);
			GdbState.setState(State.RUNNING);
	        parser = new Parser(this, p);
			parser.addOutOfBandHandler(new OutOfBandHandler());
			parser.start();
			commandManager = new CommandManager(p, parser);
			commandManager.start();
			// First set up the arguments
			commandManager.add("-exec-arguments " + args);
			// Now set up the breakpoints
			Vector<Breakpoint> bps = BreakpointList.getInstance().getBreakpoints();
			for (int i = 0; i < bps.size(); i++) {
				Breakpoint b = bps.get(i);
				b.initialize();
				if (! b.isEnabled())
					b.setEnabled(false);
			}
			commandManager.add("-exec-run");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void toggleBreakpoint(View view)
	{
		Buffer buffer = view.getBuffer();
		JEditTextArea ta = view.getTextArea();
		int line = ta.getCaretLine() + 1;
		Vector<Breakpoint> breakpoints =
			BreakpointList.getInstance().get(buffer.getPath(), line);
		if (breakpoints.isEmpty())
			setBreakpoint(view, buffer, line);
		else
			removeBreakpoint(view);
	}
	public void setBreakpoint(View view) {
		Buffer buffer = view.getBuffer();
		JEditTextArea ta = view.getTextArea();
		int line = ta.getCaretLine() + 1;
		setBreakpoint(view, buffer, line);
	}
	private void setBreakpoint(View view, Buffer buffer, int line) {
		new Breakpoint(view, buffer, line);
	}
	public void removeBreakpoint(View view) {
		Buffer buffer = view.getBuffer();
		JEditTextArea ta = view.getTextArea();
		int line = ta.getCaretLine() + 1;
		Vector<Breakpoint> breakpoints =
			BreakpointList.getInstance().get(buffer.getPath(), line);
		if (breakpoints.isEmpty())
			return;
		for (int i = 0; i < breakpoints.size(); i++) {
			Breakpoint b = breakpoints.get(i);
			b.remove();
		}
	}

	private void stopped(String file, int line) {
		GdbState.setState(State.PAUSED);
		frontEnd.setCurrentLocation(file, line);
		
	}

	public void breakpointHit(int bkptno, String file, int line) {
		String msg = "Breakpoint " + bkptno + " hit";
		if (file != null)
			msg = msg + ", at " + file + ":" + line + ".";
		//System.err.println(msg);
		JOptionPane.showMessageDialog(null, msg);
	}

	public void signalReceived(String signal) {
		String msg = "Received signal: " + signal;
		JOptionPane.showMessageDialog(null, msg);
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
			final String getCurrentPosition = "-file-list-exec-source-file";
			String reason = res.getStringValue("reason");
			if (reason.equals("breakpoint-hit")) {
				int bkptno = Integer.parseInt(res.getStringValue("bkptno"));
				commandManager.add(getCurrentPosition, new BreakpointHitHandler(bkptno));
			} else if (reason.startsWith("exited")) {
				sessionEnded();
			} else if (reason.startsWith("signal-received")) {
				signalReceived(res.getStringValue("signal-meaning"));
				commandManager.add(getCurrentPosition, new StoppedHandler());
			} else {
				commandManager.add(getCurrentPosition, new StoppedHandler());
			}
		}
	}

	public JEditFrontEnd getFrontEnd() {
		return frontEnd;
	}

	public JPanel showControlPanel(View view) {
		if (controlView == null)
			controlView = new ControlView();
		return controlView;
	}
	public JPanel showProgramOutput(View view) {
		if (programOutput == null)
			programOutput = new Console();
		return programOutput;
	}
	public JPanel showGdbOutput(View view) {
		if (gdbOutput == null)	{
			gdbOutput = new Console(new InputHandler() {
				public void handle(String line) {
					commandManager.add(line);
				}
			});
		}
		return gdbOutput;
	}
	public JPanel showBreakpoints(View view) {
		if (breakpointsPanel == null)
			breakpointsPanel = new BreakpointView();
		return breakpointsPanel;
	}
	public JPanel showLocals(View view) {
		if (localsPanel == null)
			localsPanel = new LocalVariables();
		return localsPanel;
	}
	public JPanel showWatches(View view) {
		if (watchesPanel == null)
			watchesPanel = new Watches();
		return watchesPanel;
	}
	public JPanel showVariables(View view) {
		if (variablesPanel == null)
			variablesPanel = new Variables();
		return variablesPanel;
	}
	public JPanel showStackTrace(View view) {
		if (stackTracePanel == null)
			stackTracePanel = new StackTrace();
		return stackTracePanel;
	}
	public void gdbRecord(String line)
	{
		if (gdbOutput == null)
			showGdbOutput(jEdit.getActiveView());
		gdbOutput.append(line);
	}
	public void programError(String line) {
		if (programOutput == null)
			showProgramOutput(jEdit.getActiveView());
		programOutput.append(line);
	}
	public void programRecord(String line)
	{
		if (programOutput == null)
			showProgramOutput(jEdit.getActiveView());
		programOutput.append(line);
	}
	public static Debugger getInstance() {
		if (debugger == null)
			debugger = new Debugger();
		return debugger;
	}

	public boolean isRunning() {
		return GdbState.isRunning();
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}
	public Parser getParser() {
		return parser;
	}
}
