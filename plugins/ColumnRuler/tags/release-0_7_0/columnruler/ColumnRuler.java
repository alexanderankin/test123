package columnruler;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.util.*;

/**
 * Description of the Class
 *
 * @author     mace
 * @created    June 5, 2003
 * @modified   $Date: 2003-06-23 21:10:58 $ by $Author: bemace $
 * @version    $Revision: 1.5 $
 */
public class ColumnRuler extends JComponent implements EBComponent, CaretListener, ScrollListener, MouseListener, MouseMotionListener {
	private JEditTextArea _textArea;
	private DnDManager _dndManager;
	private int caretColumn = -1;
	private Mark.WrapMark wrapMarker;
	private Mark tempMarker = new Mark("",Color.GRAY);
	
	public ColumnRuler(JEditTextArea textArea) {
		_textArea = textArea;
		wrapMarker = new Mark.WrapMark(_textArea.getBuffer());
		wrapMarker.setColumn(getWrapColumn());
		tempMarker.setVisible(false);
		caretColumn = getCaretColumn();
		_dndManager = new DnDManager(this);
	}
	//{{{ paint() method
	public void paint(Graphics gfx) {
		
		//{{{ Get ready
		_textArea.getBuffer().readLock();
		int textAreaWidth = _textArea.getWidth();
		int lineHeight = getLineHeight();
		FontMetrics fm = getFontMetrics();
		int charHeight = getCharHeight();
		int charWidth = getCharWidth();
		int xOffset = getXOffset();
		int hScroll = _textArea.getHorizontalOffset();
		Selection selection = getCurrentSelection();
		//}}}

		//{{{ Draw background
		gfx.setColor(getBackground());
		gfx.fillRect(0, 0, getWidth(), getHeight());
		//}}}

		//{{{ Highlight selection columns
		if (selection != null) {
			gfx.setColor(getHighlight());
			int start = 0;
			int end = 0;
			if (selection instanceof Selection.Rect) {
				Selection.Rect rect = (Selection.Rect) selection;
				start = xOffset + rect.getStartColumn(_textArea.getBuffer()) * charWidth;
				end = xOffset + rect.getEndColumn(_textArea.getBuffer()) * charWidth;
			} else {
				Point selectionStart = _textArea.offsetToXY(selection.getStart());
				Point selectionEnd = _textArea.offsetToXY(selection.getEnd());
				if (selectionStart != null && selectionEnd != null) {
					start = xOffset + (int) selectionStart.getX();
					end = xOffset + (int) selectionEnd.getX();
				}
			}
			if (start <= end) {
				gfx.fillRect(start, 0, end - start, lineHeight);
			} else {
				gfx.fillRect(xOffset, 0, end - xOffset, lineHeight);
				gfx.fillRect(start, 0, getWidth() - start, lineHeight);
			}

			//{{{ Draw selection size
			int labelX = 1;
			int labelY = lineHeight - 1;
			int selectionHeight = selection.getEndLine() - selection.getStartLine() + 1;
			int selectionWidth = Math.abs(end - start) / charWidth;
			if (selectionHeight == 1 || selection instanceof Selection.Rect) {
				gfx.drawString(selectionHeight + "x" + selectionWidth, labelX, labelY);
			} else {
				gfx.drawString(selectionHeight + "x*", labelX, labelY);
			}
			//}}}
		}
		//}}}

		//{{{ Draw wrap marker
		if (wrapMarker.isVisible()) {
			mark(gfx, wrapMarker.getColumn(), _textArea.getPainter().getWrapGuideColor(),3);
		}
		//}}}

		//{{{ Draw caret indicator
		if (caretColumn != -1) {
			mark(gfx,caretColumn,getCaretColor());
		}
		// }}}

		//{{{ Draw temp marker
		if (tempMarker.isVisible()) {
			mark(gfx,tempMarker.getColumn(),tempMarker.getColor());
		}
		//}}}
		
		//{{{ Draw tick marks
		gfx.setColor(getForeground());
		for (int col = 0; col < (textAreaWidth - hScroll) / charWidth; col++) {
			int x = xOffset + hScroll + col * charWidth;
			switch (col % 10) {
				case 0:
				case 5:
					gfx.drawLine(x, lineHeight, x, 2 * lineHeight / 3);
					break;
				default:
					gfx.drawLine(x, lineHeight, x, 4 * lineHeight / 5);
			}
		}
		//}}}

		//{{{ Draw numbers
		gfx.setColor(getForeground());
		gfx.setFont(gfx.getFont().deriveFont(Font.BOLD));
		for (int n = 0; n < (textAreaWidth - hScroll) / charWidth; n += 10) {
			gfx.drawString(n + "", xOffset + (n * charWidth) - (fm.stringWidth(n + "")) / 2 + 1 + hScroll, charHeight - 2);
		}
		//}}}

		_textArea.getBuffer().readUnlock();
	}//}}}

