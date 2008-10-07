/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package superabbrevs.gui.searchdialog;

import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import superabbrevs.model.Abbrev;

/**
 *
 * @author Sune Simonsen
 */
public class SearchDialogModel implements TableModel {
    Abbrev[] abbrevs;
    ArrayList<Abbrev> matches = new ArrayList<Abbrev>();
    
    public SearchDialogModel(ArrayList<Abbrev> abbrevs) {
        this.abbrevs = abbrevs.toArray(new Abbrev[abbrevs.size()]);
        Arrays.sort(this.abbrevs);
        searchTextChanged("");
    }
    
    public int getRowCount() {
        return matches.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0: return matches.get(rowIndex).name; 
            default: return matches.get(rowIndex).abbreviation; 
        }
    }
    
    public Object getRowObject(int row) {
        return matches.get(row);
    }
    
    private ArrayList<TableModelListener> tableModelListeners = 
            new ArrayList<TableModelListener>();

    public void addTableModelListener(TableModelListener l) {
        tableModelListeners.add(l);
    }

    public void removeTableModelListener(TableModelListener l) {
        tableModelListeners.remove(l);
    }
    
    public void searchTextChanged(String searchText) {
        searchText = searchText.toLowerCase();
        matches.clear();
        for (Abbrev abbrev : abbrevs) {
            if (abbrev.name.toLowerCase().indexOf(searchText) != -1 ||
                abbrev.abbreviation.toLowerCase().indexOf(searchText) != -1) {
                matches.add(abbrev);
            }
        }
        
        for (TableModelListener listener : tableModelListeners) {
            TableModelEvent e = new TableModelEvent(this);
            listener.tableChanged(e);
        }
    }

    public boolean showHeader() {
        return false;
    }
    
    public int getColumnCount() {
        return 2;
    }

    public String getColumnName(int columnIndex) {
        return "";
    }

    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }
    
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        
    }
    
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
