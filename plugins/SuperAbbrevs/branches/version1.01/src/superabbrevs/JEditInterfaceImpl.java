package superabbrevs;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.SwingUtilities;
import javax.swing.event.CaretListener;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.buffer.BufferListener;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;

import superabbrevs.utilities.Log;

public class JEditInterfaceImpl implements JEditInterface {
    private View view;
    private JEditTextArea textArea;
    private Buffer buffer;

    public JEditInterfaceImpl(View view, JEditTextArea textArea, Buffer buffer) {
        this.view = view;
        this.textArea = textArea;
        this.buffer = buffer;
    }
    
    public Mode getCurrentMode() {
        return buffer.getMode();
    }
    
    public String getCurrentModeName() {
        return buffer.getMode().getName();
    }
    
    public List<Mode> getModes() {
        return Arrays.asList(jEdit.getModes());
    }
    
    public SortedSet<String> getModesNames() {
    	SortedSet<String> modeNames = new TreeSet<String>();
    	for (Mode mode : jEdit.getModes()) {
			modeNames.add(mode.getName());
		}
        return modeNames;
    }

	public void setCaretPosition(int newCaret) {
		textArea.setCaretPosition(newCaret);
	}

	public void remove(int offset, int length) {
		buffer.remove(offset, length);
	}

	public void insert(int offset, String text) {
		buffer.insert(offset, text);
	}

	public int getCaretLine() {
		return textArea.getCaretLine();
	}

	public int getCaretPosition() {
		return textArea.getCaretPosition();
	}

	public String getLineText(int line) {
		return textArea.getLineText(line);
	}

	public Point getPopUpLocation() {
		int offset = textArea.getCaretPosition();
		Point location = textArea.offsetToXY(offset);
        location.y += textArea.getPainter().getFontMetrics().getHeight();

        SwingUtilities.convertPointToScreen(location,textArea.getPainter());
		return location;
	}

	public View getView() {
		return view;
	}

	public void setSelection(Selection selection) {
		textArea.setSelection(selection);		
	}

	public void setSelectedText(String text) {
		textArea.setSelectedText(text);
	}

	public int getLineOfOffset(int line) {
		return textArea.getLineOfOffset(line);
	}

	
	public int getSelectionCount() {
		return textArea.getSelectionCount();
	}

	
	public int getSelectionStart(int selection) {
		return textArea.getSelection(selection).getStart();
	}

	
	public void selectNone() {
		textArea.selectNone();		
	}

	
	public void addToSelection(Selection selection) {
		textArea.addToSelection(selection);
	}

	
	public int getBufferLength() {
		return textArea.getBufferLength();
	}

	
	public int getLineEndOffset(int line) {
		return textArea.getLineEndOffset(line);
	}

	
	public int getLineStartOffset(int line) {
		return textArea.getLineStartOffset(line);
	}

	
	public int[] getSelectedLines() {
		return textArea.getSelectedLines();
	}

	
	public void selectAll() {
		textArea.selectAll();
	}

	
	public void selectLine() {
		textArea.selectLine();
	}

	
	public void selectWord() {
		textArea.selectWord();
	}

	
	public String getModeAtCursor() {
        // the offset of the caret in the full text 
        int caretPos = getCaretPosition();

        // a string indication the mode of the current buffer 
        String mode = buffer.getContextSensitiveProperty(caretPos, "mode");
        Log.log(Log.Level.DEBUG, TextAreaHandlerImpl.class, "Mode: " + mode + " " + 
                buffer.getRuleSetAtOffset(caretPos).getModeName());
        return buffer.getRuleSetAtOffset(caretPos).getModeName();
	}

	public int getLineLength(int line) {
		return textArea.getLineLength(line);
	}

	public String getNoWordSep() {
		return buffer.getStringProperty("noWordSep");
	}

	public String getText(int start, int end) {
		return textArea.getText(start, end);
	}

	public String getSelectedText() {
		return textArea.getSelectedText();
	}

	public String getText() {
		return textArea.getText();
	}

	public String getBufferName() {
		return buffer.getName();
	}

	public void addCaretListener(CaretListener caretListener) {
		textArea.addCaretListener(caretListener);
	}

	public void addBufferListener(BufferListener listener) {
		buffer.addBufferListener(listener);
	}

	public void insertTabAndIndent() {
		textArea.insertTabAndIndent();
	}

	public boolean isEditable() {
		return textArea.isEditable();
	}

	public void shiftIndentLeft() {
		textArea.shiftIndentLeft();
	}

	public void playBeep() {
		textArea.getToolkit().beep();
	}

	public void removeCaretListener(CaretListener listener) {
		textArea.removeCaretListener(listener);
	}

	public void writeLock() {
		buffer.writeLock();
	}

	public void writeUnlock() {
		buffer.writeUnlock();	
	}

	public Buffer getBuffer() {
		return buffer;
	}

	public void removeBufferListener(BufferListener listener) {
		buffer.removeBufferListener(listener);
	}
	
	public String getPluginHome() {
		return EditPlugin.getPluginHome(SuperAbbrevsPlugin.class).getPath();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((buffer == null) ? 0 : buffer.hashCode());
		result = prime * result
				+ ((textArea == null) ? 0 : textArea.hashCode());
		result = prime * result + ((view == null) ? 0 : view.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JEditInterfaceImpl other = (JEditInterfaceImpl) obj;
		if (buffer == null) {
			if (other.buffer != null)
				return false;
		} else if (!buffer.equals(other.buffer))
			return false;
		if (textArea == null) {
			if (other.textArea != null)
				return false;
		} else if (!textArea.equals(other.textArea))
			return false;
		if (view == null) {
			if (other.view != null)
				return false;
		} else if (!view.equals(other.view))
			return false;
		return true;
	}
}
