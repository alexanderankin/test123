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

package gdb.variables;

import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.TitledBorder;

import org.gjt.sp.jedit.jEdit;

@SuppressWarnings("serial")
public class Variables extends JPanel {
	
	public Variables() {
		setLayout(new GridLayout(0, 1));
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
