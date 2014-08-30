/*
 * NodeSetTableModel.java - Table model for XPath node set results
 *
 * Copyright (c) 2002 Robert McKinnon
 * Copyright (c) 2013 Eric Le Lay
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
import org.gjt.sp.util.Log;

import javax.swing.table.AbstractTableModel;
import static xslt.XPathAdapter.XPathNode;
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


  private static final String[] ALL_COLUMNS_NAMES = {TYPE, NAME, VALUE};
  private static final int[] ALL_COLUMNS = {TYPE_COL, NAME_COL, VALUE_COL};

  private static final String EMPTY_STRING = "";

  private String[] columnNames = ALL_COLUMNS_NAMES;
  private int[] columns = ALL_COLUMNS;
  private XPathAdapter.Result data;


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
      try {
        return data.size();
      } catch(Exception e) {
        Log.log(Log.WARNING, NodeSetTableModel.class, "Error getting result size", e);
        return 0;
      }
    }
  }


  /**
   * Implements method from interface {@link javax.swing.table.TableModel}.
   */
  public Object getValueAt(int row, int col) {
      try{
    if(row >= 0 && row < getRowCount()) {
      XPathNode node = data.get(row);
      if(col >= 0 && col < columns.length){
        switch(columns[col]){
        case TYPE_COL:
          return node.getType();
        case NAME_COL:
          return node.getName();
        case VALUE_COL:
          return node.getDomValue();
        default:
          throw new IllegalStateException("invalid column: "+columns[col]);
        }
      }
    }
    }catch(Exception e){
      if(e instanceof IllegalStateException) throw (IllegalStateException)e;
      else{
        Log.log(Log.WARNING, NodeSetTableModel.class, "Error getting result node info", e);
      }
    }

	return EMPTY_STRING;
  }


  public XPathNode getValueAt(int row){
		if(row >= 0 && row < getRowCount()) {
			try{
				return data.get(row);
			}catch(Exception e){
				Log.log(Log.WARNING, NodeSetTableModel.class, "Error getting result node "+row, e);
			}
		}
		return null;
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
   * Deletes current model rows, puts all columns back in model if necessary and
   * adds new rows equal to the given new row count.
   */
  public void resetRows(XPathAdapter.Result result) throws Exception {
    if(getRowCount() > 0) {
      fireTableRowsDeleted(0, getRowCount() - 1);
    }

    this.data = result;

    if(result.isNodeSet()){
      boolean isNodeWithName = false;
      boolean isNodeWithValue = false;
      XPathAdapter.XPathNode node;

      for (int i = 0; i < result.size(); i++) {
        node = result.get(i);

        if (node != null) {
          isNodeWithName = isNodeWithName || node.hasExpandedName();
          isNodeWithValue = isNodeWithValue || node.hasDomValue();
        }
      }

      int[] tmpcols = new int[ALL_COLUMNS.length];
      tmpcols[0] = TYPE_COL;
      int icol = 1;
      if(isNodeWithName){
        tmpcols[icol++] = NAME_COL;
      }
      if(isNodeWithValue){
        tmpcols[icol++] = VALUE_COL;
      }

      columns = new int[icol];
      columnNames = new String[icol];
      for(int i=0;i<columns.length;i++){
        columns[i] = tmpcols[i];
        columnNames[i] = ALL_COLUMNS_NAMES[tmpcols[i]];
      }
      fireTableStructureChanged();


    if(result.size() > 0) {
        fireTableRowsInserted(0, result.size() - 1);
      }
    }
  }

}