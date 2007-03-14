package gdb.views;

import gdb.CommandManager;

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

	public void update(int frame) {
		locals.update(frame);
		watches.update();
	}
	public void sessionEnded() {
		locals.sessionEnded();
		watches.sessionEnded();
	}

	public void setCommandManager(CommandManager commandManager) {
		locals.setCommandManager(commandManager);
		watches.setCommandManager(commandManager);
	}

}
