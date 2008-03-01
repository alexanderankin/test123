/*
 * TemplateHandler.java
 *
 * Created on 16. juni 2007, 00:14
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package superabbrevs;

import superabbrevs.utilities.TextUtil;
import bsh.EvalError;
import bsh.Interpreter;
import bsh.ParseException;
import bsh.TargetError;
import java.io.IOException;
import java.io.StringReader;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.TextUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;
import superabbrevs.model.Abbrev.InputTypes;
import superabbrevs.lexer.TemplateGeneratorLexer;
import superabbrevs.lexer.TemplateGeneratorParser;
import superabbrevs.model.Abbrev;
import superabbrevs.template.EndField;
import superabbrevs.template.SelectableField;
import superabbrevs.template.Template;
import superabbrevs.template.TemplateFactory;
import superabbrevs.utilities.Log;

/**
 *
 * @author Sune Simonsen
 */
public class TemplateHandler {

    private View view;
    private JEditTextArea textArea;
    private Buffer buffer;

    /** Creates a new instance of TemplateHandler */
    public TemplateHandler(View view, JEditTextArea textArea, Buffer buffer) {
        this.view = view;
        this.textArea = textArea;
        this.buffer = buffer;
    }

    void expandAbbrev(Abbrev abbrev, boolean invokedAsACommand) {
        Interpreter interpreter = new Interpreter();
        setInput(interpreter, abbrev, invokedAsACommand);
        
        if (invokedAsACommand) {
            selectReplacementArea(abbrev.whenInvokedAsCommand);
        }
        
        int templateStart = getSelectionStart();
        String indent = getIndent(templateStart);
        
        try {
            Template t = TemplateFactory.createTemplate(abbrev.expansion, interpreter, indent);
            t.setOffset(templateStart);

            textArea.setSelectedText(t.toString());

            // select the current field in the template
            selectField(t);

            Handler h = new Handler(t, textArea, buffer);
            Handler.putHandler(buffer, h);

            TemplateCaretListener.putCaretListener(textArea, new TemplateCaretListener());
        } catch (TargetError ex) {
            Log.log(Log.Level.ERROR, TemplateHandler.class, ex);
        } catch (ParseException ex) {
            Log.log(Log.Level.ERROR, TemplateHandler.class, ex);
        } catch (EvalError ex) {
            Log.log(Log.Level.ERROR, TemplateHandler.class, ex);
        } catch (IOException ex) {
            Log.log(Log.Level.ERROR, TemplateHandler.class, ex);
        }
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

    private void selectBuffer() {
        int end = textArea.getBufferLength() - 1;
        textArea.setSelection(new Selection.Range(0, end));
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
        textArea.setSelection(new Selection.Range(start, end));
    }

    private void selectReplacementArea(Abbrev.InvokedAsCommand whenInvokedAsCommand) {
        switch (whenInvokedAsCommand.replacementType) {
            case SELECTED_LINES: selectLines(); break;
            case BUFFER: selectBuffer(); break;
            case LINE: textArea.selectLine(); break;
        }
    }

    private void setInput(Interpreter interpreter, Abbrev abbrev, 
            boolean invokedAsACommand) {
        try {
            interpreter.set("input", "");
            
            if (invokedAsACommand) {
                if (1 == textArea.getSelectionCount()) {
                    switch (abbrev.whenInvokedAsCommand.inputSelectionType) {
                        case SELECTED_LINES: setSelectedLines(interpreter); break;
                        case SELECTION: setSelection(interpreter); break;
                    }
                } else {
                    switch (abbrev.whenInvokedAsCommand.inputType) {
                        case BUFFER: setDocument(interpreter); break;
                        case LINE: setCaretLine(interpreter); break;
                        case WORD: setCaretWord(interpreter); break;
                    }
                }
            } else {
                switch (abbrev.inputType) {
                    case BUFFER: setDocument(interpreter); break;
                    case LINE: setCaretLine(interpreter); break;
                    case WORD: setCaretWord(interpreter); break;
                }
            }
            
            // set the file name of the current buffer
            interpreter.set("filename", buffer.getName());
            
            // we keep the selection variable for backwards compatibility
            interpreter.set("selection", getSelection());
        } catch (EvalError ex) {
            // Should never happen.
            // TODO: write to log
        }
    }
    
    private String getSelection() {
        String selection = textArea.getSelectedText();
        return selection == null ? "" : selection;
    }
    
    private void setDocument(Interpreter interpreter) throws EvalError {
        interpreter.set("input", textArea.getText());
        interpreter.set("offset", textArea.getCaretPosition());
    }
    
    private void setCaretLine(Interpreter interpreter) throws EvalError {
        int line = textArea.getCaretLine();
        String input = "";
        int offsetInLine = 0;
        if(textArea.getLineLength(line) != 0) {
            input = textArea.getLineText(line);
            int lineStart = textArea.getLineStartOffset(line);
            offsetInLine = textArea.getCaretPosition() - lineStart;
        }
        interpreter.set("input", input);
        interpreter.set("offset", offsetInLine);
    }
    
    private void setCaretWord(Interpreter interpreter) throws EvalError {
        int line = textArea.getCaretLine();
        String input = "";
        int caretOffsetInWord = 0;
        if(textArea.getLineLength(line) != 0) {
            String lineText = textArea.getLineText(line);
        
            int lineStart = textArea.getLineStartOffset(line);
            int offset = textArea.getCaretPosition() - lineStart;

            String noWordSep = buffer.getStringProperty("noWordSep");

            if(offset == textArea.getLineLength(line)) offset--;

            int wordStart = TextUtilities.findWordStart(lineText,offset,
                    noWordSep,true,false,false);
            int wordEnd = TextUtilities.findWordEnd(lineText,offset+1,
                    noWordSep,true,false,false);
            
            input = textArea.getText(lineStart+wordStart, wordEnd - wordStart);
            caretOffsetInWord = offset - wordStart;
        }
        interpreter.set("input", input);
        interpreter.set("offset", caretOffsetInWord);
    }

    private void setSelectedLines(Interpreter interpreter) throws EvalError {
        StringBuffer sb = new StringBuffer();
        for (int line : textArea.getSelectedLines()) {
            sb.append(textArea.getLineText(line) + "\n");
        }
        
        interpreter.set("input", sb.toString());
    }

    private void setSelection(Interpreter interpreter) throws EvalError {
        interpreter.set("input", textArea.getSelectedText());
    }
}
