/*
 *  PropertiesOptionPane.java - Panel in jEdit's Global Options dialog
 *  Copyright (C) 2001 Brian Knowles
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
package antfarm;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.io.*;

import org.gjt.sp.util.Log;

public class PropertiesOptionPane extends AbstractOptionPane
{
	private JTable table;
	private DndTableModel model;


	public PropertiesOptionPane()
	{
		super( "Properties" );
	}


	public void _init()
	{
		addComponent( new JLabel( "Set global properties to use when running ant builds." ) );

		model = new DndTableModel();
		table = new JTable( model );
		TableColumn column = null;
		for ( int i = 0; i < 2; i++ ) {
			column = table.getColumnModel().getColumn( i );
			column.setPreferredWidth( 100 );
		}

		setUpNameColumn( table.getColumnModel().getColumn( 0 ) );
		setUpValueColumn( table.getColumnModel().getColumn( 1 ) );
		JScrollPane scrollPane = new JScrollPane( table );
		addComponent( scrollPane );
	}


	/**
	 *  Called when the options dialog's `OK' button is pressed. This should save
	 *  any properties saved in this option pane.
	 *
	 * @since
	 */
	public void _save()
	{

		// get rid of old settings
		String work;
		int counter = 1;
		while ( ( work = jEdit.getProperty( "AntFarm.properties." + counter + ".name" ) ) != null ) {
			jEdit.setProperty( "AntFarm.properties." + counter + ".name", null );
			jEdit.setProperty( "AntFarm.properties." + counter + ".value", null );
			counter++;
		}
		// put in the new ones
		counter = table.getRowHeight();
		for ( int i = 0; i < counter; i++ ) {
			work = (String) table.getValueAt( i, 0 );
			String value = (String) table.getValueAt( i, 1 );
			if ( work.trim().length() > 0 && value.trim().length() > 0 ) {
				jEdit.setProperty( "AntFarm.properties." + ( i + 1 ) + ".name", work );
				jEdit.setProperty( "AntFarm.properties." + ( i + 1 ) + ".value", value );
			}
		}
		table = null;
		model = null;
	}


	public void tableChanged( TableModelEvent e )
	{
		repaint();
	}


	private void setUpNameColumn( TableColumn column )
	{
		column.setCellEditor( new DefaultCellEditor( new JTextField() ) );
	}


	private void setUpValueColumn( TableColumn column )
	{
		column.setCellEditor( new DefaultCellEditor( new JTextField() ) );
	}


	class DndTableModel extends AbstractTableModel
	{
		private String[] columnNames;
		private Vector names;
		private Vector values;


		public DndTableModel()
		{
			columnNames = new String[2];
			columnNames[0] = "Name";
			columnNames[1] = "Value";

			names = new Vector( 10 );
			values = new Vector( 10 );
			String name;
			int counter = 1;
			while ( ( name = jEdit.getProperty( "AntFarm.property." + counter + ".name" ) ) != null ) {
				values.addElement( jEdit.getProperty( "AntFarm.property." + counter + ".value" ) );
				names.addElement( name );
				counter++;
			}
			values.addElement( "" );
			names.addElement( "" );
		}


		public void setValueAt( Object value, int row, int col )
		{
			if ( ( (String) value ).length() == 0 ) {
				names.removeElementAt( row );
				values.removeElementAt( row );
				fireTableRowsDeleted( row, row );
			}
			else {
				if ( col == 0 ) {
					names.setElementAt( value, row );
				}
				else {
					values.setElementAt( value, row );
					if ( row == names.size() - 1 ) {
						names.addElement( "" );
						values.addElement( "" );
						fireTableRowsInserted( row + 1, row + 1 );
					}
				}
			}
			fireTableCellUpdated( row, col );
		}


		public int getColumnCount()
		{
			return columnNames.length;
		}


		public int getRowCount()
		{
			return names.size();
		}


		public String getColumnName( int col )
		{
			return columnNames[col];
		}


		public Object getValueAt( int row, int col )
		{
			if ( col == 0 ) {
				return names.elementAt( row );
			}
			return values.elementAt( row );
		}


		/*
		 *  JTable uses this method to determine the default renderer/
		 *  editor for each cell.  If we didn't implement this method,
		 *  then the last column would contain text ("true"/"false"),
		 *  rather than a check box.
		 */
		public Class getColumnClass( int c )
		{
			return getValueAt( 0, c ).getClass();
		}


		/*
		 *  Don't need to implement this method unless your table's
		 *  editable.
		 */
		public boolean isCellEditable( int row, int col )
		{
			return true;
		}
	}
}

