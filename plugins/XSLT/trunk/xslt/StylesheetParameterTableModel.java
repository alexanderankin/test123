/*
 * StylesheetParameterTableModel.java - Table model for XSL stylesheet parameters
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
 * Table model for XSL stylesheet parameters.
 *
 * @author Robert McKinnon
 */
public class StylesheetParameterTableModel extends AbstractTableModel {
  private static final String NAME = jEdit.getProperty("XSLTProcessor.parameters.name.header");
  private static final String VALUE = jEdit.getProperty("XSLTProcessor.parameters.value.header");
  private static final String[] COLUMN_NAMES = {NAME, VALUE};

  private static final int NAME_COL = 0;
  private static final int VALUE_COL = 1;


  /** List of instances of Parameter */
  private List parameterList = new LinkedList();


  /**
   * Implements method from interface {@link javax.swing.table.TableModel}.
   */
  public int getColumnCount() {
    return COLUMN_NAMES.length;
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
    Parameter parameter = getParameter(row);

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


  private Parameter getParameter(int row) {
    return (Parameter)parameterList.get(row);
  }


  public String getParameterName(int row) {
    return getParameter(row).getName();
  }


  public String getParameterValue(int row) {
    return getParameter(row).getValue();
  }


  /**
   * Overrides method from class {@link AbstractTableModel}.
   */
  public String getColumnName(int col) {
    return COLUMN_NAMES[col];
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
    Parameter parameter = getParameter(row);
    String text = (String)value;

    if(col == NAME_COL) {
      row = removeDuplicates(text, row);
      parameter.setName(text);
      fireTableCellUpdated(row, col);

    } else if(col == VALUE_COL) {
      parameter.setValue(text);
      fireTableCellUpdated(row, col);
    } else {
      throw new IllegalArgumentException();
    }

  }


  public void removeParameter(int row) {
    parameterList.remove(row);
    fireTableRowsDeleted(row, row);
  }


  /**
   * Adds a parameter to the table.
   */
  public void addParameter(String name, String value) {
    removeDuplicates("", -1);

    Parameter parameter = new Parameter(name, value);
    parameterList.add(parameter);
    int newRow = parameterList.size() - 1;
    fireTableRowsInserted(newRow, newRow);
  }


  private int removeDuplicates(String newName, int row) {
    Iterator iterator = parameterList.iterator();
    int i = 0;

    while(iterator.hasNext()) {
      Parameter parameter = (Parameter)iterator.next();
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
   * Class to hold parameter name and value information.
   */
  private class Parameter {
    private String name;
    private String value;


    public Parameter(String name, String value) {
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


    public boolean hasNoName() {
      return name.equals("");
    }
  }


}