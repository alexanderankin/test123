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
 *  The ruler itself. Provides the ticks and the numbers and has marks attached
 *  to it.
 *
 * @author     mace
 * @version    $Revision: 1.21 $ $Date: 2006-02-24 04:26:00 $ by $Author: bemace $
 *      
 */
public class ColumnRuler extends JComponent implements EBComponent, ScrollListener, MouseListener, MouseMotionListener, MarkContainer {
	private JEditTextArea _textArea;
	private DnDManager _dndManager;
	private ArrayList marks;
	private CaretMark caretMark;
	private WrapMark wrapMark;
	Mark tempMark = new Mark("", Color.GRAY);
	private int paint = 0;
	LineGuides guideExtension;

	// cached metrics
	private boolean metricsExpired = true;
	private int xOffset = 0;
	private Font font;
	double lineHeight;
	double charHeight;
	double charWidth;

	/**
	 * Constructs a ColumnRuler for the given textarea and adds a TextAreaExtension for painting guides.
	 */
	public ColumnRuler(JEditTextArea textArea) {
		marks = new ArrayList();
		_textArea = textArea;
		caretMark = new CaretMark();
		wrapMark = new WrapMark(_textArea.getBuffer());
		tempMark.setVisible(false);
		_dndManager = new DnDManager(this);
		guideExtension = new LineGuides();
		_textArea.getPainter().addExtension(TextAreaPainter.WRAP_GUIDE_LAYER, guideExtension);
		loadMarks();
	}

	//{{{ loadMarks()
	void loadMarks() {
		removeAllMarks();
		addMark(caretMark);
		addMark(wrapMark);
		int i = 0;
		String name = jEdit.getProperty("options.columnruler.marks." + i + ".name");
		while (name != null) {
			Mark m = new Mark(name, "options.columnruler.marks." + i);
			addMark(m);
			i++;
			name = jEdit.getProperty("options.columnruler.marks." + i + ".name");
		}
		Log.log(Log.DEBUG, this, "Marks loaded");
		
	}//}}}

	//{{{ MarkContainer impl
	/**
	 *  Adds a mark to the ruler.  This calls the Mark's <code>activate()</code> method.
	 *
	 * @param  m  The Mark to add
	 */
	public void addMark(Mark m) {
		m.activate(this);
		marks.add(m);
		repaint();
	}

	/**
	 * Removes a mark from the ruler.  This calls the Mark's <code>deactivate()</code> method.
	 */
	public void removeMark(Mark m) {
		marks.remove(m);
		m.deactivate();
	}

	public boolean containsMark(Mark m) {
		return  marks.contains(m);
	}
	
	//}}}

	/**
	 * Deactivates and removes all marks from the ruler.
	 */
	public void removeAllMarks() {
		for (int i = 0; i < marks.size(); i++) {
			Mark m = (Mark) marks.get(i);
			removeMark(m);
		}
	}

	//{{{ findFontMetrics()
	private void findFontMetrics(Graphics2D gfx) {
		xOffset = getXOffset();
		font = _textArea.getPainter().getFont();
		FontRenderContext context = _textArea.getPainter().getFontRenderContext();
		charWidth = font.getStringBounds("X", context).getWidth();

		TextLayout layout = new TextLayout("X", gfx.getFont(), gfx.getFontRenderContext());
		charHeight = layout.getBounds().getHeight();

		lineHeight = _textArea.getPainter().getFontMetrics().getHeight();
		revalidate();
	}//}}}

