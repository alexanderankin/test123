package slime;
/**
 * @author Damien Radtke
 * class REPLShell
 * A Read-Eval-Print-Loop shell for Console
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
import org.gjt.sp.jedit.jEdit;
//}}}
public abstract class SlimeShell extends Shell {
	private Hashtable<Console, ConsoleState> consoleStateMap;
	/*
 	 * Constructor for SlimeShell
 	 */
	public SlimeShell(String name) {
		super(name);
		consoleStateMap = new Hashtable<Console, ConsoleState>();
	}
	
	//{{{ openConsole()
	public void openConsole(Console console) {
		consoleStateMap.put(console, new ConsoleState());
	} //}}}
	
	//{{{ closeConsole()
	public void closeConsole(Console console) {
		consoleStateMap.remove(console);
	} //}}}
	
	//{{{ init()
	/**
	 * This method should be overridden in order to start the REPL
	 */
	public abstract void init(ConsoleState state) throws IOException;
	//}}}
	
	//{{{ initStreams()
	/**
	 * Initialize streams to read the process' output and error streams
	 */
	public void initStreams(Console console, ConsoleState state) {
		new ProcessReader(console, state, false).start();
		new ProcessReader(console, state, true).start();
	} //}}}
	
	//{{{ eval()
	/**
	 * The default implementation of eval() sends the string to the REPL's
	 * standard input.
	 */
	public void eval(Console console, String str) {
		send(console, str);
	} //}}}
	
	//{{{ evalBuffer()
	/**
	 * The default implementation of evalBuffer() simply sends the buffer's
	 * contents to eval()
	 */
	public void evalBuffer(Console console, Buffer buffer) {
		send(console, buffer.getText(0, buffer.getLength()));
	} //}}}
	
	//{{{ getPromptRegex()
	/**
	 * The prompt regex is used to stop the running animation and to tell
	 * waitFor() when to return. Both of these actions happen after a prompt
	 * has been printed, but by default no prompt is specified, so none of
	 * these will happen.
	 */
	public String getPromptRegex() {
		return null;
	} //}}}
	
	//{{{ printInfoMessage()
	public void printInfoMessage(Output output) {
		System.out.println("printing info message");
		output.print(null, jEdit.getProperty("msg.slime.info-message"));
	} //}}}
	
	//{{{ printPrompt()
	/**
	 * The first time printPrompt() gets called is the first time we're sure
	 * everything's been done and initialized, so if the process isn't running,
	 * attempt to start it up here
	 */
	public void printPrompt(Console console, Output output) {
		ConsoleState state = consoleStateMap.get(console);
		if (!state.running) {
			start(console, output);
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
				jEdit.getProperty("msg.slime.starting"));
			init(state);
			initStreams(console, state);
			state.running = true;
		}
		catch (IOException e) {
			e.printStackTrace();
			console.getOutput().writeAttrs(ConsolePane.colorAttributes(
				console.getErrorColor()),
				jEdit.getProperty("msg.slime.error-starting"));
		}
	} //}}}
	
	//{{{ send()
	/**
	 * Utility version of send()
	 */
	public void send(Console console, String str) {
		send(console, str, false);
	}
	
	/**
	 * Sends input to the running process
	 */
	protected void send(Console console, String str, boolean fromConsole) {
		final ConsoleState state = consoleStateMap.get(console);
		if (!state.running) {
			console.getOutput().print(console.getErrorColor(),
				jEdit.getProperty("msg.slime.no-process"));
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
				"\n"+jEdit.getProperty("msg.slime.stopped"));
			state.running = false;
		}
	} //}}}

	//{{{ waitFor()
	/**
	 * If the process is running, this method returns only when a prompt
	 * has been printed
	 */
	public boolean waitFor(Console console) {
		ConsoleState state = consoleStateMap.get(console);
		if (!state.running) return true;
		while (state.waiting) {
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
					String data = new String(buf, 0, read);
					Output output = (error ? state.error : state.output);
					output.writeAttrs(col, data);
					String prompt = getPromptRegex();
					if (prompt != null) {
						if (data.indexOf("\n") != -1) {
							data = data.substring(data.lastIndexOf("\n")+1);
						}
						if (data.matches(prompt)) {
							state.waiting = false;
							output.commandDone();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				output.print(null, jEdit.getProperty(
					"msg.slime.stopped"));
			}
		}
	} //}}}
	
	//{{{ ConsoleState class
	static class ConsoleState {
		protected Process p;
		protected boolean running;
		protected boolean waiting;
		protected Output output;
		protected Output error;
		
		public ConsoleState() {
			p = null;
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
