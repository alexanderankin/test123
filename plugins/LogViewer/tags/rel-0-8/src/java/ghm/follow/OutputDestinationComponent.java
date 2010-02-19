
package ghm.follow;

import javax.swing.JComponent;

/**
 * This allows a component other than a JTextArea to be used to
 * display a log file.
 */
public abstract class OutputDestinationComponent extends JComponent implements OutputDestination {
    
    public abstract boolean getWordWrap();
    
    public abstract void setWordWrap(boolean wrap);
    
    public abstract void toggleWordWrap();
    
    public abstract boolean autoPositionCaret();
    
    public abstract void setAutoPositionCaret(boolean b);
    
    public abstract void toggleAutoPositionCaret();

    public abstract void find(String toFind);
    
    public abstract void findNext(String toFind);
    
    public abstract void setWrapFind(boolean wrap);
}

