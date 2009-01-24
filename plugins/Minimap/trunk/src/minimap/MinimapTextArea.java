/*
Copyright (C) 2009  Shlomy Reinstein

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package minimap;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JScrollBar;
import javax.swing.event.MouseInputAdapter;

import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.syntax.SyntaxStyle;
import org.gjt.sp.jedit.textarea.DisplayManager;
import org.gjt.sp.jedit.textarea.JEditEmbeddedTextArea;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.ScrollListener;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.jedit.textarea.TextAreaPainter;
import org.gjt.sp.util.Log;

@SuppressWarnings("serial")
public class MinimapTextArea extends JEditEmbeddedTextArea implements EBComponent {

	private JEditTextArea textArea;
	private ScrollListener textAreaScrollListener;
	private boolean drag = false;
	private int dragY = 0;
	private int line = 0;
	private MouseListener ml;
	private MouseMotionListener mml;
	private boolean lastFoldProp;
	private JScrollBar scrollBar;
	
	public MinimapTextArea(JEditTextArea textArea) {
		this.textArea = textArea;
		getBuffer().setProperty("folding","explicit");
		setMapFont();
		setBuffer(textArea.getBuffer());
		scrollBar = findScrollBar(this);
		setScrollBarVisibility();
		textAreaScrollListener = new TextAreaScrollListener();
		ml = new MapMouseListener();
		mml = new MapMouseMotionListener();
		getPainter().setCursor(
			Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lastFoldProp = Options.getFoldProp();
	}

	@Override
	public void setFirstPhysicalLine(int firstLine) {
		super.setFirstPhysicalLine(firstLine);
		updateFolds(false);
	}

	private void setScrollBarVisibility() {
		if (scrollBar != null)
			scrollBar.setVisible(Options.getScrollProp());
	}

	private JScrollBar findScrollBar(Container c) {
		for (Component comp: c.getComponents()) {
			if (comp instanceof JScrollBar)
				return (JScrollBar) comp;
			if (comp instanceof Container) {
				JScrollBar sb = findScrollBar((Container) comp);
				if (sb != null)
					return sb;
			}
		}
		return null;
	}
	
	private void setMapFont() {
		TextAreaPainter painter = getPainter();
		Font f = deriveFont(painter.getFont());
		painter.setFont(f);
		SyntaxStyle [] styles = painter.getStyles();
		updateStyles(styles);
		painter.setStyles(styles);
		styles = painter.getFoldLineStyle();
		updateStyles(styles);
		painter.setFoldLineStyle(styles);
	}

	private float getFontSize() {
		return (float) Options.getSizeProp();
	}
	private Font deriveFont(Font f) {
		Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>(f.getAttributes());
		attributes.put(TextAttribute.FAMILY, Options.getFontProp());
		attributes.put(TextAttribute.SIZE, getFontSize());
		return f.deriveFont(attributes);
	}
	public void start() {
		textArea.addScrollListener(textAreaScrollListener);
		painter.addMouseListener(ml);
		painter.addMouseMotionListener(mml);
		EditBus.addToBus(this);
		scrollToMakeTextAreaVisible();
	}
	
	public void stop() {
		EditBus.removeFromBus(this);
		painter.removeMouseMotionListener(mml);
		painter.removeMouseListener(ml);
		textArea.removeScrollListener(textAreaScrollListener);
		dispose();
	}

	//{{{ setMouseHandler() method
	public void setMouseHandler(MouseInputAdapter mouseInputAdapter)
	{
	} //}}}


	private void updateStyles(SyntaxStyle[] styles) {
		for (int i = 0; i < styles.length; i++) {
			SyntaxStyle style = styles[i];
			styles[i] = new SyntaxStyle(style.getForegroundColor(),
						    style.getBackgroundColor(),
						    deriveFont(style.getFont()));
		}
	}

	private class TextAreaScrollListener implements ScrollListener {
		public void scrolledHorizontally(TextArea textArea) {
		}

		public void scrolledVertically(TextArea textArea) {
			scrollToMakeTextAreaVisible();
		}
	}

	private void scrollToMakeTextAreaVisible() {
		int otherFirst = textArea.getFirstPhysicalLine();
		int thisFirst = getFirstPhysicalLine();
		if (otherFirst < thisFirst)
			setFirstPhysicalLine(otherFirst);
		else {
			int otherLast = otherFirst + textArea.getVisibleLines() - 1;
			int thisLast = thisFirst + getVisibleLines() - 1;
			if (otherLast > thisLast)
				setFirstPhysicalLine(thisFirst + otherLast - thisLast);
		}
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Color c = g.getColor();
		g.setColor(Color.RED);
		TextAreaPainter painter = getPainter();
		int x = 1;
		int width = painter.getWidth() - 1;
		int h = painter.getFontMetrics().getHeight();
		int y = (textArea.getFirstPhysicalLine() - getFirstPhysicalLine()) * h;
		int height = textArea.getVisibleLines() * h - 1;
		g.drawRect(x, y, width, height);
		g.setColor(c);
	}

	public void handleMessage(EBMessage message) {
		if (message instanceof EditPaneUpdate) {
			EditPaneUpdate epu = (EditPaneUpdate) message;
			EditPane ep = (EditPane) epu.getSource();
			if (ep.getTextArea() == textArea) {
				if (epu.getWhat() == EditPaneUpdate.BUFFER_CHANGED) {
					setBuffer(textArea.getBuffer());
					repaint();
				}
			}
		} else if ((message instanceof PropertiesChanged) ||
			 ((message instanceof BufferUpdate) &&
			  (((BufferUpdate) message).getWhat() == BufferUpdate.PROPERTIES_CHANGED)))
		{
			EditPane.initPainter(getPainter());
			setMapFont();
			boolean foldProp = Options.getFoldProp();
			if (foldProp != lastFoldProp) {
				lastFoldProp = foldProp;
				if (foldProp)
					updateFolds();
				else
					getDisplayManager().expandAllFolds();
			}
			setScrollBarVisibility();
			propertiesChanged();
		}
	}

	private class MapMouseListener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getButton() != MouseEvent.BUTTON1)
				return;
			TextAreaPainter painter = getPainter();
			int h = painter.getFontMetrics().getHeight();
			int y = (textArea.getFirstPhysicalLine() - getFirstPhysicalLine()) * h;
			int height = textArea.getVisibleLines() * h - 1;
			if (e.getY() >= y && e.getY() < y + height) {
				line = textArea.getFirstPhysicalLine();
				dragY = e.getY();
				drag = true;
				e.consume();
			}
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.getButton() != MouseEvent.BUTTON1)
				return;
			if (drag)
				e.consume();
			else {
				// Move the text area to where the mouse was released
				int h = painter.getFontMetrics().getHeight();
				int line = getFirstPhysicalLine() + e.getY() / h;
				int visibleLines = textArea.getVisibleLines();
				line -= visibleLines >> 1;
				scrollTextArea(line);
			}
			drag = false;
		}
	}
	private class MapMouseMotionListener extends MouseMotionAdapter {
		@Override
		public void mouseDragged(MouseEvent e) {
			if (! drag)
				return;
			TextAreaPainter painter = getPainter();
			int h = painter.getFontMetrics().getHeight();
			int visibleLines = textArea.getVisibleLines();
			int newFirstLine;
			if (e.getY() < getY())
				newFirstLine = textArea.getFirstPhysicalLine() - visibleLines;
			else if (e.getY() > getY() + getHeight())
				newFirstLine = textArea.getFirstPhysicalLine() + visibleLines;
			else {
				int diff = (e.getY() - dragY) / h;
				newFirstLine = line + diff;
			}
			scrollTextArea(newFirstLine);
			e.consume();
		}
	}

	private void scrollTextArea(int newFirstLine) {
		int visibleLines = textArea.getVisibleLines();
		int count = textArea.getLineCount();
		if (newFirstLine > count - visibleLines)
			newFirstLine = count - visibleLines;
		if (newFirstLine < 0)
			newFirstLine = 0;
		textArea.setFirstPhysicalLine(newFirstLine);
		int first = getFirstPhysicalLine();
		if (newFirstLine < first)
			setFirstPhysicalLine(newFirstLine);
		else if (newFirstLine + visibleLines > getLastPhysicalLine())
			setFirstPhysicalLine(newFirstLine + visibleLines - getVisibleLines());
		repaint();
	}
	
	public void updateFolds() {
		updateFolds(true);
	}
	
	public void updateFolds(boolean allowMove) {
		if (! Options.getFoldProp())
			return;
		if (getDisplayManager().getScrollLineCount() ==
			textArea.getDisplayManager().getScrollLineCount())
		{
			return;
		}
		int first = getFirstPhysicalLine();
		DisplayManager tdm = textArea.getDisplayManager();
		DisplayManager dm = getDisplayManager();
		int i = first;
		int j = dm.getLastVisibleLine();
		while (i < j) {
			if (tdm.isLineVisible(i)) {
				if (tdm.isLineVisible(i + 1)) {
					dm.expandFold(i, false);
					if (i >= getLastPhysicalLine())
						break;
					i++;
				}
				else {
					if (dm.isLineVisible(i + 1))
						dm.collapseFold(i);
					try {
						i = tdm.getNextVisibleLine(i);
						if (i == -1) {
							// there is no next visible fold
							break;
						}
					} catch (Exception e) {
						Log.log(Log.ERROR, this, e);
						break;
					}
				}
			} else {
				dm.collapseFold(i - 1);
				i = tdm.getNextVisibleLine(i);
			}
		}
		if (allowMove)
			scrollToMakeTextAreaVisible();
	}
}
