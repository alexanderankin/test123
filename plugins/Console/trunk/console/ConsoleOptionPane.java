/*
 * ConsoleOptionPane.java - Console options panel
 * Copyright (C) 1999, 2000, 2001 Slava Pestov
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

import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.Vector;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;

class ConsoleOptionPane extends AbstractOptionPane
{
	public ConsoleOptionPane()
	{
		super("console");
	}

	// protected members

	protected void _init()
	{
		addComponent(consoleToolBar = new JCheckBox(jEdit.getProperty(
			"options.console.toolbar")));
		consoleToolBar.getModel().setSelected(jEdit.getBooleanProperty(
			"console.toolbar.enabled"));

		addComponent(commandoToolBar = new JCheckBox(jEdit.getProperty(
			"options.commando.toolbar")));
		commandoToolBar.getModel().setSelected(jEdit.getBooleanProperty(
			"commando.toolbar.enabled"));

		addComponent(jEdit.getProperty("options.console.bgColor"),
			bgColor = createColorButton("console.bgColor"));
		addComponent(jEdit.getProperty("options.console.plainColor"),
			plainColor = createColorButton("console.plainColor"));
		addComponent(jEdit.getProperty("options.console.infoColor"),
			infoColor = createColorButton("console.infoColor"));
		addComponent(jEdit.getProperty("options.console.warningColor"),
			warningColor = createColorButton("console.warningColor"));
		addComponent(jEdit.getProperty("options.console.errorColor"),
			errorColor = createColorButton("console.errorColor"));
		addComponent(new JLabel(jEdit.getProperty("options.console.errors")));

		addComponent(Box.createVerticalStrut(6));

		JPanel errors = new JPanel(new BorderLayout());
		errorListModel = createMatcherListModel();
		errors.add(BorderLayout.CENTER,new JScrollPane(errorList = new JList(errorListModel)));
		errorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		errorList.addListSelectionListener(new ListHandler());
		errorList.addMouseListener(new MouseHandler());
		errorList.setVisibleRowCount(5);

		JPanel buttons = new JPanel();
		buttons.setBorder(new EmptyBorder(6,0,0,0));
		buttons.setLayout(new BoxLayout(buttons,BoxLayout.X_AXIS));

		buttons.add(edit = new JButton(jEdit.getProperty("options.console.errors.edit")));
		edit.addActionListener(new ActionHandler());
		buttons.add(Box.createHorizontalStrut(6));

		buttons.add(add = new JButton(jEdit.getProperty("options.console.errors.add")));
		add.addActionListener(new ActionHandler());
		buttons.add(Box.createHorizontalStrut(6));

		buttons.add(remove = new JButton(jEdit.getProperty("options.console.errors.remove")));
		remove.addActionListener(new ActionHandler());
		buttons.add(Box.createHorizontalStrut(6));

		buttons.add(Box.createGlue());

		errors.add(BorderLayout.SOUTH,buttons);

		updateButtons();

		GridBagConstraints cons = new GridBagConstraints();
		cons.gridy = y++;
		cons.gridheight = cons.REMAINDER;
		cons.gridwidth = cons.REMAINDER;
		cons.fill = GridBagConstraints.BOTH;
		cons.weightx = cons.weighty = 1.0f;

		gridBag.setConstraints(errors,cons);
		add(errors);
	}

	protected void _save()
	{
		jEdit.setBooleanProperty("console.toolbar.enabled",
			consoleToolBar.getModel().isSelected());
		jEdit.setBooleanProperty("commando.toolbar.enabled",
			commandoToolBar.getModel().isSelected());
		jEdit.setProperty("console.bgColor",GUIUtilities
			.getColorHexString(bgColor.getBackground()));
		jEdit.setProperty("console.plainColor",GUIUtilities
			.getColorHexString(plainColor.getBackground()));
		jEdit.setProperty("console.infoColor",GUIUtilities
			.getColorHexString(infoColor.getBackground()));
		jEdit.setProperty("console.warningColor",GUIUtilities
			.getColorHexString(warningColor.getBackground()));
		jEdit.setProperty("console.errorColor",GUIUtilities
			.getColorHexString(errorColor.getBackground()));

		StringBuffer list = new StringBuffer();
		for(int i = 0; i < errorListModel.getSize(); i++)
		{
			if(i != 0)
				list.append(' ');

			ErrorMatcher matcher = (ErrorMatcher)errorListModel.getElementAt(i);
			matcher.save();

			if(matcher.user)
				list.append(matcher.internalName);
		}

		jEdit.setProperty("console.error.user",list.toString());
	}

	// private members
	private JCheckBox consoleToolBar;
	private JCheckBox commandoToolBar;
	private JButton bgColor;
	private JButton plainColor;
	private JButton infoColor;
	private JButton warningColor;
	private JButton errorColor;
	private JList errorList;
	private DefaultListModel errorListModel;
	private JButton edit;
	private JButton add;
	private JButton remove;

	private JButton createColorButton(String property)
	{
		JButton b = new JButton(" ");
		b.setBackground(GUIUtilities.parseColor(jEdit.getProperty(property)));
		b.addActionListener(new ActionHandler());
		b.setRequestFocusEnabled(false);
		return b;
	}

	private DefaultListModel createMatcherListModel()
	{
		DefaultListModel listModel = new DefaultListModel();

		ErrorMatcher[] matchers = ConsolePlugin.getErrorMatchers();
		for(int i = 0; i < matchers.length; i++)
		{
			listModel.addElement(matchers[i].clone());
		}

		return listModel;
	}

	private void updateButtons()
	{
		int index = errorList.getSelectedIndex();
		if(index == -1)
		{
			edit.setEnabled(false);
			remove.setEnabled(false);
		}
		else
		{
			edit.setEnabled(true);

			ErrorMatcher matcher = (ErrorMatcher)errorList.getSelectedValue();
			remove.setEnabled(matcher.user);
		}
	}

	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			Object source = evt.getSource();
			if(source == edit)
			{
				ErrorMatcher error = (ErrorMatcher)errorList
					.getSelectedValue();
				new ErrorMatcherDialog(ConsoleOptionPane.this,error);
				errorList.repaint();
			}
			else if(source == add)
			{
				ErrorMatcher matcher = new ErrorMatcher();
				if(new ErrorMatcherDialog(ConsoleOptionPane.this,matcher).isOK())
				{
					int index = errorList.getSelectedIndex();
					if(index == -1)
						index = errorListModel.getSize();
					else
						index++;
		
					errorListModel.insertElementAt(matcher,index);
					errorList.setSelectedIndex(index);
				}
			}
			else if(source == remove)
			{
				errorListModel.removeElementAt(errorList.getSelectedIndex());
				updateButtons();
			}
			/* else if(source == up)
			{
				int index = errorList.getSelectedIndex();
				Object selected = errorList.getSelectedValue();
				errorListModel.removeElementAt(index);
				errorListModel.insertElementAt(selected,index-1);
				errorList.setSelectedIndex(index-1);
			}
			else if(source == down)
			{
				int index = errorList.getSelectedIndex();
				Object selected = errorList.getSelectedValue();
				errorListModel.removeElementAt(index);
				errorListModel.insertElementAt(selected,index+1);
				errorList.setSelectedIndex(index+1);
			} */
			else
			{
				JButton button = (JButton)source;
				Color c = JColorChooser.showDialog(ConsoleOptionPane.this,
					jEdit.getProperty("colorChooser.title"),
					button.getBackground());
				if(c != null)
					button.setBackground(c);
			}
		}
	}

	class ListHandler implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent evt)
		{
			updateButtons();
		}
	}

	class MouseHandler extends MouseAdapter
	{
		public void mouseClicked(MouseEvent evt)
		{
			if(evt.getClickCount() == 2)
			{
				ErrorMatcher error = (ErrorMatcher)errorList
					.getSelectedValue();
				new ErrorMatcherDialog(ConsoleOptionPane.this,error);
				errorList.repaint();
			}
		}
	}
}

