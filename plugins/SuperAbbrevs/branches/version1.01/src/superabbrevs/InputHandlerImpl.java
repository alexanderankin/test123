package superabbrevs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.JDialog;

import superabbrevs.gui.AbbrevsDialog;
import superabbrevs.model.Abbreviation;
import superabbrevs.repository.ModeRepository;
import superabbrevs.utilities.Log;
import superabbrevs.utilities.Log.Level;

import com.google.inject.Inject;

public class InputHandlerImpl implements InputHandler {

    private final JEditInterface jedit;
	private final TextAreaHandler textAreaHandler;
	private final AbbreviationHandler abbreviationHandler;
	private final ModeRepository modeRepository;

	@Inject 
    public InputHandlerImpl(JEditInterface jedit, TextAreaHandler textAreaHandler, 
    		AbbreviationHandler abbreviationHandler, ModeRepository modeRepository) {
				this.jedit = jedit;
				this.textAreaHandler = textAreaHandler;
				this.abbreviationHandler = abbreviationHandler;
				this.modeRepository = modeRepository;
    }
    
    /* (non-Javadoc)
	 * @see superabbrevs.InputHandler#esc()
	 */
    public void esc() {
        if(textAreaHandler.isInTemplateMode()){
            // Stop the template mode
        	textAreaHandler.stopTemplateMode();
        } else {
            jedit.selectNone();
        }
    }
    
    /* (non-Javadoc)
	 * @see superabbrevs.InputHandler#tab()
	 */
    public void tab() {
        if (!jedit.isEditable()){
            // beep if the textarea is not editable
        	jedit.playBeep();
        } else if(textAreaHandler.isInTemplateMode()){
            // If we already is in template mode, jump to the next field
            boolean selectedNextAbbrev = textAreaHandler.selectNextAbbrev();
            if (!selectedNextAbbrev) {
                tab();
            }
        } else if(0 < jedit.getSelectionCount()){
            // If there is a selection in the buffer use the default behavior
            // for the tab key
            jedit.insertTabAndIndent();
        } else {
            String getTextBeforeCaret = textAreaHandler.getTextBeforeCaret();
            String mode = textAreaHandler.getModeAtCursor();
            LinkedList<Abbreviation> abbrevs = 
            	abbreviationHandler.getAbbrevs(mode, getTextBeforeCaret);
            
            if (abbrevs.size() == 1) {
                // There is only one expansion
                Abbreviation a = abbrevs.getFirst();
                
                Log.log(Level.NOTICE, InputHandlerImpl.class, 
                		String.format("Expanding abbreviation: %s", a));
                
                textAreaHandler.removeAbbrev(a);
                textAreaHandler.expandAbbrev(a, false);
            } else if (!abbrevs.isEmpty()) {
                Collections.sort(abbrevs);
                textAreaHandler.showAbbrevsPopup(abbrevs);
            } else {
                // There was no abbreviation to expand before the caret
                jedit.insertTabAndIndent();
            }
        }
    }

    /* (non-Javadoc)
	 * @see superabbrevs.InputHandler#shiftTab()
	 */
    public void shiftTab() {
        if (!jedit.isEditable()){
            // beep if the textarea is not editable
            jedit.playBeep();
        } else if(textAreaHandler.isInTemplateMode()){
            // If we already is in template mode, jump to the next field
            textAreaHandler.selectPrevAbbrev();
        } else {
            jedit.shiftIndentLeft();
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
            new AbbrevsOptionPaneController(jedit, modeRepository);
	    JDialog dialog = new AbbrevsDialog(jedit.getView(), false, controller);
	    dialog.setVisible(true);
	}
}
