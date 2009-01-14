package minimap;
import java.awt.Container;
import java.awt.FlowLayout;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JSplitPane;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.JEditEmbeddedTextArea;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.jedit.visitors.JEditVisitorAdapter;

public class MinimapPlugin extends EBPlugin {

	@Override
	public void handleMessage(EBMessage message) {
		// TODO Auto-generated method stub
		super.handleMessage(message);
	}

	public void stop()
	{
	}

	public void start()
	{
	}
	static public void show(View view) {
		EditPane editPane = view.getEditPane();
		JEditTextArea textArea = editPane.getTextArea();
		TextArea text = new JEditEmbeddedTextArea();
        text.getBuffer().setProperty("folding","explicit");
        text.getPainter().setFont(text.getPainter().getFont().deriveFont((float) 2.0));
		Container c = textArea.getParent();
		int i;
		for (i = 0; i < c.getComponentCount(); i++)
			if (c.getComponent(i) == textArea)
				break;
		c.remove(textArea);
		JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitter.add(textArea);
		splitter.add(text);
		c.add(splitter);
		c.validate();
		splitter.setDividerLocation(0.7);
		text.setBuffer(textArea.getBuffer());
	}

}
