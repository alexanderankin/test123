/*
 *  JImporterOptionPane.java - Plugin for add java imports to the top of a java file.
 *  Copyright (C) 2002 Matthew Flower (MattFlower@yahoo.com)
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package jimporter.options;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.event.EventListenerList;
import jimporter.classpath.Classpath;
import jimporter.searchmethod.SearchMethod;
import jimporter.sorting.SortCaseInsensitiveOption;
import jimporter.sorting.SortOnImportOption;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

/**
 * This class constructs the option pane that is used to collect information
 * about how to set up JImporter. It also is responsible for saving that
 * information to the jEdit properties file.
 *
 * @author    Matthew Flower
 */
public abstract class JImporterOptionPane extends AbstractOptionPane {
    /**
     * Public constructor.
     *
     * @param name The name that should appear on this pane's tab (if it has one)
     * or in the tree list of the global properties.
     */
    public JImporterOptionPane(String name) {
        super(name);
    }

    /**
     * A method defined by <code>AbstractOptionPane</code> which is called when
     * jEdit wants the option pane to populate itself.
     */
    public abstract void _init();

    /**
     * This method saves out any information modified in the JImporter option
     * page.
     */
    public void _save() {
        //Let all of the listeners know that a change has occurred
        fireSaveChanges();
    }

    /**
     * A list of listeners that want to know when the pane is trying to save 
     * itself.
     */
    EventListenerList saveListeners = new EventListenerList();
    
    /**
     * Add another class that is interested in knowing when this option pane want
     * to save its values.
     *
     * @param l an <code>OptionSaveListener</code> instance that wants to know
     * when jEdit wants the option pane to save itself.
     * @see #removeSaveListener
     */
    public void addSaveListener(OptionSaveListener l) {
        saveListeners.add(OptionSaveListener.class, l);
    }
    
    /**
     * Remove a class that was listening for the option pane to save itself.
     *
     * @param l an <code>OptionSaveListener</code> that no longer wants to be in 
     * the list.
     * @see #addSaveListener
     */
    public void removeSaveListener(OptionSaveListener l) {
        saveListeners.remove(OptionSaveListener.class, l);
    }
    
    /**
     * Gets a list of listeners waiting for this option pane to save itself.
     *
     * @return an <code>OptionSaveListener</code> array filled with listeners 
     * that want to know when this option pane wants to be saved.
     */
    public OptionSaveListener[] getSaveListeners() {
        return (OptionSaveListener[])saveListeners.getListeners(OptionSaveListener.class);
    }
    
    /**
     * This method is called when option pane is ready to notify its listeners
     * of a requested save.
     */
    private void fireSaveChanges() {
        OptionSaveListener[] listeners = (OptionSaveListener[])saveListeners.getListeners(OptionSaveListener.class);

        for (int i = 0; i < listeners.length; i++) {
            listeners[i].saveChanges();
        }
    }
    

}

