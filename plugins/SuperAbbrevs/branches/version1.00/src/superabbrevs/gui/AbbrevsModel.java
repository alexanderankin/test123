package superabbrevs.gui;

import java.util.ArrayList;
import java.util.Collections;
import javax.swing.table.AbstractTableModel;
import superabbrevs.Abbrev;

/**
 * @author sune
 * Created on 3. februar 2007, 14:30
 *
 */
public class AbbrevsModel extends AbstractTableModel {
  
  /**
   * Creates a new instance of AbbrevsModel
   */
  public AbbrevsModel(ArrayList<Abbrev> abbrevs) {
    this.abbrevs = abbrevs;
  }
  
  public int getRowCount() {
    return abbrevs.size();
  }
  
  public int getColumnCount() {
    return 1;
  }
  
  public Object getValueAt(int rowIndex, int columnIndex) {
    return abbrevs.get(rowIndex);
  }
  
  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    get(rowIndex).name = aValue.toString();
  }
  
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return true;
  }
  
  int sort(int selection) {
    Object selectedObject = get(selection);
    
    // Sort the table
    Collections.sort(abbrevs);
    
    // Find the added element
    for (int i = 0; true; i++) {
      if(selectedObject == (Object)abbrevs.get(i)) {
        fireTableRowsUpdated(0,getRowCount()-1);
        return i;
      }
    }
  }
  
  public void add() {
    abbrevs.add(new Abbrev("","",""));
    fireTableRowsInserted(abbrevs.size()-1,0);
  }
  
  void remove(int selection) {
    abbrevs.remove(selection);
  }
  
  Abbrev get(int selection) {
    return abbrevs.get(selection);
  }
  
  private ArrayList<Abbrev> abbrevs;
}
