/*
 * TaskListGeneralOptionPane.java - TaskList plugin
 * Copyright (C) 2001 Oliver Rutherfurd
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


// TODO: remove unused packages
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.net.*;
import java.util.Vector;
import java.util.StringTokenizer;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;

import org.gjt.sp.util.Log;

public class TaskListGeneralOptionPane extends AbstractOptionPane
{
	public TaskListGeneralOptionPane()
	{
		super("tasklist.general");
	}

	protected void _init()
	{
		// NOTE: parse delay does not appear to be implemented in application
		/*
		String _parseDelay = "";

		try
		{
			_parseDelay = "" + Integer.parseInt(jEdit.getProperty("tasklist.parsedelay"));
		}
		catch(NumberFormatException nf)
		{
			_parseDelay = "1000";	// IDEA: replace with constant
		}

		addComponent(jEdit.getProperty("options.tasklist.general.parsedelay"),
			parseDelay = new JTextField(_parseDelay));

		addComponent(Box.createVerticalStrut(3));
		*/
		addComponent(jEdit.getProperty("options.tasklist.general.buffer.display"),
			bufferDisplay = new JComboBox(
			new String[]
			{
				jEdit.getProperty("options.tasklist.general.buffer.display.fullpath"),
				jEdit.getProperty("options.tasklist.general.buffer.display.namedir"),
				jEdit.getProperty("options.tasklist.general.buffer.display.nameonly"),
			}));

		addComponent(Box.createVerticalStrut(3));

		addComponent(bHorizontalLines = new JCheckBox(
			jEdit.getProperty("options.tasklist.general.table.horizontal-lines"),
			jEdit.getBooleanProperty("tasklist.table.horizontal-lines", false)));

		addComponent(Box.createVerticalStrut(3));

		addComponent(bVerticalLines = new JCheckBox(
			jEdit.getProperty("options.tasklist.general.table.vertical-lines"),
			jEdit.getBooleanProperty("tasklist.table.vertical-lines", false)));

		addComponent(Box.createVerticalStrut(3));


		// DONE: change default to false, until we get it working well
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

		// set current value of viewBuffers
//		String _viewBuffers = jEdit.getProperty("tasklist.view.buffers");
//		if(_viewBuffers == "" || _viewBuffers == null)
//			viewBuffers.setSelectedItem(jEdit.getProperty(
//				"options.tasklist.general.view.buffers.panes"));
//		else
//			viewBuffers.setSelectedItem(_viewBuffers);
	}


	public void _save()
	{
		// NOTE: parsedelay property not used in this version
		/*
		jEdit.setProperty("tasklist.parsedelay", parseDelay.getText());
		*/

		jEdit.setProperty("tasklist.buffer.display",
			bufferDisplay.getSelectedItem().toString());

		jEdit.setProperty("tasklist.highlight.color",
			GUIUtilities.getColorHexString(highlightColor.getBackground()));

		jEdit.setBooleanProperty("tasklist.table.horizontal-lines",
			bHorizontalLines.isSelected());

		jEdit.setBooleanProperty("tasklist.table.vertical-lines",
			bVerticalLines.isSelected());

		jEdit.setBooleanProperty("tasklist.highlight.tasks",
			highlightTasks.isSelected());


	}

	private JButton createColorButton(String property)
	{
		JButton b = new JButton(" ");
		b.setBackground(GUIUtilities.parseColor(jEdit.getProperty(property)));
		b.addActionListener(new ColorButtonHandler(b));
		b.setRequestFocusEnabled(false);
		return b;
	}

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
	}

	private class HighlightColorHandler implements ActionListener{
		public void actionPerformed(ActionEvent evt)
		{
			TaskListGeneralOptionPane.this.highlightColor.setEnabled(
				TaskListGeneralOptionPane.this.highlightTasks.isSelected());
		}
	}

	// NOTE: parseDelay component not used in this version
	// private JTextField parseDelay;
	private JComboBox bufferDisplay;
	//private JComboBox viewBuffers;
	private JCheckBox bVerticalLines;
	private JCheckBox bHorizontalLines;
	private JCheckBox highlightTasks;
	private JButton highlightColor;

}

