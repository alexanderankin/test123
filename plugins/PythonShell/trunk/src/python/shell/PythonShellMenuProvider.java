/*
* PythonShell is a Console shell for hosting a Python REPL.
* Copyright (c) 2012 Damien Radtke - www.damienradtke.org
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version
* 2.0 of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* General Public License for more details.
*
* For more information, visit http://www.gnu.org/copyleft
*/

package python.shell;

//{{{ Imports
import console.ConsolePlugin;
import console.Console;
import console.Shell;

import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.menu.DynamicMenuProvider;
import org.gjt.sp.jedit.msg.DynamicMenuChanged;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
//}}}

public class PythonShellMenuProvider implements DynamicMenuProvider {
	private LinkedList<JCheckBoxMenuItem> items;

	/**
	 * We don't want this menu to update every time it's shown.
	 */
	public boolean updateEveryTime() {
		return false;
	}

	/**
	 * Append available interpreters to the plugin menu.
	 */
	public void update(JMenu menu) {
		this.items = new LinkedList<JCheckBoxMenuItem>();
		int selected = jEdit.getIntegerProperty("python-shell.selected-interpreter");
		for (int i = 0; true; i++) {
			String interpreter = jEdit.getProperty("python-shell.interpreter." + i);
			if (interpreter == null)
				break;

			JCheckBoxMenuItem item = new JCheckBoxMenuItem(interpreter, i == selected);
			item.addActionListener(new InterpreterListener(i));
			menu.add(item);
		}
	}

	/**
	 * Interpreter menu item listener. This will change the selected interpreter,
	 * restarting the shell if it's already open.
	 */
	class InterpreterListener implements ActionListener {
		private int index;
		public InterpreterListener(int index) {
			this.index = index;
		}

		public void actionPerformed(ActionEvent e) {
			jEdit.setIntegerProperty("python-shell.selected-interpreter", index);
			for (int i = 0; i<items.size(); i++) {
				items.get(i).setSelected(i == index);
			}

			EditBus.send(new DynamicMenuChanged("plugin.python.shell.PythonShellPlugin.menu"));
			PythonShell shell = (PythonShell) Shell.getShell("Python");
			Console console = ConsolePlugin.getConsole(jEdit.getActiveView());
			if (shell != null && console != null) {
				DockableWindowManager wm = console.getView().getDockableWindowManager();
				wm.showDockableWindow("console");
				shell.restart(console);
				console.setShell(shell.getName());
			}
		}
	}
}
