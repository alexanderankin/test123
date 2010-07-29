package slime;
/**
 * @author Damien Radtke
 * class PythonShell
 * TODO: comment
 */
//{{{ Imports
import console.Console;
import java.io.IOException;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.util.Log;
//}}}
public class PythonShell extends SlimeShell {
	
	public PythonShell() {
		super("Python");
	}
	
	public void init(ConsoleState state) throws IOException {
		state.p = Runtime.getRuntime().exec("python -i");
	}
	
	public void eval(Console console, String str) {
		send(console, "exec(\""+str+"\")");
	}
	
	public void evalBuffer(Console console, Buffer buffer) {
		send(console, "execfile(\""+buffer.getPath()+"\")");
	}
	
	public String getPromptRegex() {
		return ">>> ";
	}
	
}
