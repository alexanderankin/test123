package org.jedit.plugins.columnruler;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.util.*;

import org.jedit.plugins.columnruler.event.*;

/**
 *  The ruler itself. Provides the ticks and the numbers and has marks attached
 *  to it.
 *
 * @author     mace
 * @version    $Revision: 1.10 $ $Date: 2006-10-11 18:18:40 $ by $Author: k_satoda $
 *      
 */
public class ColumnRuler extends JComponent implements EBComponent, ScrollListener, MouseListener, MouseMotionListener, MarkManagerListener {
	private TextArea _textArea;
	private DnDManager _dndManager;
	private StaticMark tempMark = new StaticMark("", Color.GRAY);
	private LineGuides guideExtension;
	private Action clearMarks;

	/**
	 * Constructs a ColumnRuler for the given textarea.
	 */
	public ColumnRuler(TextArea textArea) {
		_textArea = textArea;
		_dndManager = new DnDManager(this);
		tempMark.setGuideVisible(true);
		tempMark.setVisible(false);
		guideExtension = new LineGuides();
		clearMarks = new AbstractAction("Clear Marks") {
			public void actionPerformed(ActionEvent ae) {
				MarkManager.getInstance().removeAll();
			}
		};
	}

	//{{{ MarkManagerListener impl
	public void markAdded(StaticMark m) {
		marksUpdated();
		if (m.isGuideVisible()) {
			guidesUpdated();
		}
	}
	
	public void markRemoved(StaticMark m) {
		marksUpdated();
		if (m.isGuideVisible()) {
			guidesUpdated();
		}
	}
	
	public void marksUpdated() {
		repaint();
		_textArea.repaint();
	}
	
	public void guidesUpdated() {
		_textArea.repaint();
	}

	//}}}

