package columnruler;

import java.awt.*;
import java.awt.datatransfer.*;

import org.gjt.sp.jedit.*;

/**
 * Description of the Class
 *
 * @author     mace
 * @created    June 8, 2003
 * @modified   $Date: 2003-06-09 17:54:10 $ by $Author: bemace $
 * @version    $Revision: 1.1 $
 */
public class Mark implements Transferable {
	static final DataFlavor MARK_FLAVOR = new DataFlavor(Mark.class,"ColumnRuler.Mark");

	private String _name;
	private Color _color;
	private int _column = 0;
	private boolean _visible = true;

	public Mark(String name) {
		_name = name;
	}
	
	public Mark(String name, Color c) {
		this(name);
		_color = c;
	}

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
	
	public String getName() {
		return _name;
	}

	public Color getColor() {
		return _color;
	}
	
	public int getColumn() {
		return _column;
	}
	
	public boolean isVisible() {
		return _visible;
	}
	
	//{{{ Inner Classes
	
	public static class WrapMark extends Mark {
		private Buffer _buffer;
		public WrapMark(Buffer b) {
			super("Wrap Marker");
			setBuffer(b);
		}
		
		public void setColumn(int col) {
			super.setColumn(col);
			_buffer.setIntegerProperty("maxLineLen", col);		
			_buffer.propertiesChanged();
		}
		
		public void setBuffer(Buffer b) {
			_buffer = b;
			super.setColumn(b.getIntegerProperty("maxLineLen", 0));
		}
		
		public boolean isVisible() {
			return !_buffer.getStringProperty("wrap").equals("none");	
		}
	}
	
	//}}}
}

