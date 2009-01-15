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
	Component child;
	
	public Minimap(EditPane editPane) {
		setLayout(new GridLayout(1, 1));
		this.editPane = editPane;
		JEditTextArea textArea = editPane.getTextArea();
		miniMap = new MinimapTextArea(textArea);
		Container c = textArea.getParent();
		child = textArea;
		while (! (c instanceof EditPane)) {
			child = c;
			c = c.getParent();
		}
		JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitter.add(miniMap);
		splitter.add(child);
		add(splitter);
		miniMap.setBuffer(textArea.getBuffer());
	}
	
	public void start() {
		miniMap.start();
		editPane.add(this);
		editPane.validate();
	}
	public void stop() {
		miniMap.stop();
		editPane.remove(this);
		editPane.add(child);
		editPane.validate();
	}
}