	//{{{ paint() method
	public synchronized void paint(Graphics g) {
		//{{{ Get ready
		Graphics2D gfx = (Graphics2D) g;
		Color foreground = determineForegroundColor();
		int xOffset = 0;
		double lineHeight = getLineHeight();
		double charWidth = getCharWidth();
		double charHeight = (new TextLayout("X", gfx.getFont(), gfx.getFontRenderContext())).getBounds().getHeight();
		int textAreaWidth = _textArea.getWidth();
		int hScroll = _textArea.getHorizontalOffset();
		//}}}

		//{{{ Get information from the buffer.
		// Separation from actual painting minimizes lock duration.
		// After unlocking the buffer, operations should not touch the buffer.
		int selectedNumChars = 0;
		boolean selectionIsRect = false;
		int selectionStart = 0;
		int selectionEnd = 0;
		int selectionHeight = 0;
		int tabSize = 0;
		Point caretXY = null;
		JEditBuffer buffer = _textArea.getBuffer();
		// While the buffer is loading, getting these is not safe.
		if (!buffer.isLoading()) {
			buffer.readLock();
			try {
				Selection[] selections = _textArea.getSelection();
				if (selections != null && selections.length > 0) {
					Selection selection = selections[selections.length - 1];
					int start = selection.getStart();
					int end = selection.getEnd();
					selectedNumChars = end - start;
					selectionIsRect = selection instanceof Selection.Rect;
					if (selectionIsRect) {
						Selection.Rect rectangle = (Selection.Rect) selection;
						selectionStart = xOffset + hScroll + (int)(rectangle.getStartColumn(buffer) * charWidth);
						selectionEnd = xOffset + hScroll + (int)(rectangle.getEndColumn(buffer) * charWidth);
					} else {
						Point startXY = _textArea.offsetToXY(start);
						Point endXY = _textArea.offsetToXY(end);
						if (startXY != null && endXY != null) {
							selectionStart = xOffset + startXY.x;
							selectionEnd = xOffset + endXY.x;
						}
					}
					selectionHeight = selection.getEndLine() - selection.getStartLine() + 1;
				}
				tabSize = buffer.getTabSize();
				caretXY = _textArea.offsetToXY(_textArea.getCaretPosition());
			} finally {
				buffer.readUnlock();
			}
		}
		//}}}

		//{{{ Draw background
		gfx.setColor(determineBackgroundColor());
		gfx.fillRect(0, 0, getWidth(), getHeight());
		//}}}

		//{{{ Highlight selection columns
		if (selectedNumChars > 0) {
			jEdit.getActiveView().getStatus().setMessageAndClear(selectedNumChars + " chars selected");
			gfx.setColor(getHighlight());
			double start = selectionStart;
			double end = selectionEnd;
			Rectangle2D rect = new Rectangle2D.Double();
			if (start <= end) {
				rect.setRect(start, 0, end - start, lineHeight);
				gfx.fill(rect);
			} else {
				rect.setRect(xOffset, 0, end - xOffset, lineHeight);
				gfx.fill(rect);
				rect.setRect(start, 0, getWidth() - start, lineHeight);
				gfx.fill(rect);
			}

			//{{{ Draw selection size
			float labelX = 1;
			float labelY = new Float(charHeight).floatValue() - 1;
			int selectionWidth = (int) Math.round(Math.abs(end - start) / charWidth);
			if (selectionHeight == 1 || selectionIsRect) {
				gfx.drawString(selectionHeight + "x" + selectionWidth, labelX, labelY);
			} else {
				gfx.drawString(selectionHeight + "x*", labelX, labelY);
			}
			//}}}

		}//}}}

		//{{{ Draw numbers
		drawNumbers(gfx, foreground, xOffset, hScroll, textAreaWidth, charWidth, charHeight);
		//}}}

		//{{{ Draw markers
		for (StaticMark mark : MarkManager.getInstance().getMarks()) {
			if (mark.isVisible()) {
				mark.drawMark(gfx, this);
				//Log.log(Log.DEBUG,this,"Painted "+m.getName()+" at column "+m.getColumn());
			}
		}
		//}}}

		//{{{ Draw dynamic marks
		String[] services = ServiceManager.getServiceNames("org.jedit.plugins.columnruler.DynamicMark");
		for (String service : services) {
			DynamicMark mark = (DynamicMark) ServiceManager.getService("org.jedit.plugins.columnruler.DynamicMark", service);
			//Log.log(Log.DEBUG, this, "Painted Dynamic Mark: "+mark.getName());
			if (mark.isVisible()) {
				mark.drawMark(gfx, this);
			}
		}
		//}}}

		//{{{ Draw temp marker
		if (tempMark.isVisible()) {
			tempMark.drawMark(gfx, this);
		}
		//}}}

		//{{{ Draw tick marks
		drawTicks(gfx, foreground, xOffset, hScroll, textAreaWidth, charWidth, lineHeight);
		//}}}

		//{{{ Draw tab indicator
		if (tabSize > 0 && jEdit.getBooleanProperty("options.columnruler.nextTab", false)) {
			int caretColumn = -1;
			if (caretXY != null) {
				double caretX = (int) caretXY.getX();
				caretColumn = (int) Math.round((caretX - hScroll) / charWidth);
			}
			if (caretColumn >= 0) {
				double x0 = xOffset + hScroll + caretColumn * charWidth;
				int dist = tabSize - (caretColumn % tabSize);
				double x1 = x0 + dist * charWidth;
				double y = lineHeight / 2;
				Line2D line = new Line2D.Double();
				line.setLine(x0, y, x1, y);
				gfx.setColor(Color.RED);
				gfx.draw(line);
			}
		} // }}}

		//{{{ Draw border
		if (determineBorderColor() != null) {
			gfx.setColor(determineBorderColor());
			Line2D line = new Line2D.Double();
			line.setLine(xOffset - 4, lineHeight, textAreaWidth, lineHeight);
			gfx.draw(line);
		}
		//}}}
	}

	//{{{ Draw numbers
	private void drawNumbers(Graphics2D gfx, Color color, int xOffset, int hScroll, int textAreaWidth, double charWidth, double charHeight) {
		gfx.setColor(color);
		gfx.setFont(gfx.getFont().deriveFont(Font.BOLD));

		float x0 = (float) (xOffset + hScroll);
		float y = (float) (charHeight + 1);
		int step = 10;
		int maxDigitsLength = 10;	// assuming (column < 10000000000)
		// Draw only visible range.
		int start = (int) (Math.floor((-x0 - ((maxDigitsLength * charWidth) / 2)) / charWidth / step) * step);
		if (start < 0) {
			start = 0;
		}
		int end = (int) ((textAreaWidth - hScroll) / charWidth);
		if (jEdit.getProperty("options.columnruler.numbering", "ticks").equals("chars")) {
			x0 += (charWidth/2);
			// Do not draw "0"
			if (start == 0) {
				start = step;
			}
		}

		for (int column = start; column < end; column += step) {
			String digits = Integer.toString(column);
			float x = (float) (x0 + (column * charWidth) - ((digits.length() * charWidth) / 2) + 1);
			gfx.drawString(digits, x, y);
		}
	}//}}}

