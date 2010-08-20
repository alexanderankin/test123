package console;
/**
 * @author Damien Radtke
 * class PythonShell
 * Embeds an interactive Python session into the console
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
public class PythonShell extends ProcessShell {
	
	private String prompt = ">>> ";
	
	/*
 	 * Constructor for SlimeShell
 	 */
	public PythonShell() {
		super("Python");
	}
	
	//{{{ init()
	/**
	 * Start up Python
	 */
	protected void init(ConsoleState state) throws IOException {
		String exec = "\""+jEdit.getProperty("options.pythonrepl.exec")+"\"";
		Log.log(Log.DEBUG,this,"Attempting to start Python process: "+exec);
		state.p = Runtime.getRuntime().exec(exec+" -i");
		Log.log(Log.DEBUG,this,"Python started.");
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
		send(console, "exec(\""+str+"\")");
	} //}}}
	
	//{{{ evalBuffer()
	/**
	 * Evaluate a buffer
	 */
	public void evalBuffer(Console console, Buffer buffer) {
		send(console, "execfile(\""+buffer.getPath().replace("\\", "/")+"\")");
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
		output.print(null, jEdit.getProperty("msg.pythonrepl.info-message"));
	}
	
}
