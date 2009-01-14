package minimap;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import org.gjt.sp.jedit.syntax.SyntaxStyle;
import org.gjt.sp.jedit.textarea.JEditEmbeddedTextArea;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaPainter;

@SuppressWarnings("serial")
public class MinimapTextArea extends JEditEmbeddedTextArea {

	JEditTextArea textArea;
	
	public MinimapTextArea(JEditTextArea textArea) {
		this.textArea = textArea;
		getBuffer().setProperty("folding","explicit");
		TextAreaPainter painter = getPainter();
        Font f = painter.getFont().deriveFont((float) 2.0);
        painter.setFont(f);
        SyntaxStyle [] styles = painter.getStyles();
        for (int i = 0; i < styles.length; i++) {
        	SyntaxStyle style = styles[i];
        	styles[i] = new SyntaxStyle(style.getForegroundColor(),
        		style.getBackgroundColor(), style.getFont().deriveFont((float) 2.0));
        }
        painter.setStyles(styles);
		setBuffer(textArea.getBuffer());
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Color c = g.getColor();
		g.setColor(Color.RED);
		TextAreaPainter painter = getPainter();
		int x = 1;
		int width = painter.getWidth() - 2;
		int h = painter.getFontMetrics().getHeight();
		int y = textArea.getCaretLine() * h;
		int height = textArea.getVisibleLines() * h - 2;
		g.drawRect(x, y, width, height);
		g.setColor(c);
	}
	
}
