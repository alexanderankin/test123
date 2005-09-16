/*
 * ErrorsOptionPane.java - Error pattern option pane
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

package console.options;

//{{{ Imports
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

import console.ConsolePlugin;
import console.ErrorMatcher;
//}}}

//{{{ ErrorsOptionPane class
public class ErrorsOptionPane extends AbstractOptionPane
{
	private static final long serialVersionUID = -8757270001689935333L;
	
	
	//{{{ ErrorsOptionPane constructor
	public ErrorsOptionPane()
	{
		super("console.errors");
	} //}}}

	//{{{ Protected members

	//{{{ _init() method
	protected void _init()
	{
		addComponent(new JLabel(jEdit.getProperty(
			"options.console.errors.caption")));

		addComponent(Box.createVerticalStrut(6));

		JPanel errors = new JPanel(new BorderLayout());
		errorListModel = createMatcherListModel();
		errors.add(BorderLayout.CENTER,new JScrollPane(
			errorList = new JList(errorListModel)));
		errorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		errorList.addListSelectionListener(new ListHandler());
		errorList.addMouseListener(new MouseHandler());
		errorList.setVisibleRowCount(5);

		JPanel buttons = new JPanel();
		buttons.setBorder(new EmptyBorder(6,0,0,0));
		buttons.setLayout(new BoxLayout(buttons,BoxLayout.X_AXIS));

		buttons.add(Box.createGlue());

		buttons.add(add = new JButton(jEdit.getProperty(
			"options.console.errors.add")));
		add.addActionListener(new ActionHandler());
		buttons.add(Box.createHorizontalStrut(6));

		buttons.add(edit = new JButton(jEdit.getProperty(
			"options.console.errors.edit")));
		edit.addActionListener(new ActionHandler());
		buttons.add(Box.createHorizontalStrut(6));

		buttons.add(remove = new JButton(jEdit.getProperty(
			"options.console.errors.remove")));
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
	} //}}}

	//{{{ _save() method
	protected void _save()
	{
		
		StringBuffer list = new StringBuffer();
		for(int i = 0; i < errorListModel.getSize(); i++)
		{
			ErrorMatcher matcher = (ErrorMatcher)errorListModel.getElementAt(i);
			matcher.save();

			if(matcher.user)
			{
				if(i != 0)
					list.append(' ');
				list.append(matcher.internalName());
			}
		}

		jEdit.setProperty("console.error.user",list.toString());
	} //}}}

	//}}}

	//{{{ Private members

	//{{{ Instance variables
	private JList errorList;
	private DefaultListModel errorListModel;
	private JButton edit;
	private JButton add;
	private JButton remove;
	//}}}

	//{{{ createMatcherListModel() method
	private DefaultListModel createMatcherListModel()
	{
		DefaultListModel listModel = new DefaultListModel();

		ErrorMatcher[] matchers = ConsolePlugin.getErrorMatchers();
		for(int i = 0; i < matchers.length; i++)
		{
			listModel.addElement(matchers[i].clone());
		}

		return listModel;
	} //}}}
	
	//{{{ updateButtons() method
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
	} //}}}

	//}}}

	//{{{ ActionHandler class
	class ActionHandler implements ActionListener
	{
		

		
		public void actionPerformed(ActionEvent evt)
		{
			Object source = evt.getSource();
			if(source == edit)
			{
				ErrorMatcher error = (ErrorMatcher)errorList
					.getSelectedValue();
				new ErrorMatcherDialog(ErrorsOptionPane.this,error);
				errorList.repaint();
			}
			else if(source == add)
			{
				ErrorMatcher matcher = new ErrorMatcher();
				matcher.user = true;
				if(new ErrorMatcherDialog(ErrorsOptionPane.this,matcher).isOK())
				{
					int index = errorList.getSelectedIndex() + 1;

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
		}
	} //}}}

	//{{{ ListHandler class
	class ListHandler implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent evt)
		{
			updateButtons();
		}
	} //}}}

	//{{{ MouseHandler class
	class MouseHandler extends MouseAdapter
	{
		public void mouseClicked(MouseEvent evt)
		{
			if(evt.getClickCount() == 2)
			{
				ErrorMatcher error = (ErrorMatcher)errorList
					.getSelectedValue();
				new ErrorMatcherDialog(ErrorsOptionPane.this,error);
				errorList.repaint();
			}
		}
	} //}}}
} //}}}
