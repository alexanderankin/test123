package javamacros;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.Macros;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;
import java.util.ArrayList;

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

    protected Buffer buffer;
    protected View view;
    protected Macros.Macro macro;
    protected JEditTextArea jEditTextArea;

    /** Perform text transformation.
     *
     * @param s input text
     * @return output text
     */
    protected abstract String transform(String s);

    public final void run(Buffer buffer, View view, Macros.Macro macro, JEditTextArea jEditTextArea) throws Exception {
        this.buffer = buffer;
        this.view = view;
        this.macro = macro;
        this.jEditTextArea = jEditTextArea;
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

    protected static String[] split(String text) {
        final ArrayList<String> lines = new ArrayList<String>();
        int p0 = -1;
        for (;;) {
            int p1 = text.indexOf('\n', p0 + 1);
            if (p1 == -1) {
                break;
            }
            lines.add(text.substring(p0 + 1, p1));
            p0 = p1;
        }
        lines.add(text.substring(p0 + 1));
        return lines.toArray(new String[lines.size()]);
    }

    protected static String join(String[] lines) {
        final StringBuilder text = new StringBuilder();
        boolean firstLine = true;
        for (String line : lines) {
            if (!firstLine) {
                text.append('\n');
            } else {
                firstLine = false;
            }
            text.append(line);
        }
        return text.toString();
    }

}