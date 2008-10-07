package superabbrevs.template;

import java.io.IOException;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.bsh.EvalError;
import org.gjt.sp.jedit.bsh.Interpreter;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import superabbrevs.JEditInterface;
import superabbrevs.Paths;
import superabbrevs.model.Abbrev;
import superabbrevs.model.Abbrev.ReplacementTypes;
import superabbrevs.model.Abbrev.ReplementSelectionTypes;
import superabbrevs.stdlib.Std;
import superabbrevs.stdlib.Tpg;

public class TemplateInterpreter {
    private Interpreter interpreter = new Interpreter();
    private JEditInterface jedit;
    private Std std;
    private Tpg tpg;

    public TemplateInterpreter(JEditInterface jedit) {
        this.jedit = jedit;
    }
    
    public String evaluateCodeOnSelection(String code, String selection) throws EvalError {
        interpreter.set("s", selection);
        String result = (String) interpreter.eval(code);
        interpreter.set("s", null);
        return result;
    }
    
    public String evaluateTemplateGenerationCode(String code) throws EvalError {
        StringBuffer out = new StringBuffer();
        tpg.setOut(out);
        interpreter.set("tpg", tpg);
        interpreter.eval(code);
        interpreter.set("tpg", null);
        tpg.setOut(null);
        return out.toString();
    }
    
    public void setInput(Abbrev abbrev, boolean invokedAsACommand, String indent) 
            throws IOException {
        
        tpg = new Tpg(indent, jedit);
        std = new Std(indent);
        
        try {
            interpreter.source(Paths.ABBREVS_FUNCTION_PATH);
            interpreter.source(Paths.TEMPLATE_GENERATION_FUNCTION_PATH);
            
            interpreter.set("std", std);
            interpreter.set("input", "");
            interpreter.set("indent", indent);

            if (invokedAsACommand) {

                Abbrev.ReplementSelectionTypes replacementSelectionType =
                        abbrev.whenInvokedAsCommand.replacementSelectionType;
                if (replacementSelectionType != Abbrev.ReplementSelectionTypes.NOTHING &&
                        1 == jedit.getTextArea().getSelectionCount()) {
                    setInput(replacementSelectionType);
                } else {
                    setInput(abbrev.whenInvokedAsCommand.replacementType);
                }
            }

            // set the file name of the current buffer
            interpreter.set("filename", tpg.getFileName());
            // we keep the selection variable for backwards compatibility
            interpreter.set("selection", tpg.getSelection());
        } catch (EvalError ex) {
            assert false : "This should never happen";
        }
    }
    
    private void setCaretChar() throws EvalError {
        interpreter.set("input", "" + tpg.getChar());
    }

    private void setDocument() throws EvalError {
        interpreter.set("input", tpg.getBufferText());
    }

    private void setInput(ReplementSelectionTypes inputType) throws EvalError {
        switch (inputType) {
            case SELECTED_LINES: setSelectedLines(); break;
            case SELECTION: setSelection(); break;
        }
    }

    private void setInput(ReplacementTypes inputType)
            throws EvalError {
        switch (inputType) {
            case BUFFER: setDocument(); break;
            case LINE: setCaretLine(); break;
            case WORD: setCaretWord(); break;
            case CHAR: setCaretChar(); break;
        }
    }

    private void setCaretLine() throws EvalError {
        interpreter.set("input", tpg.getLine());
    }

    private void setCaretWord() throws EvalError {
        interpreter.set("input", tpg.getWord());
    }

    private void setSelectedLines() throws EvalError {
        interpreter.set("input", tpg.getSelectedLines());
    }

    private void setSelection() throws EvalError {
        interpreter.set("input", tpg.getSelection());
    }
}
