package columnruler;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.util.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.util.*;

/**
 * Description of the Class
 *
 * @author     mace
 * @created    June 5, 2003
 * @modified   $Date: 2004-02-09 21:59:09 $ by $Author: bemace $
 * @version    $Revision: 1.13 $
 */
public class ColumnRuler extends JComponent implements EBComponent, ScrollListener, MouseListener, MouseMotionListener {
	private JEditTextArea _textArea;
	private DnDManager _dndManager;
	private ArrayList marks;
	private CaretMark caretMark;
	private WrapMark wrapMark;
	Mark tempMark = new Mark("",Color.GRAY);
	private int paint = 0;
	private LineGuides guideExtension;

	// cached metrics
	private boolean metricsExpired = true;
	private int xOffset = 0;
	private Font font;
	double lineHeight;
	double charHeight;
	double charWidth;

	public ColumnRuler(JEditTextArea textArea) {
		marks = new ArrayList();
		_textArea = textArea;
		caretMark = new CaretMark();
		wrapMark = new WrapMark(_textArea.getBuffer());
		addMark(caretMark);
		addMark(wrapMark);
		tempMark.setVisible(false);
		_dndManager = new DnDManager(this);
		guideExtension = new LineGuides();
		_textArea.getPainter().addExtension(TextAreaPainter.BACKGROUND_LAYER,guideExtension);
	}

	public void finalize() throws Throwable {
		_textArea.getPainter().removeExtension(guideExtension);
		super.finalize();
	}

	//{{{ Add/Remove Marks
	/**
	 * Adds a mark to the ruler.
	 */
	public void addMark(Mark m) {
		m.activate(this);
		marks.add(m);
	}

	public void removeMark(Mark m) {
		marks.remove(m);
		m.deactivate();
	}

	public void removeAllMarks() {
		for (int i = 0; i < marks.size(); i++) {
			Mark m = (Mark) marks.get(i);
			removeMark(m);
		}
	}
	//}}}

	//{{{ findFontMetrics()
	private void findFontMetrics(Graphics2D gfx) {
		xOffset = getXOffset();
		font = _textArea.getPainter().getFont();
		FontRenderContext context = _textArea.getPainter().getFontRenderContext();
		charWidth = font.getStringBounds("X",context).getWidth();

		TextLayout layout = new TextLayout("X",gfx.getFont(),gfx.getFontRenderContext());
		charHeight = layout.getBounds().getHeight();

		lineHeight = _textArea.getPainter().getFontMetrics().getHeight();
	}//}}}

