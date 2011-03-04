/*
This file, unless otherwise noted, is wholly the work of Dale Anson,
danson@grafidog.com. The complete contents of this file is hereby 
released into the public domain, with no rights reserved. For questions, 
concerns, or comments, please email the author.
*/

package ise.calculator;

import javax.swing.table.*;
import java.util.*;

public class FunctionTableModel extends AbstractTableModel {

    private ArrayList data = null;
    private ArrayList<String> col_names = null;

    public FunctionTableModel() { }

    public FunctionTableModel(ArrayList v) {
        data = v;
    }

    public String getColumnName(int col) {
        if (col_names == null) {
            return null;
        }
        return col_names.get(col).toString();
    }

    public boolean isCellEditable(int row, int col) {
        return col == 0;
    }

    public Object getValueAt(int row, int col) {
        if (data == null) {
            return null;
        }
        return ((ArrayList) data.get(row)).get(col);
    }

    public void setValueAt(Object value, int row, int col) {
        if (data == null || value == null) {
            return ;
        }
        ArrayList row_data = (ArrayList) data.get(row);
        if (row_data == null) {
            return ;
        }
        row_data.set(col, value);
    }

    public void setData(ArrayList v, ArrayList<String> col_names) {
        data = v;
        this.col_names = col_names;
        fireTableDataChanged();
    }

    public int getRowCount() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    public int getColumnCount() {
        if (col_names == null) {
            return 0;
        }
        return col_names.size();
    }

    public Class getColumnClass(int col) {
        if (data == null) {
            return null;
        }
        return getValueAt(0, col).getClass();
    }
}