	//{{{ Draw tick marks
	private void drawTicks(Graphics2D gfx, Color color, int xOffset, int hScroll, int textAreaWidth, double charWidth, double lineHeight) {
		gfx.setColor(color);

		double x0 = xOffset + hScroll;
		// Draw only visible range.
		int start = (int) (-x0 / charWidth);
		if (start < 0) {
			start = 0;
		}
		int end = (int) ((textAreaWidth - hScroll) / charWidth);

		int emStep = 0;
		if (jEdit.getProperty("options.columnruler.numbering", "ticks").equals("ticks")) {
			emStep = 5;
		}
		Line2D tick = new Line2D.Double();
		for (int column = start; column < end; ++column) {
			double x = x0 + column * charWidth;
			int heightReduction = (emStep != 0 && (column % emStep == 0)) ? 3 : 5;
			double height = lineHeight / heightReduction;
			tick.setLine(x, lineHeight, x, lineHeight - height);
			gfx.draw(tick);
		}
	}//}}}

	//}}}

	//{{{ EBComponent.handleMessage() method
	public void handleMessage(EBMessage m) {
		//Log.log(Log.DEBUG,this,m);
		if (m instanceof ViewUpdate) {
			ViewUpdate vu = (ViewUpdate) m;
			fullUpdate();
		}
		
		if (m instanceof EditPaneUpdate) {
			EditPaneUpdate epu = (EditPaneUpdate) m;
			if (epu.getWhat().equals(epu.BUFFER_CHANGED) || epu.getWhat().equals(epu.CREATED)) {
				fullUpdate();
			}
		}

		if (m instanceof PropertiesChanged) {
			fullUpdate();
		}
	}
	//}}}

