/*
 * NodeSetTableModel.java - Table model for XPath node set results
 *
 * Copyright (c) 2002 Robert McKinnon
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

/**
 * Table model for XPath node set results.
 *
 * @author Robert McKinnon
 */
public class NodeSetTableModel extends AbstractTableModel {
  private static final String TYPE = jEdit.getProperty("xpath.result.node.type");
  private static final String NAME = jEdit.getProperty("xpath.result.node.name");
  private static final String VALUE = jEdit.getProperty("xpath.result.node.dom-value");

  private static final int TYPE_COL = 0;
  private static final int NAME_COL = 1;
  private static final int VALUE_COL = 2;

  private static final String[] ALL_COLUMNS = {TYPE, NAME, VALUE};
  private static final String[] NO_NAME_COLUMN = {TYPE, VALUE};
  private static final String[] NO_VALUE_COLUMN = {TYPE, NAME};
  private static final String[] NO_NAME_OR_VALUE_COLUMN = {TYPE};

  private static final String EMPTY_STRING = "";

  private String[] columnNames = ALL_COLUMNS;
  private Object[][] data;


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
    if(data == null) {
      return 0;
    } else {
      return data.length;
    }
  }


  /**
   * Implements method from interface {@link javax.swing.table.TableModel}.
   */
  public Object getValueAt(int row, int col) {
    Object cell = data[row][col];

    if(cell == null) {
      return EMPTY_STRING;
    } else {
      return cell;
    }
  }


  /**
   * Overrides method from class {@link javax.swing.table.AbstractTableModel}.
   */
  public String getColumnName(int col) {
    return columnNames[col];
  }


  /**
   * Overrides method from class {@link javax.swing.table.AbstractTableModel}.
   */
  public Class getColumnClass(int c) {
    return String.class;
  }


  /**
   * Overrides method from class {@link javax.swing.table.AbstractTableModel}.
   */
  public void setValueAt(Object value, int row, int col) {
    data[row][col] = value;
    fireTableCellUpdated(row, col);
  }


  /**
   * Sets the node type in the given row.
   */
  public void setNodeType(String type, int row) {
    setValueAt(type, row, TYPE_COL);
  }


  /**
   * Sets the node name in the given row.
   */
  public void setNodeName(String name, int row) {
    setValueAt(name, row, NAME_COL);
  }


  /**
   * Sets the node value in the given row.
   */
  public void setNodeValue(String value, int row) {
    setValueAt(value, row, VALUE_COL);
  }


  /**
   * Deletes current model rows, puts all columns back in model if necessary and
   * adds new rows equal to the given new row count.
   */
  public void resetRows(int newRowCount) {
    if(getRowCount() > 0) {
      fireTableRowsDeleted(0, getRowCount() - 1);
    }

    if(columnNames != ALL_COLUMNS) {
      setAllColumns();
      fireTableStructureChanged();
    }

    this.data = new Object[newRowCount][getColumnCount()];

    for(int i = 0; i < newRowCount; i++) {
      this.data[i] = new String[columnNames.length];
    }

    if(newRowCount > 0) {
      fireTableRowsInserted(0, newRowCount - 1);
    }
  }


  private void setAllColumns() {
    this.columnNames = ALL_COLUMNS;
    fireTableStructureChanged();
  }


  /**
   * Removes the node name column from the table model.
   */
  public void removeNameColumn() {
    // Need to put values from the last column into the middle column
    for(int i = 0; i < getRowCount(); i++) {
      data[i][NAME_COL] = data[i][VALUE_COL];
    }

    this.columnNames = NO_NAME_COLUMN;
    fireTableStructureChanged();
  }


  /**
   * Removes the node value column from the table model.
   */
  public void removeValueColumn() {
    this.columnNames = NO_VALUE_COLUMN;
    fireTableStructureChanged();
  }


  /**
   * Removes the node name and node value columns from the table model.
   */
  public void removeNameOrValueColumn() {
    this.columnNames = NO_NAME_OR_VALUE_COLUMN;
    fireTableStructureChanged();
  }

}