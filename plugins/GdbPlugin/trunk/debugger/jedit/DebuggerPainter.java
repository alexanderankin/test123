package debugger.jedit;

import java.awt.Graphics2D;
import java.awt.Point;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaExtension;

public abstract class DebuggerPainter extends TextAreaExtension {
	private EditPane editPane;
	private Buffer buffer;
	private int line;
	
	public DebuggerPainter(EditPane e, Buffer b, int l) {
		editPane = e;
		buffer = b;
		line = l;
	}
	public EditPane getEditPane() {
		return editPane;
	}
	public Buffer getBuffer() {
		return buffer;
	}
	public int getLine() {
		return line;
	}
	@Override
	public void paintValidLine(Graphics2D gfx, int screenLine,
			int physicalLine, int start, int end, int y) {
		super.paintValidLine(gfx, screenLine, physicalLine, start, end, y);
		if (buffer == editPane.getBuffer() && line == physicalLine + 1) {
			JEditTextArea textArea = editPane.getTextArea();
			Point p = textArea.offsetToXY(textArea.getLineStartOffset(physicalLine));
			paintLine(gfx, screenLine, physicalLine, start, end, y, p);
		}
	}

	protected abstract void paintLine(Graphics2D gfx, int screenLine,
			int physicalLine, int start, int end, int y, Point p);

}
