/*
 *  ----------------------------------------------------------------------+
 *  |
 *  |    $RCSfile: SortingOptionPane.java,v $
 *  |   $Revision: 1.1 $
 *  |       $Date: 2002-09-21 09:15:40 $
 *  |     $Author: mattflower $
 *  |
 *  +----------------------------------------------------------------------
 */
package jimporter.options;

import javax.swing.event.EventListenerList;
import jimporter.sorting.SortCaseInsensitiveOption;
import jimporter.sorting.SortOnImportOption;
import org.gjt.sp.jedit.AbstractOptionPane;
import jimporter.grouping.ImportGroupOption;

/**
 * Class that defines an option pane that presents sorting options.
 *
 * @author Matthew Flower
 * @since   September 18, 2002
 */
public class SortingOptionPane extends JImporterOptionPane {
    /** 
     * A list of classes that want to know when this the user requests these
     * options to be saved.
     */
    EventListenerList saveListeners = new EventListenerList();

    /**
     * Standard constructor.
     */
    public SortingOptionPane() {
        super("jimporter.sorting");
    }

    /**
     * Get the list of objects that are waiting for this dialog to request that
     * it's options be saved.
     * 
     * @return A list of classes waiting for "save" to be clicked.
     */
    public OptionSaveListener[] getSaveListeners() {
        return (OptionSaveListener[]) saveListeners.getListeners(OptionSaveListener.class);
    }

    /**
     * Initialize the visual presentation of this option pane.
     */
    public void _init() {
        new SortOnImportOption().createVisualPresentation(this);
        new SortCaseInsensitiveOption().createVisualPresentation(this);
        new ImportGroupOption().createVisualPresentation(this);
    }

    /**
     * Save any changes that have occurred in this option pane.
     */
    public void _save() {
        fireSaveChanges();
    }

    /**
     * Add a class to the list of listeners that want to know when the options
     * of this pane need to be saved.
     * 
     * @param l The new listener that needs to be added to the listener list.
     * @see #removeSaveListener
     */
    public void addSaveListener(OptionSaveListener l) {
        saveListeners.add(OptionSaveListener.class, l);
    }

    /**
     * Remove a class that was waiting for this option pane to request saving.
     *
     * @param l The object that needs to be removed from the list.
     * @see #addSaveListener
     */
    public void removeSaveListener(OptionSaveListener l) {
        saveListeners.remove(OptionSaveListener.class, l);
    }

    /**
     * Indicate to the listeners that it is time to save themselves.
     */
    private void fireSaveChanges() {
        OptionSaveListener[] listeners = (OptionSaveListener[]) saveListeners.getListeners(OptionSaveListener.class);

        for (int i = 0; i < listeners.length; i++) {
            listeners[i].saveChanges();
        }
    }
}

