package sidekick;

import java.util.ArrayList;
import java.util.List;

/**
 * An expansion model for trees.  This essentially just wraps a list of 
 * integers that represent the row numbers in the tree that should be 
 * expanded.  It is not necessary to use this model, but it is convenient
 * when creating the list of row numbers.  The value returned by <code>
 * getModel</code> is suitable for setting in the <code>expansionModel</code>
 * field in SideKickParsedData.
 */
public class ExpansionModel {
    private List<Integer> model = new ArrayList<Integer>();
    private int row = 0;

    /**
     * @return The expansion model, set this in SideKickParsedData.        
     */
    public List<Integer> getModel() {
        return model;
    }

    /**
     * Call this for each row in the tree that should be visible and expanded.
     * This will add the current row number to the model and automatically
     * inc().
     */
    public void add() {
        model.add( row );
        inc();
    }

    /**
     * Call this for each row in the tree that should be visible.
     */
    public void inc() {
        ++row;
    }

    /**
     * @return The current row value.        
     */
    public int getRow() {
        return row;
    }
}