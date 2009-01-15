package minimap;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import javax.swing.event.MouseInputAdapter;

import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.syntax.SyntaxStyle;
import org.gjt.sp.jedit.textarea.JEditEmbeddedTextArea;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.ScrollListener;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.jedit.textarea.TextAreaPainter;

@SuppressWarnings("serial")
public class MinimapTextArea extends JEditEmbeddedTextArea implements EBComponent {

	JEditTextArea textArea;
	ScrollListener textAreaScrollListener;
	
	public MinimapTextArea(JEditTextArea textArea) {
		this.textArea = textArea;
		getBuffer().setProperty("folding","explicit");
		TextAreaPainter painter = getPainter();
		Font f = painter.getFont().deriveFont((float) 2.0);
		painter.setFont(f);
		SyntaxStyle [] styles = painter.getStyles();
		updateStyles(styles);
		painter.setStyles(styles);
		styles = painter.getFoldLineStyle();
		updateStyles(styles);
		painter.setFoldLineStyle(styles);
		setBuffer(textArea.getBuffer());
	}

	public void start() {
		// Align scrolling
		textAreaScrollListener = new TextAreaScrollListener();
		textArea.addScrollListener(textAreaScrollListener);
		MouseListener ml = new MapMouseListener();
		MouseMotionListener mml = new MapMouseMotionListener();
		painter.addMouseListener(ml);
		painter.addMouseMotionListener(mml);
		EditBus.addToBus(this);
	}
	public void stop() {
		textArea.removeScrollListener(textAreaScrollListener);
		EditBus.removeFromBus(this);
	}
	
	//{{{ setMouseHandler() method
	public void setMouseHandler(MouseInputAdapter mouseInputAdapter)
	{
	} //}}}

	
	private void updateStyles(SyntaxStyle[] styles) {
		for (int i = 0; i < styles.length; i++) {
        	SyntaxStyle style = styles[i];
        	styles[i] = new SyntaxStyle(style.getForegroundColor(),
        		style.getBackgroundColor(), style.getFont().deriveFont((float) 2.0));
        }
	}

	private class TextAreaScrollListener implements ScrollListener {
		public void scrolledHorizontally(TextArea textArea) {
		}

		public void scrolledVertically(TextArea textArea) {
			repaint();
		}
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Color c = g.getColor();
		g.setColor(Color.RED);
		TextAreaPainter painter = getPainter();
		int x = 0;
		int width = painter.getWidth() - 1;
		int h = painter.getFontMetrics().getHeight();
		int y = textArea.getFirstLine() * h;
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
		}
	}
	
	private boolean drag = false;
	private int dragY = 0;
	private int line = 0;
	
	private class MapMouseListener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			TextAreaPainter painter = getPainter();
			int h = painter.getFontMetrics().getHeight();
			int y = textArea.getFirstLine() * h;
			int height = textArea.getVisibleLines() * h - 2;
			if (e.getY() >= y && e.getY() <= y + height - 1) {
				line = textArea.getFirstLine();
				dragY = e.getY();
				drag = true;
				e.consume();
			}
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			if (drag)
				e.consume();
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
			int diff = (e.getY() - dragY) / h;
			textArea.scrollTo(line + diff, 0, false);
			e.consume();
		}
	}

}
