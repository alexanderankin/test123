package debugger.itf;

import java.util.Hashtable;

public interface DebuggerTool {
	void setFrontEnd(JEditFrontEnd frontEnd);
	void start(String prog, String args, String cwd, Hashtable<String, String> env);
	void go();
	void next();
	void step();
	void pause();
	void quit();
	boolean isRunning();
	// Note: line counting begins at 0
	IBreakpoint addBreakpoint(String file, int line);
	IData getData(String name);
}
