package columnruler;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.textarea.*;

public class CaretMark extends DynamicMark implements CaretListener, ScrollListener {
	private ColumnRuler ruler;
	public CaretMark() {
		super("Caret mark");
	}

	public void activate(ColumnRuler ruler) {
		this.ruler = ruler;
		EditBus.addToBus(this);
		ruler.getTextArea().addCaretListener(this);
		ruler.getTextArea().addScrollListener(this);
	}

	public void deactivate() {
		ruler.getTextArea().removeCaretListener(this);
		ruler.getTextArea().removeScrollListener(this);
		EditBus.removeFromBus(this);
	}

	public void handleMessage(EBMessage msg) {
	}

	public void update() {
		_column = findCaretColumn();
	}

	//{{{ CaretListener implementation
	public void caretUpdate(CaretEvent e) {
		_column = findCaretColumn();
		ruler.repaint();
	}
	//}}}

	//{{{ ScrollListener implementation
	public void scrolledVertically(JEditTextArea textArea) {
		int caretCol = findCaretColumn();
		if (caretCol >= 0) {
			_column = caretCol;
			ruler.repaint();
		}
	}

	public void scrolledHorizontally(JEditTextArea textArea) {}
	//}}}

	private int findCaretColumn() {
		try {
			JEditTextArea textArea = ruler.getTextArea();
			Point caret = textArea.offsetToXY(textArea.getCaretPosition());
			int hScroll = textArea.getHorizontalOffset();
			if (caret != null) {
				int caretX = (int) caret.getX();
				int charWidth = ruler.getCharWidth();
				return (caretX - hScroll) / charWidth;
			} else {
				return -1;
			}
		} catch (Exception e) {
			return -1;
		}
	}

	public Color getColor() {
		return ruler.getTextArea().getPainter().getCaretColor();
	}
}
