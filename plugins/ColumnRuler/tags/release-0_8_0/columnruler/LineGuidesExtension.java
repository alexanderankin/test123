package columnruler;

import java.awt.*;
import javax.swing.event.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.textarea.*;

public class LineGuidesExtension extends TextAreaExtension implements CaretListener {
	public void paintValidLine(Graphics2D gfx, int screenLine, int physicalLine,int start, int end, int y) {
		JEditTextArea textArea = jEdit.getActiveView().getTextArea();
	}

	public void addTo(JEditTextArea textArea) {
		textArea.getPainter().addExtension(TextAreaPainter.BACKGROUND_LAYER,this);
		textArea.addCaretListener(this);
	}

	public void removeFrom(JEditTextArea textArea) {
		textArea.getPainter().removeExtension(this);
		textArea.removeCaretListener(this);
	}

	public void paintScreenLineRange(Graphics2D gfx, int firstLine, int lastLine, int[] physicalLines, int[] start, int[] end, int y, int lineHeight) {
		JEditTextArea textArea = jEdit.getActiveView().getTextArea();
		gfx.setColor(Color.RED);
		drawGuide(gfx,PositionCalculator.getCaretColumn(textArea));
	}

	public static void drawGuide(Graphics2D gfx, int column) {
		JEditTextArea textArea = jEdit.getActiveView().getTextArea();
		int currentLine = textArea.getCaretLine();
		int lineHeight = PositionCalculator.getLineHeight(textArea);
		int charWidth = PositionCalculator.getCharWidth(textArea);
		int hScroll = textArea.getHorizontalOffset();
		int x = column*charWidth + hScroll;
		if (jEdit.getBooleanProperty("options.columnruler.guides.caret")) {
			gfx.drawLine(x,0,x,textArea.getHeight());
		}
	}

	//{{{ CaretListener implementation
	public void caretUpdate(CaretEvent e) {
		if (jEdit.getBooleanProperty("options.columnruler.guides.caret")) {
			jEdit.getActiveView().getTextArea().repaint();
		}
	}
	//}}}

}
