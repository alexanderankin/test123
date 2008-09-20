package context;

import org.gjt.sp.jedit.Buffer;

public interface IContextFinder {
	String getContext(String identifier, Buffer buffer, int line, int pos);
}
