package gdb.variables;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

@SuppressWarnings("serial")
public class Variables extends JPanel {
	
	private LocalVariables locals;
	private Watches watches;

	public Variables() {
		setLayout(new BorderLayout());
		locals = new LocalVariables();
		watches = new Watches();
		JSplitPane pane = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT, locals, watches);
		add(pane);
	}

}
