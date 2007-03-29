package debugger.itf;

public interface JEditFrontEnd {
	void goTo(String file, int line);
	void setCurrentLocation(String file, int line);
	void programExited();
}
