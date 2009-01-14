package minimap;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.textarea.JEditTextArea;

@SuppressWarnings("serial")
public class Minimap extends JPanel {

	EditPane editPane;
	MinimapTextArea miniMap;
	
	public Minimap(EditPane editPane) {
		setLayout(new GridLayout(1, 1));
		this.editPane = editPane;
		JEditTextArea textArea = editPane.getTextArea();
		miniMap = new MinimapTextArea(textArea);
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
