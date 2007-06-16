package superabbrevs.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.AbstractListModel;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;
import superabbrevs.Abbrev;

/**
 * @author sune
 * Created on 28. januar 2007, 00:45
 *
 */
public class AbbrevsListModel extends AbstractListModel{
    
    /**
     * Creates a new instance of AbbrevsModel
     */
    public AbbrevsListModel(ArrayList<Abbrev> abbrevs) {
        this.abbrevs = abbrevs;
    }
    
    public Object getElementAt(int index) {
        return abbrevs.get(index);
    }
    
    public int sort(int selection) {
        Object selectedObject = selection != -1 ? 
            getElementAt(selection) : null; 
        // Sort the table
        Collections.sort(abbrevs);
        int newSelection = -1;
        if(selection != -1){
            // Find the added element
            boolean found = false;
            for (int i = 0; !found; i++) {
                if(selectedObject == (Object)abbrevs.get(i)) {
                    newSelection = i;
                    found = true;
                }
            }
        }
        fireContentsChanged(this, 0, abbrevs.size()-1);
        return newSelection;
    }
    
    /**
     * Returns the number of abbreviations in the model.
     * @return the number of abbreviations in the model.
     */
    public int getSize() {
        return abbrevs.size();
    }
    
    public int add(String name) {
        abbrevs.add(new Abbrev(name,"",""));
        int index = abbrevs.size()-1;
        fireIntervalAdded(this,index,index);
        return sort(index);
    }
    
    /**
     * Returns the abbreviation at the specified index.
     * @param selection The index of the wanted abbreviation.
     * @return The abbreviation at the specified index.
     */
    public Abbrev get(int selection) {
        return abbrevs.get(selection);
    }
    
    int remove(int selection) {
        abbrevs.remove(selection);
        fireIntervalRemoved(this,selection,selection);
        int newSelection = selection < abbrevs.size() ? 
            selection : selection - 1;
        //sort(newSelection)
        return newSelection;
    }
    
    int update(int selection, String name) {
        if (abbrevs.get(selection).name != name) {
            abbrevs.get(selection).name = name;
            return sort(selection);
        } else {
            return selection;
        }
    }
    
    public ArrayList<Abbrev> getAbbrevs(){
        return abbrevs;
    }
    
    public boolean unsorted = false;
    ArrayList<Abbrev> abbrevs;
}
