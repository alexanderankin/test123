/*
 * ConsoleToolBar.java - Console tool bar
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999, 2003 Slava Pestov
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

//{{{ Imports
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.Arrays;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;
//}}}

class ConsoleToolBar extends JToolBar
{
	//{{{ ConsoleToolBar constructor
	public ConsoleToolBar(View view)
	{
		setLayout(new BorderLayout(6,0));
		setFloatable(false);

		this.view = view;

		String[] shells = Shell.getShellNames();
		Arrays.sort(shells,new MiscUtilities.StringICaseCompare());
		add(BorderLayout.WEST,shellCombo = new JComboBox(shells));
		shellCombo.setSelectedItem(ConsolePlugin.getSystemShell());
		shellCombo.addActionListener(new ActionHandler());

		Box box = new Box(BoxLayout.Y_AXIS);
		box.add(Box.createGlue());
		cmd = new ConsoleTextField(view);
		Dimension dim = cmd.getPreferredSize();
		dim.width = Integer.MAX_VALUE;
		cmd.setMaximumSize(dim);
		box.add(cmd);
		box.add(Box.createGlue());
		add(BorderLayout.CENTER,box);

		cmd.setModel("console." + shellCombo.getSelectedItem());
		cmd.addActionListener(new ActionHandler());
	} //}}}

	//{{{ Private members
	private View view;
	private JComboBox shellCombo;
	private ConsoleTextField cmd;
	//}}}

	//{{{ ActionHandler class
	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			if(evt.getSource() == shellCombo)
			{
				cmd.setModel("console."
					+ shellCombo.getSelectedItem());
			}
			else if(evt.getSource() == cmd)
			{
				DockableWindowManager wm = view.getDockableWindowManager();
				wm.addDockableWindow("console");

				String command = cmd.getText();
				if(command != null && command.length() != 0)
				{
					cmd.addCurrentToHistory();
					cmd.setText(null);

					Console cons = (Console)wm.getDockable("console");
					cons.setShell((String)shellCombo.getSelectedItem());
					cons.run(cons.getShell(),cons,command);
				}
			}
		}
	} //}}}
}
