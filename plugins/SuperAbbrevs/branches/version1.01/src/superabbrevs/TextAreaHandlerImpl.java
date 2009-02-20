/*
 * AbbrevsHandler.java
 *
 * Created on 15. juni 2007, 23:32
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package superabbrevs;

import org.gjt.sp.jedit.bsh.EvalError;
import org.gjt.sp.jedit.bsh.ParseException;
import org.gjt.sp.jedit.bsh.TargetError;
import superabbrevs.model.Abbrev;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.SwingUtilities;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;

import com.google.inject.Inject;

import superabbrevs.gui.scrollablepopupmenu.ScrollablePopupMenu;
import superabbrevs.gui.scrollablepopupmenu.ScrollablePopupMenuEvent;
import superabbrevs.gui.scrollablepopupmenu.ScrollablePopupMenuListner;
import superabbrevs.gui.searchdialog.SearchAcceptedListener;
import superabbrevs.gui.searchdialog.SearchDialog;
import superabbrevs.gui.searchdialog.SearchDialogModel;
import superabbrevs.utilities.Log;

/**
 *
 * @author Sune Simonsen
 */
public class TextAreaHandlerImpl implements TextAreaHandler {
    private TemplateHandler templateHandler;

    @Inject private JEditTextArea textArea;
    @Inject private Buffer buffer;
    @Inject private View view;
    
    @Inject 
    public TextAreaHandlerImpl(JEditInterface jedit) {
        this.templateHandler = new TemplateHandler(jedit);
    }
    
    /* (non-Javadoc)
	 * @see superabbrevs.TextAreaHandler#getTextBeforeCaret()
	 */
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
    
    /* (non-Javadoc)
	 * @see superabbrevs.TextAreaHandler#getModeAtCursor()
	 */
    public String getModeAtCursor() {
        // the offset of the caret in the full text 
        int caretPos = textArea.getCaretPosition();

        // a string indication the mode of the current buffer 
        String mode = buffer.getContextSensitiveProperty(caretPos, "mode");
        Log.log(Log.Level.DEBUG, TextAreaHandlerImpl.class, "Mode: " + mode + " " + 
                buffer.getRuleSetAtOffset(caretPos).getModeName());
        return buffer.getRuleSetAtOffset(caretPos).getModeName();
    }
   
    /* (non-Javadoc)
	 * @see superabbrevs.TextAreaHandler#showAbbrevsPopup(java.util.LinkedList)
	 */
    public void showAbbrevsPopup(LinkedList<Abbrev> abbrevs) {
        int offset = textArea.getCaretPosition();
        Point location = textArea.offsetToXY(offset);
        location.y += textArea.getPainter().getFontMetrics().getHeight();

        SwingUtilities.convertPointToScreen(location,textArea.getPainter());
        
        ScrollablePopupMenu<Abbrev> menu = 
                new ScrollablePopupMenu<Abbrev>(view, location, abbrevs);
        
        menu.addActionListener(new ScrollablePopupMenuListner<Abbrev>() {
            public void selectedMenuItem(ScrollablePopupMenuEvent<Abbrev> event) {
                Abbrev a = event.getSelectedObject();
                removeAbbrev(a);
                expandAbbrev(a, false);
            }
        });
        
        menu.setVisible(true);
    }
    
    /* (non-Javadoc)
	 * @see superabbrevs.TextAreaHandler#removeAbbrev(superabbrevs.model.Abbrev)
	 */
    public void removeAbbrev(Abbrev abbrev) {
        // the offset of the caret in the full text 
        int end = textArea.getCaretPosition();
        int start = end - abbrev.getAbbreviation().length();
        textArea.setSelection(new Selection.Range(start, end));
        textArea.setSelectedText("");
    }
    
    /* (non-Javadoc)
	 * @see superabbrevs.TextAreaHandler#expandAbbrev(superabbrevs.model.Abbrev, boolean)
	 */
    public void expandAbbrev(Abbrev abbrev, boolean invokedAsACommand) {
        try {
            templateHandler.expandAbbrev(abbrev, invokedAsACommand);
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
    
    /* (non-Javadoc)
	 * @see superabbrevs.TextAreaHandler#selectNextAbbrev()
	 */
    public boolean selectNextAbbrev() {
        return templateHandler.selectNextAbbrev();
    }
    
    /* (non-Javadoc)
	 * @see superabbrevs.TextAreaHandler#selectPrevAbbrev()
	 */
    public void selectPrevAbbrev() {
        templateHandler.selectPrevAbbrev();
    }
    
    /* (non-Javadoc)
	 * @see superabbrevs.TextAreaHandler#isInTemplateMode()
	 */
    public boolean isInTemplateMode()
    {
        return templateHandler.isInTempateMode();
    }

    /* (non-Javadoc)
	 * @see superabbrevs.TextAreaHandler#showSearchDialog(java.util.ArrayList)
	 */
    public void showSearchDialog(ArrayList<Abbrev> abbrevs) {
        SearchDialogModel model = new SearchDialogModel(abbrevs);
        SearchDialog dialog = new SearchDialog(view, "Search for abbreviation", 
                false, model);
        dialog.setLocationRelativeTo(view);
        dialog.addSearchAcceptedListener(new SearchAcceptedListener() {
            public void accepted(Object o) {
                Abbrev a = (Abbrev)o;
                expandAbbrev(a, true);
            }
        });
        dialog.setVisible(true);
    }
}
