package columnruler;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.util.*;

public class CaretMark extends DynamicMark implements CaretListener, ScrollListener {
	private ColumnRuler ruler;

	public CaretMark() {
		super("Caret mark");
	}

	public void activate(ColumnRuler ruler) {
		this.ruler = ruler;
		ruler.getTextArea().addCaretListener(this);
		ruler.getTextArea().addScrollListener(this);
	}

	public void deactivate() {
		ruler.getTextArea().removeCaretListener(this);
		ruler.getTextArea().removeScrollListener(this);
	}

	public void update() {
		_column = findCaretColumn();
		if (jEdit.getBooleanProperty("options.columnruler.guides.caret")) {
			ruler.getTextArea().repaint();
		}
	}

	//{{{
	public void drawGuide(Graphics2D gfx, ColumnRuler ruler) {
		int hScroll = ruler.getTextArea().getHorizontalOffset();
		double x = getColumn()*ruler.charWidth + hScroll;
		double halfChar = ruler.charWidth/2;
		Line2D guide;
		guide = new Line2D.Double(x,0,x,ruler.getTextArea().getHeight());
		gfx.setColor(getColor());
		gfx.draw(guide);
		int screenLine = ruler.getTextArea().getScreenLineOfOffset(ruler.getTextArea().getCaretPosition());
		double y = (screenLine)*ruler.lineHeight;
		guide.setLine(x-halfChar,y-1,x+halfChar,y-1);
		gfx.draw(guide);
		guide.setLine(x-halfChar,y+ruler.lineHeight,x+halfChar,y+ruler.lineHeight);
		gfx.draw(guide);
	} //}}}

	//{{{ CaretListener implementation
	public void caretUpdate(CaretEvent e) {
		update();
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
				double caretX = (int) caret.getX();
				return (int) Math.round((caretX - hScroll) / ruler.charWidth);
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

	public void setGuideVisible(boolean b) {
		guide = b;
		jEdit.setBooleanProperty("options.columnruler.guides.caret",b);
	}

	public boolean isGuideVisible() {
		return jEdit.getBooleanProperty("options.columnruler.guides.caret",false);
	}
}
