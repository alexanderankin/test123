/*
 * TaskListGeneralOptionPane.java - TaskList plugin
 * Copyright (C) 2001,2002 Oliver Rutherfurd
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
 *
 * $Id$
 */

//{{{ imports
import java.awt.event.*;
import java.util.Vector;
import java.awt.Color;
import javax.swing.event.*;
import javax.swing.*;
import org.gjt.sp.jedit.*;
//}}}

public class TaskListGeneralOptionPane extends AbstractOptionPane
{
	public TaskListGeneralOptionPane()
	{
		super("tasklist.general");
	}

	//{{{ _init() method
	protected void _init()
	{
		addComponent(jEdit.getProperty("options.tasklist.general.buffer.display"),
			bufferDisplay = new JComboBox(
			new String[]
			{
				jEdit.getProperty("options.tasklist.general.buffer.display.fullpath"),
				jEdit.getProperty("options.tasklist.general.buffer.display.namedir"),
				jEdit.getProperty("options.tasklist.general.buffer.display.nameonly")
			}));

		addComponent(Box.createVerticalStrut(3));

		addComponent(jEdit.getProperty("options.tasklist.general.sort.choice"),
			sortCriteria = new JComboBox(
			new String[]
			{
				jEdit.getProperty("options.tasklist.general.sort.choice.0"),
				jEdit.getProperty("options.tasklist.general.sort.choice.1")
			}));

		addComponent(Box.createVerticalStrut(3));

		addComponent(jEdit.getProperty("options.tasklist.general.sort.direction"),
			sortDirection = new JComboBox(
			new String[]
			{
				jEdit.getProperty("options.tasklist.general.sort.direction.0"),
				jEdit.getProperty("options.tasklist.general.sort.direction.1")
			}));

		addComponent(Box.createVerticalStrut(3));

		addComponent(allowSingleClick = new JCheckBox(
			jEdit.getProperty("options.tasklist.single-click-selection"),
			jEdit.getBooleanProperty("tasklist.single-click-selection",false)));

		addComponent(Box.createVerticalStrut(3));

		addComponent(bHorizontalLines = new JCheckBox(
			jEdit.getProperty("options.tasklist.general.table.horizontal-lines"),
			jEdit.getBooleanProperty("tasklist.table.horizontal-lines", false)));

		addComponent(Box.createVerticalStrut(3));

		addComponent(bVerticalLines = new JCheckBox(
			jEdit.getProperty("options.tasklist.general.table.vertical-lines"),
			jEdit.getBooleanProperty("tasklist.table.vertical-lines", false)));

		addComponent(Box.createVerticalStrut(3));

		// default to false, until we get it working well
		addComponent(highlightTasks = new JCheckBox(
			jEdit.getProperty("options.tasklist.general.highlight.tasks"),
			jEdit.getBooleanProperty("tasklist.highlight.tasks", false)));

		addComponent(Box.createVerticalStrut(3));

		addComponent(
			jEdit.getProperty("options.tasklist.general.highlight.color"),
			highlightColor = createColorButton("tasklist.highlight.color"));

		// toggle whether the color button is enabled
		highlightTasks.addActionListener(new HighlightColorHandler());
		highlightColor.setEnabled(highlightTasks.isSelected());

		// set current value (default to name (dir))
		String _bufferDisplay = jEdit.getProperty("tasklist.buffer.display");
		if(_bufferDisplay == "" || _bufferDisplay == null)
			bufferDisplay.setSelectedItem(jEdit.getProperty(
				"options.tasklist.general.buffer.display.namedir"));
		else
			bufferDisplay.setSelectedItem(_bufferDisplay);
	}//}}}

	//{{{ _save() method
	public void _save()
	{
		jEdit.setProperty("tasklist.buffer.display",
			bufferDisplay.getSelectedItem().toString());

		jEdit.setProperty("tasklist.highlight.color",
			GUIUtilities.getColorHexString(highlightColor.getBackground()));

		jEdit.setBooleanProperty("tasklist.table.horizontal-lines",
			bHorizontalLines.isSelected());

		jEdit.setBooleanProperty("tasklist.table.vertical-lines",
			bVerticalLines.isSelected());

		jEdit.setBooleanProperty("tasklist.single-click-selection",
			allowSingleClick.isSelected());

		jEdit.setBooleanProperty("tasklist.highlight.tasks",
			highlightTasks.isSelected());

		jEdit.setProperty("tasklist.table.sort-column",
			String.valueOf(sortCriteria.getSelectedIndex() + 1));

		jEdit.setBooleanProperty("tasklist.table.sort-ascending",
			(sortDirection.getSelectedIndex() == 0));
	}//}}}

	//{{{ createColorButton() method
	private JButton createColorButton(String property)
	{
		JButton b = new JButton(" ");
		b.setBackground(GUIUtilities.parseColor(jEdit.getProperty(property)));
		b.addActionListener(new ColorButtonHandler(b));
		b.setRequestFocusEnabled(false);
		return b;
	}//}}}

	//{{{ ColorButtonHandler class
	private class ColorButtonHandler implements ActionListener
	{
		private JButton button;

		public ColorButtonHandler(JButton button) {
			this.button = button;
		}

		public void actionPerformed(ActionEvent evt)
		{
			JButton button = (JButton)evt.getSource();
			Color c = JColorChooser.showDialog(TaskListGeneralOptionPane.this,
				jEdit.getProperty("colorChooser.title"),
				button.getBackground()
			);
			if (c != null) {
				button.setBackground(c);
			}
		}
	}//}}}

	//{{{ HighlightColorHandler class
	private class HighlightColorHandler implements ActionListener{
		public void actionPerformed(ActionEvent evt)
		{
			TaskListGeneralOptionPane.this.highlightColor.setEnabled(
				TaskListGeneralOptionPane.this.highlightTasks.isSelected());
		}
	}//}}}

	//{{{ private members
	private JComboBox bufferDisplay;
	private JComboBox sortCriteria;
	private JComboBox sortDirection;
	private JCheckBox allowSingleClick;
	private JCheckBox bVerticalLines;
	private JCheckBox bHorizontalLines;
	private JCheckBox highlightTasks;
	private JButton highlightColor;
	//}}}
}

// :collapseFolds=1:folding=explicit:indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:
