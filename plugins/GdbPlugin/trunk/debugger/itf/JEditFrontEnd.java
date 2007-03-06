package debugger.itf;

public interface JEditFrontEnd {
	void breakpointHit(int bkptno, String file, int line);
	void goTo(String file, int line);
	void setCurrentLocation(String file, int line);
	void programExited();
}
