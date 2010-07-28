package slime.repl;
/**
 * @author Damien Radtke
 * class Python
 * Defines a REPL for running Python
 */
//{{{ Imports
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.util.Log;
import slime.REPL;
import org.gjt.sp.jedit.MiscUtilities;
//}}}
public class Python extends REPL {
	
	/**
	 * Attempts 'python -i' on the system path
	 */
	public Process getProcess() {
		Process p = null;
		try {
			p = Runtime.getRuntime().exec("python -i");
		}
		catch (Exception e) {
			Log.log(Log.ERROR,this,"Starting python failed: "+e);
		}
		finally {
			return p;
		}
	}
	
	public String getEvalCommand(String str) {
		return "exec(\""+str+"\")";
	}
	
	public String getBufferEvalCommand(Buffer buffer) {
		return "execfile(\""+buffer.getPath()+"\")";
	}
	
	public String getPromptRegex() {
		return ">>> ";
	}
	
}
