package debugger.itf;


public interface DebuggerTool {
	void setFrontEnd(JEditFrontEnd frontEnd);
	// Note: line counting begins at 0
	IBreakpoint addBreakpoint(String file, int line);
	IData getData(String name);
}