	//{{{ paint() method
	public synchronized void paint(Graphics g) {
		//Log.log(Log.DEBUG,this,"paint #"+(paint++));
		Graphics2D gfx = (Graphics2D) g;

		//{{{ Get ready
		Line2D line = new Line2D.Double();
		_textArea.getBuffer().readLock();
		if (metricsExpired) {
			findFontMetrics(gfx);
			metricsExpired = false;
		}
		int textAreaWidth = _textArea.getWidth();
		int hScroll = _textArea.getHorizontalOffset();
		Selection selection = getCurrentSelection();
		//}}}

		Rectangle2D rect = new Rectangle2D.Double();

		//{{{ Draw background
		gfx.setColor(getBackground());
		gfx.fillRect(0, 0, getWidth(), getHeight());
		//}}}

		//{{{ Highlight selection columns
		if (selection != null) {
			jEdit.getActiveView().getStatus().setMessage((selection.getEnd()-selection.getStart())+" chars selected");
			gfx.setColor(getHighlight());
			double start = 0;
			double end = 0;
			if (selection instanceof Selection.Rect) {
				Selection.Rect rectangle = (Selection.Rect) selection;
				start = xOffset + rectangle.getStartColumn(_textArea.getBuffer()) * charWidth;
				end = xOffset + rectangle.getEndColumn(_textArea.getBuffer()) * charWidth;
			} else {
				Point selectionStart = _textArea.offsetToXY(selection.getStart());
				Point selectionEnd = _textArea.offsetToXY(selection.getEnd());
				if (selectionStart != null && selectionEnd != null) {
					start = xOffset + (int) selectionStart.getX();
					end = xOffset + (int) selectionEnd.getX();
				}
			}
			if (start <= end) {
				rect.setRect(start,0,end-start,lineHeight);
				gfx.fill(rect);
			} else {
				rect.setRect(xOffset,0,end-xOffset,lineHeight);
				gfx.fill(rect);
				rect.setRect(start,0,getWidth()-start,lineHeight);
				gfx.fill(rect);
			}

			//{{{ Draw selection size
			float labelX = 1;
			float labelY = new Float(charHeight).floatValue() - 1;
			int selectionHeight = selection.getEndLine() - selection.getStartLine() + 1;
			int selectionWidth = (int) Math.round(Math.abs(end - start) / charWidth);
			if (selectionHeight == 1 || selection instanceof Selection.Rect) {
				gfx.drawString(selectionHeight + "x" + selectionWidth, labelX, labelY);
			} else {
				gfx.drawString(selectionHeight + "x*", labelX, labelY);
			}
			//}}}
		}
		//}}}

		//{{{ Draw markers
		for (int i = 0; i < marks.size(); i++) {
			Mark m = (Mark) marks.get(i);
			mark(gfx,m);
			//Log.log(Log.DEBUG,this,"Painted "+m.getName()+" at column "+m.getColumn());
		}
		//}}}

		//{{{ Draw tab indicator
		if (caretMark.getColumn() != -1) {
			if (jEdit.getBooleanProperty("options.columnruler.nextTab")) {
				double x0 = xOffset + caretMark.getColumn() * charWidth;
				int tabSize = _textArea.getBuffer().getTabSize();
				int dist = tabSize - (caretMark.getColumn() % tabSize);
				double x1 = x0 + dist * charWidth;
				double y = lineHeight/2;
				line.setLine(x0,y,x1,y);
				gfx.setColor(Color.RED);
				gfx.draw(line);
			}
		}
		// }}}

		//{{{ Draw temp marker
		if (tempMark.isVisible()) {
			mark(gfx,tempMark.getColumn(),tempMark.getColor());
		}
		//}}}

		//{{{ Draw tick marks
		gfx.setColor(getForeground());
		Line2D tick = new Line2D.Double();
		for (int col = 0; col < (textAreaWidth - hScroll) / charWidth; col++) {
			double x = xOffset + hScroll + col * charWidth;
			tick.setLine(x,lineHeight,x,2*lineHeight/3);
			switch (col % 10) {
				case 0:
				case 5:
					tick.setLine(x,lineHeight,x,2*lineHeight/3);
					break;
				default:
					tick.setLine(x,lineHeight,x,4*lineHeight/5);
			}
			gfx.draw(tick);
		}
		//}}}

		//{{{ Draw numbers
		gfx.setColor(getForeground());
		gfx.setFont(gfx.getFont().deriveFont(Font.BOLD));
		for (int n = 0; n < (textAreaWidth - hScroll) / charWidth; n += 10) {
			float x = new Float(xOffset + (n * charWidth)- ((n + "").length()*charWidth) / 2 + 1 + hScroll).floatValue();
			float y = (float) charHeight+1;
			gfx.drawString(n + "", x, y);
		}
		//}}}

		//{{{ Draw border
		if (getBorderColor() != null) {
			gfx.setColor(getBorderColor());
			line.setLine(xOffset-4,lineHeight-1,textAreaWidth,lineHeight-1);
			gfx.draw(line);
		}
		//}}}

		_textArea.getBuffer().readUnlock();
	}//}}}

	//{{{ mark() methods
	private void mark(Graphics2D gfx, Mark m) {
		mark(gfx,m.getColumn(),m.getColor(),m.getSize());
	}

