package columnruler;

import java.awt.*;
import javax.swing.event.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.textarea.*;

/**
 * Description of the Class
 *
 * @author     mace
 * @created    June 5, 2003
 * @modified   $Date: 2003-06-06 06:46:18 $ by $Author: bemace $
 * @version    $Revision: 1.2 $
 */
public class ColumnRuler extends Component implements CaretListener, ScrollListener {
	private JEditTextArea _textArea;


	public ColumnRuler(JEditTextArea textArea) {
		_textArea = textArea;
		_textArea.addCaretListener(this);
		_textArea.addScrollListener(this);
	}
	//{{{ paint() method
	public void paint(Graphics gfx) {

		int textAreaWidth = _textArea.getWidth();
		int lineHeight = getLineHeight();
		FontMetrics fm = _textArea.getPainter().getFontMetrics();
		int charHeight = _textArea.getFont().getSize();
		int charWidth = fm.charWidth('X');
		int xOffset = _textArea.getGutter().getWidth();
		int hScroll = _textArea.getHorizontalOffset();
		Selection[] selection = _textArea.getSelection();

		//{{{ Draw background
		gfx.setColor(getBackground());
		gfx.fillRect(0, 0, getWidth(), getHeight());
		//}}}

		//{{{ Highlight selection columns
		if (selection != null && selection.length > 0) {
			gfx.setColor(getHighlight());
			int start;
			int end;
			if (selection[0] instanceof Selection.Rect) {
				Selection.Rect rect = (Selection.Rect) selection[0];
				start = xOffset + rect.getStartColumn(_textArea.getBuffer()) * charWidth;
				end = xOffset + rect.getEndColumn(_textArea.getBuffer()) * charWidth;
			} else {
				start = xOffset + (int) _textArea.offsetToXY(selection[0].getStart()).getX();
				end = xOffset + (int) _textArea.offsetToXY(selection[0].getEnd()).getX();
			}
			if (start < end) {
				gfx.fillRect(start, 0, end - start, lineHeight);
			} else {
				gfx.fillRect(xOffset, 0, end, lineHeight);
				gfx.fillRect(start, 0, getWidth() - start, lineHeight);
			}
		}
		//}}}

		//{{{ Draw tick marks
		gfx.setColor(getForeground());
		for (int col = 0; col < (textAreaWidth-hScroll) / charWidth; col++) {
			int x = xOffset + hScroll + col * charWidth;
			switch (col % 10) {
				case 0:
				case 5:
					gfx.drawLine(x, lineHeight, x, 2 * lineHeight / 3);
					break;
				default:
					gfx.drawLine(x, lineHeight, x, 4 * lineHeight / 5);
			}
		}
		//}}}

		//{{{ Draw caret indicator
		gfx.setColor(_textArea.getPainter().getCaretColor());
		Point caret = _textArea.offsetToXY(_textArea.getCaretPosition());
		if (caret != null) {
			int caretX = xOffset + (int) caret.getX();
			gfx.drawLine(caretX, 0, caretX, lineHeight);
		}
		// }}}

		//{{{ Draw numbers
		gfx.setColor(getForeground());
		gfx.setFont(gfx.getFont().deriveFont(Font.BOLD));
		for (int n = 0; n < (textAreaWidth-hScroll) / charWidth; n += 10) {
			gfx.drawString(n + "", xOffset + (n * charWidth) - (fm.stringWidth(n + "")) / 2 + 1 + hScroll, charHeight - 2);
		}
		//}}}
	}//}}}

	public void caretUpdate(CaretEvent e) {
		repaint();
	}
	
	public void scrolledVertically(JEditTextArea textArea) {}
	
	public void scrolledHorizontally(JEditTextArea textArea){
		if (textArea.equals(_textArea)) {
			repaint();
		}
	}

	void destroy() {
		_textArea.removeCaretListener(this);
		_textArea.removeScrollListener(this);
	}
	
	private int getLineHeight() {
		return _textArea.getPainter().getFontMetrics().getHeight();
	}

	//{{{ Accessors

	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	public Dimension getPreferredSize() {
		return new Dimension(getWidth(), getLineHeight());
	}

	public Color getForeground() {
		return jEdit.getColorProperty(ColumnRulerPlugin.OPTION_PREFIX + "foreground", Color.BLACK);
	}

	public Color getBackground() {
		return jEdit.getColorProperty(ColumnRulerPlugin.OPTION_PREFIX + "background", Color.WHITE);
	}

	public Color getHighlight() {
		return _textArea.getPainter().getSelectionColor();
	}

	public String toString() {
		return "Column Ruler";
	}

	//}}}
}

