/*
 * InputHandler.java
 *
 * Created on 16. juni 2007, 00:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package superabbrevs;

import java.util.LinkedList;
import javax.swing.JOptionPane;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;

/**
 *
 * @author Sune Simonsen
 */
public class InputHandler {

    private View view;

    private JEditTextArea textArea;

    private Buffer buffer;

    private TextAreaHandler textAreaHandler;

    private AbbrevsHandler abbrevsHandler;
    
    private TemplateHandler templateHandler;
    
    /** Creates a new instance of InputHandler */
    public InputHandler(View view, JEditTextArea textArea, Buffer buffer) {
        this.view = view;
        this.textArea = textArea;
        this.buffer = buffer;
        
        textAreaHandler = new TextAreaHandler(view, textArea, buffer);
        abbrevsHandler = new AbbrevsHandler();
    }
    
    public void tab() {
        LinkedList<Abbrev> abbrevs;
                
        if (!textArea.isEditable()){
            // beep if the textarea is not editable
            textArea.getToolkit().beep();
        } else if(templateHandler != null){
            // If we already is in template mode, jump to the next field
            
        } else if(0 < textArea.getSelectionCount()){
            // If there is a selection in the buffer use the default behavior
            // for the tab key
            textArea.insertTabAndIndent();
        } else {
            textAreaHandler = new TextAreaHandler(view, textArea, buffer);
            
            String getTextBeforeCaret = textAreaHandler.getTextBeforeCaret();
            String mode = textAreaHandler.getModeAtCursor();
            abbrevs = abbrevsHandler.getAbbrevs(mode, getTextBeforeCaret);
            
            if (!abbrevs.isEmpty()) {
                JOptionPane.showMessageDialog(view, abbrevs);
            } else {
                // There was no abbreviation to expand before the caret
                textArea.insertTabAndIndent();
            }
        }
    }
    
}
