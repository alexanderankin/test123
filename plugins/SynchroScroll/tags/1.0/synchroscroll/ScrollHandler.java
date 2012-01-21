package synchroscroll;


import org.gjt.sp.jedit.textarea.ScrollListener;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.EditPane;

public class ScrollHandler implements ScrollListener {
    
    View view;

    ScrollHandler(View view) {
        this.view = view;
    }
    
    /**
     * Synchronizes horizontal scrolling between all text areas in the View.
     * @param textArea The text area doing the scrolling.
     */
    public void scrolledHorizontally( TextArea textArea ) {
        int offset = textArea.getHorizontalOffset();
        EditPane[] editPanes = view.getEditPanes();
        for(EditPane editPane : editPanes) {
            TextArea other = editPane.getTextArea();
            other.setHorizontalOffset(offset);
        }
    }

    /**
     * Synchronizes vertical scrolling between all text areas in the View.
     * @param textArea The text area doing the scrolling.
     */
    public void scrolledVertically( TextArea textArea ) {
        int firstLine = textArea.getFirstLine();
        Integer BL = (Integer)textArea.getClientProperty(SynchroScrollPlugin.BASELINE);
        int baseline = BL == null ? 0 : BL.intValue();
        int offset = firstLine - baseline;
        EditPane[] editPanes = view.getEditPanes();
        for(EditPane editPane : editPanes) {
            TextArea other = editPane.getTextArea();
            Integer otherBL = (Integer)other.getClientProperty(SynchroScrollPlugin.BASELINE);
            int otherBaseline = otherBL == null ? 0 : otherBL.intValue();
            int otheroffset = otherBaseline + offset;
            otheroffset = otheroffset < 0 ? 0 : otheroffset;
            otheroffset = otheroffset >= other.getLineCount() ? other.getLineCount() - 1 : otheroffset;
            other.setFirstLine(otheroffset);
        }
    }
}