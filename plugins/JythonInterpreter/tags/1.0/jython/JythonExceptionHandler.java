package jython;

import org.python.core.PyException;
import org.gjt.sp.jedit.jEdit;

public final class JythonExceptionHandler {

    public void handle(Throwable t) throws Throwable {
        if (t instanceof PyException) {
            new JythonErrorDialog("jython.jython-awt-error.title", "jython.jython-awt-error.message", jEdit.getActiveView(), t);
        }
    }
}
