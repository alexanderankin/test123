/*
 * CompileRunOptionPane.java - Compile & run option pane
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2002 Slava Pestov
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
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.jEdit;

import console.ConsolePlugin;
//}}}

//{{{ CompileRunOptionPane class
public class CompileRunOptionPane extends AbstractOptionPane
{
	private static final long serialVersionUID = -1672963909425664168L;
	public static final String NONE = "none";

	//{{{ CompileRunOptionPane constructor
	public CompileRunOptionPane()
	{
		super("console.compile-run");
	} //}}}

	//{{{ Protected members

	//{{{ _init() method
	protected void _init()
	{
		setLayout(new BorderLayout());

		JLabel label = new JLabel(jEdit.getProperty(
			"options.console.compile-run.caption"));
		label.setBorder(new EmptyBorder(0,0,6,0));
		add(BorderLayout.NORTH,label);

		add(BorderLayout.CENTER,createModeTableScroller());
	} //}}}

	//{{{ _save() method
	protected void _save()
	{
		model.save();
	} //}}}

	//}}}

	//{{{ Private members
	private ModeTableModel model;

	//{{{ createModeTableScroller() method
	private JScrollPane createModeTableScroller()
	{
		EditAction[] commandos = ConsolePlugin.getCommandoCommands();
		String[] labels = new String[commandos.length + 1];
		for(int i = 0; i < commandos.length; i++)
		{
			labels[i + 1] = commandos[i].getLabel();
		}

		labels[0] = CompileRunOptionPane.NONE;

		model = new ModeTableModel();
		JTable table = new JTable(model);
		table.getTableHeader().setReorderingAllowed(false);
		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(false);
		table.setCellSelectionEnabled(false);

		CommandoCellRenderer comboBox = new CommandoCellRenderer(labels);
		comboBox.setRequestFocusEnabled(false);
		table.setRowHeight(comboBox.getPreferredSize().height);

		TableColumn column = table.getColumnModel().getColumn(1);
		column.setCellRenderer(comboBox);
		comboBox = new CommandoCellRenderer(labels);
		comboBox.setRequestFocusEnabled(false);
		column.setCellEditor(new DefaultCellEditor(comboBox));

		comboBox = new CommandoCellRenderer(labels);
		comboBox.setRequestFocusEnabled(false);
		column = table.getColumnModel().getColumn(2);
		column.setCellRenderer(comboBox);
		comboBox = new CommandoCellRenderer(labels);
		comboBox.setRequestFocusEnabled(false);
		column.setCellEditor(new DefaultCellEditor(comboBox));

		Dimension d = table.getPreferredSize();
		d.height = Math.min(d.height,200);
		JScrollPane scroller = new JScrollPane(table);
		scroller.setPreferredSize(d);
		return scroller;
	} //}}}

	//}}}

	//{{{ CommandoCellRenderer class
	class CommandoCellRenderer extends JComboBox
		implements TableCellRenderer
	{

		private static final long serialVersionUID = 6013923260715257510L;

		CommandoCellRenderer(String[] labels)
		{
			super(labels);
		}

		public Component getTableCellRendererComponent(JTable table,
			Object value, boolean isSelected, boolean hasFocus,
			int row, int column)
		{
			setSelectedItem(value);
			return this;
		}
	} //}}}
} //}}}

//{{{ ModeTableModel class
class ModeTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 5314012645070764005L;
	private ArrayList<Entry> modes;

	//{{{ ModeTableModel constructor
	ModeTableModel()
	{
		Mode[] _modes = jEdit.getModes();

		modes = new ArrayList<Entry>(_modes.length);

		for(int i = 0; i < _modes.length; i++)
		{
			modes.add(new Entry(_modes[i].getName()));
		}
	} //}}}

	//{{{ getColumnCount() method
	public int getColumnCount()
	{
		return 3;
	} //}}}

	//{{{ getRowCount() method
	public int getRowCount()
	{
		return modes.size();
	} //}}}

	//{{{ getColumnClass() method
	public Class getColumnClass(int col)
	{
		return String.class;
	} //}}}

	//{{{ getValueAt() method
	public Object getValueAt(int row, int col)
	{
		Entry mode = (Entry)modes.get(row);
		switch(col)
		{
		case 0:
			return mode.name;
		case 1:
			return (mode.compiler == null ? CompileRunOptionPane.NONE
				: mode.compiler);
		case 2:
			return (mode.interpreter == null ? CompileRunOptionPane.NONE
				: mode.interpreter);
		default:
			throw new InternalError();
		}
	} //}}}

	//{{{ isCellEditable() method
	public boolean isCellEditable(int row, int col)
	{
		return (col != 0);
	} //}}}

	//{{{ setValueAt() method
	public void setValueAt(Object value, int row, int col)
	{
		if(col == 0)
			return;

		Entry mode = (Entry)modes.get(row);
		switch(col)
		{
		case 1:
			mode.compiler = (String)value;
			if(mode.compiler.equals(CompileRunOptionPane.NONE))
				mode.compiler = null;
			break;
		case 2:
			mode.interpreter = (String)value;
			if(mode.interpreter.equals(CompileRunOptionPane.NONE))
				mode.interpreter = null;
			break;
		default:
			throw new InternalError();
		}

		fireTableRowsUpdated(row,row);
	} //}}}

	//{{{ getColumnName() method
	public String getColumnName(int index)
	{
		switch(index)
		{
		case 0:
			return jEdit.getProperty("options.console.compile-run.mode");
		case 1:
			return jEdit.getProperty("options.console.compile-run.compiler");
		case 2:
			return jEdit.getProperty("options.console.compile-run.interpreter");
		default:
			throw new InternalError();
		}
	} //}}}

	//{{{ save() method
	public void save()
	{
		for(int i = 0; i < modes.size(); i++)
		{
			((Entry)modes.get(i)).save();
		}
	} //}}}

	//{{{ Entry class
	static class Entry
	{
		String name;
		String compiler;
		String interpreter;

		Entry(String name)
		{
			this.name = name;

			compiler = jEdit.getProperty("mode." + name + ".commando.compile");
			interpreter = jEdit.getProperty("mode." + name + ".commando.run");
		}

		void save()
		{
			if(compiler != null)
			{
				jEdit.setProperty("mode." + name + ".commando.compile",
					compiler.replace(' ','_'));
			}
			else
				jEdit.unsetProperty("mode." + name + ".commando.compile");
			if(interpreter != null)
			{
				jEdit.setProperty("mode." + name + ".commando.run",
					interpreter.replace(' ','_'));
			}
			else
				jEdit.unsetProperty("mode." + name + ".commando.run");
		}
	} //}}}
} //}}}
