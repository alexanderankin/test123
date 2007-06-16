package superabbrevs.gui;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import superabbrevs.Abbrev;

/**
 * @author sune
 * Created on 2. februar 2007, 22:25
 *
 */
public class AbbrevsTable extends JTable {
    
    /**
     * Creates a new instance of AbbrevsTable
     */
    public AbbrevsTable() {
      super();
      setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
   }
    
    /**
     * Overrides <code>processKeyEvent</code> to process events. *
     */
    protected void processKeyEvent(KeyEvent e) {
      if (e.getKeyCode() == KeyEvent.VK_TAB) {
        if(isEditing()) {
          getCellEditor().stopCellEditing();
        }
        transferFocus();
      } else {
        super.processKeyEvent(e);
      }
    }
    
    Abbrev getSelectedAbbrev() {
      int selection = getSelectedRow();
      if(selection != -1) {
        AbbrevsModel model = (AbbrevsModel)getModel();
        return model.get(selection);
      } else {
        return null;
      }
    }
    
    void sort() {
      int selection = getSelectedRow();
      AbbrevsModel model = (AbbrevsModel)getModel();
      int newSelection = model.sort(selection);
      getSelectionModel().setSelectionInterval(newSelection,newSelection);
    } 
    
    void add() {
      AbbrevsModel model = (AbbrevsModel)getModel();
      model.add();
      grabFocus();
      int selection = model.getRowCount()-1;
      getSelectionModel().setSelectionInterval(selection,selection);
      editCellAt(selection,0);
    }
        
    public String toString() {
      return "";
    }
}
