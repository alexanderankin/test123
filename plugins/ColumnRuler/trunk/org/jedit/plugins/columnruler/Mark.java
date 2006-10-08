package org.jedit.plugins.columnruler;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.datatransfer.*;
import java.awt.geom.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.textarea.*;

/**
 *  A mark on the ruler. Marks can have their color, size, and name configured,
 *  and can have a guide associated with them. Marks can be dragged along the
 *  ruler to move them, and can also be made invisible. 
 *
 *  <p>Static marks can be added through the LineGuidesOptions, and additional
 *  dynamic marks can be added by other plugins.</p>
 *
 * @author     mace
 * @version    $Revision: 1.5 $ $Date: 2006-10-08 08:21:53 $ by $Author: k_satoda $
 *      
 */
public abstract class Mark implements Cloneable, Transferable {
	final static DataFlavor MARK_FLAVOR = new DataFlavor(Mark.class, "ColumnRuler.Mark");

	private String _name;
	private Color _color;
	private int size = 1;
	private boolean _visible = true;
	protected boolean guide = false;
	protected String property = null;

	public Mark() {
	}
	
	public Mark(String name) {
		_name = name;
	}

	public Mark(String name, Color c) {
		this(name);
		_color = c;
	}

	//{{{ drawMark
	public void drawMark(Graphics2D gfx, ColumnRuler ruler) {
		if (getPositionOn(ruler) < 0) {
			return;
		}
		TextArea textArea = ruler.getTextArea();
		int hScroll = textArea.getHorizontalOffset();
		int xOffset = textArea.getGutter().getWidth();
		double x = xOffset + hScroll + getPositionOn(ruler) * ruler.getCharWidth();
		gfx.setColor(getColor());
		Rectangle2D mark;
		if (getSize() % 2 == 0) {
			mark = new Rectangle2D.Double(x - getSize() / 2, 0, getSize(), ruler.getLineHeight());
		}
		else {
			mark = new Rectangle2D.Double(x - (getSize() - 1) / 2, 0, getSize(), ruler.getLineHeight());
		}
		gfx.fill(mark);
	} //}}}
	
	//{{{ drawGuide
	/**
	 *  Draws this mark's guide in the TextArea.  Subclasses can override this to draw fancier guides.
	 *
	 * @param  gfx    Description of the Parameter
	 * @param  ruler  Description of the Parameter
	 */
	public void drawGuide(Graphics2D gfx, ColumnRuler ruler) {
		int hScroll = ruler.getTextArea().getHorizontalOffset();
		double x = getPositionOn(ruler) * ruler.getCharWidth() + hScroll;
		Line2D guide;
		guide = new Line2D.Double(x, 0, x, ruler.getTextArea().getHeight());
		gfx.setColor(getColor());
		gfx.draw(guide);
	} //}}}

	//{{{ Transferable impl
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
	//}}}

	//{{{ Accessors + Mutators

	/**
	 *  Shows/Hides this mark.
	 *
	 * @param  b  The new visible
	 */
	public void setVisible(boolean b) {
		_visible = b;
	}
	
	public final void setName(String name) {
		this._name = name;
	}

	/**
	 *  Sets the width of the mark.
	 *
	 * @param  s  The new size
	 */
	public void setSize(int s) {
		size = s;
	}

	public void setColor(Color c) {
		_color = c;
	}

	//{{{ setGuideVisible()
	/**
	 *  Turns guide drawing of/off.
	 *
	 * @param  b  The new guideVisible
	 */
	public void setGuideVisible(boolean b) {
		guide = b;
		if (property != null) {
			jEdit.setBooleanProperty(property + ".guide", b);
		}
		if (jEdit.getActiveView() != null) {
			jEdit.getActiveView().getTextArea().repaint();
		}
	}//}}}

	//{{{ getName()
	/**
	 *  Gets the name of this marker, used in tooltips.
	 *
	 * @return    The name
	 */
	public final String getName() {
		return _name;
	} //}}}

	//{{{ getColor()
	/**
	 *  Gets the color to use for the mark and its guide.
	 *
	 * @return    The color
	 */
	public Color getColor() {
		return _color;
	} //}}}

	public abstract void setPositionOn(ColumnRuler ruler, int column);
	
	public abstract int getPositionOn(ColumnRuler ruler);
	
	//{{{ getSize()
	/**
	 *  Gets the width of this mark.
	 *
	 * @return    The size
	 */
	public int getSize() {
		return size;
	} //}}}

	//{{{ isVisible()
	/**
	 *  Returns true if the mark is currently visible.
	 *
	 * @return    The visible
	 */
	public boolean isVisible() {
		return _visible;
	} //}}}

	//{{{ isGuideVisible()
	/**
	 *  Returns true if the guide for this mark is visible.
	 *
	 * @return    The guideVisible
	 */
	public boolean isGuideVisible() {
		if (!isVisible())
			return false;

		return guide;
	} //}}}
	
	//}}}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
}