	private void mark(Graphics2D gfx, int col, Color c) {
		mark(gfx,col,c,1);
	}

	/**
	 * Draws a colored line at the given column
	 *
	 * @param gfx  Description of the Parameter
	 * @param col  Description of the Parameter
	 * @param c    Description of the Parameter
	 */
	private void mark(Graphics2D gfx, int col, Color c, int width) {
		int hScroll = _textArea.getHorizontalOffset();
		double x = xOffset + col * charWidth;
		gfx.setColor(c);
		Rectangle2D mark;
		if (width % 2 == 0) {
			mark = new Rectangle2D.Double(x + hScroll - width / 2, 0, width, lineHeight);
		} else {
			mark = new Rectangle2D.Double(x + hScroll - (width - 1) / 2, 0, width, lineHeight);
		}
		gfx.fill(mark);
	}

	//}}}

	//{{{ methods for finding data needed to paint ruler

	private int getXOffset() {
		return _textArea.getGutter().getWidth();
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
			fullUpdate();
		}
	}
	//}}}

	private void fullUpdate() {
		metricsExpired = true;
		for (int i = 0; i < marks.size(); i++) {
			Mark m = (Mark) marks.get(i);
			m.update();
		}
		repaint();
	}

	//{{{ Listeners

	//{{{ ScrollListener implementation
	public void scrolledVertically(JEditTextArea textArea) {}

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
		Mark mark = getMarkAtPoint(e.getPoint());
		if (mark != null) {
			setToolTipText(mark.getName());
		} else {
			setToolTipText(null);
		}
	}

	//}}}

	//}}}

	int getColumnAtPoint(Point p) {
		int hScroll = _textArea.getHorizontalOffset();
		int xOffset = _textArea.getGutter().getWidth();
		double x = p.getX()- xOffset - hScroll;
		return (int) Math.round(x/charWidth);
	}

	Mark getMarkAtPoint(Point p) {
		int col = getColumnAtPoint(p);
		for (int i = 0; i < marks.size(); i++) {
			Mark m = (Mark) marks.get(i);
			if (m.getColumn() == col)
				return m;
		}
		return null;
	}

	//{{{ Add/Remove Notify
	public void addNotify() {
		super.addNotify();
		EditBus.addToBus(this);
		_textArea.addScrollListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public void removeNotify() {
		super.removeNotify();
		EditBus.removeFromBus(this);
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
		if (lineHeight == 0) {
			Font font = jEdit.getFontProperty("view.font");
			return new Dimension(getWidth(), font.getSize());
		} else {
			return new Dimension(getWidth(), (int) Math.round(lineHeight));
		}
	}

	public Color getForeground() {
		return jEdit.getColorProperty(ColumnRulerPlugin.OPTION_PREFIX + "foreground", _textArea.getPainter().getForeground());
	}

	public Color getBackground() {
		String bgSrc = jEdit.getProperty("options.columnruler.background.src","textarea");
		if (bgSrc.equals("textarea")) {
			return _textArea.getPainter().getBackground();
		} else if (bgSrc.equals("gutter")) {
			return jEdit.getColorProperty("view.gutter.bgColor",Color.WHITE);
		} else {
			return jEdit.getColorProperty("options.columnruler.background.color",Color.WHITE);
		}
	}

	public Color getBorderColor() {
		String borderSrc = jEdit.getProperty("options.columnruler.border.src","none");
		if (borderSrc.equals("none")) {
			return null;
		} else if (borderSrc.equals("gutter")) {
			if (_textArea.equals(jEdit.getActiveView().getTextArea())) {
				return jEdit.getColorProperty("view.gutter.focusBorderColor",Color.YELLOW);
			} else {
				return jEdit.getColorProperty("view.gutter.noFocusBorderColor",Color.BLUE);
			}
		} else {
			return jEdit.getColorProperty("options.columnruler.border.color",Color.BLACK);
		}
	}

	public Color getHighlight() {
		return _textArea.getPainter().getSelectionColor();
	}

	public JEditTextArea getTextArea() {
		return _textArea;
	}

	public String toString() {
		return "Column Ruler";
	}

	//}}}

	//{{{ Inner classes

	//{{{ SetWrapAction
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
	}//}}}

	//{{{ LineGuides
	class LineGuides extends TextAreaExtension {
		public void paintScreenLineRange(Graphics2D gfx, int firstLine, int lastLine, int[] physicalLines, int[] start, int[] end, int y, int lineHeight) {
			JEditTextArea textArea = jEdit.getActiveView().getTextArea();
			gfx.setColor(Color.RED);
			for (int i = 0; i < marks.size(); i++) {
				Mark m = (Mark) marks.get(i);
				if (m.isGuideVisible()) {
					m.drawGuide(gfx,ColumnRuler.this);
				}
			}
		}
	}
	//}}}

	//{{{ DnDManager
	class DnDManager implements DropTargetListener, DragGestureListener {
		private ColumnRuler ruler;

		public DnDManager(ColumnRuler r) {
			ruler = r;
			DropTarget target = new DropTarget(ruler, this);
			target.setDefaultActions(DnDConstants.ACTION_COPY_OR_MOVE);
			DragSource source = DragSource.getDefaultDragSource();
			source.createDefaultDragGestureRecognizer(ruler, DnDConstants.ACTION_COPY_OR_MOVE, this);
		}

		//{{{ DropTargetListener implementation
		private boolean isDragAcceptable(DropTargetDragEvent e) {
			return e.isDataFlavorSupported(Mark.MARK_FLAVOR);
		}

		private boolean isDropAcceptable(DropTargetDropEvent e) {
			return e.isDataFlavorSupported(Mark.MARK_FLAVOR);
		}

		/**
		 * Called when an object is drug into the ruler
		 *
		 * @param e  Description of the Parameter
		 */
		public void dragEnter(DropTargetDragEvent e) {
			ruler.tempMark.setVisible(true);
			ruler.repaint();
			if (!isDragAcceptable(e)) {
				e.rejectDrag();
			}
		}

		/**
		 * Called when an object is drug out of the ruler
		 *
		 * @param e  Description of the Parameter
		 */
		public void dragExit(DropTargetEvent e) {
			ruler.tempMark.setVisible(false);
			ruler.repaint();
		}

		/**
		 * Called when an object is over the ruler
		 *
		 * @param e  Description of the Parameter
		 */
		public void dragOver(DropTargetDragEvent e) {
			ruler.tempMark.setColumn(ruler.getColumnAtPoint(e.getLocation()));
			ruler.tempMark.setVisible(true);
			ruler.repaint();
		}

		public void dropActionChanged(DropTargetDragEvent e) {
			if (!isDragAcceptable(e)) {
				e.rejectDrag();
			}
		}

		/**
		 * Called when something is dropped on the ruler
		 *
		 * @param e  Description of the Parameter
		 */
		public void drop(DropTargetDropEvent e) {
			ruler.tempMark.setVisible(false);
			if (!isDropAcceptable(e)) {
				e.rejectDrop();
			}
			e.acceptDrop(DnDConstants.ACTION_COPY);
			try {
				Mark m = (Mark) e.getTransferable().getTransferData(Mark.MARK_FLAVOR);
				if (m != null) {
					m.setColumn(ruler.getColumnAtPoint(e.getLocation()));
				}
			} catch (UnsupportedFlavorException ufe) {
			} catch (IOException ioe) {}
			e.dropComplete(true);
			ruler.repaint();
		}

		//}}}

		//{{{ DragGestureListener implementation
		public void dragGestureRecognized(DragGestureEvent e) {
			Mark m = ruler.getMarkAtPoint(e.getDragOrigin());
			if (m != null) {
				e.startDrag(null, m);
			}
		}
		//}}}

	}//}}}

	//}}}

}

