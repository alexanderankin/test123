/*
Copyright (C) 2007  Shlomy Reinstein

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package perl.variables;

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