	//{{{ Painting helpers
	private void mark(Graphics gfx, int col, Color c) {
		mark(gfx,col,c,1);	
	}
	/**
	 * Draws a colored line at the given column
	 *
	 * @param gfx  Description of the Parameter
	 * @param col  Description of the Parameter
	 * @param c    Description of the Parameter
	 */
	private void mark(Graphics gfx, int col, Color c, int width) {
		int xOffset = getXOffset();
		int hScroll = _textArea.getHorizontalOffset();
		int x = xOffset + col * getCharWidth();
		gfx.setColor(c);
		gfx.fillRect(x + hScroll - (width - 1) / 2, 0, width, getLineHeight());
	}
	
	//}}}

	//{{{ methods for finding data needed to paint ruler

	private int getCaretColumn() {
		Point caret = _textArea.offsetToXY(_textArea.getCaretPosition());
		int hScroll = _textArea.getHorizontalOffset();
		if (caret != null) {
			int caretX = (int) caret.getX();
			int charWidth = getCharWidth();
			return (caretX - hScroll) / charWidth;		
		} else {
			return -1;
		}
	}

	private Color getCaretColor() {
		return _textArea.getPainter().getCaretColor();
	}
	
	private FontMetrics getFontMetrics() {
		return _textArea.getPainter().getFontMetrics();
	}

	private int getCharHeight() {
		return _textArea.getFont().getSize();
	}

	private int getCharWidth() {
		return getFontMetrics().charWidth('X');
	}

	private int getXOffset() {
		return _textArea.getGutter().getWidth();
	}

	private int getLineHeight() {
		return _textArea.getPainter().getFontMetrics().getHeight();
	}
	
	private String getWrapMode() {
		return _textArea.getBuffer().getStringProperty("wrap");
	}
	
	private int getWrapColumn() {
		return _textArea.getBuffer().getIntegerProperty("maxLineLen", 0);		
	}

	private Selection getCurrentSelection() {
		Selection[] selection = _textArea.getSelection();
		if (selection.length == 0) {
			return null;
		}
		return selection[selection.length-1];
	}
	//}}}

	//{{{ EBComponent.handleMessage() method
	public void handleMessage(EBMessage m) {
		//Log.log(Log.DEBUG,this,m);
		if (m instanceof EditPaneUpdate) {
			EditPaneUpdate epu = (EditPaneUpdate) m;
			if (epu.getWhat().equals(epu.BUFFER_CHANGED) || epu.getWhat().equals(epu.CREATED)) {
				fullUpdate();
			}
		}
		
		if (m instanceof BufferUpdate) {
			BufferUpdate bu = (BufferUpdate) m;
			if (bu.getWhat().equals(bu.CREATED) || bu.getWhat().equals(bu.LOADED)) {
				fullUpdate();
			}
		}
		
		if (m instanceof PropertiesChanged) {
			caretColumn = getCaretColumn();
			wrapMarker.setColumn(getWrapColumn());
			repaint();
		}
	}
	//}}}
	
