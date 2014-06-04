package org.jedit.plugins.columnruler.marks;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.util.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.util.Log;

import org.jedit.plugins.columnruler.*;

/**
 *  Mark which follows the caret's position in the text area. The caret mark
 *  paints its guide with tick marks indicating the current line.
 *
 * @author     Brad Mace
 * @version    $Revision: 1.6 $ $Date: 2006-10-11 17:21:41 $
 */
public class CaretMark extends DynamicMark implements CaretListener, ScrollListener {

	public CaretMark() {
		super("Caret", "options.columnruler.marks.caret");
	}

	public void activate(TextArea textArea) {
		textArea.addCaretListener(this);
		textArea.addScrollListener(this);
	}

	public void deactivate(TextArea textArea) {
		textArea.removeCaretListener(this);
		textArea.removeScrollListener(this);
	}
	
	public void shutdown() {
	}

	private void updatePosition(TextArea textArea) {
		ColumnRuler ruler = ColumnRulerPlugin.getColumnRulerForTextArea(textArea);
		if (ruler == null) {
			return;
		}
		if (!textArea.getBuffer().isLoading()) {
			Point caret = textArea.offsetToXY(textArea.getCaretPosition());
			if (caret != null) {
				double caretX = (int) caret.getX();
				int hScroll = textArea.getHorizontalOffset();
				int caretCol = (int) Math.round((caretX - hScroll) / ruler.getCharWidth());
				positionMap.put(ruler, caretCol);
				if (isVisible()) {
					ruler.repaint();
					if (isGuideVisible()) {
						textArea.repaint();
					}
				}
			}
		}
	}
	
	//{{{ handleMessage
	public void handleMessage(EBMessage message) {
		super.handleMessage(message);
		if (message instanceof ViewUpdate) {
			ViewUpdate vu = (ViewUpdate) message;
			if (vu.getWhat().equals(ViewUpdate.EDIT_PANE_CHANGED)) {
				updatePosition(vu.getView().getTextArea());
			}
		}
		if (message instanceof EditPaneUpdate) {
			EditPaneUpdate epu = (EditPaneUpdate) message;
			if (epu.getWhat().equals(EditPaneUpdate.BUFFER_CHANGED)) {
				updatePosition(epu.getEditPane().getTextArea());
			}
		}
	} //}}}
	
	//{{{ drawGuide()
	public void drawGuide(Graphics2D gfx, ColumnRuler ruler) {
		int hScroll = ruler.getTextArea().getHorizontalOffset();
		double x = getPositionOn(ruler) * ruler.getCharWidth() + hScroll;
		int screenLine = ruler.getTextArea().getScreenLineOfOffset(ruler.getTextArea().getCaretPosition());
		double y = (screenLine) * ruler.getLineHeight();
		double halfChar = ruler.getCharWidth() / 2;

		if (screenLine < 0)
			return;

		gfx.setColor(getColor());
		Line2D guide;
		// vertical portions
		guide = new Line2D.Double(x, 0, x, y - 1);
		gfx.draw(guide);
		guide.setLine(x, y + ruler.getLineHeight(), x, ruler.getTextArea().getHeight());
		gfx.draw(guide);
		// horizontal ticks
		guide.setLine(x - halfChar, y - 1, x + halfChar, y - 1);
		gfx.draw(guide);
		guide.setLine(x - halfChar, y + ruler.getLineHeight(), x + halfChar, y + ruler.getLineHeight());
		gfx.draw(guide);
	}//}}}

	//{{{ CaretListener implementation
	public void caretUpdate(CaretEvent e) {
		Object source = e.getSource();
		if (source instanceof TextArea) {
			updatePosition((TextArea)source);
		}
	}
	//}}}

	//{{{ ScrollListener implementation
	/**
	 * This method handles the caret coming in and out of view as the text area is scrolled vertically.
	 */
	public void scrolledVertically(TextArea textArea) {
		updatePosition(textArea);
	}

	public void scrolledHorizontally(TextArea textArea) { }
	//}}}

	public Color getColor() {
		return jEdit.getActiveView().getTextArea().getPainter().getCaretColor();
	}

	/**
	 *  Caret mark only follows the caret, it doesn't change it.
	 *
	 * @param  col  The new column
	 */
	public void setPositionOn(ColumnRuler ruler, int col) {
	}

}

