package gdb.variables;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

public class VarTreeMouseListener extends MouseAdapter {
	@Override
	public void mouseClicked(MouseEvent e) {
		JTree tree = (JTree) e.getSource();
		if (e.getButton() != MouseEvent.BUTTON3) {
			super.mouseClicked(e);
			return;
		}
		TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
		tree.setSelectionPath(selPath);
		GdbVar v = (GdbVar) selPath.getLastPathComponent();
		if (v != null)
			v.contextRequested();
	}
}