	private void fullUpdate() {
		caretColumn = getCaretColumn();
		wrapMarker.setBuffer(_textArea.getBuffer());
		wrapMarker.setColumn(getWrapColumn());
		repaint();
	}
	
	//{{{ CaretListener implementation
	public void caretUpdate(CaretEvent e) {
		caretColumn = getCaretColumn();
		repaint();
	}
	//}}}

	//{{{ ScrollListener implementation
	public void scrolledVertically(JEditTextArea textArea) { 
		if (getCaretColumn() >= 0) {
			caretColumn = getCaretColumn();
			repaint();
		}
	}

	public void scrolledHorizontally(JEditTextArea textArea) {
		repaint();
	}

	//}}}

	//{{{ MouseListener implementation

	public void mouseClicked(MouseEvent e) { }

	public void mouseEntered(MouseEvent e) { }

	public void mouseExited(MouseEvent e) { }

	public void mousePressed(MouseEvent e) {
		if (GUIUtilities.isPopupTrigger(e)) {
			JPopupMenu p = new JPopupMenu();
			p.add(new SetWrapAction("No Wrap","none"));
			p.add(new SetWrapAction("Soft Wrap","soft"));
			p.add(new SetWrapAction("Hard Wrap","hard"));
			p.setInvoker(this);
			p.setLocation(e.getPoint());
			p.pack();
			p.show(this,e.getX(),e.getY());
		}
	}

	public void mouseReleased(MouseEvent e) { }
	
	//}}}
	
	//{{{ MouseMotionListener implementation
	
	public void mouseDragged(MouseEvent e) {}

	public void mouseMoved(MouseEvent e) {
		int col = getColumnAtPoint(e.getPoint());
		final int wrapCol = getWrapColumn();
		if (col == getWrapColumn() && !getWrapMode().equals("none")) {
			setToolTipText("Wrap marker");
		} else if (col == caretColumn && col >= 0) {
			setToolTipText("Caret marker");
		} else {
			setToolTipText("");
		}
	}

	//}}}
	
	int getColumnAtPoint(Point p) {
		int hScroll = _textArea.getHorizontalOffset();
		int x = (int) p.getX();
		int charWidth = getCharWidth();
		return (-1 * getXOffset() - hScroll + x + charWidth / 2) / charWidth; 
	}
	
	Mark getMarkAtPoint(Point p) {
		int col = getColumnAtPoint(p);
		if (wrapMarker.getColumn() == col) {
			return wrapMarker;
		}
		return null;
	}
	
	//{{{ Add/Remove Notify
	public void addNotify() {
		super.addNotify();
		EditBus.addToBus(this);
		_textArea.addCaretListener(this);
		_textArea.addScrollListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	public void removeNotify() {
		super.removeNotify();
		EditBus.removeFromBus(this);
		_textArea.removeCaretListener(this);
		_textArea.removeScrollListener(this);
		removeMouseListener(this);
		removeMouseMotionListener(this);
	}
	//}}}

	//{{{ Accessors

	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	public Dimension getPreferredSize() {
		return new Dimension(getWidth(), getLineHeight());
	}

	public Color getForeground() {
		return jEdit.getColorProperty(ColumnRulerPlugin.OPTION_PREFIX + "foreground", _textArea.getPainter().getForeground());
	}

	public Color getBackground() {
		return jEdit.getColorProperty(ColumnRulerPlugin.OPTION_PREFIX + "background", _textArea.getPainter().getBackground());
	}

	public Color getHighlight() {
		return _textArea.getPainter().getSelectionColor();
	}

	public Mark getTempMarker() {
		return tempMarker;
	}
	
	public String toString() {
		return "Column Ruler";
	}

	//}}}

	//{{{ Inner classes	
	class SetWrapAction extends AbstractAction {
		private String _mode;
		public SetWrapAction(String name, String mode) {
			super(name);
			_mode = mode;
		}
		public void actionPerformed(ActionEvent e) {
			_textArea.getBuffer().setStringProperty("wrap",_mode);
			_textArea.propertiesChanged();
		}
	}
	//}}}
}

