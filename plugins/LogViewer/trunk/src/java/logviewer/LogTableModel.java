
package logviewer;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

/**
 * A fairly fast table model for log files. This model allows adding multiple
 * rows at once, then fires a single event, which greatly improves speed. The
 * table data is stored in a List of Lists.
 *
 * @version   $Revision$
 */
public class LogTableModel extends AbstractTableModel {

    // table data
    private java.util.List rowData;

    // column names
    private List columnNames = null;

    /**
     * Constructor for LogTableModel
     *
     * @param columnNames a list of column names, may not be null or empty
     */
    public LogTableModel(List columnNames) {
        if (columnNames == null || columnNames.size() == 0)
            throw new IllegalArgumentException("column names not given");
        this.columnNames = columnNames;
        rowData = new ArrayList();
    }

    /**
     * Adds a row of data to the model.
     *
     * @param data  The row date
     */
    public void addRow(List data) {
        int row = rowData.size();
        rowData.add(data);
        fireTableRowsInserted(row, row);
    }

    /**
     * Adds several rows at once to the model
     *
     * @param data  The row data
     */
    public void addRows(List data) {
        int row = rowData.size();
        rowData.addAll(data);
        fireTableRowsInserted(row, row + data.size());
    }

    /**
     * Gets the rowCount attribute of the LogTableModel object
     *
     * @return   The rowCount value
     */
    public int getRowCount() {
        return rowData.size();
    }

    /**
     * Gets the columnCount attribute of the LogTableModel object
     *
     * @return   The columnCount value
     */
    public int getColumnCount() {
        return columnNames.size();
    }

    /**
     * Gets the column name of the given column index.
     *
     * @param column index to column
     * @return  The name of the column
     */
    public String getColumnName(int column) {
        return (String) columnNames.get(column);
    }

    /**
     * Gets the data value at the given cell index.
     *
     * @param rowIndex the row index
     * @param columnIndex the column index
     * @return             The value in the cell
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex > rowData.size() - 1)
            return null;
        if (columnIndex > ((List) rowData.get(rowIndex)).size() - 1)
            return null;
        return ((java.util.List) rowData.get(rowIndex)).get(columnIndex);
    }
}