	private void fullUpdate() {
		java.util.List<StaticMark> marks = MarkManager.getInstance().getMarks();

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
	public void scrolledVertically(TextArea textArea) { }

	/**
	 *  This method is public as an implementation side-effect.
	 *
	 * @param  textArea  Description of the Parameter
	 */
	public void scrolledHorizontally(TextArea textArea) {
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
	 *  This method is public as an implementation side-effect.  This is where clicks on the ColumnRuler are handled.
	 *
	 * @param  e  Description of the Parameter
	 */
	public void mousePressed(MouseEvent e) {
		final Mark mark = (Mark) getMarkAtPoint(e.getPoint());
		if (GUIUtilities.isPopupTrigger(e)) {
			JPopupMenu popup = new JPopupMenu();
			if (mark instanceof StaticMark) {
				Action editMark = new AbstractAction("Edit '"+mark.getName()+"' Mark") {
					public void actionPerformed(ActionEvent ae) {
						MarkDialog d = new MarkDialog((StaticMark) mark, "Edit Mark");
						d.pack();
						d.setVisible(true);
					}
				};
				Action deleteMark = new AbstractAction("Delete '"+mark.getName()+"' mark") {
					public void actionPerformed(ActionEvent ae) {
						if (mark.isGuideVisible()) {
							mark.setGuideVisible(false);
						}
						MarkManager.getInstance().removeMark((StaticMark) mark);
						ColumnRuler.this.repaint();
					}
				};
				popup.add(editMark);
				popup.add(deleteMark);
				popup.addSeparator();
			}
			popup.add(new SetWrapAction("No Wrap", "none"));
			popup.add(new SetWrapAction("Soft Wrap", "soft"));
			popup.add(new SetWrapAction("Hard Wrap", "hard"));
			popup.addSeparator();
			
			clearMarks.setEnabled(MarkManager.getInstance().getMarkCount() > 0);
			popup.add(clearMarks);
				
			popup.show(this, e.getX(), e.getY());
		}
		
		if (e.getClickCount() == 2) {
			if (mark == null) {
				MarkDialog d = new MarkDialog(getColumnAtPoint(e.getPoint()));
				d.pack();
				d.setVisible(true);
			} else {
				mark.setGuideVisible(!mark.isGuideVisible());
				MarkManager.getInstance().fireMarksUpdated();
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
		double x = p.getX() - hScroll;
		return (int) Math.round(x / getCharWidth());
	}

	Mark getMarkAtPoint(Point p) {
		int col = getColumnAtPoint(p);
		java.util.List<Mark> marks = new ArrayList<Mark>();
		marks.addAll(MarkManager.getInstance().getMarks());
		marks.addAll(ColumnRulerPlugin.getDynamicMarks());
		
		for (int i = 0; i < marks.size(); i++) {
			Mark m = (Mark) marks.get(i);
			if (m.getPositionOn(this) == col)
				return m;
		}
		
		return null;
	}

	Mark getGuideAtPoint(Point p) {
		int hScroll = _textArea.getHorizontalOffset();
		double x = p.getX() - hScroll;
		int col = (int) Math.round(x / getCharWidth());
		
		java.util.List<Mark> marks = new ArrayList<Mark>();
		marks.addAll(MarkManager.getInstance().getMarks());
		marks.addAll(ColumnRulerPlugin.getDynamicMarks());

		for (Mark mark : marks) {
			if (mark.getPositionOn(this) == col)
				return mark;
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
		_textArea.getPainter().addExtension(TextAreaPainter.WRAP_GUIDE_LAYER, guideExtension);
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
		int height = (int)getLineHeight();
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
	 * Gets the TextArea this ruler is associated with.
	 */
	public TextArea getTextArea() {
		return _textArea;
	}

	public String toString() {
		return "Column Ruler";
	}
	
	public double getCharWidth() {
		TextAreaPainter painter = _textArea.getPainter();
		return painter.getFont().getStringBounds("X", painter.getFontRenderContext()).getWidth();
	}
	
	public double getLineHeight() {
		return _textArea.getPainter().getFontMetrics().getHeight();
	}
	
	//}}}

	//{{{ Inner classes

	//{{{ SetWrapAction
	/**
	 *  An action for setting the buffer's wrap mode.
	 *
	 * @author     Brad Mace
	 * @version    $Revision: 1.10 $ $Date: 2006-10-11 18:18:40 $
	 */
	class SetWrapAction extends AbstractAction {
		private String _mode;

		public SetWrapAction(String name, String mode) {
			super(name);
			_mode = mode;
		}

		public void actionPerformed(ActionEvent e) {
			JEditBuffer jebuffer = _textArea.getBuffer();
			if (jebuffer instanceof Buffer) {
				Buffer buffer = (Buffer)jebuffer;
				buffer.setStringProperty("wrap", _mode);
				buffer.propertiesChanged();
			}
		}
	}//}}}

	//{{{ LineGuides
	/**
	 *  Painter for line guides of this ruler's marks.
	 *
	 * @author     Brad Mace
	 * @version    $Revision: 1.10 $ $Date: 2006-10-11 18:18:40 $
	 */
	class LineGuides extends TextAreaExtension {
		public void paintScreenLineRange(Graphics2D gfx, int firstLine, int lastLine, int[] physicalLines, int[] start, int[] end, int y, int lineHeight) {
			TextArea textArea = ColumnRuler.this.getTextArea();
			java.util.List<Mark> marks = new ArrayList<Mark>();
			marks.addAll(MarkManager.getInstance().getMarks());
			marks.add(tempMark);
			marks.addAll(ColumnRulerPlugin.getDynamicMarks());
			
			for (int i = 0; i < marks.size(); i++) {
				Mark m = (Mark) marks.get(i);
				if (m.isGuideVisible()) {
					m.drawGuide(gfx, ColumnRuler.this);
				}
			}
		}

		public String getToolTipText(int x, int y) {
			Mark mark = (Mark) getGuideAtPoint(new Point(x, y));
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
	 * @version    $Revision: 1.10 $ $Date: 2006-10-11 18:18:40 $
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
			jEdit.getActiveView().getTextArea().repaint();
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
			jEdit.getActiveView().getTextArea().repaint();
			if (!isDropAcceptable(e)) {
				e.rejectDrop();
			}
			e.acceptDrop(DnDConstants.ACTION_COPY);
			try {
				Mark m = (Mark) e.getTransferable().getTransferData(Mark.MARK_FLAVOR);
				if (m != null) {
					m.setPositionOn(ruler, ruler.getColumnAtPoint(e.getLocation()));
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

