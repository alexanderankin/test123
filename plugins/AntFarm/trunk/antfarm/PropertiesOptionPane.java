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
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.io.*;

import org.gjt.sp.util.Log;

public class PropertiesOptionPane extends AbstractOptionPane
{

	final static String PROPERTY = "AntFarm.property.";
	final static String NAME = ".name";
	final static String VALUE = ".value";

	private PropertiesTable _table;


	public PropertiesOptionPane()
	{
		super( "Properties" );
	}


	public void _init()
	{
		addComponent( new JLabel( "Set global properties to use when running ant builds." ) );

		_table = new PropertiesTable( AntFarmPlugin.getGlobalProperties() );
		TableColumn column = null;
		for ( int i = 0; i < 2; i++ ) {
			column = _table.getColumnModel().getColumn( i );
			column.setPreferredWidth( 100 );
		}

		JScrollPane scrollPane = new JScrollPane( _table );
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
		String name;
		int counter = 1;
		while ( ( name = jEdit.getProperty( PROPERTY + counter + NAME ) ) != null ) {
			jEdit.setProperty( PROPERTY + counter + NAME, null );
			jEdit.setProperty( PROPERTY + counter + VALUE, null );
			counter++;
		}
		// put in the new ones

		Properties properties = _table.getProperties();
		Enumeration propertyNames = properties.propertyNames();
		for ( int i = 0; propertyNames.hasMoreElements(); i++ ) {
			name = (String) propertyNames.nextElement();
			String value = properties.getProperty( name );

			// Check if there is anything to save.
			if ( name == null || value == null )
				continue;

			if ( name.trim().length() > 0 && value.trim().length() > 0 ) {
				jEdit.setProperty( PROPERTY + ( i + 1 ) + NAME, name );
				jEdit.setProperty( PROPERTY + ( i + 1 ) + VALUE, value );
			}
		}
	}


	public void tableChanged( TableModelEvent e )
	{
		repaint();
	}

}


