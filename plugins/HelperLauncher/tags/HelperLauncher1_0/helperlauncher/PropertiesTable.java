/*
 *  PropertiesTable.java - Extention of JTable to enter properties.
 *  Copyright (C) 2001 Brian Knowles, 2007 Carmine Lucarelli
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package helperlauncher;

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.io.*;
import org.gjt.sp.util.Log;


/**
 *  Description of the Class
 */
public class PropertiesTable extends JTable
{

	final static String NAME_HISTORY_MODEL = HelperLauncherOptionPane.PROPERTY + HelperLauncherOptionPane.NAME;
	final static String VALUE_HISTORY_MODEL = HelperLauncherOptionPane.PROPERTY + HelperLauncherOptionPane.VALUE;


	/**
	 *  Constructor for the PropertiesTable object
	 *
	 *@param  properties Description of the Parameter
	 */
	public PropertiesTable(Properties properties)
	{
		super(new PropertiesTableModel(properties));

		setUpNameColumn(getColumnModel().getColumn(0));
		setUpValueColumn(getColumnModel().getColumn(1));
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	}


	/**
	 *  Constructor for the PropertiesTable object
	 */
	public PropertiesTable()
	{
		this(new Properties());
	}


	/**
	 *  Gets the properties attribute of the PropertiesTable object
	 *
	 *@return  The properties value
	 */
	public Properties getProperties()
	{
		Properties properties = new Properties();
		int counter = getRowHeight();
		String name = "";
		String value = "";
		for(int i = 0; i < counter; i++)
		{
			name = (String)getValueAt(i, 0);
			value = (String)getValueAt(i, 1);
			if(name == null || value == null)
				continue;
			properties.setProperty(name, value);
		}
		return properties;
	}


	private void setUpNameColumn(TableColumn column)
	{
		DefaultCellEditor cellEditor = new DefaultCellEditor(
			new HistoryTextField(NAME_HISTORY_MODEL, true, false)
			);
		cellEditor.setClickCountToStart(1);
		column.setCellEditor(cellEditor);
		column.setPreferredWidth(75);

	}


	private void setUpValueColumn(TableColumn column)
	{
		DefaultCellEditor cellEditor = new DefaultCellEditor(
			new HistoryTextField(VALUE_HISTORY_MODEL, true, false)
			);
		cellEditor.setClickCountToStart(1);
		column.setCellEditor(cellEditor);
		column.setPreferredWidth(75);

	}

}


class PropertiesTableModel extends AbstractTableModel
{


	private String[] columnNames;
	private Vector names;
	private Vector values;


	/**
	 *  Constructor for the PropertiesTableModel object
	 *
	 *@param  properties Description of the Parameter
	 */
	public PropertiesTableModel(Properties properties)
	{
		columnNames = new String[2];
		columnNames[0] = "File Extension Glob, e.g. *.{jpg,gif,png}";
		columnNames[1] = "Executable, e.g. /usr/bin/gimp";

		names = new Vector(10);
		values = new Vector(10);

		String name = "";
		Enumeration keys = properties.propertyNames();
		while(keys.hasMoreElements())
		{
			name = (String)keys.nextElement();
			names.addElement(name);
			values.addElement(properties.getProperty(name));
		}
		names.addElement("");
		values.addElement("");
	}


	/**
	 *  Sets the valueAt attribute of the PropertiesTableModel object
	 *
	 *@param  value The new valueAt value
	 *@param  row The new valueAt value
	 *@param  col The new valueAt value
	 */
	public void setValueAt(Object value, int row, int col)
	{
		if(((String)value).length() == 0)
		{
			names.removeElementAt(row);
			values.removeElementAt(row);
			fireTableRowsDeleted(row, row);
			if(names.size() < 1)
				insertBlankRow(row);
		}
		else
		{
			if(col == 0)
			{
				names.setElementAt(value, row);

				// add the value to our history model
				HistoryModel.getModel(PropertiesTable.NAME_HISTORY_MODEL).addItem((String)value);
			}
			else
			{
				values.setElementAt(value, row);

				// add the value to our history model
				HistoryModel.getModel(PropertiesTable.VALUE_HISTORY_MODEL).addItem((String)value);

			}
			if(row == names.size() - 1)
				insertBlankRow(row);
		}

		fireTableCellUpdated(row, col);
	}


	/**
	 *  Gets the columnCount attribute of the PropertiesTableModel object
	 *
	 *@return  The columnCount value
	 */
	public int getColumnCount()
	{
		return columnNames.length;
	}


	/**
	 *  Gets the rowCount attribute of the PropertiesTableModel object
	 *
	 *@return  The rowCount value
	 */
	public int getRowCount()
	{
		return names.size();
	}


	/**
	 *  Gets the columnName attribute of the PropertiesTableModel object
	 *
	 *@param  col Description of the Parameter
	 *@return  The columnName value
	 */
	public String getColumnName(int col)
	{
		return columnNames[col];
	}


	/**
	 *  Gets the valueAt attribute of the PropertiesTableModel object
	 *
	 *@param  row Description of the Parameter
	 *@param  col Description of the Parameter
	 *@return  The valueAt value
	 */
	public Object getValueAt(int row, int col)
	{

		if(row >= names.size() || row >= names.size())
			return null;

		if(col == 0)
			return names.elementAt(row);
		return values.elementAt(row);
	}


	/*
	 *  JTable uses this method to determine the default renderer/
	 *  editor for each cell.  If we didn't implement this method,
	 *  then the last column would contain text ("true"/"false"),
	 *  rather than a check box.
	 */
	/**
	 *  Gets the columnClass attribute of the PropertiesTableModel object
	 *
	 *@param  c Description of the Parameter
	 *@return  The columnClass value
	 */
	public Class getColumnClass(int c)
	{
		return getValueAt(0, c).getClass();
	}


	/*
	 *  Don't need to implement this method unless your table's
	 *  editable.
	 */
	/**
	 *  Gets the cellEditable attribute of the PropertiesTableModel object
	 *
	 *@param  row Description of the Parameter
	 *@param  col Description of the Parameter
	 *@return  The cellEditable value
	 */
	public boolean isCellEditable(int row, int col)
	{
		return true;
	}


	private void insertBlankRow(int row)
	{
		names.addElement("");
		values.addElement("");
		fireTableRowsInserted(row + 1, row + 1);
	}
}

