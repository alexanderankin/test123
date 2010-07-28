package slime;
/**
 * @author Damien Radtke
 * class REPL
 * A configuration class for defining REPL's
 */
//{{{ Imports
import org.gjt.sp.jedit.Buffer;
//}}}
public abstract class REPL {
	/**
	 * Returns the running REPL process
	 * Should return null if process creation failed
	 */
	public abstract Process getProcess();
	
	/**
	 * Returns a command to evaluate 'buffer'
	 * A check is done prior to running this method to make sure the buffer
	 * exists on disk. If it doesn't, this method is not used
	 * @param buffer the buffer to evaluate
	 * @return the string used to evaluate the buffer, or null if the buffer's
	 * contents should simply be fed to getEvalCommand, which is the default
	 */
	public String getBufferEvalCommand(Buffer buffer) {
		return null;
	}
	
	/**
	 * Returns a command to evaluate 'str'
	 * If this method returns null (which is the default), the string will
	 * simply be fed to standard input.
	 * @param str the string to evaluate
	 * @return the command to evaluate str, or null for standard input
	 */
	public String getEvalCommand(String str) {
		return null;
	}
	
	/**
	 * If any text is printed by the REPL that matches this regex,
	 * the animation will stop and waitFor() will return.
	 * By default, returning null means that the animation will continue
	 * indefinitely and waitFor() will never return.
	 * @return a regex to match the REPL's prompt
	 */
	public String getPromptRegex() {
		return null;
	}
	
}
