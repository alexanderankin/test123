package superabbrevs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.JDialog;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.textarea.JEditTextArea;

import superabbrevs.gui.AbbrevsDialog;
import superabbrevs.model.Abbrev;

public class InputHandler {

    private JEditTextArea textArea;
    private Buffer buffer;

    private TextAreaHandler textAreaHandler;
    private AbbrevsHandler abbrevsHandler = new AbbrevsHandler();
	private final JEditInterface jEdit;
       
    /** Creates a new instance of InputHandler */
    public InputHandler(JEditInterface jEdit) {
        this.jEdit = jEdit;
		this.textArea = jEdit.getTextArea();
        this.buffer = jEdit.getBuffer();
        
        textAreaHandler = new TextAreaHandler(jEdit);
    }
    
    public void esc() {
        if(textAreaHandler.isInTemplateMode()){
            // Stop the template mode
            Handler.removeHandler(buffer);
        } else {
            textArea.selectNone();
        }
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
        Set<Abbrev> abbrevs = abbrevsHandler.getAbbrevs(mode);
        textAreaHandler.showSearchDialog(new ArrayList<Abbrev>(abbrevs));
    }

	public void showOptionsPane() {
		AbbrevsOptionPaneController controller = 
            new AbbrevsOptionPaneController(jEdit);
	    JDialog dialog = new AbbrevsDialog(jEdit.getView(), false, controller);
	    dialog.setVisible(true);
	}
    
}
