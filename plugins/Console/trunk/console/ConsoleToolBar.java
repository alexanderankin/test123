/*
 * ConsoleToolBar.java - Console tool bar
 * Copyright (C) 1999, 2000 Slava Pestov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package console;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;

class ConsoleToolBar extends JToolBar
{
	public ConsoleToolBar(View view)
	{
		setLayout(new BorderLayout(6,0));
		setFloatable(false);

		this.view = view;

		add(BorderLayout.WEST,shells = new JComboBox(EditBus
			.getNamedList(Shell.SHELLS_LIST)));
		shells.setSelectedItem(ConsolePlugin.CONSOLE_SHELL);

		Box box = new Box(BoxLayout.Y_AXIS);
		box.add(Box.createGlue());
		cmd = new HistoryTextField("console.console");
		Dimension dim = cmd.getPreferredSize();
		dim.width = Integer.MAX_VALUE;
		cmd.setMaximumSize(dim);
		box.add(cmd);
		box.add(Box.createGlue());
		add(BorderLayout.CENTER,box);

		cmd.addActionListener(new ActionHandler());
	}

	// private members
	private View view;
	private JComboBox shells;
	private HistoryTextField cmd;

	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			DockableWindowManager wm = view.getDockableWindowManager();
			wm.addDockableWindow("console");

			String command = cmd.getText();
			if(command != null && command.length() != 0)
			{
				cmd.setModel("console." + shells.getSelectedItem());
				cmd.addCurrentToHistory();
				cmd.setText(null);

				Console cons = (Console)wm.getDockableWindow("console");
				cons.setShell((Shell)shells.getSelectedItem());
				cons.run(command);
			}
			else
				wm.addDockableWindow("console");
		}
	}
}
