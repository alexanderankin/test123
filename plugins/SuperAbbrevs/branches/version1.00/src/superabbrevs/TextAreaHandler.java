/*
 * AbbrevsHandler.java
 *
 * Created on 15. juni 2007, 23:32
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package superabbrevs;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;

/**
 *
 * @author Sune Simonsen
 */
public class TextAreaHandler {

    private JEditTextArea textArea;

    private Buffer buffer;

    private View view;
    
    
    public TextAreaHandler(View view, JEditTextArea textArea, Buffer buffer) {
        this.view = view;
        this.buffer = buffer;
        this.textArea = textArea;
    }
    
    public String getTextBeforeCaret() {
        // the line number of the current line 
        int line = textArea.getCaretLine();
        // the start position of the current line in the full text  
        int lineStart = buffer.getLineStartOffset(line);
        // the offset of the caret in the full text 
        int caretPos = textArea.getCaretPosition();
        // the offset of the caret in the current line 
        int caretPosition = caretPos - lineStart;

        // the text on the current line
        String lineText = textArea.getLineText(line);
        
        return lineText.substring(0,caretPosition);
    }
    
    public String getModeAtCursor() {
        // the offset of the caret in the full text 
        int caretPos = textArea.getCaretPosition();

        // a string indication the mode of the current buffer 
        return buffer.getRuleSetAtOffset(caretPos).getModeName();
    }
    
}
