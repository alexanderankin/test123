/*
 * EditModesOptionPane.java - ShortcutSaver plugin's editmodes option pane
 *
 * Copyright (C) 2003 Carmine Lucarelli
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

package shortcutSaver;

import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.ArrayList;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;

public class EditModesOptionPane extends AbstractOptionPane
{
	private int paneNumber;
	
	public EditModesOptionPane(int paneNumber)
	{
		super("shortcutSaver.editmodes");
		this.paneNumber = paneNumber;
	}

	protected void _init()
	{
		setLayout(new BorderLayout());

		JLabel label = new JLabel(jEdit.getProperty(
			"options.shortcutSaver.editmodes.caption"));
		label.setBorder(new EmptyBorder(0,0,6,0));
		add(BorderLayout.NORTH,label);
		add(BorderLayout.CENTER,createModeTableScroller());
	}

	protected void _save()
	{
		model.save();
	}

	private ModeTableModel model;
	private JTable table;

	private JScrollPane createModeTableScroller()
	{
		model = new ModeTableModel(paneNumber);
		table = new JTable(model);
		table.getTableHeader().setReorderingAllowed(false);
		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(false);
		table.setCellSelectionEnabled(false);
		table.addMouseListener(new TableMouseHandler());

		Dimension d = table.getPreferredSize();
		d.height = Math.min(d.height,200);
		JScrollPane scroller = new JScrollPane(table);
		scroller.setPreferredSize(d);
		return scroller;
	}


	class TableMouseHandler extends MouseAdapter
	{
		public void mouseClicked(MouseEvent evt)
		{
			int row = table.getSelectedRow();
			ShortcutsDialog dialog = new ShortcutsDialog(GUIUtilities.getParentDialog(
				EditModesOptionPane.this), (String)model.getValueAt(row, 0), (String)model.getValueAt(row, 1));
			if(dialog.isOK())
				table.setValueAt(dialog.getAction(), row, 1);
		}
	}
} 

class ModeTableModel extends AbstractTableModel
{
	private ArrayList modes;
	private int paneNumber;
	
	ModeTableModel(int paneNumber)
	{
		this.paneNumber = paneNumber;

		Mode[] _modes = jEdit.getModes();
		modes = new ArrayList(_modes.length + 1);
		modes.add(new Entry("Default"));
		for(int i = 0; i < _modes.length; i++)
		{
			modes.add(new Entry(_modes[i].getName()));
		}
	} 

	public int getColumnCount()
	{
		return 2;
	} 

	public int getRowCount()
	{
		return modes.size();
	} 

	public Class getColumnClass(int col)
	{
		return String.class;
	} 

	public Object getValueAt(int row, int col)
	{
		Entry mode = (Entry)modes.get(row);
		switch(col)
		{
			case 0:
				return mode.name;
			case 1:
				return (mode.action == null ? "" : mode.action);
			default:
				throw new InternalError();
		}
	} 

	public boolean isCellEditable(int row, int col)
	{
		return (col != 0);
	} 

	public void setValueAt(Object value, int row, int col)
	{
		if(col == 0)
			return;

		Entry mode = (Entry)modes.get(row);
		switch(col)
		{
			case 1:
				mode.action = (String)value;
				break;
			default:
				throw new InternalError();
		}

		fireTableRowsUpdated(row,row);
	} 

	public String getColumnName(int index)
	{
		switch(index)
		{
			case 0:
				return jEdit.getProperty("options.shortcutSaver.editmodes.mode");
			case 1:
				return jEdit.getProperty("options.shortcutSaver.editmodes.actions");
			default:
				throw new InternalError();
		}
	}

	public void save()
	{
		for(int i = 0; i < modes.size(); i++)
		{
			((Entry)modes.get(i)).save();
		}
	}

	class Entry
	{
		String name;
		String action;

		Entry(String name)
		{
			this.name = name;
			action = jEdit.getProperty(ActionUtils.PROP_PREFIX + paneNumber + "." + name, "");
		}

		void save()
		{
			if(action.length() > 0)
				jEdit.setProperty(ActionUtils.PROP_PREFIX + paneNumber + "." + name, action);
			else
				jEdit.unsetProperty(ActionUtils.PROP_PREFIX + paneNumber + "." + name);
		}
	}
}
