package superabbrevs;

import java.awt.Point;
import java.awt.Window;
import java.util.List;
import java.util.SortedSet;

import javax.swing.event.CaretListener;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.buffer.BufferListener;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.jedit.textarea.Selection.Range;

public interface JEditInterface extends ModeService {

	public abstract Mode getCurrentMode();

	public abstract String getCurrentModeName();

	public abstract List<Mode> getModes();

	public abstract SortedSet<String> getModesNames();

	public abstract void setCaretPosition(int caret);

	public abstract void remove(int offset, int length);

	public abstract void insert(int offset, String text);

	public abstract int getCaretLine();

	public abstract int getCaretPosition();

	public abstract String getLineText(int line);

	public abstract	Point getPopUpLocation();

	public abstract View getView();

	public abstract void setSelection(Selection selection);

	public abstract void setSelectedText(String text);

	public abstract int getLineOfOffset(int line);

	public abstract int getSelectionCount();

	public abstract int getSelectionStart(int selection);

	public abstract void selectNone();

	public abstract int getBufferLength();

	public abstract void selectLine();

	public abstract void selectAll();

	public abstract void selectWord();

	public abstract int getLineEndOffset(int line);

	public abstract int getLineStartOffset(int line);

	public abstract int[] getSelectedLines();

	public abstract void addToSelection(Selection selection);

	public abstract String getModeAtCursor();

	public abstract int getLineLength(int line);

	public abstract String getNoWordSep();

	public abstract String getText(int start, int end);

	public abstract String getText();

	public abstract String getSelectedText();

	public abstract String getBufferName();

	public abstract void addCaretListener(
			CaretListener caretListener);

	public abstract void addBufferListener(BufferListener listener);

	public abstract void shiftIndentLeft();

	public abstract boolean isEditable();

	public abstract void insertTabAndIndent();

	public abstract void playBeep();

	public abstract void removeCaretListener(CaretListener listener);

	public abstract void writeLock();

	public abstract void writeUnlock();
	
	public abstract Buffer getBuffer();

	public abstract void removeBufferListener(BufferListener listener);
	
	public String getPluginHome();
}