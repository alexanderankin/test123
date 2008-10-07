/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package superabbrevs.gui;

import java.util.ArrayList;
import java.util.Collections;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import superabbrevs.model.Abbrev;

/**
 *
 * @author sune
 */
public class AbbrevsTableModel extends AbstractTableModel {

    enum ColumnNames {
        Name,
        Abbreviation
    }
    
    private ColumnNames[] columns = {
        ColumnNames.Name, 
        ColumnNames.Abbreviation
    };
    
    private ArrayList<Abbrev> abbrevs;
    
    public AbbrevsTableModel(ArrayList<Abbrev> abbrevs) {
        // Sort the table
        Collections.sort(abbrevs);
        this.abbrevs = abbrevs;
    }
    
    public int getRowCount() {
        return abbrevs.size();
    }

    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columns[columnIndex].name();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Abbrev a = abbrevs.get(rowIndex);
        ColumnNames name = columns[columnIndex];
        switch (name) {
            case Name:
                return a.getName();
            case Abbreviation:
                return a.getAbbreviation();
            default:
                assert false;
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Abbrev a = abbrevs.get(rowIndex);
        ColumnNames name = columns[columnIndex];
        switch (name) {
            case Name:
                a.setName((String) aValue);
            case Abbreviation:
                a.setAbbreviation((String) aValue);
            default:
                assert false;
        }
    }
}
