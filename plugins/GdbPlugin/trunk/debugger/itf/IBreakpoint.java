package debugger.itf;


public interface IBreakpoint {
	String getFile();
	int getLine();
	boolean canSetEnabled();
	void setEnabled(boolean enabled);
	void remove();
}
