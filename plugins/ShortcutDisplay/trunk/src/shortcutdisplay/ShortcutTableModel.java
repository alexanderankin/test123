/*
 * :tabSize=4:indentSize=4:noTabs=true:
 * 
 *
 *  $Source$
 *  Copyright (C) 2004 Jeffrey Hoyt
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
package shortcutdisplay;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.table.AbstractTableModel;
import org.gjt.sp.jedit.*;
import shortcutdisplay.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.util.*; 

/**
 *  Table model for the pop up display box
 *
 *@author	 jchoyt
 *@created	April 29, 2004
 */
class ShortcutTableModel extends AbstractTableModel
{

	
	String[] columnTitles = {"Name", "Shortcut"};
	List shortcuts;
	int maxActionLength = 0;
	int maxShortcutLength = 0; 

	
	/**
	 *  Gets the preferredWidth attribute of the ShortcutTableModel object
	 *
	 *@return	The preferredWidth value
	 */
	public int getPreferredWidth()
	{
		return (int) ((maxActionLength + maxShortcutLength) * 1.1);
	} 

	
	/**
	 *  Constructor for the ShortcutTableModel object
	 *
	 *@param  bindings  Description of the Parameter
	 */
	ShortcutTableModel(Map bindings)
	{
		shortcuts = ShortcutTableModel.parseShortcuts(bindings, this);
	} 

	
	/**
	 *  Description of the Method
	 *
	 *@param  bindings  Description of the Parameter
	 *@param  model	 Description of the Parameter
	 *@return		   Description of the Return Value
	 */
	public static ArrayList parseShortcuts(Map bindings, ShortcutTableModel model)
	{
		ArrayList ret = new ArrayList();
		String prefix = (String) bindings.get(DefaultInputHandler.PREFIX_STR);
		if (prefix == null)
		{
			throw new NullPointerException("Incorrectly built Map.  There should be a binding in here stored under " + DefaultInputHandler.PREFIX_STR);
		}
		Set keys = bindings.keySet();
		Iterator iter = keys.iterator();
		Object key;
		Object value;
		while (iter.hasNext())
		{
			key = iter.next();
			value = bindings.get(key);
			if (key instanceof KeyEventTranslator.Key)
			{
				Log.log(Log.DEBUG, ShortcutDialog.class, key);
				key = keyToString((KeyEventTranslator.Key) key);
			}
			if (value instanceof String)
			{
				if (!key.equals(DefaultInputHandler.PREFIX_STR))
				{
					ret.add(new Shortcut((String) value, prefix + " " + key));
				}
				model.setMaxColumnWidths(prefix + " " + key, (String) value);

			}
			else if (value instanceof Map)
			{
				ret.addAll(parseShortcuts((Map) value, model));
			}
			else
			{
				throw new IllegalStateException("There should only be Strings or Maps in here.  Value was " + String.valueOf(value));
			}
		}
		if (jEdit.getBooleanProperty("options.shortcuts.sortbyaction", true))
		{
			Collections.sort(ret, ShortcutActionComparator.getComparator());
		}
		else
		{
			Collections.sort(ret);
		}
		return ret;
	} 

	
	/**
	 *  A modification of the code pilfered (with modifications) from GrabKeyDialog
	 *
	 *@param  key  Description of the Parameter
	 *@return	  Description of the Return Value
	 */
	protected static String keyToString(KeyEventTranslator.Key key)
	{
		StringBuffer keyString = new StringBuffer();
		if (key.modifiers != null & key.modifiers != "")
		{
			keyString.append(key.modifiers);
			keyString.append("+");
		}
		if (key.input == ' ')
		{
			keyString.append("SPACE");
		}
		else if (key.input != '\0')
		{
			keyString.append(key.input);
		}
		else
		{
			String symbolicName = GrabKeyDialog.getSymbolicName(key.key);

			if (symbolicName == null)
			{
				return "ERROR - NULL value returned";
			}

			keyString.append(symbolicName);
		}
		return keyString.toString();
	} 

	
	/**
	 *  Constructor for the setMaxColumnWidths object
	 *
	 *@param  shortcut  Description of the Parameter
	 *@param  action	Description of the Parameter
	 */
	protected void setMaxColumnWidths(String shortcut, String action)
	{
		maxActionLength = maxActionLength > action.length() ? maxActionLength : action.length();
		maxShortcutLength = maxShortcutLength > shortcut.length() ? maxShortcutLength : shortcut.length();
	} 

	
	/**
	 *  Gets the cellEditable attribute of the ShortcutTableModel object
	 *
	 *@param  rowIndex	 Description of the Parameter
	 *@param  columnIndex  Description of the Parameter
	 *@return			  The cellEditable value
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return false;
	} 

	
	/**
	 *  Gets the columnName attribute of the ShortcutTableModel object
	 *
	 *@param  col  Description of the Parameter
	 *@return	  The columnName value
	 */
	public String getColumnName(int col)
	{
		return columnTitles[col];
	} 

	
	/**
	 *  Gets the columnClass attribute of the ShortcutTableModel object
	 *
	 *@param  col  Description of the Parameter
	 *@return	  The columnClass value
	 */
	public Class getColumnClass(int col)
	{
		return String.class;
	} 

	
	/**
	 *  Gets the rowCount attribute of the ShortcutTableModel object
	 *
	 *@return	The rowCount value
	 */
	public int getRowCount()
	{
		return shortcuts.size();
	} 

	
	/**
	 *  Gets the columnCount attribute of the ShortcutTableModel object
	 *
	 *@return	The columnCount value
	 */
	public int getColumnCount()
	{
		return 2;
	} 

	
	/**
	 *  Gets the valueAt attribute of the ShortcutTableModel object
	 *
	 *@param  row	 Description of the Parameter
	 *@param  column  Description of the Parameter
	 *@return		 The valueAt value
	 */
	public Object getValueAt(int row, int column)
	{
		switch (column)
		{
			case 0:
				return (((Shortcut) shortcuts.get(row)).getAction());
			case 1:
				return (((Shortcut) shortcuts.get(row)).getShortcut1());
			default:
				throw new IllegalArgumentException("Only 2 columns in the list");
		}
	} 
} 


