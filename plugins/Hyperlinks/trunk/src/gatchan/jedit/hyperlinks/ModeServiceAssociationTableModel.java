/*
 * ModeServiceAssociationTableModel.java - A table that will associate modes with services
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2007 Matthieu Casanova
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
package gatchan.jedit.hyperlinks;

import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.ServiceManager;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.*;
import java.util.Arrays;
import java.util.Vector;
import java.util.Collections;
import java.awt.*;
import java.util.stream.IntStream;

/**
 * @author Matthieu Casanova
 * @version $Id: Buffer.java 8190 2006-12-07 07:58:34Z kpouer $
 */
public class ModeServiceAssociationTableModel extends AbstractTableModel
{
	private final Vector<Entry> modes;
	private final String col1;
	private final String col2;
	private final String defaultOption;
	private final String propertySuffix;

	private ModeServiceAssociationTableModel(String col1, String col2, String defaultOption, String propertySuffix)
	{
		this.col1 = col1;
		this.col2 = col2;
		this.defaultOption = defaultOption;
		this.propertySuffix = propertySuffix;
		Mode[] modes = jEdit.getModes();
		this.modes = new Vector<>(modes.length);
		Arrays
			.stream(modes)
			.map(Mode::getName)
			.map(Entry::new)
			.forEach(this.modes::add);
		Collections.sort(this.modes);
	}

	public static JTable getTable(String serviceName, String col2, String defaultValue, String propertySuffix)
	{
		ModeServiceAssociationTableModel tableModel = new ModeServiceAssociationTableModel("Mode", col2, defaultValue, propertySuffix);
		JTable table = new JTable(tableModel);
		table.getTableHeader().setReorderingAllowed(false);
		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(false);
		table.setCellSelectionEnabled(false);
		String[] serviceNames = ServiceManager.getServiceNames(serviceName);
		Vector<String> serviceVector = new Vector<>(serviceNames.length + 2);
		serviceVector.add(null);
		serviceVector.add(defaultValue);
		serviceVector.addAll(Arrays.asList(serviceNames));
		serviceVector.sort((a, b) ->
		{
			a = a == null ? "" : a;
			b = b == null ? "" : b;
			return a.compareToIgnoreCase(b);
		});

		MyCellRenderer comboBox = new MyCellRenderer(serviceVector);
		table.setRowHeight(comboBox.getPreferredSize().height);
		TableColumn column = table.getColumnModel().getColumn(1);
		column.setCellRenderer(comboBox);
		column.setCellEditor(new DefaultCellEditor(new MyCellRenderer(serviceVector)));

		return table;
	}

	//{{{ getColumnCount() method
	@Override
	public int getColumnCount()
	{
		return 2;
	} //}}}

	//{{{ getRowCount() method
	@Override
	public int getRowCount()
	{
		return modes.size();
	} //}}}

	//{{{ getColumnClass() method
	@Override
	public Class<String> getColumnClass(int col)
	{
		switch (col)
		{
			case 0:
			case 1:
				return String.class;
			default:
				throw new InternalError();
		}
	} //}}}

	//{{{ getValueAt() method
	@Override
	public Object getValueAt(int row, int col)
	{
		Entry entry = modes.elementAt(row);
		switch (col)
		{
			case 0:
				return entry.getMode();
			case 1:
				return entry.getServiceName();
			default:
				throw new InternalError();
		}
	} //}}}

	//{{{ isCellEditable() method
	@Override
    public boolean isCellEditable(int row, int col)
	{
		return col == 1;
	} //}}}

	//{{{ setValueAt() method
	@Override
    public void setValueAt(Object value, int row, int col)
	{
		if (col == 0)
			return;

		Entry entry = modes.elementAt(row);
		switch (col)
		{
			case 1:
				entry.setServiceName((String) value);
				break;
			default:
				throw new InternalError();
		}

		fireTableRowsUpdated(row, row);
	} //}}}

	//{{{ getColumnName() method
	@Override
	public String getColumnName(int index)
	{
		switch (index)
		{
			case 0:
				return col1;
			case 1:
				return col2;
			default:
				throw new InternalError();
		}
	} //}}}

	//{{{ save() method
	public void save()
	{
		IntStream.range(0, modes.size()).forEach(i -> modes.get(i).save());
	} //}}}

	//{{{ Entry class
	private class Entry implements Comparable<Entry>
	{
		private final String mode;
		private String serviceName;

		Entry(String mode)
		{
			this.mode = mode;
			serviceName = jEdit.getProperty("mode." + this.mode + '.' + propertySuffix);
		}

		public String getMode()
		{
			return mode;
		}

		public String getServiceName()
		{
			return serviceName;
		}

		public void setServiceName(String serviceName)
		{
			this.serviceName = serviceName;
		}

		@Override
		public boolean equals(Object o)
		{
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			Entry entry = (Entry) o;
			return mode.equals(entry.mode);
		}

		@Override
		public int hashCode()
		{
			return mode.hashCode();
		}

		void save()
		{
			if (defaultOption.equals(serviceName))
				jEdit.resetProperty("mode." + mode + '.' + propertySuffix);
			else
				jEdit.setProperty("mode." + mode + '.' + propertySuffix, serviceName);
		}

		@Override
		public int compareTo(Entry a)
		{
			return mode.compareToIgnoreCase(a.mode);
		}
	} //}}}

	private static class MyCellRenderer extends JComboBox<String> implements TableCellRenderer
	{
		MyCellRenderer(Vector<String> vector)
		{
			super(vector);
			setRequestFocusEnabled(false);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
							       Object value, boolean isSelected, boolean hasFocus,
							       int row, int column)
		{
			setSelectedItem(value);
			return this;
		}
	}
} //}}}