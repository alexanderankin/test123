package gdb.variables;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.TitledBorder;

import org.gjt.sp.jedit.jEdit;

@SuppressWarnings("serial")
public class Variables extends JPanel {
	
	public Variables() {
		setLayout(new BorderLayout());
		JPanel locals = new JPanel();
		locals.setLayout(new BoxLayout(locals, 1));
		TitledBorder border = new TitledBorder(
				jEdit.getProperty("debugger-show-locals.title"));
		locals.setBorder(border);
		locals.add(new LocalVariables());
		JPanel watches = new JPanel();
		watches.setLayout(new BoxLayout(watches, 1));
		border = new TitledBorder(
				jEdit.getProperty("debugger-watches.title"));
		watches.setBorder(border);
		watches.add(new Watches());
		JSplitPane pane = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT, locals, watches);
		add(pane);
	}

}
