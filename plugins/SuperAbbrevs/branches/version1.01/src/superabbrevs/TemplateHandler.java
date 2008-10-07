/*
 * TemplateHandler.java
 *
 * Created on 16. juni 2007, 00:14
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package superabbrevs;

import superabbrevs.model.Abbrev.ReplementSelectionTypes;
import superabbrevs.model.Abbrev.ReplacementTypes;
import superabbrevs.utilities.TextUtil;
import org.gjt.sp.jedit.bsh.EvalError;
import org.gjt.sp.jedit.bsh.ParseException;
import org.gjt.sp.jedit.bsh.TargetError;
import java.io.IOException;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;
import superabbrevs.model.Abbrev;
import superabbrevs.template.fields.EndField;
import superabbrevs.template.fields.SelectableField;
import superabbrevs.template.Template;
import superabbrevs.template.TemplateFactory;
import superabbrevs.template.TemplateInterpreter;

/**
 *
 * @author Sune Simonsen
 */
public class TemplateHandler {

    private JEditTextArea textArea;
    private Buffer buffer;
    private JEditInterface jedit;

    /** Creates a new instance of TemplateHandler */
    public TemplateHandler(JEditInterface jedit) {
        this.textArea = jedit.getTextArea();
        this.buffer = jedit.getBuffer();
        this.jedit = jedit;
    }

    void expandAbbrev(Abbrev abbrev, boolean invokedAsACommand)
            throws TargetError, ParseException, EvalError, IOException {
        
        int templateStart = getSelectionStart();
        String indent = getIndent(templateStart);
        
        TemplateInterpreter ti = new TemplateInterpreter(jedit);
        ti.setInput(abbrev, invokedAsACommand, indent);

        if (invokedAsACommand) {
            selectReplacementArea(abbrev.whenInvokedAsCommand);
        }        

        TemplateFactory tf = new TemplateFactory(ti, indent);
        
        Template t = tf.createTemplate(abbrev.expansion);
        t.setOffset(templateStart);

        textArea.setSelectedText(t.toString());

        // select the current field in the template
        selectField(t);

        Handler h = new Handler(t, jedit);
        Handler.putHandler(buffer, h);

        TemplateCaretListener.putCaretListener(textArea,
                new TemplateCaretListener());
    }

    public boolean isInTempateMode() {
        return Handler.enabled(buffer);
    }

    private String getIndent(int templateStart) {
        // the line number of the current line
        int lineNumber = textArea.getLineOfOffset(templateStart);

        // the text on the current line
        String line = textArea.getLineText(lineNumber);

        return TextUtil.getIndent(line);
    }

    private int getSelectionStart() {
        if (0 < textArea.getSelectionCount()) {
            return textArea.getSelection(0).getStart();
        } else {
            return textArea.getCaretPosition();
        }
    }

    private void selectChar() {
        textArea.selectNone();
        int caretPos = textArea.getCaretPosition();
        if (caretPos < textArea.getBufferLength()) {
            textArea.setSelection(new Selection.Range(caretPos, caretPos + 1));
        }
    }

    /**
     * Method selectField(JEditTextArea textArea, SelectableField field)
     * Select the field in the buffer
     */
    private void selectField(Template t) {
        SelectableField field = t.getCurrentField();

        int start = field.getOffset();
        int end = start + field.getLength();
        textArea.setCaretPosition(end);
        textArea.addToSelection(new Selection.Range(start, end));
    }

    public boolean selectNextAbbrev() {
        Handler h = Handler.getHandler(buffer);
        Template t = h.getTemplate();

        TemplateCaretListener listener =
                TemplateCaretListener.removeCaretListener(textArea);

        if (t.getCurrentField() instanceof EndField) {
            Handler.removeHandler(buffer);
            return false;
        }

        t.nextField();
        SelectableField f = t.getCurrentField();

        if (f != null) {
            int start = f.getOffset();
            int end = start + f.getLength();
            textArea.setCaretPosition(end);
            textArea.addToSelection(new Selection.Range(start, end));
        }
        TemplateCaretListener.putCaretListener(textArea, listener);

        return true;
    }

    public void selectPrevAbbrev() {
        Handler h = Handler.getHandler(buffer);
        Template t = h.getTemplate();

        if (t != null) {
            TemplateCaretListener listener =
                    TemplateCaretListener.removeCaretListener(textArea);
            t.prevField();
            SelectableField f = t.getCurrentField();
            if (f != null) {
                int start = f.getOffset();
                int end = start + f.getLength();
                textArea.setCaretPosition(end);
                textArea.addToSelection(new Selection.Range(start, end));
            }
            TemplateCaretListener.putCaretListener(textArea, listener);
        }
    }

    private void selectLines() {
        int[] lines = textArea.getSelectedLines();
        int start = textArea.getLineStartOffset(lines[0]);
        int end = textArea.getLineEndOffset(lines[lines.length - 1]);

        textArea.setSelection(new Selection.Range(start, --end));
    }

    private void selectReplacementArea(ReplementSelectionTypes replaceType) {
        switch (replaceType) {
            case NOTHING: textArea.selectNone(); break;
            case SELECTED_LINES: selectLines(); break;
            case SELECTION: break;
        }
    }

    private void selectReplacementArea(ReplacementTypes replaceType) {
        switch (replaceType) {
            case AT_CARET: textArea.selectNone(); break;
            case CHAR: selectChar(); break;
            case WORD: textArea.selectWord(); break;
            case LINE: textArea.selectLine(); break;
            case BUFFER: textArea.selectAll(); break;
        }
    }

    private void selectReplacementArea(
            Abbrev.WhenInvokedAsCommand whenInvokedAsCommand) {
        Abbrev.ReplementSelectionTypes replacementSelectionType =
                whenInvokedAsCommand.replacementSelectionType;
        if (replacementSelectionType != Abbrev.ReplementSelectionTypes.NOTHING &&
                1 == textArea.getSelectionCount()) {
            selectReplacementArea(replacementSelectionType);
        } else {
            selectReplacementArea(whenInvokedAsCommand.replacementType);
        }
    }
}
