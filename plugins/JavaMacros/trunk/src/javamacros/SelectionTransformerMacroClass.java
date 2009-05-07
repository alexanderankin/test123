package javamacros;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.Macros;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;

/** Base class for macros that transforms text.
 * Macro must implement {@link #transform} method that would do actual work.
 * Macro behaves as follows: <ul>
 * <li>if something is selected, the selected text is transformed</li>
 * <li>otherwise, entire buffer is transformed</li>
 * </ul>
 *
 * Here is a sample macro:
 * <pre>
 * package macros.Edit.Case;
 * 
 * import javamacros.*;
 *
 * public class To_Lower extends SelectionTransformerMacroClass {
 *
 *    protected String transform(String s) {
 *        return s.toLowerCase();
 *    }
 *
 * }</pre>
 */
public abstract class SelectionTransformerMacroClass implements MacroClass {

    /** Perform text transformation.
     *
     * @param s input text
     * @return output text
     */
    protected abstract String transform(String s);

    public final void run(Buffer buffer, View view, Macros.Macro macro, JEditTextArea jEditTextArea) throws Exception {
        if (jEditTextArea.getSelectionCount() == 0) {
            final String source = jEditTextArea.getText();
            final String result = transform(source);
            jEditTextArea.setText(result);
        } else {
            for (int selectionNo=0; selectionNo<jEditTextArea.getSelectionCount(); ++selectionNo) {
                final Selection selection = jEditTextArea.getSelection(selectionNo);
                final String source = jEditTextArea.getSelectedText(selection);
                final String result = transform(source);
                jEditTextArea.setSelectedText(selection, result);
            }
        }
    }

}