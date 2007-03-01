package debugger.itf;

import java.util.Hashtable;

public interface DebuggerTool {
	void setFrontEnd(JEditFrontEnd frontEnd);
	void start(String prog, String args, String cwd, Hashtable env);
	void go();
	void next();
	void step();
	void pause();
	void quit();
	boolean isRunning();
	// Note: line counting begins at 0
	IBreakpoint addBreakpoint(String file, int line);
	void removeBreakpoint(IBreakpoint brkpt);
	IData getData(String name);
}
