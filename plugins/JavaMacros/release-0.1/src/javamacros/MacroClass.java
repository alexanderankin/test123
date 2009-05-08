package javamacros;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.Macros;
import org.gjt.sp.jedit.textarea.JEditTextArea;

/** Interface to be implemented by Java macros.
 *
 * Macro class must have public default constructor.<br/>
 * Macro class must implement {@link #run} method that would be called when macro is invoked.<br/>
 * Macro class can throw {@link javamacros.MacroErrorMessageException} exception to output error
 * message to user. Alternatively, macro can throw any other exception, but it would be
 * considered as unexpected error and would be written to activity log as error.
 *
 * Here is the "Hello World" java macro:
 * <pre>
 * package macros;
 *
 * import javamacros.*;
 * import org.gjt.sp.jedit.*;
 *
 * public class Hello_World implements MacroClass {
 *
 *     public void run(Buffer buffer, View view, Macros.Macro macro, JEditTextArea textArea) throws Exception {
 *         Macros.message(view, "Hello World!");
 *     }
 * 
 * }</pre>
 */
public interface MacroClass {

    /** Invoke a macro.
     *
     * @param buffer edit buffer on which macro is called
     * @param view edit view on which macro is called
     * @param macro macro that is called
     * @param textArea textArea component
     * @throws Exception
     */
    public void run(Buffer buffer, View view, Macros.Macro macro, JEditTextArea textArea) throws Exception;

}