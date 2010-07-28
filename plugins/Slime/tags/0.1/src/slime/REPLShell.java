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
import java.io.InputStream;
import java.io.OutputStream;
import javax.swing.text.AttributeSet;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.jEdit;
import slime.REPL;
//}}}
public class REPLShell extends Shell {
	private boolean running;
	private Process p;
	private Output output;
	private Console console;
	private REPL repl;
	private OutputStream replOut;
	private boolean waiting;
	/*
 	 * Constructor for REPLShell
 	 */
	public REPLShell() {
		super("REPL");
		running = false;
		waiting = false;
	}
	
	//{{{ printInfoMessage()
	public void printInfoMessage(Output output) {
		output.print(null, jEdit.getProperty("msg.slime.info-message"));
	} //}}}
	
	//{{{ printPrompt()
	public void printPrompt(Console console, Output output) {
		this.console = console;
		this.output = output;
	} //}}}

	//{{{ execute()
	public void execute(Console console, String input, final Output output,
	Output error, String command) {
		if (!running) {
			// Ignore blank lines
			if (command.trim().length() == 0) {
				output.commandDone();
				return;
			}
			// Check to see if the entered text is a valid REPL
			// If so, start it; if not, display an error message
			REPL _repl = (REPL) ServiceManager.getService(
				"slime.REPL", command);
			if (_repl == null) {
				output.commandDone();
				output.print(console.getErrorColor(), jEdit.getProperty(
					"msg.slime.unknown")+command);
				return;
			} else {
				startREPL(_repl);
			}
		} else {
			// If a loop is already running, send input to it
			sendToREPL(command, true);
		}
	} //}}}
	
	//{{{ sendToREPL
	public void sendToREPL(String str) {
		sendToREPL(str, false);
	}
	
	protected void sendToREPL(String str, boolean fromConsole) {
		final String cmd = str+"\n";
		if (!fromConsole) {
			if (str.indexOf("\n") != -1) {
				output.print(console.getInfoColor(), "...");
			} else {
				output.print(console.getInfoColor(), str);
			}
			//output.print(console.getInfoColor(), str);
		}
		new Thread() {
			public void run() {
				try {
					waiting = true;
					replOut.write(cmd.getBytes());
					replOut.flush();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	} //}}}
	
	//{{{ printNoREPLError()
	public void printNoREPLError() {
		output.print(console.getErrorColor(), jEdit.getProperty(
			"msg.slime.none-running"));
	} //}}}

	//{{{ startREPL() 
	public void startREPL(REPL repl) {
		if (running) {
			stop(console);
		}
		try {
			p = repl.getProcess();
			if (p == null) {
				// Process failed to start
				throw new Exception();
			}
			output.print(console.getInfoColor(), "Starting...");
			replOut = p.getOutputStream();
			
			// Set up threads for reading input
			new REPLReader(p.getInputStream(),
				ConsolePane.colorAttributes(console.getPlainColor())).start();
			new REPLReader(p.getErrorStream(),
				ConsolePane.colorAttributes(console.getErrorColor())).start();
				
			running = true;
			this.repl = repl;
		}
		catch (Exception e) {
			running = false;
			output.print(console.getErrorColor(), "Could not start REPL.\n"+
				"Check the Activity Log for details.");
			output.commandDone();
			e.printStackTrace();
		}
	} //}}}
	
	//{{{ stop()
	public void stop(Console console) {
		output.commandDone();
		if (running) {
			p.destroy();
			output.print(console.getInfoColor(), "\nREPL Stopped.");
			running = false;
			repl = null;
		} else {
			printNoREPLError();
		}
	} //}}}

	//{{{ waitFor()
	public boolean waitFor(Console console) {
		while (waiting) {
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
		stop(console);
	} //}}}
	
	//{{{ isRunning()
	public boolean isRunning() {
		return running;
	} //}}}
	
	//{{{ getRunningREPL()
	public REPL getRunningREPL() {
		return repl;
	} //}}}
	
	//{{{ REPLReader class 
	class REPLReader extends Thread {
		private InputStream in;
		private AttributeSet col;
		public REPLReader(InputStream in, AttributeSet col) {
			this.in = in;
			this.col = col;
		}
		public void run() {
			try {
				String cache = "";
				byte[] buf = new byte[4096];
				int read = -1;
				while ((read = in.read(buf)) != -1) {
					String data = new String(buf, 0, read);
					output.writeAttrs(col, data);
					String prompt = repl.getPromptRegex();
					if (prompt != null) {
						if (data.indexOf("\n") != -1) {
							data = data.substring(data.lastIndexOf("\n")+1);
						}
						if (data.matches(prompt)) {
							waiting = false;
							output.commandDone();
						}
					}
				}
			} catch (Exception e) {
				//e.printStackTrace();
				output.print(console.getInfoColor(), jEdit.getProperty(
					"msg.slime.stopped"));
			}
		}
	} //}}}
	
}
