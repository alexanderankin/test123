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
 * @version    $Revision: 1.1 $ $Date: 2006-03-17 16:27:52 $
 */
public class CaretMark extends DynamicMark implements CaretListener, ScrollListener {

	private List<EditPane> monitoredPanes;
	
	public CaretMark() {
		super("Caret", "options.columnruler.marks.caret");
		monitoredPanes = new ArrayList<EditPane>();
	}

	public void activate(EditPane editPane) {
		Log.log(Log.DEBUG, this, "Activated");
		monitoredPanes.add(editPane);
		editPane.getTextArea().addCaretListener(this);
		editPane.getTextArea().addScrollListener(this);
	}

	public void deactivate(EditPane editPane) {
		Log.log(Log.DEBUG, this, "Deactivated");
		editPane.getTextArea().removeCaretListener(this);
		editPane.getTextArea().removeScrollListener(this);
		monitoredPanes.remove(editPane);
	}
	
	public void shutdown() {
		for (EditPane editPane : monitoredPanes) {
			deactivate(editPane);
		}
	}
	
	//{{{ handleMessage
	public void handleMessage(EBMessage message) {
		super.handleMessage(message);
		if (message instanceof ViewUpdate) {
			ViewUpdate vu = (ViewUpdate) message;
			if (vu.getWhat().equals(ViewUpdate.CREATED)) {
				for (EditPane editPane : vu.getView().getEditPanes()) {
					activate(editPane);
				}
			}
			if (vu.getWhat().equals(ViewUpdate.CLOSED)) {
				for (EditPane editPane : vu.getView().getEditPanes()) {
					deactivate(editPane);
				}
			}
			if (vu.getWhat().equals(ViewUpdate.EDIT_PANE_CHANGED)) {
				caretUpdate(null);
			}
		}
		if (message instanceof EditPaneUpdate) {
			EditPaneUpdate epu = (EditPaneUpdate) message;
			if (epu.getWhat().equals(EditPaneUpdate.CREATED)) {
				activate(epu.getEditPane());
			}
			if (epu.getWhat().equals(EditPaneUpdate.BUFFER_CHANGED)) {
				caretUpdate(null);
			}
			if (epu.getWhat().equals(EditPaneUpdate.DESTROYED)) {
				deactivate(epu.getEditPane());
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
		//Log.log(Log.DEBUG, this, "Caret Moved");
		JEditTextArea textArea = jEdit.getActiveView().getTextArea();
		ColumnRuler ruler = ColumnRulerPlugin.getColumnRulerForTextArea(textArea);
		if (ruler == null) {
			Log.log(Log.WARNING, this, "Ruler not found");
			return;
		}
		positionMap.put(ruler, findCaretColumn(textArea));
		//setPositionOn(ruler, findCaretColumn(textArea));
		ruler.repaint();
		if (isGuideVisible()) {
			ruler.getTextArea().repaint();
		}
	}
	//}}}

	//{{{ ScrollListener implementation
	/**
	 * This method handles the caret coming in and out of view as the text area is scrolled vertically.
	 */
	public void scrolledVertically(JEditTextArea textArea) {
		if (!isVisible()) {
			return;
		}
		
		int caretCol = findCaretColumn(textArea);
		if (caretCol >= 0) {
			ColumnRuler ruler = ColumnRulerPlugin.getColumnRulerForTextArea(textArea);
			positionMap.put(ruler, findCaretColumn(textArea));
			//setPositionOn(ruler, caretCol);
			ruler.repaint();
			if (isGuideVisible()) {
				textArea.repaint();
			}
		}
	}

	public void scrolledHorizontally(JEditTextArea textArea) { }
	//}}}

	//{{{ findCaretColumn()
	private int findCaretColumn(JEditTextArea textArea) {
		ColumnRuler ruler = ColumnRulerPlugin.getColumnRulerForTextArea(textArea);
		try {
			Point caret = textArea.offsetToXY(textArea.getCaretPosition());
			int hScroll = textArea.getHorizontalOffset();
			if (caret != null) {
				double caretX = (int) caret.getX();
				return (int) Math.round((caretX - hScroll) / ruler.getCharWidth());
			} else {
				return -1;
			}
		} catch (Exception e) {
			return -1;
		}
	}//}}}

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

