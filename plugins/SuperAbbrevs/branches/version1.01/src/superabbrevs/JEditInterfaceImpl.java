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

import com.google.inject.Inject;

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

	@Override
	public void remove(int offset, int length) {
		buffer.remove(offset, length);
	}

	@Override
	public void insert(int offset, String text) {
		buffer.insert(offset, text);
	}

	@Override
	public int getCaretLine() {
		return textArea.getCaretLine();
	}

	@Override
	public int getCaretPosition() {
		return textArea.getCaretPosition();
	}

	@Override
	public String getLineText(int line) {
		return textArea.getLineText(line);
	}

	@Override
	public Point getPopUpLocation() {
		int offset = textArea.getCaretPosition();
		Point location = textArea.offsetToXY(offset);
        location.y += textArea.getPainter().getFontMetrics().getHeight();

        SwingUtilities.convertPointToScreen(location,textArea.getPainter());
		return location;
	}

	@Override
	public View getView() {
		return view;
	}

	@Override
	public void setSelection(Selection selection) {
		textArea.setSelection(selection);		
	}

	@Override
	public void setSelectedText(String text) {
		textArea.setSelectedText(text);
	}

	@Override
	public int getLineOfOffset(int line) {
		return textArea.getLineOfOffset(line);
	}

	@Override
	public int getSelectionCount() {
		return textArea.getSelectionCount();
	}

	@Override
	public int getSelectionStart(int selection) {
		return textArea.getSelection(selection).getStart();
	}

	@Override
	public void selectNone() {
		textArea.selectNone();		
	}

	@Override
	public void addToSelection(Selection selection) {
		textArea.addToSelection(selection);
	}

	@Override
	public int getBufferLength() {
		return textArea.getBufferLength();
	}

	@Override
	public int getLineEndOffset(int line) {
		return textArea.getLineEndOffset(line);
	}

	@Override
	public int getLineStartOffset(int line) {
		return textArea.getLineStartOffset(line);
	}

	@Override
	public int[] getSelectedLines() {
		return textArea.getSelectedLines();
	}

	@Override
	public void selectAll() {
		textArea.selectAll();
	}

	@Override
	public void selectLine() {
		textArea.selectLine();
	}

	@Override
	public void selectWord() {
		textArea.selectWord();
	}

	@Override
	public String getModeAtCursor() {
        // the offset of the caret in the full text 
        int caretPos = getCaretPosition();

        // a string indication the mode of the current buffer 
        String mode = buffer.getContextSensitiveProperty(caretPos, "mode");
        Log.log(Log.Level.DEBUG, TextAreaHandlerImpl.class, "Mode: " + mode + " " + 
                buffer.getRuleSetAtOffset(caretPos).getModeName());
        return buffer.getRuleSetAtOffset(caretPos).getModeName();
	}

	@Override
	public int getLineLength(int line) {
		return textArea.getLineLength(line);
	}

	@Override
	public String getNoWordSep() {
		return buffer.getStringProperty("noWordSep");
	}

	@Override
	public String getText(int start, int end) {
		return textArea.getText(start, end);
	}

	@Override
	public String getSelectedText() {
		return textArea.getSelectedText();
	}

	@Override
	public String getText() {
		return textArea.getText();
	}

	@Override
	public String getBufferName() {
		return buffer.getName();
	}

	@Override
	public void addCaretListener(CaretListener caretListener) {
		textArea.addCaretListener(caretListener);
	}

	@Override
	public void addBufferListener(BufferListener listener) {
		buffer.addBufferListener(listener);
	}

	@Override
	public void insertTabAndIndent() {
		textArea.insertTabAndIndent();
	}

	@Override
	public boolean isEditable() {
		return textArea.isEditable();
	}

	@Override
	public void shiftIndentLeft() {
		textArea.shiftIndentLeft();
	}

	@Override
	public void playBeep() {
		textArea.getToolkit().beep();
	}

	@Override
	public void removeCaretListener(CaretListener listener) {
		textArea.removeCaretListener(listener);
	}

	@Override
	public void writeLock() {
		buffer.writeLock();
	}

	@Override
	public void writeUnlock() {
		buffer.writeUnlock();	
	}

	@Override
	public Buffer getBuffer() {
		return buffer;
	}

	@Override
	public void removeBufferListener(BufferListener listener) {
		buffer.removeBufferListener(listener);
	}
	
	@Override
	public String getPluginHome() {
		return EditPlugin.getPluginHome(SuperAbbrevsPlugin.class).getPath();
	}
}
