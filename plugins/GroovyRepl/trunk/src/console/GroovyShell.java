package console;
/**
 * @author Damien Radtke
 * class GroovyShell
 * Embeds an interactive Groovy session into the console
 */
//{{{ Imports
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
import procshell.ProcessShell;
//}}}
public class GroovyShell extends ProcessShell {
	
	private String prompt = "groovy:\\d\\d\\d> ";
	
	/*
 	 * Constructor for SlimeShell
 	 */
	public GroovyShell() {
		super("Groovy");
	}
	
	//{{{ init()
	/**
	 * Start up Groovy
	 */
	protected void init(ConsoleState state) throws IOException {
		Log.log(Log.DEBUG,this,"Attempting to start Groovy process");
		ProcessBuilder pb = new ProcessBuilder("groovysh", 
			"-Djline.terminal=jline.UnsupportedTerminal", "--color=N");
		state.p = pb.start();
		Log.log(Log.DEBUG,this,"Groovy started.");
	}
	//}}}
	
	//{{{ eval()
	/**
	 * Evaluate text
	 */
	public void eval(Console console, String str) {
		str += "\n";
		str = str.replace("\n", "\\n");
		str = str.replace("\t", "\\t");
		str = str.replace("\"", "\\\"");
		send(console, str);
	} //}}}
	
	//{{{ evalBuffer()
	/**
	 * Evaluate a buffer
	 */
	public void evalBuffer(Console console, Buffer buffer) {
		send(console, buffer.getText(0, buffer.getLength()));
	} //}}}
	
	protected void onRead(Output output, ConsoleState state, String str) {
		if (str.indexOf("\n") != -1) {
			str = str.substring(str.lastIndexOf("\n")+1);
		}
		if (str.matches(prompt)) {
			state.waiting = false;
			output.commandDone();
		}
	}
	
	public void printInfoMessage(Output output) {
		output.print(null, jEdit.getProperty("msg.groovyrepl.info-message"));
	}
	
}
