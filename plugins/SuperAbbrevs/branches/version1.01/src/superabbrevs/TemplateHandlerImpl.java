/*
 * TemplateHandler.java
 *
 * Created on 16. juni 2007, 00:14
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package superabbrevs;

import java.io.IOException;

import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.bsh.EvalError;
import org.gjt.sp.jedit.bsh.ParseException;
import org.gjt.sp.jedit.bsh.TargetError;
import org.gjt.sp.jedit.msg.BufferChanging;
import org.gjt.sp.jedit.msg.ViewUpdate;
import org.gjt.sp.jedit.textarea.Selection;

import superabbrevs.model.Abbreviation;
import superabbrevs.model.ReplacementTypes;
import superabbrevs.model.SelectionReplacementTypes;
import superabbrevs.template.Template;
import superabbrevs.template.TemplateFactory;
import superabbrevs.template.TemplateInterpreter;
import superabbrevs.template.fields.EndField;
import superabbrevs.template.fields.SelectableField;
import superabbrevs.utilities.Log;
import superabbrevs.utilities.TextUtil;
import superabbrevs.utilities.Log.Level;

import com.google.inject.Inject;

/**
 *
 * @author Sune Simonsen
 */
public class TemplateHandlerImpl implements TemplateHandler, EBComponent {

    private final JEditInterface jedit;
	private final TemplateBufferListener handler;

	@Inject
    public TemplateHandlerImpl(JEditInterface jedit, TemplateBufferListener handler) {
        this.jedit = jedit;
		this.handler = handler;
		EditBus.addToBus(this);
    }

    void expandAbbrev(Abbreviation abbrev, boolean invokedAsACommand)
            throws TargetError, ParseException, EvalError, IOException {
        
        String indent = getIndent(getSelectionStart());
        
        TemplateInterpreter ti = new TemplateInterpreter(jedit);
        ti.setInput(abbrev, invokedAsACommand, indent);

        if (invokedAsACommand) {
            selectReplacementArea(abbrev);
        }        

        TemplateFactory tf = new TemplateFactory(ti, indent);

        int templateStart = getSelectionStart();
        Template t = tf.createTemplate(abbrev.getExpansion());
        t.setOffset(templateStart);

        jedit.setSelectedText(t.toString());

        // select the current field in the template
        selectField(t);

        handler.setTemplate(t);
        handler.startListening();
    }

    /* (non-Javadoc)
	 * @see superabbrevs.TemplateHandler#isInTempateMode()
	 */
    public boolean isInTempateMode() {
        return handler.isListening();
    }

    private String getIndent(int templateStart) {
        // the line number of the current line
        int lineNumber = jedit.getLineOfOffset(templateStart);

        // the text on the current line
        String line = jedit.getLineText(lineNumber);

        return TextUtil.getIndent(line);
    }

    private int getSelectionStart() {
        if (0 < jedit.getSelectionCount()) {
            return jedit.getSelectionStart(0);
        } else {
            return jedit.getCaretPosition();
        }
    }

    private void selectChar() {
        jedit.selectNone();
        int caretPos = jedit.getCaretPosition();
        if (caretPos < jedit.getBufferLength()) {
            jedit.setSelection(new Selection.Range(caretPos, caretPos + 1));
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
        jedit.setCaretPosition(end);
        jedit.addToSelection(new Selection.Range(start, end));
    }

    /* (non-Javadoc)
	 * @see superabbrevs.TemplateHandler#selectNextAbbrev()
	 */
    public boolean selectNextAbbrev() {
        Template t = handler.getTemplate();

        handler.stopListening();
        
        if (t.getCurrentField() instanceof EndField) {
            return false;
        }

        t.nextField();
        SelectableField f = t.getCurrentField();

        if (f != null) {
            int start = f.getOffset();
            int end = start + f.getLength();
            jedit.setCaretPosition(end);
            jedit.addToSelection(new Selection.Range(start, end));
        }
        
        handler.startListening();
        
        return true;
    }

    /* (non-Javadoc)
	 * @see superabbrevs.TemplateHandler#selectPrevAbbrev()
	 */
    public void selectPrevAbbrev() {
        Template t = handler.getTemplate();

        handler.stopListening();
        
        t.prevField();
        SelectableField f = t.getCurrentField();
        if (f != null) {
            int start = f.getOffset();
            int end = start + f.getLength();
            jedit.setCaretPosition(end);
            jedit.addToSelection(new Selection.Range(start, end));
        }
        
        handler.startListening();
    }

    private void selectLines() {
        int[] lines = jedit.getSelectedLines();
        int start = jedit.getLineStartOffset(lines[0]);
        int end = jedit.getLineEndOffset(lines[lines.length - 1]);

        jedit.setSelection(new Selection.Range(start, --end));
    }

    private void selectReplacementArea(SelectionReplacementTypes replacementType) {
        switch (replacementType) {
            case NOTHING: jedit.selectNone(); break;
            case SELECTED_LINES: selectLines(); break;
            case SELECTION: break;
        }
    }

    private void selectReplacementArea(ReplacementTypes replaceType) {
        switch (replaceType) {
            case AT_CARET: jedit.selectNone(); break;
            case CHAR: selectChar(); break;
            case WORD: jedit.selectWord(); break;
            case LINE: jedit.selectLine(); break;
            case BUFFER: jedit.selectAll(); break;
        }
    }

    private void selectReplacementArea(Abbreviation abbrev) {
        if (hasSelection()) {
            selectReplacementArea(abbrev.getSelectionReplacementArea());
        } else {
            selectReplacementArea(abbrev.getReplacementArea());
        }
    }

	private boolean hasSelection() {
		return 1 == jedit.getSelectionCount();
	}

	/* (non-Javadoc)
	 * @see superabbrevs.TemplateHandler#stopTemplateMode()
	 */
	public void stopTemplateMode() {
		handler.stopListening();
	}

	public void handleMessage(EBMessage message) {
		Log.log(Level.MESSAGE, TemplateHandlerImpl.class, "Message received: " + message);
		if (message instanceof ViewUpdate) {
			handler.stopListening();
		}
	}
}
