package procshell;
/**
 * @author Damien Radtke
 * class ProcessShell
 * The base class for shells that use an external process to run
 */
//{{{ Imports
import console.Console;
import console.ConsolePane;
import console.ConsolePlugin;
import console.Output;
import console.Shell;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import javax.swing.text.AttributeSet;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.util.Log;
//}}}
public abstract class ProcessShell extends Shell {
	protected Hashtable<Console, ConsoleState> consoleStateMap;
	public ProcessShell(String name) {
		super(name);
		consoleStateMap = new Hashtable<Console, ConsoleState>();
	}
	
	protected abstract void init(ConsoleState state) throws IOException;
	protected abstract void onRead(Output output,
		ConsoleState state, String str);
	
	//{{{ openConsole()
	public void openConsole(Console console) {
		consoleStateMap.put(console, new ConsoleState());
	} //}}}
	
	//{{{ closeConsole()
	public void closeConsole(Console console) {
		consoleStateMap.remove(console);
	} //}}}
	
	//{{{ initStreams()
	/**
	 * Initialize streams to read the process' output and error streams
	 */
	public void initStreams(Console console, ConsoleState state) {
		new ProcessReader(console, state, false).start();
		new ProcessReader(console, state, true).start();
	} //}}}
	
	//{{{ printPrompt()
	/**
	 * The first time printPrompt() gets called is the first time we're sure
	 * everything's been done and initialized, so if the process isn't running,
	 * attempt to start it up here
	 */
	public void printPrompt(Console console, Output output) {
		ConsoleState state = consoleStateMap.get(console);
		if (state.isNew) {
			start(console, output);
			state.isNew = false;
		}
	} //}}}

	//{{{ execute()
	/**
	 * Sends input to the running process
	 */
	public void execute(Console console, String input, final Output output,
	Output error, String command) {
		ConsoleState state = consoleStateMap.get(console);
		if (error == null)
			error = output;
		state.output = output;
		state.error = error;
		if (state.running) {
			send(console, command, true);
		} else {
			start(console, output);
		}
	} //}}}
	
	//{{{ start()
	protected void start(Console console, Output output) {
		ConsoleState state = consoleStateMap.get(console);
		state.output = output;
		state.error = output;
		try {
			output.writeAttrs(ConsolePane.colorAttributes(
				console.getInfoColor()),
				jEdit.getProperty("msg.procshell.starting"));
			init(state);
			initStreams(console, state);
			state.running = true;
		}
		catch (IOException e) {
			e.printStackTrace();
			console.getOutput().writeAttrs(ConsolePane.colorAttributes(
				console.getErrorColor()),
				jEdit.getProperty("msg.procshell.error-starting"));
		}
	} //}}}
	
	//{{{ send()
	/**
	 * Utility version of send()
	 */
	public void send(Console console, String str) {
		if (consoleStateMap.get(console) == null) {
			console.setShell(this);
			waitFor(console);
		}
		send(console, str, false);
	}
	
	/**
	 * Sends input to the running process
	 */
	protected void send(Console console, String str, boolean fromConsole) {
		final ConsoleState state = consoleStateMap.get(console);
		if (!state.running) {
			console.getOutput().print(console.getErrorColor(),
				jEdit.getProperty("msg.procshell.no-process"));
			return;
		}
		final String cmd = str+"\n";
		if (!fromConsole) {
			if (str.indexOf("\n") != -1) {
				console.getOutput().print(console.getInfoColor(), "...");
			} else {
				console.getOutput().print(console.getInfoColor(), str);
			}
			//output.print(console.getInfoColor(), str);
		}
		new Thread() {
			public void run() {
				try {
					state.write(cmd);
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	} //}}}
	
	//{{{ stop()
	/**
	 * Halts the REPL
	 */
	public void stop(Console console) {
		console.commandDone();
		ConsoleState state = consoleStateMap.get(console);
		if (state.running) {
			state.p.destroy();
			console.getOutput(getName()).print(console.getInfoColor(),
				"\n"+jEdit.getProperty("msg.procshell.stopped"));
			state.running = false;
			state.waiting = false;
		}
	}
	
	/**
	 * Halts all running REPL's
	 */
	public void stop() {
		for (Console console : consoleStateMap.keySet()) {
			stop(console);
		}
	} //}}}

	//{{{ waitFor()
	/**
	 * This method only returns if state.waiting is set to false
	 * For repl's, this is true after a prompt has been printed
	 */
	public boolean waitFor(Console console) {
		ConsoleState state = consoleStateMap.get(console);
		if (state != null && !state.running) return true;
		while (state == null || state.waiting) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	} //}}}
	
	//{{{ endOfFile()
	public void endOfFile(Console console) {
		send(console, "^D");
	} //}}}
	
	//{{{ ProcessReader class 
	class ProcessReader extends Thread {
		Console console;
		ConsoleState state;
		Output output;
		InputStream in;
		AttributeSet col;
		boolean error;
		public ProcessReader(Console console, ConsoleState state, 
				boolean error) {
			this.console = console;
			this.state = state;
			this.col = ConsolePane.colorAttributes((error) ?
				console.getErrorColor() : console.getPlainColor());
			this.error = error;
			in = (error) ? state.getErrorStream() : state.getInputStream();
		}
		public void run() {
			try {
				byte[] buf = new byte[4096];
				int read = -1;
				while ((read = in.read(buf)) != -1) {
					String str = new String(buf, 0, read);
					Output output = (error ? state.error : state.output);
					output.writeAttrs(col, str);
					onRead(output, state, str);
				}
			} catch (Exception e) {
				e.printStackTrace();
				output.print(null, jEdit.getProperty(
					"msg.procshell.stopped"));
			}
		}
	} //}}}
	
	//{{{ ConsoleState class
	public static class ConsoleState {
		public Process p;
		public boolean isNew;
		public boolean running;
		public boolean waiting;
		protected Output output;
		protected Output error;
		
		public ConsoleState() {
			p = null;
			isNew = true;
			running = false;
			waiting = false;
		}
		
		public InputStream getInputStream() {
			return p.getInputStream();
		}
		
		public InputStream getErrorStream() {
			return p.getErrorStream();
		}
		
		public void write(String cmd) throws IOException {
			OutputStream out = p.getOutputStream();
			out.write(cmd.getBytes());
			out.flush();
		}
	} //}}}
}
