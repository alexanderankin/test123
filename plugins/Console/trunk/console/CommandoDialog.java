/*
 * CommandoDialog.java - Commando dialog box
 * Copyright (C) 2001 Slava Pestov
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

import javax.swing.border.EmptyBorder;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import org.gjt.sp.jedit.gui.EnhancedDialog;
import org.gjt.sp.jedit.*;

public class CommandoDialog extends EnhancedDialog
{
	public CommandoDialog(View view, String command)
	{
		super(view,jEdit.getProperty("commando.title"),false);

		JPanel content = new JPanel(new BorderLayout());
		content.setBorder(new EmptyBorder(12,12,12,12));
		setContentPane(content);

		JPanel top = new JPanel(new BorderLayout());
		top.setBorder(new EmptyBorder(0,0,12,0));

		JLabel label = new JLabel(jEdit.getProperty("commando.caption"));
		label.setBorder(new EmptyBorder(0,0,0,12));
		top.add(BorderLayout.WEST,label);

		CommandoCommand[] commands = ConsolePlugin.getCommandoCommands();

		commandCombo = new JComboBox(commands);
		commandCombo.addActionListener(new ActionHandler());
		top.add(BorderLayout.CENTER,commandCombo);

		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab(jEdit.getProperty("commando.settings"),
			settings = new SettingsPane());
		tabs.addTab(jEdit.getProperty("commando.commands"),
			commandLine = new TextAreaPane());
		tabs.addTab(jEdit.getProperty("commando.properties"),
			properties = new TextAreaPane());

		if(command == null)
			command = jEdit.getProperty("commando.last-command");

		for(int i = 0; i < commands.length; i++)
		{
			if(commands[i].name.equals(command))
			{
				commandCombo.setSelectedIndex(i);
				break;
			}
		}

		content.add(BorderLayout.CENTER,tabs);
		pack();
		setLocationRelativeTo(view);
		show();
	}

	public void ok()
	{
		dispose();
	}

	public void cancel()
	{
		dispose();
	}

	// private members
	private JComboBox commandCombo;
	private SettingsPane settings;
	private TextAreaPane commandLine;
	private TextAreaPane properties;
	private CommandoCommand command;

	private void loadCommand(CommandoCommand command)
	{
		this.command = command;
		settings.removeAll();
		commandLine.setText(null);
		properties.setText(null);

		// do loading here

		getRootPane().revalidate();
		pack();
	}

	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			CommandoCommand command = (CommandoCommand)commandCombo
				.getSelectedItem();
			loadCommand(command);
		}
	}

	static class SettingsPane extends JPanel
	{
		SettingsPane()
		{
			setLayout(gridBag = new GridBagLayout());
		}

		void addComponent(String label, Component comp)
		{
			GridBagConstraints cons = new GridBagConstraints();
			cons.gridy = y++;
			cons.gridheight = 1;
			cons.gridwidth = 1;
			cons.weightx = 0.0f;
			cons.fill = GridBagConstraints.BOTH;

			JLabel l = new JLabel(label,SwingConstants.RIGHT);
			l.setBorder(new EmptyBorder(0,0,0,12));
			gridBag.setConstraints(l,cons);
			add(l);

			cons.gridx = 1;
			cons.weightx = 1.0f;
			gridBag.setConstraints(comp,cons);
			add(comp);
		}

		void addComponent(Component comp)
		{
			GridBagConstraints cons = new GridBagConstraints();
			cons.gridy = y++;
			cons.gridheight = 1;
			cons.gridwidth = cons.REMAINDER;
			cons.fill = GridBagConstraints.NONE;
			cons.anchor = GridBagConstraints.WEST;
			cons.weightx = 1.0f;

			gridBag.setConstraints(comp,cons);
			add(comp);
		}

		void addSeparator(String label)
		{
			Box box = new Box(BoxLayout.X_AXIS);
			Box box2 = new Box(BoxLayout.Y_AXIS);
			box2.add(Box.createGlue());
			box2.add(new JSeparator(JSeparator.HORIZONTAL));
			box2.add(Box.createGlue());
			box.add(box2);
			JLabel l = new JLabel(jEdit.getProperty(label));
			l.setMaximumSize(l.getPreferredSize());
			box.add(l);
			Box box3 = new Box(BoxLayout.Y_AXIS);
			box3.add(Box.createGlue());
			box3.add(new JSeparator(JSeparator.HORIZONTAL));
			box3.add(Box.createGlue());
			box.add(box3);

			GridBagConstraints cons = new GridBagConstraints();
			cons.gridy = y++;
			cons.gridheight = 1;
			cons.gridwidth = cons.REMAINDER;
			cons.fill = GridBagConstraints.BOTH;
			cons.anchor = GridBagConstraints.WEST;
			cons.weightx = 1.0f;

			gridBag.setConstraints(box,cons);
			add(box);
		}

		// private members
		private GridBagLayout gridBag;
		private int y;
	}

	static class TextAreaPane extends JPanel
	{
		TextAreaPane()
		{
			super(new BorderLayout());

			JPanel panel = new JPanel(new BorderLayout());
			panel.setBorder(new EmptyBorder(6,0,6,0));
			panel.add(BorderLayout.WEST,copy = new JButton(
				jEdit.getProperty("commando.copy")));
			copy.addActionListener(new ActionHandler());

			add(BorderLayout.NORTH,panel);

			add(BorderLayout.CENTER,new JScrollPane(
				textArea = new JTextArea(8,40)));
			textArea.setEditable(false);
		}

		void setText(String text)
		{
			textArea.setText(text);
		}

		// private emembers
		private JButton copy;
		private JTextArea textArea;

		class ActionHandler implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				textArea.copy();
			}
		}
	}
}
