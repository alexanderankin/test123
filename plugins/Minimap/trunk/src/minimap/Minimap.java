package minimap;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.syntax.SyntaxStyle;
import org.gjt.sp.jedit.textarea.JEditEmbeddedTextArea;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.jedit.textarea.TextAreaPainter;

@SuppressWarnings("serial")
public class Minimap extends JPanel {

	EditPane editPane;
	TextArea miniMap;
	
	public Minimap(EditPane editPane) {
		setLayout(new GridLayout(1, 1));
		this.editPane = editPane;
		miniMap = new JEditEmbeddedTextArea();
		miniMap.getBuffer().setProperty("folding","explicit");
		TextAreaPainter painter = miniMap.getPainter();
        Font f = painter.getFont().deriveFont((float) 2.0);
        painter.setFont(f);
        SyntaxStyle [] styles = painter.getStyles();
        for (int i = 0; i < styles.length; i++) {
        	SyntaxStyle style = styles[i];
        	styles[i] = new SyntaxStyle(style.getForegroundColor(),
        		style.getBackgroundColor(), style.getFont().deriveFont((float) 2.0));
        }
        painter.setStyles(styles);
		JEditTextArea textArea = editPane.getTextArea();
		Container c = textArea.getParent();
		Component prev = textArea;
		while (! (c instanceof EditPane)) {
			prev = c;
			c = c.getParent();
		}
		JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitter.add(miniMap);
		splitter.add(prev);
		add(splitter);
		miniMap.setBuffer(textArea.getBuffer());
	}
}
