/*
 * ParserOptionPane.java - Sidekick parsers configuration panel
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2005 Matthieu Casanova
 * Portions copyright (C) 2000, 2003 Slava Pestov
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

package sidekick;

//{{{ Imports
import javax.swing.table.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import org.gjt.sp.jedit.*;
//}}}

//{{{ ParserOptionPane class
/**
 * An option pane to configure the mode - parsers associations.
 *
 * @author Matthieu Casanova
 */
public class ParserOptionPane extends AbstractOptionPane
{
	//{{{ ParserOptionPane constructor
	public ParserOptionPane()
	{
		super("sidekick.parser");
	} //}}}

	//{{{ _init() method
	public void _init()
	{
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, createTableScroller());
	} //}}}

	//{{{ _save() method
	public void _save()
	{
		tableModel.save();
	} //}}}

	//{{{ Private members

	//{{{ Instance variables
	private JTable table;
	private MyTableModel tableModel;
	//}}}

	//{{{ createTableScroller() method
	private JScrollPane createTableScroller()
	{
		tableModel = createModel();
		table = new JTable(tableModel);
		table.getTableHeader().setReorderingAllowed(false);
		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(false);
		table.setCellSelectionEnabled(false);
		String[] serviceNames = ServiceManager.getServiceNames(SideKickParser.SERVICE);
		Vector parserList = new Vector(serviceNames.length+2);
		parserList.add(null);
		parserList.add(SideKickPlugin.DEFAULT);
		for (int i = 0; i < serviceNames.length; i++)
		{
			parserList.add(serviceNames[i]);
		}
		Collections.sort(parserList, new Comparator(){
			public int compare(Object a, Object b) {
				a = a == null ? "" : a;
				b = b == null ? "" : b;
				return a.toString().compareToIgnoreCase(b.toString());
			}
		} );
		ParserCellRenderer comboBox = new ParserCellRenderer(parserList);
		table.setRowHeight(comboBox.getPreferredSize().height);
		TableColumn column = table.getColumnModel().getColumn(1);
		column.setCellRenderer(comboBox);
		column.setCellEditor(new DefaultCellEditor(new ParserCellRenderer(parserList)));

		Dimension d = table.getPreferredSize();
		d.height = Math.min(d.height,50);
		JScrollPane scroller = new JScrollPane(table);
		scroller.setPreferredSize(d);
		return scroller;
	} //}}}

	//{{{ createWindowModel() method
	private MyTableModel createModel()
	{
		return new MyTableModel();
	} //}}}

	//}}}

	//{{{ DockPositionCellRenderer class
	class ParserCellRenderer extends JComboBox
		implements TableCellRenderer
	{
		ParserCellRenderer(Vector vector)
		{
			super(vector);
			ParserCellRenderer.this.setRequestFocusEnabled(false);
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

//{{{ WindowTableModel class
class MyTableModel extends AbstractTableModel
{
	private java.util.List<Entry> modes;

//	public static final String DEFAULTPARSER = "default parser";

	//{{{ WindowTableModel constructor
	MyTableModel()
	{
		Mode[] modes = jEdit.getModes();
		this.modes = new ArrayList<Entry>(modes.length);
		for(Mode mode : modes)
		{
			Entry entry = new Entry(mode.getName());
			entry.completion = jEdit.getBooleanProperty("mode." + mode.getName() + ".sidekick.completion", true);
			this.modes.add(entry);
		}
		Collections.sort(this.modes);
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
		switch(col)
		{
		case 0:
		case 1:
			return String.class;
		case 2:
			return Boolean.class;
		default:
			throw new InternalError();
		}
	} //}}}

	//{{{ getValueAt() method
	public Object getValueAt(int row, int col)
	{
		Entry modeParser = (Entry)modes.get(row);
		switch(col)
		{
		case 0:
			return modeParser.mode;
		case 1:
			return modeParser.parser;
		case 2:
			return modeParser.completion;
		default:
			throw new InternalError();
		}
	} //}}}

	//{{{ isCellEditable() method
	public boolean isCellEditable(int row, int col)
	{
		return col == 1 || col == 2;
	} //}}}

	//{{{ setValueAt() method
	public void setValueAt(Object value, int row, int col)
	{
		if(col == 0)
			return;

		Entry modeParser = (Entry)modes.get(row);
		switch(col)
		{
		case 1:
			modeParser.parser = (String)value;
			break;
		case 2:
			modeParser.completion = (Boolean)value;
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
			return jEdit.getProperty("options.sidekick.parser.mode");
		case 1:
			return jEdit.getProperty("options.sidekick.parser.parser");
		case 2:
			return jEdit.getProperty("options.sidekick.parser.completion");
		default:
			throw new InternalError();
		}
	} //}}}

	//{{{ save() method
	public void save()
	{
		for(Entry entry : modes)
		{
			entry.save();
		}
	} //}}}

	//{{{ Entry class
	class Entry implements Comparable
	{
		String mode;
		String parser = null;
		boolean completion = false;

		Entry(String mode)
		{
			this.mode = mode;
			parser = jEdit.getProperty("mode."+ this.mode + ".sidekick.parser");
			completion = jEdit.getBooleanProperty("mode." + this.mode + ".sidekick.completion", true);
		}

		void save()
		{
			if (parser == SideKickPlugin.DEFAULT)
				jEdit.resetProperty("mode." + mode + ".sidekick.parser");
			else
				jEdit.setProperty("mode." + mode + ".sidekick.parser",parser);
			jEdit.setBooleanProperty("mode." + mode + ".sidekick.completion", completion);
		}

		public int compareTo(Object a) {
			return this.mode.compareToIgnoreCase(((Entry)a).mode);
		}
	} //}}}

} //}}}
