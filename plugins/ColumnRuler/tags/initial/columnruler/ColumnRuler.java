package columnruler;

import java.awt.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.textarea.*;

/**
 * Description of the Class             
 *
 * @author     mace
 * @created    June 5, 2003
 * @modified   $Date: 2003-06-05 21:51:44 $ by $Author: bemace $
 * @version    $Revision: 1.1.1.1 $
 */
public class ColumnRuler extends Component {
	private JEditTextArea _textArea;

	public ColumnRuler(JEditTextArea textArea) {
		_textArea = textArea;
	}

	public void paint(Graphics gfx) {
		
		int textAreaWidth = _textArea.getWidth();
		int lineHeight = getLineHeight();
		FontMetrics fm = _textArea.getPainter().getFontMetrics();
		int charHeight = _textArea.getFont().getSize();
		int charWidth = fm.charWidth('X');
		int xOffset = _textArea.getGutter().getWidth();
		
		gfx.setColor(getBackground());
		gfx.fillRect(0, 0, getWidth(), getHeight());

		gfx.setColor(getForeground());
		// Draw tick marks
		for (int col = 0; col < textAreaWidth / charWidth; col++) {
			int x = xOffset + col *charWidth;
			switch (col % 10) {
				case 0:
				case 5:
					gfx.drawLine(x, lineHeight, x, 2 * lineHeight / 3);
					break;
				default:
					gfx.drawLine(x, lineHeight, x, 4 * lineHeight / 5);
			}
		}
		// Draw numbers
		gfx.setFont(gfx.getFont().deriveFont(Font.BOLD));
		for (int n = 0; n < textAreaWidth / charWidth; n += 10) {
			gfx.drawString(n + "", xOffset + (n * charWidth) - (fm.stringWidth(n + "")) / 2 + 1, charHeight-2);
		}
	}
	
	public Dimension getMinimumSize() {
		return getPreferredSize();	
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(getWidth(),getLineHeight());	
	}
	
	public Color getForeground() {
		return jEdit.getColorProperty(ColumnRulerPlugin.OPTION_PREFIX + "foreground", Color.BLACK);
	}
	
	public Color getBackground() {
		return jEdit.getColorProperty(ColumnRulerPlugin.OPTION_PREFIX + "background", Color.WHITE);
	}
		
	private int getLineHeight() {
		return _textArea.getPainter().getFontMetrics().getHeight();
	}

	public String toString() {
		return "Column Ruler";
	}
}

