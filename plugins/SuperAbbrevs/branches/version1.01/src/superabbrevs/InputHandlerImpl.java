package superabbrevs;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.JDialog;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;

import superabbrevs.gui.AbbrevsDialog;
import superabbrevs.model.Abbreviation;

import com.google.inject.Inject;

public class InputHandlerImpl implements InputHandler {

    @Inject private View view;
	@Inject private JEditTextArea textArea;
    @Inject private Buffer buffer;
    @Inject private TextAreaHandler textAreaHandler;
    
    @Inject private JEditInterface jedit;
    
    @Inject
    private AbbreviationHandler abbreviationHandler;

    public InputHandlerImpl() {
    }
    
    /* (non-Javadoc)
	 * @see superabbrevs.InputHandler#esc()
	 */
    public void esc() {
        if(textAreaHandler.isInTemplateMode()){
            // Stop the template mode
            Handler.removeHandler(buffer);
        } else {
            textArea.selectNone();
        }
    }
    
    /* (non-Javadoc)
	 * @see superabbrevs.InputHandler#tab()
	 */
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
            LinkedList<Abbreviation> abbrevs = 
            	abbreviationHandler.getAbbrevs(mode, getTextBeforeCaret);
            
            if (abbrevs.size() == 1) {
                // There is only one expansion
                Abbreviation a = abbrevs.getFirst();
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

    /* (non-Javadoc)
	 * @see superabbrevs.InputHandler#shiftTab()
	 */
    public void shiftTab() {
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

    /* (non-Javadoc)
	 * @see superabbrevs.InputHandler#showSearchDialog()
	 */
    public void showSearchDialog() {
        String mode = textAreaHandler.getModeAtCursor();
        Set<Abbreviation> abbrevs = abbreviationHandler.getAbbrevs(mode);
        textAreaHandler.showSearchDialog(new ArrayList<Abbreviation>(abbrevs));
    }

	/* (non-Javadoc)
	 * @see superabbrevs.InputHandler#showOptionsPane()
	 */
	public void showOptionsPane() {
		AbbrevsOptionPaneController controller = 
            new AbbrevsOptionPaneController(jedit, new Persistence());
	    JDialog dialog = new AbbrevsDialog(view, false, controller);
	    dialog.setVisible(true);
	}
}