	//{{{ paint() method
	public synchronized void paint(Graphics g) {
		//Log.log(Log.DEBUG,this,"paint #"+(paint++));
		Graphics2D gfx = (Graphics2D) g;
		Color foreground = determineForegroundColor();

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
		gfx.setColor(determineBackgroundColor());
		gfx.fillRect(0, 0, getWidth(), getHeight());
		//}}}

		//{{{ Highlight selection columns
		if (selection != null) {
			jEdit.getActiveView().getStatus().setMessageAndClear((selection.getEnd() - selection.getStart()) + " chars selected");
			gfx.setColor(getHighlight());
			double start = 0;
			double end = 0;
			if (selection instanceof Selection.Rect) {
				Selection.Rect rectangle = (Selection.Rect) selection;
				start = xOffset + rectangle.getStartColumn(_textArea.getBuffer()) * charWidth;
				end = xOffset + rectangle.getEndColumn(_textArea.getBuffer()) * charWidth;
			}
			else {
				Point selectionStart = _textArea.offsetToXY(selection.getStart());
				Point selectionEnd = _textArea.offsetToXY(selection.getEnd());
				if (selectionStart != null && selectionEnd != null) {
					start = xOffset + (int) selectionStart.getX();
					end = xOffset + (int) selectionEnd.getX();
				}
			}
			if (start <= end) {
				rect.setRect(start, 0, end - start, lineHeight);
				gfx.fill(rect);
			}
			else {
				rect.setRect(xOffset, 0, end - xOffset, lineHeight);
				gfx.fill(rect);
				rect.setRect(start, 0, getWidth() - start, lineHeight);
				gfx.fill(rect);
			}

			//{{{ Draw selection size
			float labelX = 1;
			float labelY = new Float(charHeight).floatValue() - 1;
			int selectionHeight = selection.getEndLine() - selection.getStartLine() + 1;
			int selectionWidth = (int) Math.round(Math.abs(end - start) / charWidth);
			if (selectionHeight == 1 || selection instanceof Selection.Rect) {
				gfx.drawString(selectionHeight + "x" + selectionWidth, labelX, labelY);
			}
			else {
				gfx.drawString(selectionHeight + "x*", labelX, labelY);
			}
			//}}}

		}//}}}

		//{{{ Draw numbers
		gfx.setColor(foreground);
		gfx.setFont(gfx.getFont().deriveFont(Font.BOLD));
		for (int n = 0; n < (textAreaWidth - hScroll) / charWidth; n += 10) {
			float x = new Float(xOffset + (n * charWidth) - ((n + "").length() * charWidth) / 2 + 1 + hScroll).floatValue();
			float y = (float) charHeight + 1;
			gfx.drawString(n + "", x, y);
		}
		//}}}

		//{{{ Draw markers
		for (int i = 0; i < marks.size(); i++) {
			Mark mark = (Mark) marks.get(i);
			if (mark.isVisible())
				mark(gfx, mark);
			//Log.log(Log.DEBUG,this,"Painted "+m.getName()+" at column "+m.getColumn());
		}
		//}}}

		//{{{ Draw temp marker
		if (tempMark.isVisible()) {
			mark(gfx, tempMark.getColumn(), tempMark.getColor());
		}
		//}}}

		//{{{ Draw tick marks
		gfx.setColor(foreground);
		Line2D tick = new Line2D.Double();
		for (int col = 0; col < (textAreaWidth - hScroll) / charWidth; col++) {
			double x = xOffset + hScroll + col * charWidth;
			tick.setLine(x, lineHeight, x, 2 * lineHeight / 3);
			switch (col % 10) {
							case 0:
							case 5:
								tick.setLine(x, lineHeight, x, 2 * lineHeight / 3);
								break;
							default:
								tick.setLine(x, lineHeight, x, 4 * lineHeight / 5);
			}
			gfx.draw(tick);
		}
		//}}}

		//{{{ Draw tab indicator
		if (caretMark.getColumn() != -1) {
			if (jEdit.getBooleanProperty("options.columnruler.nextTab",false)) {
				double x0 = xOffset + hScroll + caretMark.getColumn() * charWidth;
				int tabSize = _textArea.getBuffer().getTabSize();
				int dist = tabSize - (caretMark.getColumn() % tabSize);
				double x1 = x0 + dist * charWidth;
				double y = lineHeight / 2;
				line.setLine(x0, y, x1, y);
				gfx.setColor(Color.RED);
				gfx.draw(line);
			}
		}
		// }}}

		//{{{ Draw border
		if (determineBorderColor() != null) {
			gfx.setColor(determineBorderColor());
			line.setLine(xOffset - 4, lineHeight, textAreaWidth, lineHeight);
			gfx.draw(line);
		}
		//}}}

		_textArea.getBuffer().readUnlock();
	}//}}}

