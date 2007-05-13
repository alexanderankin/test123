/*
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * ContextOptionPane.java - Context menu options panel
 * Copyright (C) 2000, 2001 Slava Pestov
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

package projectviewer.config;

//{{{ Imports
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import java.util.StringTokenizer;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.ButtonGroup;
import javax.swing.JScrollPane;
import javax.swing.JRadioButton;
import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.ActionSet;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.gui.EnhancedDialog;
import org.gjt.sp.jedit.gui.RolloverButton;
import org.gjt.sp.jedit.AbstractOptionPane;

import org.gjt.sp.util.StandardUtilities;

import common.gui.OkCancelButtons;

import projectviewer.vpt.VPTContextMenu;
//}}}

/**
 *	Right-click context menu editor. Slightly modified for ProjectViewer.
 *
 *	@author		Slava Pestov
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class ContextOptionPane extends AbstractOptionPane {

	//{{{ Private Members
	private DefaultListModel listModel;
	private JList list;
	private JButton add;
	private JButton remove;
	private JButton moveUp, moveDown;
	JCheckBox showUserFirst;
	//}}}

	//{{{ Constructor
	public ContextOptionPane(String name) {
		super(name);
	}
	//}}}

	//{{{ _init() method
	protected void _init()
	{
		setLayout(new BorderLayout());

		JLabel caption = new JLabel(jEdit.getProperty(
			"projectviewer.options.context.caption"));
		add(BorderLayout.NORTH,caption);

		String contextMenu = ProjectViewerConfig.getInstance().getUserContextMenu();
		listModel = new DefaultListModel();
		if (contextMenu != null) {
			StringTokenizer st = new StringTokenizer(contextMenu);
			while(st.hasMoreTokens())
			{
				String actionName = (String)st.nextToken();
				if(actionName.equals("-"))
					listModel.addElement(new ContextOptionPane.MenuItem("-","-"));
				else
				{
					EditAction action = jEdit.getAction(actionName);
					if(action == null)
						continue;
					String label = action.getLabel();
					if(label == null)
						continue;
					listModel.addElement(new ContextOptionPane.MenuItem(actionName,label));
				}
			}
		}

		list = new JList(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListHandler());

		add(BorderLayout.CENTER,new JScrollPane(list));

		JPanel buttons = new JPanel();
		buttons.setBorder(new EmptyBorder(3,0,0,0));
		buttons.setLayout(new BoxLayout(buttons,BoxLayout.X_AXIS));
		ActionHandler actionHandler = new ActionHandler();
		add = new RolloverButton(GUIUtilities.loadIcon("Plus.png"));
		add.setToolTipText(jEdit.getProperty("options.context.add"));
		add.addActionListener(actionHandler);
		buttons.add(add);
		buttons.add(Box.createHorizontalStrut(6));
		remove = new RolloverButton(GUIUtilities.loadIcon("Minus.png"));
		remove.setToolTipText(jEdit.getProperty("options.context.remove"));
		remove.addActionListener(actionHandler);
		buttons.add(remove);
		buttons.add(Box.createHorizontalStrut(6));
		moveUp = new RolloverButton(GUIUtilities.loadIcon("ArrowU.png"));
		moveUp.setToolTipText(jEdit.getProperty("options.context.moveUp"));
		moveUp.addActionListener(actionHandler);
		buttons.add(moveUp);

		moveDown = new RolloverButton(GUIUtilities.loadIcon("ArrowD.png"));
		moveDown.setToolTipText(jEdit.getProperty("options.context.moveDown"));
		moveDown.addActionListener(actionHandler);
		buttons.add(moveDown);

		showUserFirst = new JCheckBox(jEdit.getProperty("options.projectviewer.contextmenu.userfirst"));
		showUserFirst.setSelected(jEdit.getBooleanProperty("projectviewer.contextmenu.userfirst"));
		buttons.add(showUserFirst);

		buttons.add(Box.createHorizontalStrut(6));
		buttons.add(Box.createGlue());

		updateButtons();
		add(BorderLayout.SOUTH,buttons);
	} //}}}

	//{{{ MenuItemCompare class
	static class MenuItemCompare implements Comparator
	{
		public int compare(Object obj1, Object obj2)
		{
			return StandardUtilities.compareStrings(
				((MenuItem)obj1).label,
				((MenuItem)obj2).label,
				true);
		}
	} //}}}

	//{{{ _save() method
	protected void _save()
	{
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < listModel.getSize(); i++)
		{
			if(i != 0)
				buf.append(' ');
			buf.append(((MenuItem)listModel.elementAt(i)).actionName);
		}
		ProjectViewerConfig.getInstance().setUserContextMenu(buf.toString());
		jEdit.setBooleanProperty("projectviewer.contextmenu.userfirst", showUserFirst.isSelected());
		VPTContextMenu.userMenuChanged();
	} //}}}

	//{{{ updateButtons() method
	private void updateButtons()
	{
		int index = list.getSelectedIndex();
		remove.setEnabled(index != -1 && listModel.getSize() != 0);
		moveUp.setEnabled(index > 0);
		moveDown.setEnabled(index != -1 && index != listModel.getSize() - 1);
	} //}}}

	//{{{ MenuItem class
	static class MenuItem
	{
		String actionName;
		String label;

		MenuItem(String actionName, String label)
		{
			this.actionName = actionName;
			this.label = GUIUtilities.prettifyMenuLabel(label);
		}

		public String toString()
		{
			return label;
		}
	} //}}}

	//{{{ ActionHandler class
	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			Object source = evt.getSource();

			if(source == add)
			{
				ContextAddDialog dialog = new ContextAddDialog(
					ContextOptionPane.this);
				String selection = dialog.getSelection();
				if(selection == null)
					return;

				int index = list.getSelectedIndex();
				if(index == -1)
					index = listModel.getSize();
				else
					index++;

				MenuItem menuItem;
				if(selection.equals("-"))
					menuItem = new ContextOptionPane.MenuItem("-","-");
				else
				{
					menuItem = new ContextOptionPane.MenuItem(selection,
						jEdit.getAction(selection)
						.getLabel());
				}

				listModel.insertElementAt(menuItem,index);
				list.setSelectedIndex(index);
				list.ensureIndexIsVisible(index);
			}
			else if(source == remove)
			{
				int index = list.getSelectedIndex();
				listModel.removeElementAt(index);
				updateButtons();
			}
			else if(source == moveUp)
			{
				int index = list.getSelectedIndex();
				Object selected = list.getSelectedValue();
				listModel.removeElementAt(index);
				listModel.insertElementAt(selected,index-1);
				list.setSelectedIndex(index-1);
				list.ensureIndexIsVisible(index - 1);
			}
			else if(source == moveDown)
			{
				int index = list.getSelectedIndex();
				Object selected = list.getSelectedValue();
				listModel.removeElementAt(index);
				listModel.insertElementAt(selected,index+1);
				list.setSelectedIndex(index+1);
				list.ensureIndexIsVisible(index+1);
			}
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
}

class ContextAddDialog extends EnhancedDialog
{

	//{{{  Private Members
	private boolean isOK;
	private JRadioButton separator, action;
	private JComboBox combo;
	private JList list;
	private JButton ok, cancel;
	//}}}

	//{{{ Constructor
	public ContextAddDialog(Component comp)
	{
		super(GUIUtilities.getParentDialog(comp),
			jEdit.getProperty("options.context.add.title"),
			true);

		JPanel content = new JPanel(new BorderLayout());
		content.setBorder(new EmptyBorder(12,12,12,12));
		setContentPane(content);

		ActionHandler actionHandler = new ActionHandler();
		ButtonGroup grp = new ButtonGroup();

		JPanel typePanel = new JPanel(new GridLayout(3,1,6,6));
		typePanel.setBorder(new EmptyBorder(0,0,6,0));
		typePanel.add(new JLabel(
			jEdit.getProperty("options.context.add.caption")));

		separator = new JRadioButton(jEdit.getProperty("options.context"
			+ ".add.separator"));
		separator.addActionListener(actionHandler);
		grp.add(separator);
		typePanel.add(separator);

		action = new JRadioButton(jEdit.getProperty("options.context"
			+ ".add.action"));
		action.addActionListener(actionHandler);
		grp.add(action);
		action.setSelected(true);
		typePanel.add(action);

		content.add(BorderLayout.NORTH,typePanel);

		JPanel actionPanel = new JPanel(new BorderLayout(6,6));

		ActionSet[] actionsList = jEdit.getActionSets();
		Vector vec = new Vector(actionsList.length);
		for(int i = 0; i < actionsList.length; i++)
		{
			ActionSet actionSet = actionsList[i];
			if(actionSet.getActionCount() != 0)
				vec.addElement(actionSet);
		}
		combo = new JComboBox(vec);
		combo.addActionListener(actionHandler);
		actionPanel.add(BorderLayout.NORTH,combo);

		list = new JList();
		list.setVisibleRowCount(8);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		actionPanel.add(BorderLayout.CENTER,new JScrollPane(list));

		content.add(BorderLayout.CENTER,actionPanel);
		content.add(BorderLayout.SOUTH, new OkCancelButtons(this));
		updateList();

		pack();
		setLocationRelativeTo(GUIUtilities.getParentDialog(comp));
		setVisible(true);
	} //}}}

	//{{{ ok() method
	public void ok()
	{
		isOK = true;
		dispose();
	} //}}}

	//{{{ cancel() method
	public void cancel()
	{
		dispose();
	} //}}}

	//{{{ getSelection() method
	public String getSelection()
	{
		if(!isOK)
			return null;

		if(separator.isSelected())
			return "-";
		else if(action.isSelected())
		{
			Object selected = list.getSelectedValue();
			if (selected != null) {
				return ((ContextOptionPane.MenuItem)selected).actionName;
			} else {
				return null;
			}
		}
		else
			throw new InternalError();
	} //}}}

	//{{{ updateList() method
	private void updateList()
	{
		ActionSet actionSet = (ActionSet)combo.getSelectedItem();
		EditAction[] actions = actionSet.getActions();
		Vector listModel = new Vector(actions.length);

		for(int i = 0; i < actions.length; i++)
		{
			EditAction action = actions[i];
			String label = action.getLabel();
			if(label == null)
				continue;

			listModel.addElement(new ContextOptionPane.MenuItem(
				action.getName(),label));
		}

		Collections.sort(listModel, new ContextOptionPane.MenuItemCompare());

		list.setListData(listModel);
	} //}}}

	//{{{ ActionHandler class
	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			Object source = evt.getSource();
			if(source instanceof JRadioButton)
			{
				combo.setEnabled(action.isSelected());
				list.setEnabled(action.isSelected());
			}
			if(source == ok)
				ok();
			else if(source == cancel)
				cancel();
			else if(source == combo)
				updateList();
		}
	} //}}}

}

