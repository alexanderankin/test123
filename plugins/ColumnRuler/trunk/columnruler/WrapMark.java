package columnruler;

import java.awt.*;
import java.awt.geom.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;

/**
 *  Mark which follows the wrap column of the current buffer. Dragging the wrap
 *  mark will set the buffer's line length. The wrap mark paints its guide with
 *  a dashed line.
 *
 * @author     mace
 * @version    $Revision: 1.9 $ modified $Date: 2004-02-27 20:00:20 $ by
 *      $Author: bemace $
 */
public class WrapMark extends Mark implements EBComponent {
	private ColumnRuler ruler;
	private Buffer _buffer;

	public WrapMark(Buffer b) {
		super("Wrap", "options.columnruler.marks.wrap");
		setBuffer(b);
		setSize(3);
	}

	public void activate(ColumnRuler ruler) {
		this.ruler = ruler;
		EditBus.addToBus(this);
	}

	public void deactivate() {
		EditBus.removeFromBus(this);
	}

	public void handleMessage(EBMessage msg) {
		if (msg instanceof PropertiesChanged) {
			_column = _buffer.getIntegerProperty("maxLineLen", 0);
			ruler.repaint();
		}
		if (msg instanceof EditPaneUpdate) {
			EditPaneUpdate epu = (EditPaneUpdate) msg;
			if (epu.getWhat().equals(epu.BUFFER_CHANGED) || epu.getWhat().equals(epu.CREATED)) {
				update();
				ruler.repaint();
			}
		}
	}

	public void update() {
		setBuffer(ruler.getTextArea().getBuffer());
	}

	/**
	 * Draws the wrap guide as a dashed line.
	 */
	public void drawGuide(Graphics2D gfx, ColumnRuler ruler) {
		gfx.setColor(getColor());
		int hScroll = ruler.getTextArea().getHorizontalOffset();
		double x = getColumn() * ruler.charWidth + hScroll;
		Line2D guide = new Line2D.Double();
		double dashLength = 2 * ruler.lineHeight / 3;
		double dashSpacing = 2 * ruler.lineHeight / 3;
		double yOffset = ruler.getTextArea().getPhysicalLineOfScreenLine(0)*ruler.lineHeight % (dashLength+dashSpacing);
		if (yOffset > dashSpacing) {
			guide.setLine(x,0,x,yOffset-dashSpacing);
			gfx.draw(guide);
		}
		for (double y = yOffset; y < ruler.getTextArea().getHeight(); y += dashLength +dashSpacing) {
			guide.setLine(x, y, x, y + dashLength);
			gfx.draw(guide);
		}
	}

	//{{{ Accessors and Mutators
	public void setColumn(int col) {
		super.setColumn(col);
		_buffer.setIntegerProperty("maxLineLen", col);
		_buffer.propertiesChanged();
	}

	public void setBuffer(Buffer b) {
		_buffer = b;
		_column = _buffer.getIntegerProperty("maxLineLen", 0);
	}

	public void setGuideVisible(boolean b) {
		super.setGuideVisible(b);
		if (b) {
			jEdit.setBooleanProperty("view.wrapGuide", false);
		}
	}

	public Color getColor() {
		return ruler.getTextArea().getPainter().getWrapGuideColor();
	}

	public boolean isVisible() {
		if (_buffer.getStringProperty("wrap").equals("none"))
			return false;

		return super.isVisible();
	}

	public boolean isGuideVisible() {
		if (getColumn() == 0)
			return false;
		return super.isGuideVisible();
	}

	//}}}

}