	//{{{ mark() methods
	private void mark(Graphics2D gfx, Mark m) {
		mark(gfx, m.getColumn(), m.getColor(), m.getSize());
	}

	private void mark(Graphics2D gfx, int col, Color c) {
		mark(gfx, col, c, 1);
	}

	/**
	 *  Draws a colored line at the given column
	 *
	 * @param  gfx    Graphics2D object
	 * @param  col    column to draw mark at
	 * @param  c      color to draw mark with
	 * @param  width  how thick to draw the mark
	 */
	private void mark(Graphics2D gfx, int col, Color c, int width) {
		int hScroll = _textArea.getHorizontalOffset();
		double x = xOffset + hScroll + col * charWidth;
		gfx.setColor(c);
		Rectangle2D mark;
		if (width % 2 == 0) {
			mark = new Rectangle2D.Double(x - width / 2, 0, width, lineHeight);
		}
		else {
			mark = new Rectangle2D.Double(x - (width - 1) / 2, 0, width, lineHeight);
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
		return selection[selection.length - 1];
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
			loadMarks();
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

		Color bg = determineBackgroundColor();
		if ((bg.getRed()+bg.getGreen()+bg.getBlue()) / 3 < 122) {
			tempMark.setColor(Color.GRAY.brighter());
		} else {
			tempMark.setColor(Color.GRAY.darker());
		}
		//tempMark.setColor(new Color(255 - bg.getRed(), 255 - bg.getGreen(), 255 - bg.getBlue()));

		repaint();
	}

	//{{{ Listeners

	//{{{ ScrollListener implementation
	/**
	 *  This method is public as an implementation side-effect.
	 *
	 * @param  textArea  Description of the Parameter
	 */
	public void scrolledVertically(JEditTextArea textArea) { }

	/**
	 *  This method is public as an implementation side-effect.
	 *
	 * @param  textArea  Description of the Parameter
	 */
	public void scrolledHorizontally(JEditTextArea textArea) {
		repaint();
	}
	//}}}

	//{{{ MouseListener implementation

	//{{{ mouseClicked
	/**
	 *  This method is public as an implementation side-effect.
	 *
	 * @param  e  Description of the Parameter
	 */
	public void mouseClicked(MouseEvent e) { 
	} //}}}

	//{{{ mouseEntered
	/**
	 *  This method is public as an implementation side-effect.
	 *
	 * @param  e  Description of the Parameter
	 */
	public void mouseEntered(MouseEvent e) { } //}}}

	//{{{ mouseExited
	/**
	 *  This method is public as an implementation side-effect.
	 *
	 * @param  e  Description of the Parameter
	 */
	public void mouseExited(MouseEvent e) { } //}}}

	/**
	 *  This method is public as an implementation side-effect.
	 *
	 * @param  e  Description of the Parameter
	 */
	public void mousePressed(MouseEvent e) {
		final Mark mark = (Mark) getMarkAtPoint(e.getPoint());
		if (GUIUtilities.isPopupTrigger(e)) {
			JPopupMenu p = new JPopupMenu();
			if (mark == null) {
				p.add(new SetWrapAction("No Wrap", "none"));
				p.add(new SetWrapAction("Soft Wrap", "soft"));
				p.add(new SetWrapAction("Hard Wrap", "hard"));
				p.setInvoker(this);
				p.setLocation(e.getPoint());
				p.pack();
				p.show(this, e.getX(), e.getY());
			} else {
				JPopupMenu popup = new JPopupMenu("'"+mark.getName()+"' Mark Options");
				popup.setLocation(e.getX(), e.getY());
				Action editMark = new AbstractAction("Edit Mark") {
					public void actionPerformed(ActionEvent ae) {
						MarkDialog d = new MarkDialog(mark, "Edit Mark");
						d.pack();
						d.setVisible(true);
					}
				};
				Action deleteMark = new AbstractAction("Delete mark") {
					public void actionPerformed(ActionEvent ae) {
						if (mark.isGuideVisible()) {
							mark.setGuideVisible(false);
						}
						removeMark(mark);
						ColumnRuler.this.repaint();
					}
				};
				popup.add(editMark);
				popup.add(deleteMark);
				popup.show(this, e.getX(), e.getY());
			}
		}
		
		if (e.getClickCount() == 2) {
			if (mark == null) {
				MarkDialog d = new MarkDialog(this, getColumnAtPoint(e.getPoint()));
				d.pack();
				d.setVisible(true);
			} else {
				mark.setGuideVisible(!mark.isGuideVisible());
			}
		}
		
	}

	//{{{ mouseReleased
	/**
	 *  This method is public as an implementation side-effect.
	 *
	 * @param  e  Description of the Parameter
	 */
	public void mouseReleased(MouseEvent e) { } //}}}

	//}}}

	//{{{ MouseMotionListener implementation

	/**
	 *  This method is public as an implementation side-effect.
	 *
	 * @param  e  Description of the Parameter
	 */
	public void mouseDragged(MouseEvent e) { }

	/**
	 *  This method is public as an implementation side-effect.
	 *
	 * @param  e  Description of the Parameter
	 */
	public void mouseMoved(MouseEvent e) {
		Mark mark = getMarkAtPoint(e.getPoint());
		if (mark != null && mark.isVisible()) {
			setToolTipText(mark.getName() + " mark");
		}
		else {
			setToolTipText(null);
		}
	}

	//}}}

	//}}}

	int getColumnAtPoint(Point p) {
		int hScroll = _textArea.getHorizontalOffset();
		int xOffset = _textArea.getGutter().getWidth();
		double x = p.getX() - xOffset - hScroll;
		return (int) Math.round(x / charWidth);
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
	/**
	 * Over-ridden to set up listeners.
	 */
	public void addNotify() {
		super.addNotify();
		EditBus.addToBus(this);
		_textArea.addScrollListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	/**
	 * Over-ridden to remove listeners.
	 */
	public void removeNotify() {
		super.removeNotify();
		EditBus.removeFromBus(this);
		_textArea.removeScrollListener(this);
		removeMouseListener(this);
		removeMouseMotionListener(this);
		_textArea.getPainter().removeExtension(guideExtension);
	}
	//}}}

	//{{{ Accessors

	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	public Dimension getPreferredSize() {
		int height = (int) Math.round(lineHeight);
		if (height == 0) {
			height = _textArea.getFont().getSize();
		}
		if (!jEdit.getProperty("options.columnruler.border.src", "none").equals("none"))
			height += 1;
		//Log.log(Log.DEBUG,this,"ruler height: "+height);
		return new Dimension(getWidth(), height);
	}

	//{{{ determineForegroundColor()
	/**
	 * Retrieves the user's desired foreground color.
	 */
	protected Color determineForegroundColor() {
		return jEdit.getColorProperty(ColumnRulerPlugin.OPTION_PREFIX + "foreground", _textArea.getPainter().getForeground());
	} //}}}

	//{{{ determineBackgroundColor()
	/**
	 * Retrieves the user's desired background color.
	 */
	protected Color determineBackgroundColor() {
		String bgSrc = jEdit.getProperty("options.columnruler.background.src", "textarea");
		if (bgSrc.equals("textarea")) {
			return _textArea.getPainter().getBackground();
		}
		else if (bgSrc.equals("gutter")) {
			return jEdit.getColorProperty("view.gutter.bgColor", Color.WHITE);
		}
		else {
			return jEdit.getColorProperty("options.columnruler.background.color", Color.WHITE);
		}
	} //}}}

	//{{{ determineBorderColor()
	/**
	 * Figures out what color the ruler's border should be.
	 */
	protected Color determineBorderColor() {
		String borderSrc = jEdit.getProperty("options.columnruler.border.src", "none");
		if (borderSrc.equals("none")) {
			return null;
		} else if (borderSrc.equals("gutter")) {
			if (_textArea.equals(jEdit.getActiveView().getTextArea())) {
				return jEdit.getColorProperty("view.gutter.focusBorderColor", Color.YELLOW);
			} else {
				return jEdit.getColorProperty("view.gutter.noFocusBorderColor", Color.BLUE);
			}
		} else {
			return jEdit.getColorProperty("options.columnruler.border.color", Color.BLACK);
		}
	} //}}}

	/**
	 * Gets the background color for selected text.
	 */
	public Color getHighlight() {
		return _textArea.getPainter().getSelectionColor();
	}

	/**
	 * Gets the JEditTextArea this ruler is associated with.
	 */
	public JEditTextArea getTextArea() {
		return _textArea;
	}

	public String toString() {
		return "Column Ruler";
	}

	//}}}

	//{{{ Inner classes

	//{{{ SetWrapAction
	/**
	 *  An action for setting the buffer's wrap mode.
	 *
	 * @author     Brad Mace
	 * @version    $Revision: 1.21 $ $Date: 2006-02-24 04:26:00 $
	 */
	class SetWrapAction extends AbstractAction {
		private String _mode;

		public SetWrapAction(String name, String mode) {
			super(name);
			_mode = mode;
		}

		public void actionPerformed(ActionEvent e) {
			_textArea.getBuffer().setStringProperty("wrap", _mode);
			_textArea.propertiesChanged();
			ColumnRuler.this.repaint();
		}
	}//}}}

	//{{{ LineGuides
	/**
	 *  Painter for line guides of this ruler's marks.
	 *
	 * @author     Brad Mace
	 * @version    $Revision: 1.21 $ $Date: 2006-02-24 04:26:00 $
	 */
	class LineGuides extends TextAreaExtension {
		public void paintScreenLineRange(Graphics2D gfx, int firstLine, int lastLine, int[] physicalLines, int[] start, int[] end, int y, int lineHeight) {
			JEditTextArea textArea = jEdit.getActiveView().getTextArea();
			for (int i = 0; i < marks.size(); i++) {
				Mark m = (Mark) marks.get(i);
				if (m.isGuideVisible()) {
					m.drawGuide(gfx, ColumnRuler.this);
				}
			}
		}

		public String getToolTipText(int x, int y) {
			Mark mark = (Mark) getMarkAtPoint(new Point(x, y));
			if (mark != null && mark.isGuideVisible())
				return mark.getName() + " guide";
			else
				return null;
		}
	}
	//}}}

	//{{{ DnDManager
	/**
	 *  Allows marks to be dragged along the ruler.
	 *
	 * @author     Brad Mace
	 * @version    $Revision: 1.21 $ $Date: 2006-02-24 04:26:00 $
	 */
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
		 *  Called when an object is drug into the ruler
		 *
		 * @param  e  Description of the Parameter
		 */
		public void dragEnter(DropTargetDragEvent e) {
			ruler.tempMark.setVisible(true);
			ruler.repaint();
			if (!isDragAcceptable(e)) {
				e.rejectDrag();
			}
		}

		/**
		 *  Called when an object is drug out of the ruler
		 *
		 * @param  e  Description of the Parameter
		 */
		public void dragExit(DropTargetEvent e) {
			ruler.tempMark.setVisible(false);
			ruler.repaint();
		}

		/**
		 *  Called when an object is over the ruler
		 *
		 * @param  e  Description of the Parameter
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
		 *  Called when something is dropped on the ruler
		 *
		 * @param  e  Description of the Parameter
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

