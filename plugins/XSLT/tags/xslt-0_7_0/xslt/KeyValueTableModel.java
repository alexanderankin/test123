/*
 * KeyValueTableModel.java - Table model for XSL stylesheet parameters and XPath namespaces
 *
 * Copyright (c) 2003 Robert McKinnon
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

package xslt;

import org.gjt.sp.jedit.jEdit;

import javax.swing.table.AbstractTableModel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Table model for XSL stylesheet parameters and XPath namespaces
 *
 * @author Robert McKinnon
 */
public class KeyValueTableModel extends AbstractTableModel {
  private final String[] columnNames;

  private static final int NAME_COL = 0;
  private static final int VALUE_COL = 1;


  /** List of instances of KeyValue */
  private List parameterList = new LinkedList();


  public KeyValueTableModel(String name) {
  	   columnNames = new String[]{
  	   		  jEdit.getProperty(name+".table.name.header")
  	   		, jEdit.getProperty(name+".table.value.header")};
  }
  
  
  /**
   * Removes all of the elements from this model. The model will
   * be empty after this call returns (unless it throws an exception).
   */
  public void clear() {
    int index = parameterList.size() - 1;
    parameterList.clear();

    if(index >= 0) {
      fireTableRowsDeleted(0, index);
    }
  }


  /**
   * Implements method from interface {@link javax.swing.table.TableModel}.
   */
  public int getColumnCount() {
    return columnNames.length;
  }


  /**
   * Implements method from interface {@link javax.swing.table.TableModel}.
   */
  public int getRowCount() {
    return parameterList.size();
  }


  /**
   * Implements method from interface {@link javax.swing.table.TableModel}.
   */
  public Object getValueAt(int row, int col) {
    KeyValue parameter = getKeyValue(row);

    if(col == NAME_COL) {
      return parameter.getName();
    } else if(col == VALUE_COL) {
      return parameter.getValue();
    } else {
      throw new IllegalArgumentException();
    }
  }


  /**
   * Implements method from interface {@link javax.swing.table.TableModel}.
   */
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return true;
  }


  private KeyValue getKeyValue(int row) {
    return (KeyValue)parameterList.get(row);
  }


  public String getKeyValueName(int row) {
    return getKeyValue(row).getName();
  }


  public String getKeyValueValue(int row) {
    return getKeyValue(row).getValue();
  }


  /**
   * Overrides method from class {@link AbstractTableModel}.
   */
  public String getColumnName(int col) {
    return columnNames[col];
  }


  /**
   * Overrides method from class {@link AbstractTableModel}.
   */
  public Class getColumnClass(int col) {
    return String.class;
  }


  /**
   * Overrides method from class {@link AbstractTableModel}.
   */
  public void setValueAt(Object value, int row, int col) {
    KeyValue parameter = getKeyValue(row);
    String text = (String)value;

    if(col == NAME_COL) {
      if(text.equals("")) {// delete parameter
        parameterList.remove(row);
        fireTableRowsDeleted(row, row);
      } else {
        row = removeDuplicates(text, row);
        parameter.setName(text);
        fireTableCellUpdated(row, col);
      }

    } else if(col == VALUE_COL) {
      parameter.setValue(text);
      fireTableCellUpdated(row, col);
    } else {
      throw new IllegalArgumentException();
    }

  }


  public void removeKeyValue(int row) {
    parameterList.remove(row);
    fireTableRowsDeleted(row, row);
  }


  /**
   * Adds a parameter to the table.
   */
  public void addKeyValue(String name, String value) {
    removeDuplicates("", -1);

    KeyValue parameter = new KeyValue(name, value);
    parameterList.add(parameter);
    int newRow = parameterList.size() - 1;
    fireTableRowsInserted(newRow, newRow);
  }


  public int removeDuplicates(String newName, int row) {
    Iterator iterator = parameterList.iterator();
    int i = 0;

    while(iterator.hasNext()) {
      KeyValue parameter = (KeyValue)iterator.next();
      if(parameter.getName().equals(newName) && i != row) {
        iterator.remove();
        row--;
        fireTableRowsDeleted(i, i);
      }
      i++;
    }

    return row;
  }


  /**
   * Class to hold name and value information.
   */
  private class KeyValue {
    private String name;
    private String value;


    public KeyValue(String name, String value) {
      this.name = name;
      this.value = value;
    }


    public String getName() {
      return name;
    }


    public String getValue() {
      return value;
    }


    public void setName(String name) {
      this.name = name;
    }


    public void setValue(String value) {
      this.value = value;
    }

  }


}