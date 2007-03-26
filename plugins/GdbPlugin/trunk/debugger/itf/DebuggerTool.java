package debugger.itf;


public interface DebuggerTool {
	void setFrontEnd(JEditFrontEnd frontEnd);
	void start();
	void go();
	void next();
	void step();
	void finishCurrentFunction();
	void pause();
	void quit();
	boolean isRunning();
	// Note: line counting begins at 0
	IBreakpoint addBreakpoint(String file, int line);
	IData getData(String name);
}
