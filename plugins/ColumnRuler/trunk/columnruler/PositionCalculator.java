package columnruler;

import java.awt.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.textarea.*;

public class PositionCalculator {

	public static FontMetrics getFontMetrics(JEditTextArea textArea) {
		return textArea.getPainter().getFontMetrics();
	}

	public static int getCharHeight(JEditTextArea textArea) {
		return textArea.getFont().getSize();
	}

	public static int getCharWidth(JEditTextArea textArea) {
		return getFontMetrics(textArea).charWidth('X');
	}

	public static int getXOffset(JEditTextArea textArea) {
		return textArea.getGutter().getWidth();
	}

	public static int getLineHeight(JEditTextArea textArea) {
		return textArea.getPainter().getFontMetrics().getHeight();
	}

	public static int getWrapColumn(JEditTextArea textArea) {
		return textArea.getBuffer().getIntegerProperty("maxLineLen", 0);
	}

	public static int getCaretColumn(JEditTextArea textArea) {
		try {
			Point caret = textArea.offsetToXY(textArea.getCaretPosition());
			int hScroll = textArea.getHorizontalOffset();
			if (caret != null) {
				int caretX = (int) caret.getX();
				int charWidth = getCharWidth(textArea);
				return (caretX - hScroll) / charWidth;
			} else {
				return -1;
			}
		} catch (Exception e) {
			return -1;
		}
	}

}
