package columnruler;

import java.awt.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;

/**
 * Description of the Class
 *
 * @author    mace
 * @version   $Revision: 1.5 $ modified $Date: 2004-02-11 08:32:58 $ by $Author: bemace $
 */
public class WrapMark extends Mark implements EBComponent {
	private ColumnRuler ruler;
	private Buffer _buffer;

	public WrapMark(Buffer b) {
		super("Wrap","options.columnruler.marks.wrap");
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
			jEdit.setBooleanProperty("view.wrapGuide",false);
		}
	}
	
	public Color getColor() {
		return ruler.getTextArea().getPainter().getWrapGuideColor();
	}

}

