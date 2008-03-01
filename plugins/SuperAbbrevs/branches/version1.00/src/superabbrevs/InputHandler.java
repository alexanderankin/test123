/*
 * InputHandler.java
 *
 * Created on 16. juni 2007, 00:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package superabbrevs;

import java.util.ArrayList;
import superabbrevs.model.Abbrev;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
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

    private AbbrevsHandler abbrevsHandler = new AbbrevsHandler();
       
    /** Creates a new instance of InputHandler */
    public InputHandler(View view, JEditTextArea textArea, Buffer buffer) {
        this.view = view;
        this.textArea = textArea;
        this.buffer = buffer;
        
        textAreaHandler = new TextAreaHandler(view, textArea, buffer);
    }
    
    public void tab() {
        if (!textArea.isEditable()){
            // beep if the textarea is not editable
            textArea.getToolkit().beep();
        } else if(textAreaHandler.isInTemplateMode()){
            // If we already is in template mode, jump to the next field
            boolean selectedNextAbbrev = textAreaHandler.selectNextAbbrev();
            if (!selectedNextAbbrev) {
                tab();
            }
        } else if(0 < textArea.getSelectionCount()){
            // If there is a selection in the buffer use the default behavior
            // for the tab key
            textArea.insertTabAndIndent();
        } else {
            String getTextBeforeCaret = textAreaHandler.getTextBeforeCaret();
            String mode = textAreaHandler.getModeAtCursor();
            LinkedList<Abbrev> abbrevs = 
                    abbrevsHandler.getAbbrevs(mode, getTextBeforeCaret);
            
            if (abbrevs.size() == 1) {
                // There is only one expansion
                Abbrev a = abbrevs.getFirst();
                textAreaHandler.removeAbbrev(a);
                textAreaHandler.expandAbbrev(a, false);
            } else if (!abbrevs.isEmpty()) {
                Collections.sort(abbrevs);
                                                
                textAreaHandler.showAbbrevsPopup(abbrevs);
            } else {
                // There was no abbreviation to expand before the caret
                textArea.insertTabAndIndent();
            }
        }
    }

    void shiftTab() {
        if (!textArea.isEditable()){
            // beep if the textarea is not editable
            textArea.getToolkit().beep();
        } else if(textAreaHandler.isInTemplateMode()){
            // If we already is in template mode, jump to the next field
            textAreaHandler.selectPrevAbbrev();
        } else {
            textArea.shiftIndentLeft();
        }
    }

    void showSearchDialog() {
        String mode = textAreaHandler.getModeAtCursor();
        ArrayList<Abbrev> abbrevs = abbrevsHandler.getAbbrevs(mode);
        textAreaHandler.showSearchDialog(abbrevs);
    }
    
}
