package columnruler;

import java.awt.*;
import java.awt.datatransfer.*;

import org.gjt.sp.jedit.*;

/**
 * Description of the Class
 *
 * @author     mace
 * @created    June 8, 2003
 * @modified   $Date: 2004-02-08 20:06:53 $ by $Author: bemace $
 * @version    $Revision: 1.4 $
 */
public class Mark implements Transferable {
	static final DataFlavor MARK_FLAVOR = new DataFlavor(Mark.class,"ColumnRuler.Mark");

	private String _name;
	private Color _color;
	protected int _column = 0;
	private int size = 1;
	private boolean _visible = true;
	protected boolean guide = false;

	public Mark(String name) {
		_name = name;
	}

	public Mark(String name, Color c) {
		this(name);
		_color = c;
	}

	public void activate(ColumnRuler ruler) {}

	public void deactivate() {}

	public Object getTransferData(DataFlavor flavor) {
		return this;
	}

	public DataFlavor[] getTransferDataFlavors() {
		DataFlavor[] df = new DataFlavor[1];
		df[0] = MARK_FLAVOR;
		return df;
	}

	public boolean isDataFlavorSupported(DataFlavor f) {
		if (f.getRepresentationClass().equals(Mark.class)) {
			return true;
		}
		return false;
	}

	public void setColumn(int col) {
		_column = col;
	}

	public void setVisible(boolean b) {
		_visible = b;
	}

	public void setSize(int s) {
		size = s;
	}

	public void setGuideVisible(boolean b) {
		guide = b;
	}

	public String getName() {
		return _name;
	}

	public Color getColor() {
		return _color;
	}

	public int getColumn() {
		return _column;
	}

	public int getSize() {
		return size;
	}

	public boolean isVisible() {
		return _visible;
	}

	public boolean isGuideVisible() {
		return guide;
	}

}

