package org.jedit.plugins.columnruler.marks;

import java.awt.*;
import java.awt.geom.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.buffer.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;

import org.jedit.plugins.columnruler.*;

/**
 *  Mark which follows the wrap column of the current buffer. Dragging the wrap
 *  mark will set the buffer's line length. The wrap mark paints its guide with
 *  a dashed line.
 *
 * @author     mace
 * @version    $Revision: 1.2 $ modified $Date: 2006-03-27 16:21:28 $ by
 *      $Author: bemace $
 */
public class WrapMark extends DynamicMark implements EBComponent {
	private JEditBuffer _buffer;

	public WrapMark() {
		super("Wrap", "options.columnruler.marks.wrap");
		setSize(3);
	}

	public void handleMessage(EBMessage msg) {
		super.handleMessage(msg);
		
		if (msg instanceof PropertiesChanged) {
			Buffer buffer;
			if (msg.getSource() instanceof Buffer) {
				if (msg.getSource() == null) {
					Log.log(Log.DEBUG, this, "Null msg source");
				}
				updateRulersViewing((Buffer) msg.getSource());
			}
		}
		
		if (msg instanceof EditPaneUpdate) {
			EditPaneUpdate epu = (EditPaneUpdate) msg;
			if (epu.getWhat().equals(epu.BUFFER_CHANGED) || epu.getWhat().equals(epu.CREATED)) {
				ColumnRuler ruler = ColumnRulerPlugin.getColumnRulerForTextArea(epu.getEditPane().getTextArea());
				positionMap.put(ruler, epu.getEditPane().getBuffer().getIntegerProperty("maxLineLen", 0));
				if (isVisible()) {
					ruler.repaint();
				}
				if (isGuideVisible()) {
					ruler.getTextArea().repaint();
				}
			}
		}
		
		if (msg instanceof BufferUpdate) {
			BufferUpdate bu = (BufferUpdate) msg;
			if (bu.getWhat().equals(bu.CREATED) || bu.getWhat().equals(bu.LOADED) || bu.getWhat().equals(bu.PROPERTIES_CHANGED)) {
				updateRulersViewing(bu.getBuffer());
			}
		}
		
	}
	
	private void updateRulersViewing(JEditBuffer buffer) {
		for (View view : jEdit.getViews()) {
			for (EditPane editPane : view.getEditPanes()) {
				if (editPane.getBuffer().equals(buffer)) {
					ColumnRuler ruler = ColumnRulerPlugin.getColumnRulerForTextArea(editPane.getTextArea());
					if (ruler == null) {
						Log.log(Log.DEBUG, this, "ruler not found");
						return;
					}
					positionMap.put(ruler, buffer.getIntegerProperty("maxLineLen", 0));
					ruler.repaint();
					
					if (isGuideVisible()) {
						editPane.getTextArea().repaint();
					}
				}
			}
		}
	}

	// {{{ drawGuide
	/**
	 * Draws the wrap guide as a dashed line.
	 */
	public void drawGuide(Graphics2D gfx, ColumnRuler ruler) {
		gfx.setColor(getColor());
		int hScroll = ruler.getTextArea().getHorizontalOffset();
		double x = getPositionOn(ruler) * ruler.getCharWidth() + hScroll;
		Line2D guide = new Line2D.Double();
		double dashLength = 2 * ruler.getLineHeight() / 3;
		double dashSpacing = 2 * ruler.getLineHeight() / 3;
		double yOffset = ruler.getTextArea().getPhysicalLineOfScreenLine(0)*ruler.getLineHeight() % (dashLength+dashSpacing);
		if (yOffset > dashSpacing) {
			guide.setLine(x,0,x,yOffset-dashSpacing);
			gfx.draw(guide);
		}
		for (double y = yOffset; y < ruler.getTextArea().getHeight(); y += dashLength +dashSpacing) {
			guide.setLine(x, y, x, y + dashLength);
			gfx.draw(guide);
		}
	} //}}}

	//{{{ Accessors and Mutators
	
	public void setPositionOn(ColumnRuler ruler, int col) {
		super.setPositionOn(ruler, col);
		JEditBuffer buffer = ruler.getTextArea().getBuffer();
		buffer.setIntegerProperty("maxLineLen", col);
		ruler.getTextArea().propertiesChanged();
		if (buffer instanceof Buffer) {
			((Buffer) buffer).propertiesChanged();
		}
	}

	public void setGuideVisible(boolean b) {
		super.setGuideVisible(b);
		if (b) {
			jEdit.setBooleanProperty("view.wrapGuide", false);
		}
	}
	
	public Color getColor() {
		return jEdit.getActiveView().getTextArea().getPainter().getWrapGuideColor();
	}

	public boolean isVisible() {
		JEditBuffer buffer = jEdit.getActiveView().getTextArea().getBuffer();
		if (buffer.getStringProperty("wrap").equals("none"))
			return false;

		return super.isVisible();
	}

	public boolean isGuideVisible() {
		return super.isGuideVisible();
	}

	//}}}

}