class ErrorMatcherDialog extends EnhancedDialog
{
	public ErrorMatcherDialog(Component comp, ErrorMatcher matcher)
	{
		super(JOptionPane.getFrameForComponent(comp),
			jEdit.getProperty("options.console.errors.title"),true);
		this.matcher = matcher;

		JPanel panel = new JPanel(new GridLayout(5,2,0,6));
		panel.setBorder(new EmptyBorder(12,12,6,12));
		JLabel label = new JLabel(jEdit.getProperty(
			"options.console.errors.name"),JLabel.RIGHT);
		label.setBorder(new EmptyBorder(0,0,0,12));
		panel.add(label);
		panel.add(name = new JTextField(matcher.name));
		label = new JLabel(jEdit.getProperty(
			"options.console.errors.match"),JLabel.RIGHT);
		label.setBorder(new EmptyBorder(0,0,0,12));
		panel.add(label);
		panel.add(match = new JTextField(matcher.match));
		label = new JLabel(jEdit.getProperty(
			"options.console.errors.filename"),JLabel.RIGHT);
		label.setBorder(new EmptyBorder(0,0,0,12));
		panel.add(label);
		panel.add(filename = new JTextField(matcher.filename));
		label = new JLabel(jEdit.getProperty(
			"options.console.errors.line"),JLabel.RIGHT);
		label.setBorder(new EmptyBorder(0,0,0,12));
		panel.add(label);
		panel.add(line = new JTextField(matcher.line));
		label = new JLabel(jEdit.getProperty(
			"options.console.errors.message"),JLabel.RIGHT);
		label.setBorder(new EmptyBorder(0,0,0,12));
		panel.add(label);
		panel.add(message = new JTextField(matcher.message));

		getContentPane().add(BorderLayout.CENTER,panel);

		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
		panel.setBorder(new EmptyBorder(6,12,12,12));
		panel.add(Box.createGlue());
		ok = new JButton(jEdit.getProperty("common.ok"));
		ok.addActionListener(new ActionHandler());
		getRootPane().setDefaultButton(ok);
		panel.add(ok);
		panel.add(Box.createHorizontalStrut(6));
		cancel = new JButton(jEdit.getProperty("common.cancel"));
		cancel.addActionListener(new ActionHandler());
		panel.add(cancel);
		panel.add(Box.createGlue());
		getContentPane().add(BorderLayout.SOUTH,panel);

		Dimension screen = getToolkit().getScreenSize();
		pack();
		setLocation((screen.width - getSize().width) / 2,
			(screen.height - getSize().height) / 2);
		show();
	}

	public void ok()
	{
		// Check values
		String _name = name.getText();
		String _match = match.getText();
		String _filename = filename.getText();
		String _line = line.getText();
		String _message = message.getText();
		if(_name == null || _match == null || _filename == null
			|| _line == null || _message == null)
		{
			GUIUtilities.error(JOptionPane.getFrameForComponent(this),
				"console.not-filled-out",null);
			return;
		}
		else
		{
			matcher.name = _name;
			matcher.match = _match;
			matcher.filename = _filename;
			matcher.line = _line;
			matcher.message = _message;
		}

		isOK = true;
		dispose();
	}

	public void cancel()
	{
		dispose();
	}

	public boolean isOK()
	{
		return isOK;
	}

	// private members
	private ErrorMatcher matcher;
	private JTextField name;
	private JTextField match;
	private JTextField filename;
	private JTextField line;
	private JTextField message;
	private JButton ok;
	private JButton cancel;
	private boolean isOK;

	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			if(evt.getSource() == ok)
				ok();
			else
				cancel();
		}
	}
}
